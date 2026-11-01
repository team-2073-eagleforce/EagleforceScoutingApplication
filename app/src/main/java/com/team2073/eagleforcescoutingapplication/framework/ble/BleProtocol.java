package com.team2073.eagleforcescoutingapplication.framework.ble;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * BLE protocol v2 shared with the stands machine's ble-gateway (replay-system/services/ble-gateway).
 * Messages are NDJSON in both directions, sliced into writes of at most (MTU - 3) bytes.
 */
public final class BleProtocol {
    public static final UUID SERVICE_UUID = UUID.fromString("6c457832-5cf1-4b3e-b113-77570a15e1aa");
    /** tablet → gateway (write) */
    public static final UUID RX_UUID = UUID.fromString("22a869a9-7e86-41cb-ba72-282a151c7d35");
    /** gateway → tablet (notify) */
    public static final UUID TX_UUID = UUID.fromString("6263090a-8d15-4c49-b5bf-3c3386baf37f");
    public static final UUID CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final int PROTO_VERSION = 2;
    public static final int REQUESTED_MTU = 247;

    private BleProtocol() {}

    /** Slice one message into BLE-sized frames, never splitting a UTF-8 character. */
    public static List<byte[]> encode(JSONObject msg, int mtu) {
        byte[] bytes = (msg.toString() + "\n").getBytes(StandardCharsets.UTF_8);
        int max = Math.max(20, mtu - 3);
        List<byte[]> out = new ArrayList<>();
        int start = 0;
        while (start < bytes.length) {
            int end = Math.min(start + max, bytes.length);
            while (end < bytes.length && end > start && (bytes[end] & 0xC0) == 0x80) end--;
            byte[] frame = new byte[end - start];
            System.arraycopy(bytes, start, frame, 0, frame.length);
            out.add(frame);
            start = end;
        }
        return out;
    }

    /** Reassembles NDJSON from notification fragments. */
    public static final class Decoder {
        private final ByteArrayOutputStream buf = new ByteArrayOutputStream();

        public List<JSONObject> push(byte[] chunk) {
            List<JSONObject> out = new ArrayList<>();
            if (chunk == null) return out;
            buf.write(chunk, 0, chunk.length);
            byte[] all = buf.toByteArray();
            int lineStart = 0;
            for (int i = 0; i < all.length; i++) {
                if (all[i] == '\n') {
                    String line = new String(all, lineStart, i - lineStart, StandardCharsets.UTF_8).trim();
                    lineStart = i + 1;
                    if (line.isEmpty()) continue;
                    try {
                        out.add(new JSONObject(line));
                    } catch (JSONException ignored) {
                        // malformed line from the gateway: drop it, keep the stream alive
                    }
                }
            }
            buf.reset();
            buf.write(all, lineStart, all.length - lineStart);
            if (buf.size() > 256 * 1024) buf.reset();
            return out;
        }
    }
}
