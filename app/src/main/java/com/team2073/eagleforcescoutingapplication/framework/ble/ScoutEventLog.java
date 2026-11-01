package com.team2073.eagleforcescoutingapplication.framework.ble;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;
import org.team2073.platformkit.EfEnvelope;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * The tablet's durable, append-only scouting event log (works fully offline).
 *
 * Every value change (scouting.delta.v1) and every Submit (scouting.commit.v1) is appended as a
 * platform-kit envelope with a monotonic seq. The stands gateway acks the highest contiguous seq it
 * has persisted; anything above the ack is resent on the next sync. Nothing is ever deleted except by
 * {@link #pruneAcked()} after it has been acknowledged.
 *
 * `epoch` is minted when the log is created, so a reinstall (seq restarting at 0) never collides with
 * old acks on the gateway.
 */
public final class ScoutEventLog {
    private static final String PREFS = "ble_event_log";
    private static final String FILE = "scout_events.jsonl";
    private static ScoutEventLog INSTANCE;

    private final SharedPreferences prefs;
    private final File file;

    public static synchronized ScoutEventLog get(Context ctx) {
        if (INSTANCE == null) INSTANCE = new ScoutEventLog(ctx.getApplicationContext());
        return INSTANCE;
    }

    private ScoutEventLog(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        file = new File(ctx.getFilesDir(), FILE);
        if (prefs.getString("epoch", null) == null) {
            prefs.edit().putString("epoch", EfEnvelope.ulid()).putLong("nextSeq", 0).putLong("ackSeq", -1).commit();
        }
    }

    public String epoch() { return prefs.getString("epoch", ""); }
    public synchronized long ackSeq() { return prefs.getLong("ackSeq", -1); }
    public synchronized long nextSeq() { return prefs.getLong("nextSeq", 0); }
    public synchronized long pending() { return Math.max(0, nextSeq() - ackSeq() - 1); }

    /** Append an envelope. Returns it, or null if it could not be written. */
    public synchronized JSONObject append(String deviceId, String type, JSONObject payload) {
        long seq = nextSeq();
        try {
            JSONObject env = new JSONObject();
            env.put("id", EfEnvelope.ulid());
            env.put("type", type);
            env.put("source", "tablet:" + deviceId);
            env.put("seq", seq);
            env.put("ts_src", System.currentTimeMillis());
            env.put("payload", payload);
            try (FileOutputStream out = new FileOutputStream(file, true)) {
                out.write((env.toString() + "\n").getBytes(StandardCharsets.UTF_8));
                out.getFD().sync();
            }
            prefs.edit().putLong("nextSeq", seq + 1).commit();
            return env;
        } catch (IOException | JSONException e) {
            Timber.e(e, "ScoutEventLog append failed");
            return null;
        }
    }

    /** Unacked envelopes in seq order, at most `limit`. */
    public synchronized List<JSONObject> unacked(int limit) {
        long ack = ackSeq();
        List<JSONObject> out = new ArrayList<>();
        if (!file.exists()) return out;
        try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = r.readLine()) != null && out.size() < limit) {
                if (line.isEmpty()) continue;
                try {
                    JSONObject env = new JSONObject(line);
                    if (env.optLong("seq", -1) > ack) out.add(env);
                } catch (JSONException torn) {
                    // half-written last line after a crash: skip
                }
            }
        } catch (IOException e) {
            Timber.e(e, "ScoutEventLog read failed");
        }
        return out;
    }

    public synchronized void setAckSeq(long ack) {
        if (ack > ackSeq() && ack < nextSeq()) prefs.edit().putLong("ackSeq", ack).commit();
    }

    /**
     * Take the gateway's position as truth for this epoch, even if it is BEHIND ours (its data was reset):
     * we then resend from there. Events already pruned locally can't be resent; that is logged.
     */
    public synchronized void adoptGatewayAck(long gatewayAck) {
        if (gatewayAck >= nextSeq()) return; // gateway can't have more than we produced; ignore
        if (gatewayAck < ackSeq()) {
            Timber.w("gateway is behind (gateway ack %d, local ack %d); resending what is still on disk", gatewayAck, ackSeq());
        }
        prefs.edit().putLong("ackSeq", Math.max(-1, gatewayAck)).commit();
    }

    /** Rewrite the file keeping only unacked events (call occasionally; acked data lives on the stands). */
    public synchronized void pruneAcked() {
        List<JSONObject> keep = unacked(Integer.MAX_VALUE);
        File tmp = new File(file.getParentFile(), FILE + ".tmp");
        try (FileOutputStream out = new FileOutputStream(tmp)) {
            for (JSONObject e : keep) out.write((e.toString() + "\n").getBytes(StandardCharsets.UTF_8));
            out.getFD().sync();
        } catch (IOException e) {
            Timber.e(e, "prune failed");
            return;
        }
        if (!tmp.renameTo(file)) Timber.e("prune rename failed");
    }
}
