package org.team2073.platformkit;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Pairing secrets for offline tablets (platform-kit/auth/SPEC.md §4). Java 8 / Android API 25 safe,
 * no dependencies. Byte-identical to libs/node and libs/python (checked against auth/vectors).
 */
public final class EfPairing {
    public static final String ALPHABET = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
    private static final byte[] KDF_LABEL = "ef-pairing-v1".getBytes(StandardCharsets.UTF_8);
    private static final SecureRandom RNG = new SecureRandom();

    private EfPairing() {}

    /** Accepts short ("K7QM-4XPA"), long ("ef1-....") or a QR JSON payload. Throws IllegalArgumentException. */
    public static String normalize(String input) {
        if (input == null) throw new IllegalArgumentException("pairing secret required");
        String s = input.trim();
        if (s.startsWith("{")) s = extractQrSecret(s);
        s = s.toUpperCase(Locale.ROOT).replace(" ", "").replace("-", "").replace("\t", "").replace("\n", "");
        if (s.startsWith("EF1")) s = s.substring(3);
        if (s.length() < 6) throw new IllegalArgumentException("pairing secret too short");
        // Generated secrets use the unambiguous ALPHABET; custom typed secrets may use any A-Z / 0-9.
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!((c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9'))) {
                throw new IllegalArgumentException("pairing secret may only contain letters, digits, spaces and dashes");
            }
        }
        return s;
    }

    /** Minimal extraction of "s" from {"v":1,"t":"ef-pair","s":"...","n":"..."} without a JSON dependency. */
    static String extractQrSecret(String json) {
        if (!json.replace(" ", "").contains("\"t\":\"ef-pair\"")) throw new IllegalArgumentException("not an ef-pair QR payload");
        int k = json.indexOf("\"s\"");
        if (k < 0) throw new IllegalArgumentException("QR payload missing secret");
        int colon = json.indexOf(':', k);
        int q1 = json.indexOf('"', colon + 1);
        int q2 = json.indexOf('"', q1 + 1);
        if (colon < 0 || q1 < 0 || q2 < 0) throw new IllegalArgumentException("bad QR payload");
        return json.substring(q1 + 1, q2);
    }

    public static byte[] deriveKey(String input) {
        return hmac(KDF_LABEL, normalize(input).getBytes(StandardCharsets.UTF_8));
    }

    public static String clientProof(byte[] key, String gatewayNonce, String clientNonce, String deviceId) {
        return proof(key, "client", gatewayNonce, clientNonce, deviceId);
    }

    public static String serverProof(byte[] key, String gatewayNonce, String clientNonce, String deviceId) {
        return proof(key, "server", gatewayNonce, clientNonce, deviceId);
    }

    /** Constant-time hex comparison, for verifying the gateway's proof. */
    public static boolean proofEquals(String a, String b) {
        if (a == null || b == null) return false;
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.US_ASCII), b.getBytes(StandardCharsets.US_ASCII));
    }

    public static String newNonce() {
        byte[] b = new byte[16];
        RNG.nextBytes(b);
        return EfHmac.hex(b);
    }

    private static String proof(byte[] key, String role, String g, String c, String d) {
        String msg = "ef-pair-" + role + "\n" + g + "\n" + c + "\n" + d;
        return EfHmac.hex(hmac(key, msg.getBytes(StandardCharsets.UTF_8)));
    }

    static byte[] hmac(byte[] key, byte[] msg) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(msg);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
