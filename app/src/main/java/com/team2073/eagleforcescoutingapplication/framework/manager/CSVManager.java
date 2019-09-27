package com.team2073.eagleforcescoutingapplication.framework.manager;

import android.app.Activity;
import android.util.Log;

import com.opencsv.CSVWriter;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CSVManager {
    private static final String TAG = "CSVManager";
    public static CSVManager INSTANCE;
    private Activity mActivity;
    private File mainDir;
    private File csvFile;
    private ScoutingFormPresenter presenter;

    public static CSVManager getInstance(Activity activity) {
        if (INSTANCE == null) {
            INSTANCE = new CSVManager(activity);
        }
        return INSTANCE;
    }

    private CSVManager(Activity activity) {
        mActivity = activity;
    }

    public void setCSVFile(File csvFile) {
        this.csvFile = csvFile;
    }

    public void createCSV(String root, int teamNumber) {
        File mainDir = new File(root + "/" + "ScoutingDataApplication");
        Log.i(TAG, "Instantiated file");
        if (!mainDir.exists()) {
            mainDir.mkdir();
            Log.i(TAG, mainDir.getPath() + " created successfully");
        }
        setCSVFile(new File(mainDir, teamNumber + ".csv"));

        if (!csvFile.exists()) {
            try {
                csvFile.createNewFile();
                Log.i(TAG, csvFile.getAbsolutePath() + " created successfully");
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG, "Failed to make CSV");
            }
        }
    }

    public void writeData(String[] data) {
        try {
            CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile));

            csvWriter.writeNext(convertIntToString(data));

            csvWriter.close();

            Log.d(TAG, "CSV Wrote Successfully");
        } catch (IOException e) {
            Log.e(TAG, "CSV Failed to Write");
        }
    }

    public String[] convertIntToString(Object[] array) {
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

    public File getFormCSVFile(int teamNumber, int matchNumber){
        String fileName = teamNumber + "-" + matchNumber + ".csv";
        File rootFile = new File(presenter.getRootDirectory());
        File returnedFile = null;
        File[] files = rootFile.listFiles();
        for(File file: files){
            if (file.getName() == fileName){
                returnedFile =  file;
            }
        }
        return returnedFile;
    }

    public void setFormCSVFile(File mainDir, String csvFile) {
        this.csvFile = new File(csvFile);
        this.mainDir = mainDir;
    }
}