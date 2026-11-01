package com.team2073.eagleforcescoutingapplication.framework.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.team2073.platformkit.EfPairing;

import java.util.ArrayDeque;
import java.util.List;

import timber.log.Timber;

/**
 * One connect → authenticate → push unacked events → disconnect session with the stands gateway.
 *
 * Android BLE allows only ONE outstanding GATT operation, so every write goes through a queue that
 * advances in onCharacteristicWrite. All state lives on the main looper.
 */
@SuppressLint("MissingPermission") // callers check BLUETOOTH_CONNECT / location before starting a session
final class BleSyncSession {
    interface Listener {
        void onState(String state);
        void onFinished(boolean ok, String message, String gatewayId);
    }

    private static final int BATCH = 40;
    private static final long TIMEOUT_MS = 30_000;

    private final Context ctx;
    private final BluetoothDevice device;
    private final byte[] pairingKey;
    private final String deviceId;
    private final String scout;
    private final ScoutEventLog log;
    private final Listener listener;
    /** "scout" = push events (sync). "controller" + command = run one command (e.g. start_match) and leave. */
    private final String role;
    private final JSONObject command;
    private final Handler main = new Handler(Looper.getMainLooper());
    private final BleProtocol.Decoder decoder = new BleProtocol.Decoder();
    private final ArrayDeque<byte[]> writeQueue = new ArrayDeque<>();

    private BluetoothGatt gatt;
    private BluetoothGattCharacteristic rx;
    private int mtu = 23;
    private boolean writing = false;
    private boolean finished = false;
    private String gatewayNonce;
    private String clientNonce;
    private String gatewayId = "";
    private long ackBeforeBatch = -1;

    BleSyncSession(Context ctx, BluetoothDevice device, byte[] pairingKey, String deviceId, String scout,
                   ScoutEventLog log, Listener listener) {
        this(ctx, device, pairingKey, deviceId, scout, log, listener, "scout", null);
    }

    BleSyncSession(Context ctx, BluetoothDevice device, byte[] pairingKey, String deviceId, String scout,
                   ScoutEventLog log, Listener listener, String role, JSONObject command) {
        this.role = role;
        this.command = command;
        this.ctx = ctx.getApplicationContext();
        this.device = device;
        this.pairingKey = pairingKey;
        this.deviceId = deviceId;
        this.scout = scout;
        this.log = log;
        this.listener = listener;
    }

    void start() {
        listener.onState("connecting");
        main.postDelayed(() -> finish(false, "timed out"), TIMEOUT_MS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            gatt = device.connectGatt(ctx, false, callback, android.bluetooth.BluetoothDevice.TRANSPORT_LE);
        } else {
            gatt = device.connectGatt(ctx, false, callback);
        }
    }

