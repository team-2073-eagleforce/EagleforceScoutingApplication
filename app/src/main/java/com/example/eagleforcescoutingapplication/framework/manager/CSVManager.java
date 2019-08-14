package com.example.eagleforcescoutingapplication.framework.manager;

import android.app.Activity;
import android.util.Log;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CSVManager {
    private static final String TAG = "CSVManager";
    public static CSVManager INSTANCE;
    private Activity mActivity;
    private File csvFile;

    public static CSVManager getInstance(Activity activity){
        if (INSTANCE == null) {
            INSTANCE = new CSVManager(activity);
        }
        return INSTANCE;
    }

    private CSVManager(Activity activity){
        mActivity = activity;
    }

    public void setCSVFile(File csvFile){
        this.csvFile = csvFile;
    }

    public void writeData(String...data) {
        try {
            CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile));

            csvWriter.writeNext(data);

            csvWriter.close();

            Log.d(TAG, "CSV Wrote Successfully");
        }catch (IOException e){
            Log.e(TAG, "CSV Failed to Write");
        }
    }
}
