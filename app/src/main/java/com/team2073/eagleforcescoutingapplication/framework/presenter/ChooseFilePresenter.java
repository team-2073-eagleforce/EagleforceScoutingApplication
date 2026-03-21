package com.team2073.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.team2073.eagleforcescoutingapplication.activities.SettingsActivity;
import com.team2073.eagleforcescoutingapplication.framework.manager.FileManager;
import com.team2073.eagleforcescoutingapplication.framework.view.ChooseFileView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;

import timber.log.Timber;

public class ChooseFilePresenter extends BasePresenter<ChooseFileView> {
    private static final int READ_REQUEST_CODE = 42;
    private final Activity mActivity;
    private final FileManager fileManager;
    private Class activityClass = SettingsActivity.class;

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

    public void chooseFile(Class activityClass) {
        this.activityClass = activityClass;

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        mActivity.startActivityForResult(intent, READ_REQUEST_CODE);
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param resultData  Stores filepath of the chosen CSV file
     */
    public void saveScheduleFile(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                Uri uri = resultData.getData();
                File file = copyUriToCache(uri);

                if (file == null || !file.exists()) {
                    Toast.makeText(mActivity, "Failed to read schedule file", Toast.LENGTH_SHORT).show();
                    return;
                }

                FileManager.getInstance(mActivity).setScheduleFile(file);

                Toast.makeText(mActivity, "schedule file saved: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                mActivity.startActivity(new Intent(mActivity, activityClass));
                mActivity.finish();
            }
        }
    }

    private File copyUriToCache(Uri uri) {
        try {
            InputStream inputStream = mActivity.getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;
            File cacheFile = new File(mActivity.getCacheDir(), "schedule.csv");
            FileOutputStream outputStream = new FileOutputStream(cacheFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
            return cacheFile;
        } catch (IOException e) {
            Timber.e(e, "Failed to copy schedule URI to cache");
            return null;
        }
    }
}
