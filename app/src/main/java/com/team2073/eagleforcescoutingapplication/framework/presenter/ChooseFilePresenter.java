package com.team2073.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.team2073.eagleforcescoutingapplication.activities.SettingsActivity;
import com.team2073.eagleforcescoutingapplication.framework.manager.FileManager;
import com.team2073.eagleforcescoutingapplication.framework.view.ChooseFileView;

import java.io.File;

public class ChooseFilePresenter extends BasePresenter<ChooseFileView> {
    private Activity mActivity;
    private FileManager fileManager;
    private static final int READ_REQUEST_CODE = 42;

    public ChooseFilePresenter(Activity activity){
        this.mActivity = activity;
        fileManager = FileManager.getInstance(mActivity);
    }

    public void chooseFile(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        mActivity.startActivityForResult(intent, READ_REQUEST_CODE);
    }

    public void saveScheduleFile(int requestCode, int resultCode, Intent resultData){
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                File scheduleFile = new File(uri.getPath());
                fileManager.setScheduleFile(scheduleFile);
                Toast.makeText(mActivity, "schedule file saved: " + scheduleFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                mActivity.startActivity(new Intent(mActivity, SettingsActivity.class));
            }
        }
    }
}
