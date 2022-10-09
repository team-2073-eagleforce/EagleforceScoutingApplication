package com.team2073.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import com.google.zxing.common.StringUtils;
import com.team2073.eagleforcescoutingapplication.activities.QrGeneratorActivity;
import com.team2073.eagleforcescoutingapplication.activities.fragment.ui.UIPagerAdapter;
import com.team2073.eagleforcescoutingapplication.framework.form.RapidReactScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.form.ScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.manager.CSVManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.DrawerManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.FileManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.PrefsDataManager;
import com.team2073.eagleforcescoutingapplication.framework.view.QrGeneratorView;
import com.team2073.eagleforcescoutingapplication.util.Match;

import java.util.ArrayList;
import java.util.Arrays;

import timber.log.Timber;

public class QrGeneratorPresenter extends BasePresenter<QrGeneratorView> {

    private Activity mActivity;
    private CSVManager csvManager;
    private FileManager fileManager;
    private DrawerManager drawerManager;
    private PrefsDataManager prefsDataManager;
    private ScoutingForm scoutingForm = new RapidReactScoutingForm();
    private ArrayList<String> allData;

    public QrGeneratorPresenter(Activity activity) {
        this.mActivity = activity;
        csvManager = CSVManager.getInstance(mActivity);
        fileManager = FileManager.getInstance(mActivity);
        drawerManager = DrawerManager.getInstance(mActivity);
        prefsDataManager = PrefsDataManager.getInstance(mActivity);

        allData = prefsDataManager.readFromPreferences(scoutingForm.getFieldNames());
    }


    public void makeDrawer(Toolbar toolbar) {
        drawerManager.makeDrawer(toolbar);
    }

    public String fetchAllData(){
        String toList = allData.toString();

        toList = toList.replace("[", "")
                .replace("]", "")
                .replace(" ", "");

        return toList;
    }

    public String fetchTeamAndMatch(){
            return "Team: " + allData.get(0) + "; Match: " + allData.get(1);
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
