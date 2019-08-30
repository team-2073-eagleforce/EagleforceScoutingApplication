package com.example.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;

import androidx.preference.PreferenceFragmentCompat;

import com.example.eagleforcescoutingapplication.framework.manager.CSVManager;
import com.example.eagleforcescoutingapplication.framework.manager.DrawerManager;
import com.example.eagleforcescoutingapplication.framework.view.ViewScheduleView;

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
