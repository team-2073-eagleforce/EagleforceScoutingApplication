package com.team2073.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;

import com.team2073.eagleforcescoutingapplication.framework.manager.CSVManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.DrawerManager;
import com.team2073.eagleforcescoutingapplication.framework.view.ViewScheduleView;

public class ViewSchedulePresenter extends BasePresenter<ViewScheduleView> {
    private Activity activity;
    private CSVManager csvManager;
    private DrawerManager drawerManager;

    public ViewSchedulePresenter(Activity activity){
        this.activity = activity;
        csvManager = CSVManager.getInstance(activity);
        drawerManager = DrawerManager.getInstance(activity);
    }

    public void makeDrawer(){
        drawerManager.makeDrawer();
    }
}
