package com.team2073.eagleforcescoutingapplication.framework.manager;

import android.app.Activity;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.team2073.eagleforcescoutingapplication.util.Match;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import timber.log.Timber;

public class CSVManager {

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

    public void createCSV(String root, String teamNumber, String matchNumber) {
        File mainDir = new File(root + "/" + "ScoutingDataApplication");
        Timber.i("Instantiated file");
        if (!mainDir.exists()) {
            mainDir.mkdir();
            Timber.i("%s Created Successfully", mainDir.getPath());
        }
        setCSVFile(new File(mainDir, teamNumber + "-" + matchNumber + ".csv"));

        if (!csvFile.exists()) {
            try {
                csvFile.createNewFile();
                Timber.i("%s created successfully", csvFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                Timber.i("Failed to make CSV");
            }
        }
    }

    public void writeData(Object[] data) {
        try {
            CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile));

            csvWriter.writeNext(convertIntToString(data));

            csvWriter.close();

            Timber.d("CSV Wrote Successfully");
        } catch (IOException e) {
            Timber.e("CSV Failed to Write");
        }
    }

    public String[] convertIntToString(Object[] array) {
        return Arrays.copyOf(array, array.length, String[].class);
    }

    public File getFormCSVFile(int teamNumber, int matchNumber) {
        String fileName = teamNumber + "-" + matchNumber + ".csv";
        File rootFile = new File(presenter.getRootDirectory());
        File returnedFile = null;
        File[] files = rootFile.listFiles();
        for (File file : files) {
            if (file.getName() == fileName) {
                returnedFile = file;
            }
        }
        return returnedFile;
    }

    public void setFormCSVFile(File mainDir, String csvFile) {
        this.csvFile = new File(csvFile);
        this.mainDir = mainDir;
    }

    public File getCsvFile() {
        return csvFile;
    }

    public ArrayList<Match> readScheduleFile(File schedule) {
        ArrayList<Match> scheduleList = new ArrayList<>();

        try {
            FileReader fileReader = new FileReader(schedule);
            CSVReader csvReader = new CSVReaderBuilder(fileReader).withSkipLines(1).build();
            //TODO make it so index doesn't need to change by file
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                Match match = new Match(
                        nextRecord[1],
                        nextRecord[3],
                        nextRecord[4],
                        nextRecord[5],
                        nextRecord[6],
                        nextRecord[7],
                        nextRecord[8]);
                scheduleList.add(match);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return scheduleList;
    }
}