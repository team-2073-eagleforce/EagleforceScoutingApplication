package com.team2073.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;

import androidx.appcompat.widget.Toolbar;

import com.team2073.eagleforcescoutingapplication.util.Match;
import com.team2073.eagleforcescoutingapplication.framework.manager.CSVManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.DrawerManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.FileManager;
import com.team2073.eagleforcescoutingapplication.framework.view.ViewScheduleView;

import java.io.File;
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

    public void makeDrawer(Toolbar toolbar) {
        drawerManager.makeDrawer(toolbar);
    }

    public List getAllTeamsPerMatch(){
        scheduleList = csvManager.readScheduleFile(fileManager.getScheduleFile());
        return scheduleList;
    }

    public File getScheduleFile() {
        return fileManager.getScheduleFile();
    }
}
