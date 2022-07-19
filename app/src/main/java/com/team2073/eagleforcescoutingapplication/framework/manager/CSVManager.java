package com.team2073.eagleforcescoutingapplication.framework.manager;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.team2073.eagleforcescoutingapplication.util.Match;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class CSVManager {

    private static CSVManager INSTANCE;
    private Activity mActivity;
    private File mainDir;
    private File csvFile;


    public static CSVManager getInstance(Activity activity) {
        if (INSTANCE == null) {
            INSTANCE = new CSVManager(activity);
        }
        return INSTANCE;
    }

    private CSVManager(Activity activity) {
        mActivity = activity;
    }

//    public void setCSVFile(File csvFile) {
//        this.csvFile = csvFile;
//    }
//
//    /**
//     * @param root directory which csv will be saved
//     * @param fileName
//
//     * Creates a CSV.
//     */
//    public void createCSV(String root, String fileName) {
//        File mainDir = new File(root + "/" + "ScoutingDataApplication");
//        Timber.i("Instantiated file");
//        if (!mainDir.exists()) {
//            mainDir.mkdir();
//            Timber.i("%s Created Successfully", mainDir.getPath());
//        }
//        setCSVFile(new File(mainDir, fileName));
//
//        if (!csvFile.exists()) {
//            try {
//                csvFile.createNewFile();
//                Timber.i("%s created successfully", csvFile.getAbsolutePath());
//            } catch (IOException e) {
//                e.printStackTrace();
//                Timber.i("Failed to make CSV");
//            }
//        }
//    }
//
//    /**
//     * Writes data from Scouts to a CSV file.
//     * @param data
//     */
//    public void writeData(Object[] data) {
//        try {
//            CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile));
//
//            csvWriter.writeNext(convertIntToString(data));
//
//            csvWriter.close();
//
//            Timber.d("CSV Wrote Successfully");
//        } catch (IOException e) {
//            Timber.e("CSV Failed to Write");
//        }
//    }

    private String[] convertIntToString(Object[] array) {
        return Arrays.copyOf(array, array.length, String[].class);
    }

//    public File getCsvFile() {
//        return csvFile;
//    }

    /**
     *
     * @param schedule
     * @return
     *
     *Converts a CSV to an {@link ArrayList} to be better manipulated
     */
    public ArrayList<Match> readScheduleFile(File schedule) {
        ArrayList<Match> scheduleList = new ArrayList<>();

        try {
            FileReader fileReader = new FileReader(schedule);
            CSVReader csvReader = new CSVReaderBuilder(fileReader).withSkipLines(1).build();
            //TODO make it so index doesn't need to change by file
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                Match match = new Match(
                        nextRecord[0],
                        nextRecord[1],
                        nextRecord[2],
                        nextRecord[3],
                        nextRecord[4],
                        nextRecord[5],
                        nextRecord[6]);
                scheduleList.add(match);
            }

        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return scheduleList;
    }

//    public void sendOverBluetooth(Activity activity) {
//        mActivity = activity;
//        if (BluetoothAdapter.getDefaultAdapter() == null) {
//            return;
//        }
//
//        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            mActivity.startActivityForResult(enableBtIntent, 1);
//        }
//
//        //Bluetooth start
//        Intent intent = new Intent();
//
//        intent.setAction(Intent.ACTION_SEND);
//        intent.setType("text/plain");
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//        intent.putExtra(Intent.EXTRA_STREAM,
//                FileProvider.getUriForFile(
//                        mActivity.getApplicationContext(),
//                        "com.team2073.eagleforcescoutingapplication.util.EagleProvider",
//                        getCsvFile()));
//
//        List<ResolveInfo> appList = mActivity.getPackageManager().queryIntentActivities(intent, 0);
//
//        if (appList.size() > 0) {
//            String packageName = null;
//            String className = null;
//            boolean found = false;
//
//            for (ResolveInfo info : appList) {
//                packageName = info.activityInfo.packageName;
//                if (packageName.equals("com.android.bluetooth")) {
//                    className = info.activityInfo.name;
//                    found = true;
//                    break;
//                }
//            }
//            if (!found) {
//                Toast.makeText(mActivity, "Bluetooth Not Available: Make Sure Bluetooth Is On", Toast.LENGTH_SHORT).show();
//            } else {
//                intent.setClassName(packageName, className);
//                mActivity.startActivityForResult(intent, 1234);
//            }
//        }
//
//    }
}