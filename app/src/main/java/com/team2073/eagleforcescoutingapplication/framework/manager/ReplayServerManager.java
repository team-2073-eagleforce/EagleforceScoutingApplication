package com.team2073.eagleforcescoutingapplication.framework.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.concurrent.Executors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import timber.log.Timber;

/**
 * Manages the replay server connection config (persisted via SharedPreferences),
 * CSV schedule download, and remote start POST.
 */
public class ReplayServerManager {

    private static final String PREFS_NAME = "replay_server_prefs";
    static final String KEY_SERVER_IP = "server_ip";
    static final String KEY_SERVER_PORT = "server_port";
    static final String KEY_COMP_CODE = "replay_comp_code";
    static final String KEY_YEAR = "replay_year";
    static final String KEY_AUTH_KEY = "replay_auth_key";
    static final String KEY_START_URL = "replay_start_url";
    static final String KEY_REMOTE_START_ENABLED = "remote_start_enabled";

    private static ReplayServerManager INSTANCE;

    private final SharedPreferences prefs;
    private final Context appContext;

    public interface ScheduleDownloadCallback {
        void onSuccess(File csvFile);
        void onError(int httpCode, String message);
        void onNetworkError(String message);
    }

    public interface RemoteStartCallback {
        void onSuccess();
        void onError(int httpCode, String message);
        void onNetworkError(String message);
    }

