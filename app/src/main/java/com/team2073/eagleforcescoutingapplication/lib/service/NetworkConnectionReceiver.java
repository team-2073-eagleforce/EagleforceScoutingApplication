package com.team2073.eagleforcescoutingapplication.lib.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.team2073.eagleforcescoutingapplication.util.NetworkUtil;

public class NetworkConnectionReceiver extends BroadcastReceiver {

    public static ConnectionReceiverListener connectionReceiverListener;

    public NetworkConnectionReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        if (connectionReceiverListener != null) {
            connectionReceiverListener.onNetworkConnectionChanged(NetworkUtil.isNetworkConntected(context));
        }
    }

    public interface ConnectionReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}
