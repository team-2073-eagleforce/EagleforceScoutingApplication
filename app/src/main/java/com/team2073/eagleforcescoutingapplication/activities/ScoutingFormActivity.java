package com.team2073.eagleforcescoutingapplication.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;
import com.team2073.eagleforcescoutingapplication.framework.view.ScoutingFormView;

public class ScoutingFormActivity extends BaseActivity implements ScoutingFormView {

    private ScoutingFormPresenter scoutingFormPresenter;

    String[] externalStoragePermission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    String[] bluetoothPermission = {Manifest.permission.BLUETOOTH};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        scoutingFormPresenter.makeDrawer(toolbar);

        scoutingFormPresenter.clearPreferences();
        scoutingFormPresenter.advanceOnSubmit();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, externalStoragePermission, 23);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, bluetoothPermission, 23);
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_scouting_form;
    }

    @Override
    protected void initEvent() {
        scoutingFormPresenter.createTabs();
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void bindView() {
        scoutingFormPresenter = new ScoutingFormPresenter(this);
        scoutingFormPresenter.bindView(this);
    }


    /**
     * gets called after bluetooth intent activity is started. Clears preferences for next activity and starts a new form
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        scoutingFormPresenter.clearPreferences();
        startActivity(new Intent(this, ScoutingFormActivity.class));
        finish();
    }
}