    public static synchronized ReplayServerManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ReplayServerManager(context.getApplicationContext());
        }
        return INSTANCE;
    }

    private ReplayServerManager(Context context) {
        appContext = context;
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ---- Config accessors ----

    public String getServerIp() { return prefs.getString(KEY_SERVER_IP, ""); }
    public String getServerPort() { return prefs.getString(KEY_SERVER_PORT, "3000"); }
    public String getCompCode() { return prefs.getString(KEY_COMP_CODE, ""); }
    public String getYear() { return prefs.getString(KEY_YEAR, ""); }
    public String getAuthKey() { return prefs.getString(KEY_AUTH_KEY, ""); }
    public String getStartUrl() { return prefs.getString(KEY_START_URL, ""); }
    public boolean isRemoteStartEnabled() { return prefs.getBoolean(KEY_REMOTE_START_ENABLED, false); }

    public void setServerIp(String ip) { prefs.edit().putString(KEY_SERVER_IP, ip).apply(); }
    public void setServerPort(String port) { prefs.edit().putString(KEY_SERVER_PORT, port).apply(); }
    public void setCompCode(String code) { prefs.edit().putString(KEY_COMP_CODE, code).apply(); }
    public void setYear(String year) { prefs.edit().putString(KEY_YEAR, year).apply(); }
    public void setAuthKey(String key) { prefs.edit().putString(KEY_AUTH_KEY, key).apply(); }
    public void setStartUrl(String url) { prefs.edit().putString(KEY_START_URL, url).apply(); }
    public void setRemoteStartEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_REMOTE_START_ENABLED, enabled).apply();
    }

    /**
     * Parses and persists data from a scanned Schedule QR JSON payload.
     * Expected keys: server_ip, port, comp_code, year
     */
    public void saveScheduleQrData(JSONObject json) throws JSONException {
        String serverIp = json.optString("server_ip", "");
        String port = json.optString("port", "3000");
        String compCode = json.isNull("comp_code") ? "" : json.optString("comp_code", "");
        String year = json.isNull("year") ? "" : String.valueOf(json.opt("year"));
        prefs.edit()
                .putString(KEY_SERVER_IP, serverIp)
                .putString(KEY_SERVER_PORT, port)
                .putString(KEY_COMP_CODE, compCode)
                .putString(KEY_YEAR, year)
                .apply();
    }

    /**
     * Parses and persists data from a scanned Remote Start QR JSON payload.
     * Expected keys: auth_key, start_url, server_ip, port
     */
    public void saveRemoteStartQrData(JSONObject json) throws JSONException {
        String authKey = json.optString("auth_key", "");
        String startUrl = json.optString("start_url", "");
        String serverIp = json.optString("server_ip", "");
        String port = json.optString("port", "3000");
        SharedPreferences.Editor editor = prefs.edit()
                .putString(KEY_AUTH_KEY, authKey)
                .putString(KEY_START_URL, startUrl);
        if (!serverIp.isEmpty()) editor.putString(KEY_SERVER_IP, serverIp);
        if (!port.isEmpty()) editor.putString(KEY_SERVER_PORT, port);
        editor.apply();
    }

    /**
     * Builds the schedule CSV download URL from stored config.
     * Returns null if any required field is missing.
     */
    public String buildScheduleUrl() {
        String ip = getServerIp();
        String port = getServerPort();
        String comp = getCompCode();
        String year = getYear();
        if (ip.isEmpty() || comp.isEmpty() || year.isEmpty()) return null;
        return "https://" + ip + ":" + port + "/schedule/" + year + comp + "_matches.csv";
    }

    /**
     * Downloads the schedule CSV from the replay server and saves it to internal storage.
     * Supports HTTPS with self-signed certificates (LAN use).
     * Calls back on the main thread.
     */
    public void downloadSchedule(ScheduleDownloadCallback callback) {
        String urlStr = buildScheduleUrl();
        if (urlStr == null) {
            callback.onError(-1, "Server config incomplete. Check IP, comp code, and year.");
            return;
        }
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Executors.newSingleThreadExecutor().execute(() -> {
            HttpURLConnection conn = null;
            try {
                conn = openConnection(urlStr);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(15000);
                conn.setRequestMethod("GET");
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    StringBuilder sb = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                    }
                    File scheduleDir = new File(appContext.getFilesDir(), "schedules");
                    if (!scheduleDir.exists()) scheduleDir.mkdirs();
                    String filename = getYear() + getCompCode() + "_matches.csv";
                    File csvFile = new File(scheduleDir, filename);
                    try (FileOutputStream fos = new FileOutputStream(csvFile)) {
                        fos.write(sb.toString().getBytes(StandardCharsets.UTF_8));
                    }
                    mainHandler.post(() -> callback.onSuccess(csvFile));
                } else {
                    final int code = responseCode;
                    mainHandler.post(() -> callback.onError(code, "HTTP " + code));
                }
            } catch (Exception e) {
                Timber.e(e, "Schedule download failed");
                mainHandler.post(() -> callback.onNetworkError(e.getMessage()));
            } finally {
                if (conn != null) conn.disconnect();
            }
        });
    }

    /**
     * POSTs to the start_url with the stored auth_key.
     * Fire-and-forget: caller may pass null for callback.
     * Calls back on the main thread.
     */
    public void fireRemoteStart(RemoteStartCallback callback) {
        String startUrl = getStartUrl();
        String authKey = getAuthKey();
        if (startUrl.isEmpty() || authKey.isEmpty()) {
            if (callback != null) {
                callback.onError(401, "No remote start config. Scan Remote Start QR first.");
            }
            return;
        }
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Executors.newSingleThreadExecutor().execute(() -> {
            HttpURLConnection conn = null;
            try {
                conn = openConnection(startUrl);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                JSONObject body = new JSONObject();
                body.put("auth_key", authKey);
                byte[] bodyBytes = body.toString().getBytes(StandardCharsets.UTF_8);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(bodyBytes);
                }
                int responseCode = conn.getResponseCode();
                final int code = responseCode;
                mainHandler.post(() -> {
                    if (callback == null) return;
                    if (code == 200) {
                        callback.onSuccess();
                    } else {
                        callback.onError(code, "HTTP " + code);
                    }
                });
            } catch (Exception e) {
                Timber.e(e, "Remote start request failed");
                mainHandler.post(() -> {
                    if (callback != null) callback.onNetworkError(e.getMessage());
                });
            } finally {
                if (conn != null) conn.disconnect();
            }
        });
    }

    /**
     * Opens an HttpURLConnection, using a trust-all SSL context for HTTPS
     * to support self-signed LAN certificates.
     */
    private HttpURLConnection openConnection(String urlStr)
            throws IOException, NoSuchAlgorithmException, KeyManagementException {
        URL url = new URL(urlStr);
        if (urlStr.startsWith("https://")) {
            TrustManager[] trustAll = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAll, new java.security.SecureRandom());
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier((hostname, session) -> true);
            return conn;
        } else {
            return (HttpURLConnection) url.openConnection();
        }
    }
}
