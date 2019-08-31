package com.team2073.eagleforcescoutingapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.View;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ReceiveFilePresenter;
import com.team2073.eagleforcescoutingapplication.framework.view.ReceiveFileView;

import java.net.Socket;

public class ReceiveFileActivity extends BaseActivity implements ReceiveFileView, View.OnClickListener {
    private ReceiveFilePresenter receiveFilePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_receive_file;
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void bindView() {
        receiveFilePresenter = new ReceiveFilePresenter(this);
        receiveFilePresenter.bindView(this);
    }

    @Override
    public void onReceiveFileSuccessful() {

    }

    @Override
    public void onReceiveFileFailed() {

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.receiveFileBluetoothButton: {
//                receiveFilePresenter.receiveFileBluetooth();
            }
            case R.id.receiveFileLocalButton: {

            }
        }
    }
}
