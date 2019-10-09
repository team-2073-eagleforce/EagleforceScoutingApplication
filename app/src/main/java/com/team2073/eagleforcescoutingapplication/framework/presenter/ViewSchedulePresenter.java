package com.team2073.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;

import com.team2073.eagleforcescoutingapplication.TeamsEachMatch;
import com.team2073.eagleforcescoutingapplication.framework.manager.CSVManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.DrawerManager;
import com.team2073.eagleforcescoutingapplication.framework.view.ViewScheduleView;

import java.util.List;

public class ViewSchedulePresenter extends BasePresenter<ViewScheduleView> {
    private Activity activity;
    private CSVManager csvManager;
    private DrawerManager drawerManager;
    private List<TeamsEachMatch> listTeamsPerMatch;

    public ViewSchedulePresenter(Activity activity){
        this.activity = activity;
        csvManager = CSVManager.getInstance(activity);
        drawerManager = DrawerManager.getInstance(activity);
    }

    public void makeDrawer(){
        drawerManager.makeDrawer();
    }

    public List getAllTeamsPerMatch(){
        return listTeamsPerMatch;
    }
}
