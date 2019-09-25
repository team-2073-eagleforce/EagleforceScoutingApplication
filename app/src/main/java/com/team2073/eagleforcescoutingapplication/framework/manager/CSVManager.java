package com.team2073.eagleforcescoutingapplication.framework.manager;

import android.app.Activity;
import android.util.Log;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import timber.log.Timber;

public class CSVManager {
    private static final String TAG = "CSVManager";
    public static CSVManager INSTANCE;
    private Activity mActivity;
    private File formCSVFile;
    private String fileName;

    public static CSVManager getInstance(Activity activity) {
        if (INSTANCE == null) {
            INSTANCE = new CSVManager(activity);
        }
        return INSTANCE;
    }

    private CSVManager(Activity activity) {
        mActivity = activity;
    }

    public void setFormCSVFile(File dir, String fileName) {
        formCSVFile = new File(dir, fileName);
        try {
            formCSVFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getFormCSVFile() {
        return formCSVFile;
    }

    public void createCSV(String root, int teamNumber) {
        File mainDir = new File(root + "/" + "ScoutingData:P");
        if (!mainDir.exists()) {
            mainDir.mkdir();
        }
        formCSVFile = new File(mainDir, teamNumber + ".csv");
        if (!formCSVFile.exists()) {
            try {
                formCSVFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeData(Object[] data) {
        try {

            CSVWriter csvWriter = new CSVWriter(new FileWriter(formCSVFile));

            csvWriter.writeNext(convertObjectToString(data));

            csvWriter.close();

            Log.d(TAG, "CSV Wrote Successfully");
        } catch (IOException e) {
            Log.e(TAG, "CSV Failed to Write");
        }
    }

    public String[] convertObjectToString(Object[] array) {
        String[] strArr = (String[]) array;
        for (int i = 0; i < array.length; i++) {
            try {
                strArr[i] = array[i].toString();
            } catch (NullPointerException ex) {
                // do some default initialization
            }
        }
        return strArr;
    }
}
