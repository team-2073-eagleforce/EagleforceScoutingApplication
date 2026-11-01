package org.team2073.platformkit;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * EF1-HMAC-SHA256 request signing (platform-kit/auth/SPEC.md §3). Java 8 / Android API 25 safe.
 */
public final class EfHmac {
    public static final String ALGORITHM = "EF1-HMAC-SHA256";
    private static final SecureRandom RNG = new SecureRandom();
    private static final char[] HEX = "0123456789abcdef".toCharArray();

    private EfHmac() {}

    public static Map<String, String> sign(String keyId, String secret, String method, String target,
                                           byte[] body, long timestamp, String nonce) {
        if (body == null) body = new byte[0];
        if (nonce == null) nonce = randomNonce();
        int q = target.indexOf('?');
        String path = q < 0 ? target : target.substring(0, q);
        String query = q < 0 ? "" : target.substring(q + 1);
        String content = hex(sha256(body));
        String canon = canonicalString(keyId, timestamp, nonce, method, path, query, content);
        String sig = hex(EfPairing.hmac(secret.getBytes(StandardCharsets.UTF_8), canon.getBytes(StandardCharsets.UTF_8)));
        Map<String, String> h = new LinkedHashMap<>();
        h.put("X-EF-Key-Id", keyId);
        h.put("X-EF-Timestamp", Long.toString(timestamp));
        h.put("X-EF-Nonce", nonce);
        h.put("X-EF-Content-SHA256", content);
        h.put("Authorization", ALGORITHM + " Signature=" + sig);
        return h;
    }

    public static String canonicalString(String keyId, long ts, String nonce, String method, String path,
                                         String query, String contentSha256) {
        return ALGORITHM + "\n" + keyId + "\n" + ts + "\n" + nonce + "\n" + method.toUpperCase(Locale.ROOT) + "\n"
                + path + "\n" + canonicalQuery(query) + "\n" + contentSha256;
    }

    public static String canonicalQuery(String raw) {
        if (raw == null || raw.isEmpty()) return "";
        if (raw.startsWith("?")) raw = raw.substring(1);
        if (raw.isEmpty()) return "";
        List<String[]> pairs = new ArrayList<>();
        for (String part : raw.split("&")) {
            if (part.isEmpty()) continue;
            int i = part.indexOf('=');
            String n = decode(i < 0 ? part : part.substring(0, i));
            String v = decode(i < 0 ? "" : part.substring(i + 1));
            pairs.add(new String[]{n, v});
        }
        Collections.sort(pairs, (a, b) -> {
            int c = compareUtf8(a[0], b[0]);
            return c != 0 ? c : compareUtf8(a[1], b[1]);
        });
        StringBuilder sb = new StringBuilder();
        for (String[] p : pairs) {
            if (sb.length() > 0) sb.append('&');
            sb.append(rfc3986(p[0])).append('=').append(rfc3986(p[1]));
        }
        return sb.toString();
    }

    private static int compareUtf8(String a, String b) {
        byte[] x = a.getBytes(StandardCharsets.UTF_8), y = b.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < Math.min(x.length, y.length); i++) {
            int d = (x[i] & 0xff) - (y[i] & 0xff);
            if (d != 0) return d;
        }
        return x.length - y.length;
    }

    /** Percent-decode (with '+' as space); returns input unchanged if it is not valid UTF-8. */
    private static String decode(String s) {
        s = s.replace("+", "%20");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < s.length(); ) {
            char c = s.charAt(i);
            if (c == '%' && i + 2 < s.length() && isHex(s.charAt(i + 1)) && isHex(s.charAt(i + 2))) {
                out.write(Integer.parseInt(s.substring(i + 1, i + 3), 16));
                i += 3;
            } else {
                byte[] b = String.valueOf(c).getBytes(StandardCharsets.UTF_8);
                out.write(b, 0, b.length);
                i++;
            }
        }
        byte[] bytes = out.toByteArray();
        try {
            return StandardCharsets.UTF_8.newDecoder().decode(java.nio.ByteBuffer.wrap(bytes)).toString();
        } catch (java.nio.charset.CharacterCodingException e) {
            return s;
        }
    }

    private static boolean isHex(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }

    private static String rfc3986(String s) {
        StringBuilder sb = new StringBuilder();
        for (byte b : s.getBytes(StandardCharsets.UTF_8)) {
            int v = b & 0xff;
            char c = (char) v;
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '-' || c == '_' || c == '.' || c == '~') {
                sb.append(c);
            } else {
                sb.append('%').append(Character.toUpperCase(HEX[v >> 4])).append(Character.toUpperCase(HEX[v & 15]));
            }
        }
        return sb.toString();
    }

    public static byte[] sha256(byte[] data) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(data);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static String hex(byte[] b) {
        char[] out = new char[b.length * 2];
        for (int i = 0; i < b.length; i++) {
            out[i * 2] = HEX[(b[i] >> 4) & 15];
            out[i * 2 + 1] = HEX[b[i] & 15];
        }
        return new String(out);
    }

    public static String randomNonce() {
        byte[] b = new byte[16];
        RNG.nextBytes(b);
        // java.util.Base64 is API 26+ and minSdk is 25, so hex (32 chars, matches [A-Za-z0-9_-]{16,128}).
        return hex(b);
    }
}
