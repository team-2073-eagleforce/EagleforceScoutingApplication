package com.team2073.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.widget.Toast;

import com.team2073.eagleforcescoutingapplication.activities.SettingsActivity;
import com.team2073.eagleforcescoutingapplication.framework.manager.FileManager;
import com.team2073.eagleforcescoutingapplication.framework.view.ChooseFileView;

import java.io.File;

public class ChooseFilePresenter extends BasePresenter<ChooseFileView> {
    private Activity mActivity;
    private FileManager fileManager;
    private static final int READ_REQUEST_CODE = 42;

    public ChooseFilePresenter(Activity activity) {
        this.mActivity = activity;
        fileManager = FileManager.getInstance(mActivity);
    }

    public void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        mActivity.startActivityForResult(intent, READ_REQUEST_CODE);
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param resultData
     *
     * Stores filepath of the chosen CSV file
     */
    public void saveScheduleFile(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                Uri uri = resultData.getData();

                String tempID = DocumentsContract.getDocumentId(uri);
                String[] split = tempID.split(":");
                String id = split[1];
//              File file = new File("/sdcard/Documents" +"/"+id);
//              for emulation
              File file = new File(uri.getPath().substring(14));

                FileManager.getInstance(mActivity).setScheduleFile(file);

                Toast.makeText(mActivity, "schedule file saved: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                mActivity.startActivity(new Intent(mActivity, SettingsActivity.class));
            }
        }
    }
}
