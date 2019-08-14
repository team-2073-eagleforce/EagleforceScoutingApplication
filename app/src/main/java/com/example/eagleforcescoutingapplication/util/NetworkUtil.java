package com.example.eagleforcescoutingapplication.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
    private static final int TYPE_NOT_CONNECTED = 0;
    private static final int TYPE_WIFI = 1;
    private static final int TYPE_MOBILE = 2;

    public static boolean isNetworkConntected(Context context) {
        int conn = getConnectivityType(context);
        return conn != TYPE_NOT_CONNECTED;
    }

    public static boolean isConnectedWifi(Context context) {
        return getConnectivityType(context) == TYPE_WIFI;
    }

    public static boolean isConnectedMobile(Context context) {
        return getConnectivityType(context) == TYPE_MOBILE;
    }

    private static int getConnectivityType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (null != activeNetwork && activeNetwork.isConnectedOrConnecting()) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return TYPE_WIFI;
            }
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return TYPE_MOBILE;
            }
        }
        return TYPE_NOT_CONNECTED;
    }
}
