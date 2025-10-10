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
import androidx.preference.SwitchPreferenceCompat;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.framework.manager.ConfigurationManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.FileManager;
import com.team2073.eagleforcescoutingapplication.framework.presenter.SettingsPresenter;
import com.team2073.eagleforcescoutingapplication.framework.view.SettingsView;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.widget.ImageView;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
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
    


    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        private final Activity mActivity;
        private final FileManager fileManager;
        private final SettingsPresenter settingsPresenter;
        private final ConfigurationManager configurationManager;

        public SettingsFragment(Activity activity) {
            this.mActivity = activity;
            fileManager = FileManager.getInstance(activity);
            settingsPresenter = new SettingsPresenter(activity);
            configurationManager = ConfigurationManager.getInstance(activity);
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            loadSavedPreferences();
            setupConfigurationPreferences();
        }

        private void loadSavedPreferences() {
            // Load saved name
            String savedName = settingsPresenter.readFromPreferences("name");
            if (!savedName.equals("0")) {
                EditTextPreference namePreference = findPreference("name");
                if (namePreference != null) {
                    namePreference.setText(savedName);
                }
            }

            // Load saved position
            String savedPosition = settingsPresenter.readFromPreferences("position");
            if (!savedPosition.equals("0")) {
                ListPreference positionPreference = findPreference("position");
                if (positionPreference != null) {
                    positionPreference.setValue(savedPosition);
                }
            }

            // Load saved field side
            String savedFieldSide = settingsPresenter.readFromPreferences("field_side");
            if (!savedFieldSide.equals("0")) {
                SwitchPreferenceCompat sidePreference = findPreference("field_side");
                if (sidePreference != null) {
                    sidePreference.setChecked(savedFieldSide.equals("1"));
                }
            }

            // Load saved competition code
            String savedCompCode = settingsPresenter.readFromPreferences("comp_code");
            if (!savedCompCode.equals("0")) {
                ListPreference compPreference = findPreference("comp_code");
                if (compPreference != null) {
                    compPreference.setValue(savedCompCode);
                }
            }
        }
        
        private void setupConfigurationPreferences() {
            Preference saveConfigPref = findPreference("save_config");
            if (saveConfigPref != null) {
                saveConfigPref.setOnPreferenceClickListener(preference -> {
                    if (configurationManager.hasLoadedConfiguration()) {
                        // Show update confirmation dialog
                        showUpdateConfigurationDialog();
                    } else {
                        configurationManager.saveConfiguration();
                    }
                    return true;
                });
            }
            
            Preference loadConfigPref = findPreference("load_config");
            if (loadConfigPref != null) {
                loadConfigPref.setOnPreferenceClickListener(preference -> {
                    String savedConfig = mActivity.getSharedPreferences("EagleforceScoutingApplication", mActivity.MODE_PRIVATE)
                        .getString("saved_config", "0");
                    if (!savedConfig.equals("0")) {
                        configurationManager.loadConfiguration();
                        loadSavedPreferences(); // Refresh UI after loading
                        
                        // Update field editor timestamp to trigger reload
                        mActivity.getSharedPreferences("field_editor", mActivity.MODE_PRIVATE)
                            .edit()
                            .putLong("config_timestamp", System.currentTimeMillis())
                            .apply();
                    } else {
                        android.widget.Toast.makeText(mActivity, "No saved configuration found", android.widget.Toast.LENGTH_SHORT).show();
                    }
                    return true;
                });
            }
            
            Preference exportConfigQRPref = findPreference("export_config_qr");
            if (exportConfigQRPref != null) {
                exportConfigQRPref.setOnPreferenceClickListener(preference -> {
                    showConfigQRDialog();
                    return true;
                });
            }
            
            Preference zoneAssociationPref = findPreference("zone_association");
            if (zoneAssociationPref != null) {
                zoneAssociationPref.setOnPreferenceClickListener(preference -> {
                    showZoneAssociationDialog();
                    return true;
                });
            }
            

        }
        
        private void showUpdateConfigurationDialog() {
            String configName = configurationManager.getCurrentLoadedConfigName();
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle("Update Configuration")
                   .setMessage("Update the loaded configuration '" + configName + "' with current settings?")
                   .setPositiveButton("Update", (dialog, which) -> {
                       configurationManager.saveConfiguration();
                   })
                   .setNeutralButton("Save as New", (dialog, which) -> {
                       configurationManager.setCurrentLoadedConfigName(null);
                       configurationManager.saveConfiguration();
                   })
                   .setNegativeButton("Cancel", null)
                   .show();
        }
        
        private void showConfigQRDialog() {
            try {
                Bitmap qrBitmap = configurationManager.createConfigQR();
                
                ImageView imageView = new ImageView(mActivity);
                imageView.setImageBitmap(qrBitmap);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setPadding(20, 20, 20, 20);
                
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle("App Configuration QR Code")
                       .setView(imageView)
                       .setPositiveButton("Close", null)
                       .show();
                       
            } catch (WriterException e) {
                Timber.e("Error creating config QR: %s", e.getMessage());
                android.widget.Toast.makeText(mActivity, "Error generating QR code", android.widget.Toast.LENGTH_SHORT).show();
            }
        }
        
        private void showZoneAssociationDialog() {
            android.content.SharedPreferences fieldPrefs = mActivity.getSharedPreferences("field_editor", mActivity.MODE_PRIVATE);
            
            String blueConfig = fieldPrefs.getString("auto_save_config_blue", "");
            String redConfig = fieldPrefs.getString("auto_save_config_red", "");
            
            int blueZones = countZones(blueConfig);
            int redZones = countZones(redConfig);
            
            String message = "Zone Association:\n\n" +
                           "🔵 Blue Alliance Side: " + blueZones + " zones\n" +
                           "🔴 Red Alliance Side: " + redZones + " zones\n\n" +
                           "Zones are automatically associated with the field side " +
                           "based on your position setting when creating them in the Field Editor.";
            
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle("Zone Association")
                   .setMessage(message)
                   .setPositiveButton("OK", null)
                   .show();
        }
        
        private int countZones(String config) {
            if (config.isEmpty()) return 0;
            try {
                return config.split("ZONE\\|").length - 1;
            } catch (Exception e) {
                return 0;
            }
        }
        

        


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            switch (s) {
                case "Schedule File":
                    findPreference(s).setSummary(fileManager.getScheduleFile().getAbsolutePath());
                    Timber.d("Scouting Schedule name: %s ", fileManager.getScheduleFile().getAbsolutePath());
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
                case "field_side":
                    try {
                        SwitchPreferenceCompat sidePreference = findPreference("field_side");
                        if (sidePreference.isChecked()) {
                            settingsPresenter.writeToPreferences("field_side", "1");
                        } else {
                            settingsPresenter.writeToPreferences("field_side", "0");
                        }
                    } catch (Exception e) {
                        Timber.d(e.toString());
                    }
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