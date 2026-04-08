package com.team2073.eagleforcescoutingapplication.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.framework.manager.FileManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.ReplayServerManager;
import com.team2073.eagleforcescoutingapplication.framework.presenter.SettingsPresenter;
import com.team2073.eagleforcescoutingapplication.framework.view.SettingsView;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

public class SettingsActivity extends BaseActivity implements SettingsView {

    private SettingsPresenter settingsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsPresenter.makeSettings(getSupportFragmentManager(), getSupportActionBar());
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        settingsPresenter.makeDrawer(toolbar);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initView() {
    }

    @Override
    protected void bindView() {
        settingsPresenter = new SettingsPresenter(this);
        settingsPresenter.bindView(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        private final Activity mActivity;
        private final FileManager fileManager;
        private final SettingsPresenter settingsPresenter;
        private ReplayServerManager replayServerManager;

        public SettingsFragment(Activity activity) {
            this.mActivity = activity;
            fileManager = FileManager.getInstance(activity);
            settingsPresenter = new SettingsPresenter(activity);
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            replayServerManager = ReplayServerManager.getInstance(mActivity);

            // Populate remote start manual fields with persisted values
            syncEditTextPreference("replay_server_ip", replayServerManager.getServerIp());
            syncEditTextPreference("replay_server_port", replayServerManager.getServerPort());
            syncEditTextPreference("replay_auth_key", replayServerManager.getAuthKey());
            syncEditTextPreference("replay_start_url", replayServerManager.getStartUrl());

            // Sync toggle
            SwitchPreferenceCompat toggle = findPreference("remote_start_enabled");
            if (toggle != null) {
                toggle.setChecked(replayServerManager.isRemoteStartEnabled());
            }

            // Scan Remote Start QR button
            Preference scanQrPref = findPreference("scan_remote_start_qr");
            if (scanQrPref != null) {
                scanQrPref.setOnPreferenceClickListener(pref -> {
                    IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                    integrator.setPrompt(getString(R.string.scan_remote_start_qr_prompt));
                    integrator.setBeepEnabled(false);
                    integrator.setOrientationLocked(false);
                    integrator.initiateScan();
                    return true;
                });
            }
        }

        private void syncEditTextPreference(String key, String value) {
            EditTextPreference pref = findPreference(key);
            if (pref != null && !value.isEmpty()) {
                pref.setText(value);
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            IntentResult qrResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (qrResult != null) {
                if (qrResult.getContents() != null) {
                    handleRemoteStartQrResult(qrResult.getContents());
                }
                return;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

        private void handleRemoteStartQrResult(String content) {
            try {
                JSONObject json = new JSONObject(content);
                replayServerManager.saveRemoteStartQrData(json);
                // Refresh the displayed values
                syncEditTextPreference("replay_server_ip", replayServerManager.getServerIp());
                syncEditTextPreference("replay_server_port", replayServerManager.getServerPort());
                syncEditTextPreference("replay_auth_key", replayServerManager.getAuthKey());
                syncEditTextPreference("replay_start_url", replayServerManager.getStartUrl());
                Toast.makeText(mActivity,
                        getString(R.string.remote_start_qr_saved), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Toast.makeText(mActivity,
                        getString(R.string.remote_start_qr_parse_error), Toast.LENGTH_LONG).show();
                Timber.e(e, "Failed to parse remote start QR JSON");
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (s == null) return;
            switch (s) {
                case "Schedule File":
                    Preference schedPref = findPreference(s);
                    if (schedPref != null && fileManager.getScheduleFile() != null) {
                        schedPref.setSummary(fileManager.getScheduleFile().getAbsolutePath());
                        Timber.d("Scouting Schedule name: %s", fileManager.getScheduleFile().getAbsolutePath());
                    }
                    break;
                case "name":
                    EditTextPreference mName = findPreference("name");
                    if (mName != null) {
                        settingsPresenter.writeToPreferences("name", mName.getText());
                    }
                    break;
                case "position":
                    ListPreference positionPreference = findPreference("position");
                    if (positionPreference != null) {
                        settingsPresenter.writeToPreferences("position", positionPreference.getValue());
                    }
                    break;
                case "field_side":
                    try {
                        SwitchPreferenceCompat sidePreference = findPreference("field_side");
                        if (sidePreference != null) {
                            settingsPresenter.writeToPreferences("field_side",
                                    sidePreference.isChecked() ? "1" : "0");
                        }
                    } catch (Exception e) {
                        Timber.d(e.toString());
                    }
                    break;
                case "comp_code":
                    ListPreference compPreference = findPreference("comp_code");
                    if (compPreference != null) {
                        settingsPresenter.writeToPreferences("comp_code", compPreference.getValue());
                    }
                    break;
                case "remote_start_enabled":
                    SwitchPreferenceCompat remoteToggle = findPreference("remote_start_enabled");
                    if (remoteToggle != null) {
                        replayServerManager.setRemoteStartEnabled(remoteToggle.isChecked());
                    }
                    break;
                case "replay_server_ip":
                    EditTextPreference ipPref = findPreference("replay_server_ip");
                    if (ipPref != null && ipPref.getText() != null) {
                        replayServerManager.setServerIp(ipPref.getText());
                    }
                    break;
                case "replay_server_port":
                    EditTextPreference portPref = findPreference("replay_server_port");
                    if (portPref != null && portPref.getText() != null) {
                        replayServerManager.setServerPort(portPref.getText());
                    }
                    break;
                case "replay_auth_key":
                    EditTextPreference authPref = findPreference("replay_auth_key");
                    if (authPref != null && authPref.getText() != null) {
                        replayServerManager.setAuthKey(authPref.getText());
                    }
                    break;
                case "replay_start_url":
                    EditTextPreference urlPref = findPreference("replay_start_url");
                    if (urlPref != null && urlPref.getText() != null) {
                        replayServerManager.setStartUrl(urlPref.getText());
                    }
                    break;
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }
    }
}