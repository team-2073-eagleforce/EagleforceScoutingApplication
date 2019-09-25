package com.team2073.eagleforcescoutingapplication.activities;

import android.os.Bundle;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.framework.view.FormDataView;

public class FormDataActivity extends BaseActivity implements FormDataView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_data);
    }

    @Override
    protected int getLayoutResourceId() {
        return 0;
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void bindView() {

    }

    @Override
    public void onDataFormatSuccess() {

    }

    @Override
    public void onDataFormatFailed() {

    }
}
