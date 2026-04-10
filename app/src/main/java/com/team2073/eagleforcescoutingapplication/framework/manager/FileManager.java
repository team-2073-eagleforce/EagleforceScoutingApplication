package com.team2073.eagleforcescoutingapplication.framework.manager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

/**
 * Manages which file is used to pull from for creating the match schedule.
 * Persists the schedule file path in SharedPreferences so it survives activity restarts.
 */
public class FileManager {

    private static final String PREFS_NAME = "file_manager_prefs";
    private static final String KEY_SCHEDULE_PATH = "schedule_file_path";

    public static FileManager INSTANCE;
    private final SharedPreferences prefs;

    public static FileManager getInstance(Activity activity){
        if(INSTANCE == null){
            INSTANCE = new FileManager(activity);
        }
        return INSTANCE;
    }

    public FileManager(Activity activity){
        prefs = activity.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setScheduleFile(File scheduleFile){
        if (scheduleFile == null) {
            prefs.edit().remove(KEY_SCHEDULE_PATH).apply();
        } else {
            prefs.edit().putString(KEY_SCHEDULE_PATH, scheduleFile.getAbsolutePath()).apply();
        }
    }

    public File getScheduleFile(){
        String path = prefs.getString(KEY_SCHEDULE_PATH, null);
        if (path == null) return null;
        File file = new File(path);
        return file.exists() ? file : null;
    }
}
