package com.team2073.eagleforcescoutingapplication.framework.manager;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

public class ConfigurationManager {
    
    private static ConfigurationManager INSTANCE;
    private final Activity mActivity;
    private final PrefsDataManager prefsDataManager;
    private String currentLoadedConfigName = null;
    
    public static ConfigurationManager getInstance(Activity activity) {
        if (INSTANCE == null) {
            INSTANCE = new ConfigurationManager(activity);
        }
        return INSTANCE;
    }
    
    private ConfigurationManager(Activity activity) {
        this.mActivity = activity;
        this.prefsDataManager = PrefsDataManager.getInstance(activity);
    }
    
    public void saveConfiguration() {
        JSONObject config = exportConfiguration();
        
        if (currentLoadedConfigName != null) {
            // Update existing configuration
            prefsDataManager.writeToPreferences("saved_config_" + currentLoadedConfigName, config.toString());
            prefsDataManager.writeToPreferences("saved_config", config.toString()); // Keep for compatibility
        } else {
            // Save as default configuration
            prefsDataManager.writeToPreferences("saved_config", config.toString());
        }
        
        // Count zones for user feedback
        int zoneCount = 0;
        try {
            if (config.has("field_config")) {
                String fieldConfig = config.getString("field_config");
                zoneCount = fieldConfig.split("ZONE\\|").length - 1;
            }
        } catch (JSONException e) {
            // Ignore
        }
        
        String message = currentLoadedConfigName != null ? 
            "Configuration '" + currentLoadedConfigName + "' updated with " + zoneCount + " zones!" :
            "Configuration saved with " + zoneCount + " zones!";
        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
    }
    
    public void loadConfiguration() {
        loadConfiguration(null);
    }
    
    public void loadConfiguration(String configName) {
        String configStr;
        if (configName != null) {
            configStr = prefsDataManager.readFromPreferences("saved_config_" + configName);
            if (configStr.equals("0")) {
                configStr = prefsDataManager.readFromPreferences("saved_config");
            }
        } else {
            configStr = prefsDataManager.readFromPreferences("saved_config");
        }
        
        if (!configStr.equals("0")) {
            try {
                JSONObject config = new JSONObject(configStr);
                importConfiguration(config);
                currentLoadedConfigName = configName;
                String message = configName != null ? 
                    "Configuration '" + configName + "' loaded!" :
                    "Configuration loaded!";
                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Toast.makeText(mActivity, "Failed to load configuration", Toast.LENGTH_SHORT).show();
                Timber.e("Error loading configuration: %s", e.getMessage());
            }
        } else {
            Toast.makeText(mActivity, "No saved configuration found", Toast.LENGTH_SHORT).show();
        }
    }
    
    public JSONObject exportConfiguration() {
        JSONObject config = new JSONObject();
        try {
            // App settings
            config.put("name", prefsDataManager.readFromPreferences("name"));
            config.put("position", prefsDataManager.readFromPreferences("position"));
            config.put("field_side", prefsDataManager.readFromPreferences("field_side"));
            config.put("comp_code", prefsDataManager.readFromPreferences("comp_code"));
            
            // Field editor configuration
            String fieldConfig = mActivity.getSharedPreferences("field_editor", mActivity.MODE_PRIVATE)
                .getString("auto_save_config", "");
            if (!fieldConfig.isEmpty()) {
                config.put("field_config", fieldConfig);
            }
            
            config.put("config_type", "app_settings");
        } catch (JSONException e) {
            Timber.e("Error exporting configuration: %s", e.getMessage());
        }
        return config;
    }
    
    public void importConfiguration(JSONObject config) {
        try {
            if (config.has("config_type") && config.getString("config_type").equals("app_settings")) {
                // Import app settings
                if (config.has("name")) {
                    prefsDataManager.writeToPreferences("name", config.getString("name"));
                }
                if (config.has("position")) {
                    prefsDataManager.writeToPreferences("position", config.getString("position"));
                }
                if (config.has("field_side")) {
                    prefsDataManager.writeToPreferences("field_side", config.getString("field_side"));
                }
                if (config.has("comp_code")) {
                    prefsDataManager.writeToPreferences("comp_code", config.getString("comp_code"));
                }
                
                // Import field configuration
                if (config.has("field_config")) {
                    mActivity.getSharedPreferences("field_editor", mActivity.MODE_PRIVATE)
                        .edit()
                        .putString("auto_save_config", config.getString("field_config"))
                        .putLong("config_timestamp", System.currentTimeMillis())
                        .apply();
                }
            }
        } catch (JSONException e) {
            Timber.e("Error importing configuration: %s", e.getMessage());
        }
    }
    
    public Bitmap createConfigQR() throws WriterException {
        JSONObject config = exportConfiguration();
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix = multiFormatWriter.encode(config.toString(), BarcodeFormat.QR_CODE, 400, 400);
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        return barcodeEncoder.createBitmap(bitMatrix);
    }
    
    public String getCurrentLoadedConfigName() {
        return currentLoadedConfigName;
    }
    
    public void setCurrentLoadedConfigName(String configName) {
        currentLoadedConfigName = configName;
    }
    
    public boolean hasLoadedConfiguration() {
        return currentLoadedConfigName != null;
    }
}