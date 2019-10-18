package com.team2073.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;

import com.team2073.eagleforcescoutingapplication.Match;
import com.team2073.eagleforcescoutingapplication.framework.manager.CSVManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.DrawerManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.FileManager;
import com.team2073.eagleforcescoutingapplication.framework.view.ViewScheduleView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewSchedulePresenter extends BasePresenter<ViewScheduleView> {
    private Activity activity;
    private CSVManager csvManager;
    private DrawerManager drawerManager;
    private FileManager fileManager;
    private List<Match> scheduleList;

    public ViewSchedulePresenter(Activity activity){
        this.activity = activity;
        csvManager = CSVManager.getInstance(activity);
        drawerManager = DrawerManager.getInstance(activity);
        fileManager = FileManager.getInstance(activity);
    }

    public void makeDrawer(){
        drawerManager.makeDrawer();
    }

    public List getAllTeamsPerMatch(){
        //TODO handle if schedule file not selected yet
        scheduleList = csvManager.readScheduleFile(fileManager.getScheduleFile());
        return scheduleList;
    }

    public File getScheduleFile() {
        return fileManager.getScheduleFile();
    }
}
