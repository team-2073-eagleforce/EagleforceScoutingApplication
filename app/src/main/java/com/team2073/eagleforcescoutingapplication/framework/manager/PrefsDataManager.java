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
    private Activity mActivity;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static PrefsDataManager getInstance(Activity activity){
        if(INSTANCE == null){
            INSTANCE = new PrefsDataManager(activity);
        }
        return INSTANCE;
    }

    public PrefsDataManager(Activity activity){
        mActivity = activity;
        sharedPreferences = mActivity.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void writeToPreferences(String key, String value){
        editor.putString(key, value);
        editor.commit();
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

    /**
     *
     * @return true if successful, false if failed
     */
    public Boolean commitToPreferences(){
        return editor.commit();
    }

    public void clearPreferences(ArrayList<String> preferences){
        for (String preference: preferences) {
            editor.remove(preference);
        }
        editor.commit();
    }

    public void clearPreferences(){
        editor.clear();
        editor.commit();
    }

}