    private final BluetoothGattCallback callback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt g, int status, int newState) {
            main.post(() -> {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    listener.onState("negotiating");
                    if (!g.requestMtu(BleProtocol.REQUESTED_MTU)) g.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    finish(false, "disconnected (status " + status + ")");
                }
            });
        }

        @Override
        public void onMtuChanged(BluetoothGatt g, int newMtu, int status) {
            main.post(() -> {
                if (status == BluetoothGatt.GATT_SUCCESS) mtu = newMtu;
                g.discoverServices();
            });
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt g, int status) {
            main.post(() -> {
                BluetoothGattService svc = g.getService(BleProtocol.SERVICE_UUID);
                if (status != BluetoothGatt.GATT_SUCCESS || svc == null) { finish(false, "not a scouting gateway"); return; }
                rx = svc.getCharacteristic(BleProtocol.RX_UUID);
                BluetoothGattCharacteristic tx = svc.getCharacteristic(BleProtocol.TX_UUID);
                if (rx == null || tx == null) { finish(false, "gateway missing characteristics"); return; }
                rx.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT); // acknowledged writes
                g.setCharacteristicNotification(tx, true);
                BluetoothGattDescriptor cccd = tx.getDescriptor(BleProtocol.CCCD_UUID);
                if (cccd == null) { finish(false, "gateway missing CCCD"); return; }
                cccd.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                writing = true;
                if (!g.writeDescriptor(cccd)) finish(false, "could not subscribe");
            });
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt g, BluetoothGattDescriptor d, int status) {
            main.post(() -> {
                writing = false;
                if (status != BluetoothGatt.GATT_SUCCESS) { finish(false, "subscribe failed"); return; }
                listener.onState("authenticating"); // gateway sends "hello" once we're subscribed
                pump();
            });
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt g, BluetoothGattCharacteristic c, int status) {
            main.post(() -> {
                writing = false;
                if (status != BluetoothGatt.GATT_SUCCESS) { finish(false, "write failed (" + status + ")"); return; }
                pump();
            });
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt g, BluetoothGattCharacteristic c) {
            final byte[] value = c.getValue() == null ? null : c.getValue().clone();
            main.post(() -> {
                for (JSONObject m : decoder.push(value)) handle(m);
            });
        }
    };

    private void send(JSONObject msg) {
        writeQueue.addAll(BleProtocol.encode(msg, mtu));
        pump();
    }

    private void pump() {
        if (writing || finished || gatt == null || rx == null || writeQueue.isEmpty()) return;
        rx.setValue(writeQueue.poll());
        writing = true;
        if (!gatt.writeCharacteristic(rx)) finish(false, "write rejected by stack");
    }

    private void handle(JSONObject m) {
        String type = m.optString("message");
        try {
            switch (type) {
                case "hello": {
                    gatewayNonce = m.optString("gatewayNonce");
                    gatewayId = m.optString("gatewayId");
                    clientNonce = EfPairing.newNonce();
                    JSONObject auth = new JSONObject();
                    auth.put("type", "auth");
                    auth.put("deviceId", deviceId);
                    auth.put("epoch", log.epoch());
                    auth.put("role", role);
                    auth.put("scout", scout);
                    auth.put("app", "eagleforce-scouting-android");
                    auth.put("clientNonce", clientNonce);
                    auth.put("proof", EfPairing.clientProof(pairingKey, gatewayNonce, clientNonce, deviceId));
                    send(auth);
                    break;
                }
                case "auth_ok": {
                    String expected = EfPairing.serverProof(pairingKey, gatewayNonce, clientNonce, deviceId);
                    if (!EfPairing.proofEquals(expected, m.optString("proof"))) { finish(false, "gateway failed to prove the pairing secret"); return; }
                    // The gateway's position is authoritative for this epoch (it may be behind us if
                    // its data was reset): adopt it so we resend whatever it doesn't have.
                    if (command != null) { listener.onState("sending command"); send(command); break; }
                    log.adoptGatewayAck(m.optLong("ackSeq", -1));
                    listener.onState("syncing");
                    sendNextBatch();
                    break;
                }
                case "ack": {
                    log.setAckSeq(m.optLong("ackSeq", -1));
                    if (log.ackSeq() <= ackBeforeBatch) { finish(false, "gateway made no progress"); return; }
                    sendNextBatch();
                    break;
                }
                case "match_started":
                case "match_stopped":
                    if (command != null) finish(true, type, gatewayId);
                    break;
                case "error": {
                    String code = m.optString("code");
                    if (command != null && !"AUTH_FAILED".equals(code)) { finish(false, code + (m.optString("detail").isEmpty() ? "" : ": " + m.optString("detail"))); break; }
                    if ("AUTH_FAILED".equals(code)) finish(false, "wrong pairing secret");
                    else Timber.w("gateway error %s: %s", code, m.optString("detail"));
                    break;
                }
                case "revoked":
                    finish(false, "pairing secret was changed on the stands");
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            finish(false, "protocol error");
        }
    }

    private void sendNextBatch() throws JSONException {
        List<JSONObject> batch = log.unacked(BATCH);
        if (batch.isEmpty()) { finish(true, "synced", gatewayId); return; }
        JSONArray arr = new JSONArray();
        for (JSONObject e : batch) arr.put(e);
        ackBeforeBatch = log.ackSeq();
        JSONObject msg = new JSONObject();
        msg.put("type", "events");
        msg.put("events", arr);
        send(msg);
    }

    private void finish(boolean ok, String message) { finish(ok, message, gatewayId); }

    private void finish(boolean ok, String message, String gw) {
        if (finished) return;
        finished = true;
        main.removeCallbacksAndMessages(null);
        try {
            if (gatt != null) { gatt.disconnect(); gatt.close(); }
        } catch (Exception ignored) {
            // stack already tore it down
        }
        listener.onFinished(ok, message, gw);
    }
}
