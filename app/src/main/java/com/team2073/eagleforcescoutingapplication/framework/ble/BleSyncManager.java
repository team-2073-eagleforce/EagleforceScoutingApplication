package com.team2073.eagleforcescoutingapplication.framework.ble;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;

import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;
import org.team2073.platformkit.EfEnvelope;
import org.team2073.platformkit.EfPairing;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import timber.log.Timber;

/**
 * Offline Bluetooth sync with the stands machine (replay-system ble-gateway).
 *
 *  - {@link #onFieldWritten} turns every form value change into a scouting.delta.v1 (generic: no
 *    per-season code; the key is whatever the form writes to PrefsDataManager).
 *  - {@link #recordCommit} stores the exact QR JSON as a scouting.commit.v1 when the scout submits.
 *  - {@link #syncNow} scans for the gateway, pushes everything unacked, and disconnects.
 *
 * QR codes keep working exactly as before; this is an additional, optional path. Nothing here ever
 * blocks the form: if the gateway isn't in range, events stay in the local log until the next sync.
 */
public final class BleSyncManager {
    public static final String PREFS = "ble_sync";
    public static final String KEY_ENABLED = "ble_sync_enabled";
    public static final String KEY_SECRET = "ble_pairing_secret";
    public static final String KEY_DEVICE_ID = "ble_device_id";

    /** Anchor/settings keys that describe the match rather than being scouted values. */
    private static final Set<String> NOT_DELTAS = new HashSet<>();
    static {
        Collections.addAll(NOT_DELTAS, "teamNumber", "matchNumber", "comp_code", "name", "quantifier",
                "position", "field_side", "_migrated");
    }

    public interface StatusListener { void onStatus(Status s); }

    public static final class Status {
        public final String state; public final long pending; public final long lastSyncAt; public final String lastMessage; public final String gatewayId;
        Status(String state, long pending, long lastSyncAt, String lastMessage, String gatewayId) {
            this.state = state; this.pending = pending; this.lastSyncAt = lastSyncAt; this.lastMessage = lastMessage; this.gatewayId = gatewayId;
        }
        public String summary() {
            String when = lastSyncAt == 0 ? "never synced" : "last sync " + ((System.currentTimeMillis() - lastSyncAt) / 1000) + "s ago";
            return String.format(Locale.US, "%s · %d waiting · %s%s", state, pending, when,
                    lastMessage == null || lastMessage.isEmpty() ? "" : " · " + lastMessage);
        }
    }

    private static BleSyncManager INSTANCE;
    private static final long SCAN_TIMEOUT_MS = 8000;
    private static final long AUTO_SYNC_MS = 60_000;

    private final Context ctx;
    private final SharedPreferences prefs;
    private final ScoutEventLog log;
    private final Handler main = new Handler(Looper.getMainLooper());
    private final CopyOnWriteArrayList<StatusListener> listeners = new CopyOnWriteArrayList<>();
    private volatile boolean busy = false;
    private volatile String state = "idle";
    private volatile String lastMessage = "";
    private volatile String gatewayId = "";
    private boolean autoSyncScheduled = false;

    public static synchronized BleSyncManager get(Context ctx) {
        if (INSTANCE == null) INSTANCE = new BleSyncManager(ctx.getApplicationContext());
        return INSTANCE;
    }

    private BleSyncManager(Context ctx) {
        this.ctx = ctx;
        this.prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        this.log = ScoutEventLog.get(ctx);
        if (prefs.getString(KEY_DEVICE_ID, null) == null) {
            prefs.edit().putString(KEY_DEVICE_ID, "tab-" + EfEnvelope.ulid().substring(20).toLowerCase(Locale.US)).apply();
        }
    }

    // ── settings ────────────────────────────────────────────────────────────
    public boolean isEnabled() { return prefs.getBoolean(KEY_ENABLED, false); }
    public void setEnabled(boolean on) { prefs.edit().putBoolean(KEY_ENABLED, on).apply(); if (on) startAutoSync(); publish(); }
    public String deviceId() { return prefs.getString(KEY_DEVICE_ID, "tab-unknown"); }
    public void setDeviceId(String id) {
        String clean = id == null ? "" : id.trim().replaceAll("[^A-Za-z0-9_.-]", "-");
        if (!clean.isEmpty() && clean.length() <= 64) prefs.edit().putString(KEY_DEVICE_ID, clean).apply();
    }
    public boolean hasSecret() { return !prefs.getString(KEY_SECRET, "").isEmpty(); }

    /** Accepts a scanned QR payload, a short code, a long string, or a typed passphrase. Throws on invalid input. */
    public void setPairingSecret(String anyFormat) {
        EfPairing.normalize(anyFormat); // validate
        prefs.edit().putString(KEY_SECRET, anyFormat.trim()).apply();
        publish();
    }

    // ── recording (called from the form; never blocks, never throws) ────────
    public void onFieldWritten(String key, String oldValue, String newValue, AnchorReader anchors) {
        if (!isEnabled() || key == null || NOT_DELTAS.contains(key) || key.startsWith("_")) return;
        if (newValue == null || newValue.equals(oldValue)) return;
        try {
            JSONObject p = matchPayload(anchors);
            if (p == null) return; // no team/match yet: nothing to attribute the value to
            p.put("field", key);
            p.put("op", "set");
            p.put("value", jsonValue(newValue));
            log.append(deviceId(), "scouting.delta.v1", p);
        } catch (Exception e) {
            Timber.w(e, "delta not recorded");
        }
    }

