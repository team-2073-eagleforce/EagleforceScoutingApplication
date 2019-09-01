package com.example.eagleforcescoutingapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.eagleforcescoutingapplication.R;
import com.example.eagleforcescoutingapplication.framework.view.FormDataView;

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
