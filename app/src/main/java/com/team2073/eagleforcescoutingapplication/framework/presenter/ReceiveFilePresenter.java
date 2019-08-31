package com.team2073.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import com.team2073.eagleforcescoutingapplication.activities.ReceiveFileActivity;
import com.team2073.eagleforcescoutingapplication.framework.view.ReceiveFileView;

import java.io.IOException;
import java.io.InputStream;

public class ReceiveFilePresenter extends BasePresenter<ReceiveFileView> {
    private Activity mActivity;

    public ReceiveFilePresenter(Activity activity) {
        mActivity = activity;
    }

    public void receiveFileBluetooth(BluetoothSocket socket) throws IOException {
        InputStream socketInputStream =  socket.getInputStream();
        byte[] buffer = new byte[256];
        int bytes;

        // Keep looping to listen for received messages
        while (true) {
            try {
                bytes = socketInputStream.read(buffer);            //read bytes from input buffer
                String readMessage = new String(buffer, 0, bytes);
                // Send the obtained bytes to the UI Activity via handler
                Log.i("logging", readMessage + "");
            } catch (IOException e) {
                break;
            }
        }

    }

    public void receiveFileLocal() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        mActivity.startActivityForResult(intent, 1);
    }
}
