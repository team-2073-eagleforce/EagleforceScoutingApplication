package com.team2073.eagleforcescoutingapplication.framework.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Base64;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.team2073.eagleforcescoutingapplication.R;
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

import java.io.ByteArrayOutputStream;
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

    public void advanceOnSubmit(String matchNum, ArrayList<Match> scheduleList, String position) {
        if (scheduleList == null) {
            Timber.e("no schedule file for auto advance");
            Toast.makeText(mActivity, "Please select a schedule file", Toast.LENGTH_SHORT).show();
            clearPreferences();
            return;
        }

        int matchNumber = Integer.parseInt(matchNum);
        String teamNumber;
        Match currentMatch = scheduleList.get(matchNumber);
        switch (position) {
            case "Red1":
                teamNumber = currentMatch.getRed1();
                break;
            case "Red2":
                teamNumber = currentMatch.getRed2();
                break;
            case "Red3":
                teamNumber = currentMatch.getRed3();
                break;
            case "Blue1":
                teamNumber = currentMatch.getBlue1();
                break;
            case "Blue2":
                teamNumber = currentMatch.getBlue2();
                break;
            case "Blue3":
                teamNumber = currentMatch.getBlue3();
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + "position");
        }
        clearPreferences();
        prefsDataManager.writeToPreferences("matchNumber", Integer.toString(matchNumber + 1));
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

    public int toggleClimb(String climbStationKey) { //when you click it it checks the case and sets the data to it+1
        switch (readData(climbStationKey)) {
            case "0":
                saveData(climbStationKey, "1");
                if (climbStationKey.equals("autoChargingStation")) {
                } else if (climbStationKey.equals("endStageClimb")) {
                    return R.drawable.park;
                }
            case "1":
                saveData(climbStationKey, "2");
                 return R.drawable.single_climb;
            case "2":
                saveData(climbStationKey, "3");
                return R.drawable.harmony;
            case "3":
                saveData(climbStationKey, "4");
                return R.drawable.buddy_climb;
            case "4":
                saveData(climbStationKey, "0");
                return R.drawable.no_climb;
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
                switch (fieldData) {
                    case "autoGrid":
                        jsonData.put("autoGrid", gridArrayConverter(scoutingForm.getAutoFieldNames()));
                        break;
                    case "teleGrid":
                        jsonData.put("teleGrid", gridArrayConverter(scoutingForm.getTeleFieldNames()));
                        break;
                    case "comp_code":
                        checkCompCode();
                        jsonData.put("compCode", prefsDataManager.readFromPreferences("comp_code"));
                        break;
                    default:
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
    public void saveQR(Bitmap myBitmap) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        byte[] byteArrayVar = bytes.toByteArray();

        String  ConvertQR = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
        File file = Environment.getExternalStorageDirectory();
        File wallpaperDirectory = new File(file.getAbsolutePath() + "/Pictures/Screenshots");
        String qrName = "Team: " + prefsDataManager.readFromPreferences("teamNumber") + " Match: " + prefsDataManager.readFromPreferences("matchNumber");

        try {
            File f = new File(wallpaperDirectory, qrName + ".jpg");
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this.mActivity.getBaseContext(), new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();

            Toast.makeText(this.mActivity.getBaseContext(), "QR Saved to : "+ f.getAbsolutePath() , Toast.LENGTH_LONG).show();
            f.getAbsolutePath();
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    public void checkCompCode(){
        if(prefsDataManager.readFromPreferences("comp_code").equals("0")){
            prefsDataManager.writeToPreferences("comp_code", "testing");
        }
    }

    public void checkReadPermissions(){
        String[] permissionsStorage = {Manifest.permission.READ_EXTERNAL_STORAGE};
        int requestExternalStorage = 1;
        int permission = ActivityCompat.checkSelfPermission(this.mActivity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.mActivity, permissionsStorage, requestExternalStorage);
        }
        permission = ActivityCompat.checkSelfPermission(this.mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.mActivity, permissionsStorage, requestExternalStorage);
        }
    }

    public ArrayList<Match> getScheduleList(){
        if(fileManager.getScheduleFile() == null) {
            return null;
        }
        return csvManager.readScheduleFile(fileManager.getScheduleFile());

    }
    public String getPosition(){
        if(prefsDataManager.readFromPreferences("position") != null)
        return prefsDataManager.readFromPreferences("position");
        return "Red1";
    }
}
