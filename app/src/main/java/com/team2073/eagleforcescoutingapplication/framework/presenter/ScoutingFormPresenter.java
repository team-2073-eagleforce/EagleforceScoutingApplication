package com.team2073.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.preference.ListPreference;
import androidx.preference.Preference;

import com.team2073.eagleforcescoutingapplication.EagleforceScoutingApplication;
import com.team2073.eagleforcescoutingapplication.Match;
import com.team2073.eagleforcescoutingapplication.framework.DeepSpaceScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.InfiniteRechargeScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.ScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.manager.CSVManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.DrawerManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.FileManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.PrefsDataManager;
import com.team2073.eagleforcescoutingapplication.framework.view.ScoutingFormView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ScoutingFormPresenter extends BasePresenter<ScoutingFormView> {
    private static final String TAG = ScoutingFormPresenter.class.getSimpleName();
    private Activity mActivity;
    private CSVManager csvManager;
    private FileManager fileManager;
    private DrawerManager drawerManager;
    private PrefsDataManager prefsDataManager;

    //REPLACE with current year's scouting form
    private ScoutingForm scoutingForm = new InfiniteRechargeScoutingForm();

    private File tempCSVDir;
    private ArrayList<String> formData;

    public ScoutingFormPresenter(Activity activity) {
        this.mActivity = activity;
        csvManager = CSVManager.getInstance(mActivity);
        fileManager = FileManager.getInstance(mActivity);
        drawerManager = DrawerManager.getInstance(mActivity);
        prefsDataManager = PrefsDataManager.getInstance(mActivity);
        tempCSVDir = new File(mActivity.getFilesDir(), "tmpCSVFiles");
    }

    public void makeDrawer() {
        drawerManager.makeDrawer();
    }

    public void saveData(String key, String data) {
        prefsDataManager.writeToPreferences(key, data);
    }

    public String readData(String key) {
        return prefsDataManager.readFromPreferences(key);
    }

    public void createCSV() {

        String match = prefsDataManager.readFromPreferences("matchNumber");
        String team = prefsDataManager.readFromPreferences("teamNumber");

        if (match.equals("")) {
            match = "0";
        }

        if (team.equals("")) {
            team = "0";
        }
        csvManager.createCSV(getRootDirectory(),
                team, match);
        writeCSV();
    }

    public void sendOverBluetooth() {

        if (BluetoothAdapter.getDefaultAdapter() == null) {
            return;
        }

        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(enableBtIntent, 1);
        }

        //Bluetooth start
        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.putExtra(Intent.EXTRA_STREAM,
                FileProvider.getUriForFile(
                        mActivity.getApplicationContext(),
                        "com.team2073.eagleforcescoutingapplication.util.EagleProvider",
                        csvManager.getCsvFile()));

        List<ResolveInfo> appList = mActivity.getPackageManager().queryIntentActivities(intent, 0);

        if (appList.size() > 0) {
            String packageName = null;
            String className = null;
            boolean found = false;

            for (ResolveInfo info : appList) {
                packageName = info.activityInfo.packageName;
                if (packageName.equals("com.android.bluetooth")) {
                    className = info.activityInfo.name;
                    found = true;
                    break;
                }
            }
            if (!found) {
                Toast.makeText(mActivity, "Bluetooth Not Available: Make Sure Bluetooth Is On", Toast.LENGTH_SHORT).show();
            } else {
                intent.setClassName(packageName, className);
                mActivity.startActivityForResult(intent, 1234);
            }
        }
    }

    public void writeCSV() {
        formData = prefsDataManager.readFromPreferences(scoutingForm.getFieldNames());
        csvManager.writeData(formData.toArray());
    }

    public String getRootDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public ScoutingForm getScoutingForm() {
        return scoutingForm;
    }

    public void clearPreferences() {
        prefsDataManager.clearPreferences(scoutingForm.getClearNames());
    }

    public void clearAllPreferences() {
        prefsDataManager.clearPreferences();
    }

    public void advanceOnSubmit() {

        if(fileManager.getScheduleFile() == null) {
            Timber.e("no schedule file for auto advance");
            Toast.makeText(mActivity, "Please select a schedule file", Toast.LENGTH_SHORT).show();
            return;
        }

        String position = prefsDataManager.readFromPreferences("position");
        // ArrayList<Match> list = csvManager.readScheduleFile(new File("C:\\Users\\Afraz Hameed\\AndroidStudioProjects\\EagleforceScoutingApplication\\Match_Schedule.csv"));
        ArrayList<Match> scheduleList = csvManager.readScheduleFile(fileManager.getScheduleFile());
        Integer matchNumber = Integer.parseInt(prefsDataManager.readFromPreferences("matchNumber")) + 1;
        String teamNumber;
        Match currentMatch = scheduleList.get(matchNumber - 1);
        switch (position) {
            case "red1":
                teamNumber = currentMatch.getRed1();
                break;
            case "red2":
                teamNumber = currentMatch.getRed2();
                break;
            case "red3":
                teamNumber = currentMatch.getRed3();
                break;
            case "blue1":
                teamNumber = currentMatch.getBlue1();
                break;
            case "blue2":
                teamNumber = currentMatch.getBlue2();
                break;
            case "blue3":
                teamNumber = currentMatch.getBlue3();
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + "position");
        }
        prefsDataManager.writeToPreferences("matchNumber", matchNumber.toString());
        prefsDataManager.writeToPreferences("teamNumber", teamNumber);


    }
}
