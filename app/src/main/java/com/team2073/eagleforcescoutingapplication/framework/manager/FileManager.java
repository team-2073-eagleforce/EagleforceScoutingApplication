package com.team2073.eagleforcescoutingapplication.framework.manager;

import android.app.Activity;

import java.io.File;

public class FileManager {
    private static final String TAG = "FileManager";
    public static FileManager INSTANCE;
    private Activity mActivity;
    private File scheduleFile;
    private File currentFormFile;

    public static FileManager getInstance(Activity activity){
        if(INSTANCE == null){
            INSTANCE = new FileManager(activity);
        }
        return INSTANCE;
    }

    public FileManager(Activity activity){
        mActivity = activity;
    }

    public void setScheduleFile(File scheduleFile){
        this.scheduleFile = scheduleFile;
    }

    public File getScheduleFile(){
        return scheduleFile;
    }
}
