package com.team2073.eagleforcescoutingapplication.framework.manager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Managers data with the SharedPreferences (short-term data storage).
 */
public class PrefsDataManager {

    private static PrefsDataManager INSTANCE;
    private SharedPreferences sharedPreferences;
    private final android.content.Context appContext;

    public static PrefsDataManager getInstance(Activity activity){
        if(INSTANCE == null){
            INSTANCE = new PrefsDataManager(activity);
        }
        return INSTANCE;
    }

    public PrefsDataManager(Activity activity){
        appContext = activity.getApplicationContext();
        sharedPreferences = activity.getApplicationContext()
                .getSharedPreferences("scouting_prefs", Context.MODE_PRIVATE);

        // One-time migration from old activity-scoped prefs
        if (!sharedPreferences.getBoolean("_migrated", false)) {
            try {
                SharedPreferences oldPrefs = activity.getPreferences(Context.MODE_PRIVATE);
                String[] keysToMigrate = {"name", "position", "field_side", "comp_code",
                        "teamNumber", "matchNumber"};
                SharedPreferences.Editor editor = sharedPreferences.edit();
                for (String key : keysToMigrate) {
                    String val = oldPrefs.getString(key, null);
                    if (val != null && sharedPreferences.getString(key, "0").equals("0")) {
                        editor.putString(key, val);
                    }
                }
                editor.putBoolean("_migrated", true);
                editor.commit();
            } catch (Exception ignored) {
                // If old prefs aren't accessible, skip migration
            }
        }
    }

    public void writeToPreferences(String key, String value){
        String previous = sharedPreferences.getString(key, null);
        sharedPreferences.edit().putString(key, value).commit();
        // Offline BLE scouting: every form value change becomes a delta event (no-op when BLE sync is off).
        com.team2073.eagleforcescoutingapplication.framework.ble.BleSyncManager.get(appContext)
                .onFieldWritten(key, previous, value, this::readFromPreferences);
    }

    /**
     *
     * @return value of string or an empty string if there's no assigned value
     */
    public String readFromPreferences(String key){
        return sharedPreferences.getString(key, "0");
    }

    public ArrayList<String> readFromPreferences(ArrayList<String> keys) {
        ArrayList<String> values = new ArrayList<>();
        for (String key: keys) {
            values.add(readFromPreferences(key));
        }
        return values;
    }

    public void clearPreferences(ArrayList<String> preferences){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (String preference: preferences) {
            editor.remove(preference);
        }
        editor.commit();
    }

    public void clearPreferences(){
        sharedPreferences.edit().clear().commit();
    }

}
