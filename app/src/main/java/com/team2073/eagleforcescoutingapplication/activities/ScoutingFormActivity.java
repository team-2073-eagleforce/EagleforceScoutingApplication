package com.team2073.eagleforcescoutingapplication.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.SectionsPagerAdapter;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;
import com.team2073.eagleforcescoutingapplication.framework.view.ScoutingFormView;

public class ScoutingFormActivity extends BaseActivity implements ScoutingFormView {

    private ScoutingFormPresenter scoutingFormPresenter;

    String[] externalStoragePermission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    String[] bluetoothPermission = {Manifest.permission.BLUETOOTH};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scoutingFormPresenter.makeDrawer();

        scoutingFormPresenter.clearPreferences();

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
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void bindView() {
        scoutingFormPresenter = new ScoutingFormPresenter(this);
        scoutingFormPresenter.bindView(this);
    }

    @Override
    public void onSubmitSuccessful() {
        Toast.makeText(this, "Failed to Submit", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubmitFailed(String errorMessage) {
        Toast.makeText(this, "Submit Success", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ScoutingFormActivity.class));
    }

    /**
     * gets called after bluetooth intent activity is started
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        scoutingFormPresenter.clearPreferences();
        startActivity(new Intent(this, ScoutingFormActivity.class));
    }
}