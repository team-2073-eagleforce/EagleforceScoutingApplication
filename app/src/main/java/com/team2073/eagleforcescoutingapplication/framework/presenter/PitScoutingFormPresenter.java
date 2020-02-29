package com.team2073.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;
import android.os.Environment;

import com.team2073.eagleforcescoutingapplication.framework.form.PitScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.form.ScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.manager.CSVManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.DrawerManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.FileManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.PrefsDataManager;
import com.team2073.eagleforcescoutingapplication.framework.view.ScoutingFormView;

import java.util.ArrayList;

public class PitScoutingFormPresenter extends BasePresenter<ScoutingFormView> {

    private Activity mActivity;
    private CSVManager csvManager;
    private FileManager fileManager;
    private DrawerManager drawerManager;
    private PrefsDataManager prefsDataManager;
    private ScoutingForm pitScoutingForm = new PitScoutingForm();

    public PitScoutingFormPresenter(Activity activity) {
        this.mActivity = activity;
        csvManager = CSVManager.getInstance(mActivity);
        fileManager = FileManager.getInstance(mActivity);
        prefsDataManager = PrefsDataManager.getInstance(mActivity);
        drawerManager = DrawerManager.getInstance(mActivity);
    }

    public void makeDrawer() {
        drawerManager.makeDrawer();
    }

    public ScoutingForm getPitScoutingForm() {
        return pitScoutingForm;
    }

    public void sendOverBluetooth() {
        csvManager.sendOverBluetooth(mActivity);
    }

    public void clearPreferences() {
        prefsDataManager.clearPreferences(pitScoutingForm.getClearNames());
    }

    public void createCSV() {

        String team = prefsDataManager.readFromPreferences("teamNumber");

        String fileName = "PIT_" + team + ".csv";

        csvManager.createCSV(Environment.getExternalStorageDirectory().getAbsolutePath(), fileName);
        writeCSV();
    }

    public void writeCSV() {
        ArrayList<String> formData = prefsDataManager.readFromPreferences(pitScoutingForm.getFieldNames());
        csvManager.writeData(formData.toArray());
    }
}
