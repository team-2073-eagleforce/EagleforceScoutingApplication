package com.team2073.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.ImageButton;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

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

    public String fetchGridImageFile(String imageButtonName, String indicator, String fragmentName, Context context) {
        String savedDataName = imageButtonName + fragmentName;
        String retrievedImage = "";
        switch (indicator) {
            case "Cube":
                switch (readData(savedDataName)) {
                    case "0":
                        saveData(savedDataName, "2");
                        imageButtonName += "2";
                        break;
                    case "2":
                        saveData(savedDataName, "0");
                        imageButtonName += "0";
                        break;
                }
                break;
            case "Cone":
                switch (readData(savedDataName)) {
                    case "0":
                        saveData(savedDataName, "1");
                        imageButtonName += "1";
                        break;
                    case "1":
                        saveData(savedDataName, "0");
                        imageButtonName += "0";
                        break;
                }
                break;
            case "Hybrid":
                switch (readData(savedDataName)) {
                    case "0":
                        saveData(savedDataName, "1");
                        imageButtonName += "1";
                        break;
                    case "1":
                        saveData(savedDataName, "2");
                        imageButtonName += "2";
                        break;
                    case "2":
                        saveData(savedDataName, "0");
                        imageButtonName += "0";
                        break;
                }
                break;
        }

        try {
            Properties properties = new Properties();
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("grid.properties");
            properties.load(inputStream);
            retrievedImage = properties.getProperty(imageButtonName);
        } catch (IOException e) {
            e.fillInStackTrace();
        }
        Timber.d("%s %s", savedDataName, readData(savedDataName));
        return retrievedImage;
    }
}