    /** The scout pressed Submit: store the exact QR JSON, then try to sync. */
    public void recordCommit(JSONObject qrJson, AnchorReader anchors) {
        if (!isEnabled()) return;
        try {
            JSONObject p = matchPayload(anchors);
            if (p == null) return;
            p.put("snapshot", qrJson);
            log.append(deviceId(), "scouting.commit.v1", p);
        } catch (Exception e) {
            Timber.w(e, "commit not recorded");
        }
        syncNow();
    }

    /** Reads the current anchors from the form's preferences. */
    public interface AnchorReader { String read(String key); }

    private JSONObject matchPayload(AnchorReader a) throws JSONException {
        Integer team = parseInt(a.read("teamNumber"));
        Integer match = parseInt(a.read("matchNumber"));
        String event = a.read("comp_code");
        if (team == null || team <= 0 || match == null || event == null) return null;
        event = event.replaceAll("[^A-Za-z0-9_-]", "");
        if (event.isEmpty() || event.length() > 20) return null;
        JSONObject p = new JSONObject();
        p.put("event", event);
        String q = a.read("quantifier");
        p.put("match_type", q == null || q.equals("0") || q.isEmpty() ? "quals" : q);
        p.put("match_number", match);
        p.put("team", team);
        String scout = a.read("name");
        if (scout != null && !scout.isEmpty() && !scout.equals("0")) p.put("scout", scout.length() > 64 ? scout.substring(0, 64) : scout);
        return p;
    }

    private static Integer parseInt(String s) {
        try { return s == null ? null : Integer.parseInt(s.trim()); } catch (NumberFormatException e) { return null; }
    }

    private static Object jsonValue(String s) {
        try {
            if (s.matches("-?\\d{1,15}")) return Long.parseLong(s);
            if (s.matches("-?\\d+\\.\\d+")) return Double.parseDouble(s);
        } catch (NumberFormatException ignored) { /* fall through */ }
        return s;
    }

    // ── sync ────────────────────────────────────────────────────────────────
    public void addListener(StatusListener l) { listeners.add(l); l.onStatus(status()); }
    public void removeListener(StatusListener l) { listeners.remove(l); }

    public Status status() {
        return new Status(isEnabled() ? state : "off", log.pending(), prefs.getLong("last_sync_at", 0), lastMessage, gatewayId);
    }

    private void publish() { Status s = status(); main.post(() -> { for (StatusListener l : listeners) l.onStatus(s); }); }
    private void setState(String s, String msg) { state = s; if (msg != null) lastMessage = msg; publish(); }

    public static String[] requiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT};
        }
        return new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    }

    public boolean hasPermissions() {
        for (String p : requiredPermissions()) {
            if (ContextCompat.checkSelfPermission(ctx, p) != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

    /** Periodic background sync while the app is alive (connect → sync → disconnect each time). */
    public void startAutoSync() {
        if (autoSyncScheduled) return;
        autoSyncScheduled = true;
        main.postDelayed(new Runnable() {
            @Override public void run() {
                if (isEnabled() && log.pending() > 0) syncNow();
                main.postDelayed(this, AUTO_SYNC_MS);
            }
        }, AUTO_SYNC_MS);
    }

    @SuppressLint("MissingPermission")
    public void syncNow() {
        if (!isEnabled() || busy) return;
        if (!hasSecret()) { setState("not paired", "scan the stands pairing QR in Settings"); return; }
        if (!hasPermissions()) { setState("needs permission", "allow Bluetooth in Settings"); return; }
        BluetoothManager bm = (BluetoothManager) ctx.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = bm == null ? null : bm.getAdapter();
        if (adapter == null || !adapter.isEnabled()) { setState("bluetooth off", "turn Bluetooth on"); return; }
        BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
        if (scanner == null) { setState("bluetooth off", null); return; }

        final byte[] key;
        try { key = EfPairing.deriveKey(prefs.getString(KEY_SECRET, "")); }
        catch (IllegalArgumentException e) { setState("not paired", "pairing secret invalid"); return; }

        busy = true;
        setState("scanning", null);
        final boolean[] found = {false};
        final ScanCallback cb = new ScanCallback() {
            @Override public void onScanResult(int callbackType, ScanResult result) {
                if (found[0]) return;
                found[0] = true;
                try { scanner.stopScan(this); } catch (Exception ignored) { /* adapter went away */ }
                String scout = ctx.getSharedPreferences("scouting_prefs", Context.MODE_PRIVATE).getString("name", "");
                new BleSyncSession(ctx, result.getDevice(), key, deviceId(), scout, log, new BleSyncSession.Listener() {
                    @Override public void onState(String s) { setState(s, null); }
                    @Override public void onFinished(boolean ok, String message, String gw) {
                        busy = false;
                        if (gw != null && !gw.isEmpty()) gatewayId = gw;
                        if (ok) { prefs.edit().putLong("last_sync_at", System.currentTimeMillis()).apply(); maybePrune(); }
                        setState(ok ? "synced" : "offline", message);
                    }
                }).start();
            }
            @Override public void onScanFailed(int errorCode) { busy = false; setState("offline", "scan failed (" + errorCode + ")"); }
        };
        ScanFilter filter = new ScanFilter.Builder().setServiceUuid(new ParcelUuid(BleProtocol.SERVICE_UUID)).build();
        ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        scanner.startScan(Collections.singletonList(filter), settings, cb);
        main.postDelayed(() -> {
            if (!found[0]) {
                try { scanner.stopScan(cb); } catch (Exception ignored) { /* adapter went away */ }
                busy = false;
                setState("offline", "stands gateway not in range");
            }
        }, SCAN_TIMEOUT_MS);
    }

    private void maybePrune() {
        long count = prefs.getLong("syncs_since_prune", 0) + 1;
        if (count >= 20) { log.pruneAcked(); count = 0; }
        prefs.edit().putLong("syncs_since_prune", count).apply();
    }
}
