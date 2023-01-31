package com.team2073.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.team2073.eagleforcescoutingapplication.framework.form.ChargedUpScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.form.RapidReactScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.form.ScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.manager.CSVManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.DrawerManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.FileManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.PrefsDataManager;
import com.team2073.eagleforcescoutingapplication.framework.view.QrGeneratorView;
import com.team2073.eagleforcescoutingapplication.util.Match;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import timber.log.Timber;

public class QrGeneratorPresenter extends BasePresenter<QrGeneratorView> {

    private final Activity mActivity;
    private final CSVManager csvManager;
    private final FileManager fileManager;
    private final DrawerManager drawerManager;
    private final PrefsDataManager prefsDataManager;
    private final ScoutingForm scoutingForm = new ChargedUpScoutingForm();

    private final ArrayList<String> allFieldNames = scoutingForm.getFieldNames();

    public QrGeneratorPresenter(Activity activity) {
        this.mActivity = activity;
        csvManager = CSVManager.getInstance(mActivity);
        fileManager = FileManager.getInstance(mActivity);
        drawerManager = DrawerManager.getInstance(mActivity);
        prefsDataManager = PrefsDataManager.getInstance(mActivity);
    }

    public void makeDrawer(Toolbar toolbar) {
        drawerManager.makeDrawer(toolbar);
    }

    public String fetchTeamAndMatch() {
        return "Team: " + prefsDataManager.readFromPreferences(allFieldNames.get(0)) + " Match: " +
                prefsDataManager.readFromPreferences(allFieldNames.get(1));
    }

    private String gridArrayConverter(ArrayList<String> gridNames) {
        ArrayList<String> gridValuesToConvert = prefsDataManager.readFromPreferences(gridNames);
        String[][] gridValuesArray = new String[3][9];
        for (int gridRow = 0; gridRow < 3; gridRow++) {
            for (int gridElement = 0; gridElement < 9; gridElement++) {
                int gridElementNumber = 9 * gridRow + gridElement;
                gridValuesArray[gridRow][gridElement] = gridValuesToConvert.get(gridElementNumber);
            }
        }
        System.out.println(Arrays.deepToString(gridValuesArray));
        return Arrays.deepToString(gridValuesArray);
    }

    public JSONObject dataToJSON() throws JSONException {
        JSONObject jsonData = new JSONObject();
        try {
            for (String fieldData : allFieldNames) {
                if (fieldData.equals("gridValuesAuto")) {
                    jsonData.put("gridValuesAuto", gridArrayConverter(scoutingForm.getAutoFieldNames()));
                } else if (fieldData.equals("gridValuesTeleOp")) {
                    jsonData.put("gridValuesTeleOp", gridArrayConverter(scoutingForm.getTeleFieldNames()));
                } else {
                    jsonData.put(fieldData, prefsDataManager.readFromPreferences(fieldData));
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Timber.tag("json_data: ").d(jsonData.toString());
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

    /**
     * After submit button is pressed, advances the team number and match number for the next scouting form automatically based on the tablet position
     */
    public void advanceOnSubmit() {
        if (fileManager.getScheduleFile() == null) {
            Timber.e("no schedule file for auto advance");
            Toast.makeText(mActivity, "Please select a schedule file", Toast.LENGTH_SHORT).show();
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
                System.out.println(position);
                throw new IllegalStateException("Unexpected value: " + "position");
        }
        prefsDataManager.writeToPreferences("matchNumber", Integer.toString(matchNumber));
        prefsDataManager.writeToPreferences("teamNumber", teamNumber);

    }

    public void saveQRCode(ImageView qrCode) {
        qrCode.buildDrawingCache();
        Bitmap image = qrCode.getDrawingCache();

        FileOutputStream outputStream = null;
        File file = Environment.getExternalStorageDirectory();
        File dir = new File(file.getAbsolutePath() + "/Pictures/Screenshots");
        System.out.println(dir);

        String filename = String.format("%s.png", fetchTeamAndMatch());
        File outFile = new File(dir, filename);
        try {
            outputStream = new FileOutputStream(outFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
