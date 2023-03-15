package com.team2073.eagleforcescoutingapplication.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.framework.manager.FileManager;
import com.team2073.eagleforcescoutingapplication.framework.presenter.SettingsPresenter;
import com.team2073.eagleforcescoutingapplication.framework.view.SettingsView;

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

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        private final Activity mActivity;
        private final FileManager fileManager;
        private final SettingsPresenter settingsPresenter;

        public SettingsFragment(Activity activity) {
            this.mActivity = activity;
            fileManager = FileManager.getInstance(activity);
            settingsPresenter = new SettingsPresenter(activity);
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            switch (s) {
                case "Schedule File":
                    findPreference(s).setSummary(fileManager.getScheduleFile().getAbsolutePath());
                    break;
                case "name":
                    Preference namePreference = findPreference("name");
                    EditTextPreference mName = (EditTextPreference) namePreference;
                    settingsPresenter.writeToPreferences("name", ((EditTextPreference) namePreference).getText());
                    break;
                case "position":
                    ListPreference positionPreference = findPreference("position");
                    String positionPreferenceValue = positionPreference.getValue();
                    settingsPresenter.writeToPreferences("position", positionPreferenceValue);
                    break;
                case "comp_code":
                    ListPreference compPreference = findPreference("comp_code");
                    String compPreferenceValue = compPreference.getValue();
                    settingsPresenter.writeToPreferences("comp_code", compPreferenceValue);

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