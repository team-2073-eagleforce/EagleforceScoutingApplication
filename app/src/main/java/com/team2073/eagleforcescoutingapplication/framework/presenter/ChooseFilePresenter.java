package com.team2073.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Toast;

import com.team2073.eagleforcescoutingapplication.activities.SettingsActivity;
import com.team2073.eagleforcescoutingapplication.framework.manager.FileManager;
import com.team2073.eagleforcescoutingapplication.framework.view.ChooseFileView;

import java.io.File;
import java.net.URISyntaxException;

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

    public void saveScheduleFile(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                //TODO Make generic path.
                File file = new File("/sdcard/Download/Match_Schdule.csv");
                fileManager.setScheduleFile(file);

                Toast.makeText(mActivity, "schedule file saved: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                mActivity.startActivity(new Intent(mActivity, SettingsActivity.class));
            }
        }
    }

    public static String getRealPathFromURI(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        return wholeID.split(":")[1];
    }
}
