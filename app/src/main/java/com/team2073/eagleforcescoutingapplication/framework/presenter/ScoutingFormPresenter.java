package com.team2073.eagleforcescoutingapplication.framework.presenter;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.framework.manager.CSVManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.DrawerManager;
import com.team2073.eagleforcescoutingapplication.framework.view.ScoutingFormView;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoutingFormPresenter extends BasePresenter<ScoutingFormView> {
    private static final String TAG = ScoutingFormPresenter.class.getSimpleName();
    private Activity mActivity;
    private CSVManager csvManager;
    private DrawerManager drawerManager;

    private String matchNumber;
    private String position;
    private String teamNumber;
    private String name;
    private File tempCSVDir;
    private Map<String, String> formMap = new HashMap<>();

    public ScoutingFormPresenter(Activity activity) {
        this.mActivity = activity;
        csvManager = CSVManager.getInstance(mActivity);
        drawerManager = DrawerManager.getInstance(mActivity);
        tempCSVDir = new File(mActivity.getFilesDir(), "tmpCSVFiles");
    }

    public void makeDrawer() {
        drawerManager.makeDrawer();
    }

    public void saveData(String key, String data) {
        formMap.put(key, data);
    }

    public void sendOverBluetooth() {

        csvManager.setFormCSVFile(tempCSVDir, matchNumber + "-" + teamNumber + ".csv");

        csvManager.writeData(formMap.get(mActivity.getString(R.string.formNameKey)));
        csvManager.writeData(formMap.get(mActivity.getString(R.string.formCommentsKey)));

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
                        "com.team2073.eagleforcescoutingapplication",
                        csvManager.getFormCSVFile()));

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
                mActivity.startActivity(intent);
            }
        }
    }


    public String readData(String key) {
        return formMap.get(key);
    }

    public void writeCSV() {
        csvManager.writeData();
    }
}
