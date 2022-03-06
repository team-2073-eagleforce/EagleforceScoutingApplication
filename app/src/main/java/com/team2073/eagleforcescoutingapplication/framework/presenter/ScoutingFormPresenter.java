package com.team2073.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;
import android.os.Environment;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.ScoutingFormActivity;
import com.team2073.eagleforcescoutingapplication.activities.SettingsActivity;
import com.team2073.eagleforcescoutingapplication.activities.fragment.ui.UIPagerAdapter;
import com.team2073.eagleforcescoutingapplication.framework.form.InfiniteRechargeScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.form.RapidReactScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.form.ScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.manager.CSVManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.DrawerManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.FileManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.PrefsDataManager;
import com.team2073.eagleforcescoutingapplication.framework.view.ScoutingFormView;
import com.team2073.eagleforcescoutingapplication.util.Match;

import java.util.ArrayList;

import timber.log.Timber;

public class ScoutingFormPresenter extends BasePresenter<ScoutingFormView> {

    private Activity mActivity;
    private CSVManager csvManager;
    private FileManager fileManager;
    private DrawerManager drawerManager;
    private PrefsDataManager prefsDataManager;
    private UIPagerAdapter uiPagerAdapter;
    private ScoutingForm scoutingForm = new RapidReactScoutingForm();

    public ScoutingFormPresenter(Activity activity) {
        this.mActivity = activity;
        csvManager = CSVManager.getInstance(mActivity);
        fileManager = FileManager.getInstance(mActivity);
        drawerManager = DrawerManager.getInstance(mActivity);
        prefsDataManager = PrefsDataManager.getInstance(mActivity);
    }

    public void makeDrawer(Toolbar toolbar) {
        drawerManager.makeDrawer(toolbar);
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

        String fileName = team + "-" + match + ".csv";

        csvManager.createCSV(getRootDirectory(), fileName);
        writeCSV();
    }

    /**
     * Handles sending a csv file to another device through bluetooth.
     */
    public void sendOverBluetooth() {
        csvManager.sendOverBluetooth(mActivity);
    }

    public void writeCSV() {
        ArrayList<String> formData = prefsDataManager.readFromPreferences(scoutingForm.getFieldNames());
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


    /**
     * Creates the tabbed interface for the {@link ScoutingFormActivity} based on the scouting mode chosen in the {@link SettingsActivity}
     * Defaults to the UI interface
     */
    public void createTabs() {
        UIPagerAdapter sectionsPagerAdapter = new UIPagerAdapter(mActivity, ((FragmentActivity) mActivity).getSupportFragmentManager());
        ViewPager viewPager = mActivity.findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        TabLayout tabs = mActivity.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    /**
     * After submit button is pressed, advances the team number and match number for the next scouting form automatically based on the tablet position
     */
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
