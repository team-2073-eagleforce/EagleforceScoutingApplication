package com.team2073.eagleforcescoutingapplication.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ChooseFilePresenter;
import com.team2073.eagleforcescoutingapplication.framework.view.ChooseFileView;

public class ChooseFileActivity extends BaseActivity implements ChooseFileView {

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
