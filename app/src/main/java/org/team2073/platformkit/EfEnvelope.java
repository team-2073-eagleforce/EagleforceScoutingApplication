package org.team2073.platformkit;

import java.security.SecureRandom;
import java.util.regex.Pattern;

/**
 * Event envelope helpers (contracts: id = ULID, type = "domain.name.v1"). Java 8 / Android API 25 safe.
 * Envelope JSON itself is built by the caller (org.json on Android) to avoid a JSON dependency here.
 */
public final class EfEnvelope {
    private static final char[] CROCKFORD = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();
    private static final SecureRandom RNG = new SecureRandom();
    public static final Pattern ULID = Pattern.compile("^[0-9A-HJKMNP-TV-Z]{26}$");
    public static final Pattern TYPE = Pattern.compile("^[a-z0-9_]+(\\.[a-z0-9_]+)*\\.v\\d+$");

    private static long lastTs = -1;
    private static final byte[] lastRand = new byte[10];

    private EfEnvelope() {}

    /** Monotonic ULID (same-millisecond ids increment the random part, so they still sort). */
    public static synchronized String ulid(long nowMs) {
        if (nowMs <= lastTs) {
            nowMs = lastTs;
            for (int i = lastRand.length - 1; i >= 0; i--) {
                if ((lastRand[i] & 0xff) == 0xff) { lastRand[i] = 0; continue; }
                lastRand[i]++;
                break;
            }
        } else {
            RNG.nextBytes(lastRand);
            lastTs = nowMs;
        }
        char[] out = new char[26];
        long ts = nowMs;
        for (int i = 9; i >= 0; i--) { out[i] = CROCKFORD[(int) (ts % 32)]; ts /= 32; }
        // 80 random bits -> 16 chars
        int bitBuf = 0, bits = 0, o = 10;
        for (byte b : lastRand) {
            bitBuf = (bitBuf << 8) | (b & 0xff);
            bits += 8;
            while (bits >= 5) { bits -= 5; out[o++] = CROCKFORD[(bitBuf >> bits) & 31]; }
        }
        return new String(out);
    }

    public static String ulid() { return ulid(System.currentTimeMillis()); }

    public static boolean isUlid(String s) { return s != null && ULID.matcher(s).matches(); }

    public static boolean isType(String s) { return s != null && TYPE.matcher(s).matches(); }
}
