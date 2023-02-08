package com.team2073.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.ScoutingFormActivity;
import com.team2073.eagleforcescoutingapplication.activities.SettingsActivity;
import com.team2073.eagleforcescoutingapplication.activities.fragment.ui.UIPagerAdapter;
import com.team2073.eagleforcescoutingapplication.framework.form.ChargedUpScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.form.ScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.manager.CSVManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.DrawerManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.FileManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.PrefsDataManager;
import com.team2073.eagleforcescoutingapplication.framework.view.ScoutingFormView;
import com.team2073.eagleforcescoutingapplication.util.Match;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import timber.log.Timber;

public class ScoutingFormPresenter extends BasePresenter<ScoutingFormView> {

    private final Activity mActivity;
    private final CSVManager csvManager;
    private final FileManager fileManager;
    private final DrawerManager drawerManager;
    private final PrefsDataManager prefsDataManager;
    private final ScoutingForm scoutingForm = new ChargedUpScoutingForm();
    private final ArrayList<String> allFieldNames = scoutingForm.getFieldNames();

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

    public void createTabs() {
        UIPagerAdapter sectionsPagerAdapter = new UIPagerAdapter(mActivity, ((FragmentActivity) mActivity).getSupportFragmentManager());
        ViewPager viewPager = mActivity.findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        TabLayout tabs = mActivity.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
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

    public void advanceOnSubmit() {
        if (fileManager.getScheduleFile() == null) {
            Timber.e("no schedule file for auto advance");
            Toast.makeText(mActivity, "Please select a schedule file", Toast.LENGTH_SHORT).show();
            clearPreferences();
            return;
        }

        String position = prefsDataManager.readFromPreferences("position");
        ArrayList<Match> scheduleList = csvManager.readScheduleFile(fileManager.getScheduleFile());
        int matchNumber = Integer.parseInt(prefsDataManager.readFromPreferences("matchNumber")) + 1;
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
        clearPreferences();
        prefsDataManager.writeToPreferences("matchNumber", Integer.toString(matchNumber));
        prefsDataManager.writeToPreferences("teamNumber", teamNumber);

    }

    //Fragment Related Logic
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

    public int toggleClimb(String climbStationKey) {
        switch (readData(climbStationKey)) {
            case "0":
                saveData(climbStationKey, "1");
                if (climbStationKey.equals("autoChargingStation")) {
                    return R.drawable.auto_mobility;
                } else if (climbStationKey.equals("endChargingStation")) {
                    return R.drawable.community_zone;
                }
            case "1":
                saveData(climbStationKey, "2");
                 return R.drawable.auto_docked;
            case "2":
                saveData(climbStationKey, "3");
                return R.drawable.auto_engaged;
            case "3":
                saveData(climbStationKey, "0");
                return R.drawable.auto_none;
        }
        return 0;
    }

    //QR Code and Data Handling
    private String gridArrayConverter(ArrayList<String> gridNames) {
        ArrayList<String> gridValuesToConvert = prefsDataManager.readFromPreferences(gridNames);
        String[][] gridValuesArray = new String[3][9];
        for (int gridRow = 0; gridRow < 3; gridRow++) {
            for (int gridElement = 0; gridElement < 9; gridElement++) {
                int gridElementNumber = 9 * gridRow + gridElement;
                gridValuesArray[gridRow][gridElement] = gridValuesToConvert.get(gridElementNumber);
            }
        }
        return Arrays.deepToString(gridValuesArray);
    }

    public JSONObject dataToJSON() throws JSONException {
        JSONObject jsonData = new JSONObject();
        try {
            for (String fieldData : allFieldNames) {
                if (fieldData.equals("autoGrid")) {
                    jsonData.put("autoGrid", gridArrayConverter(scoutingForm.getAutoFieldNames()));
                } else if (fieldData.equals("teleGrid")) {
                    jsonData.put("teleGrid", gridArrayConverter(scoutingForm.getTeleFieldNames()));
                } else {
                    jsonData.put(fieldData, prefsDataManager.readFromPreferences(fieldData));
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Timber.tag("json_data").d(jsonData.toString());
        return jsonData;
    }

    public Bitmap createQR() throws WriterException {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(String.valueOf(dataToJSON()), BarcodeFormat.QR_CODE, 400, 400);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.createBitmap(bitMatrix);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

//    public void saveQRCode(ImageView qrCode) {
//        qrCode.buildDrawingCache();
//        Bitmap image = qrCode.getDrawingCache();
//
//        FileOutputStream outputStream = null;
//        File file = Environment.getExternalStorageDirectory();
//        File dir = new File(file.getAbsolutePath() + "/Pictures/Screenshots");
//
//        String filename = String.format("%s.png", fetchTeamAndMatch());
//        File outFile = new File(dir, filename);
//        try {
//            outputStream = new FileOutputStream(outFile);
//            image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//            outputStream.flush();
//            outputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
