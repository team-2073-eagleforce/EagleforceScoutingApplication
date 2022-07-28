package com.team2073.eagleforcescoutingapplication.activities;

import android.app.Activity;
import android.content.Context;
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
import androidx.preference.PreferenceManager;

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

        private Activity activity;
        private FileManager fileManager;
        private SettingsPresenter settingsPresenter;

        public SettingsFragment(Activity activity) {
            this.activity = activity;
            fileManager = FileManager.getInstance(activity);
            settingsPresenter = new SettingsPresenter(activity);
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            if (findPreference("position") == null) {
                settingsPresenter.writeToPreferences("position", "red1");
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (s.equals("Schedule File")) {
                findPreference(s).setSummary(fileManager.getScheduleFile().getAbsolutePath());
            }
            if (s.equals("name")) {
                Preference name = findPreference("name");
                EditTextPreference mName = (EditTextPreference) name;
                settingsPresenter.writeToPreferences("name", ((EditTextPreference) name).getText());
            }
            if (s.equals("position")) {
                ListPreference listPreference = findPreference("position");
                CharSequence currText = listPreference.getEntry();
                String currValue = listPreference.getValue();
                settingsPresenter.writeToPreferences("position", currValue);
            }
            if (s.equals("comp_code")) {
                ListPreference listPreference = findPreference("comp_code");
                CharSequence currText = listPreference.getEntry();
                String currValue = listPreference.getValue();
                settingsPresenter.writeToPreferences("comp_code", currValue);
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