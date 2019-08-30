package com.team2073.eagleforcescoutingapplication.activities;

import android.os.Bundle;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ViewSchedulePresenter;
import com.team2073.eagleforcescoutingapplication.framework.view.ViewScheduleView;

public class ViewScheduleActivity extends BaseActivity implements ViewScheduleView {
    private final String LOG = "ViewScheduleActivity";
    private ViewSchedulePresenter viewSchedulePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewSchedulePresenter.makeDrawer();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_view_schedule;
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void bindView() {
        viewSchedulePresenter = new ViewSchedulePresenter(this);
        viewSchedulePresenter.bindView(this);
    }
}
