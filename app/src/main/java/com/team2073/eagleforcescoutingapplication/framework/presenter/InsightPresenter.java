package com.team2073.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;

import androidx.appcompat.widget.Toolbar;

import com.team2073.eagleforcescoutingapplication.framework.manager.DrawerManager;
import com.team2073.eagleforcescoutingapplication.framework.view.InsightView;

public class InsightPresenter extends BasePresenter<InsightView>{
    private Activity mActivity;
    private DrawerManager drawerManager;

    public InsightPresenter(Activity activity){
        this.mActivity = activity;
        drawerManager = DrawerManager.getInstance(mActivity);
    }

    public void makeDrawer(Toolbar toolbar) {
        drawerManager.makeDrawer(toolbar);
    }
}

