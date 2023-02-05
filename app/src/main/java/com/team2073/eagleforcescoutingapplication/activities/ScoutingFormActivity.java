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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        scoutingFormPresenter.makeDrawer(toolbar);
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
}