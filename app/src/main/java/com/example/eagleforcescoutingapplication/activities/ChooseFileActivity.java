package com.example.eagleforcescoutingapplication.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.eagleforcescoutingapplication.R;
import com.example.eagleforcescoutingapplication.framework.presenter.ChooseFilePresenter;
import com.example.eagleforcescoutingapplication.framework.view.ChooseFileView;

public class ChooseFileActivity extends BaseActivity implements ChooseFileView {
    private final String LOG = "ChooseFileActivity";
    private ChooseFilePresenter chooseFilePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chooseFilePresenter.chooseFile();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_choose_file;
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void bindView() {
        chooseFilePresenter = new ChooseFilePresenter(this);
        chooseFilePresenter.bindView(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        chooseFilePresenter.saveScheduleFile(requestCode, resultCode, data);
    }
}
