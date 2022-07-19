package com.team2073.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import com.team2073.eagleforcescoutingapplication.framework.manager.DrawerManager;
import com.team2073.eagleforcescoutingapplication.framework.view.QrGeneratorView;

import java.util.ArrayList;
import java.util.Arrays;

public class QrGeneratorPresenter extends BasePresenter<QrGeneratorView> {
    private Activity mActivity;
    private final ScoutingFormPresenter scoutingFormPresenter = new ScoutingFormPresenter(null);
    private DrawerManager drawerManager;

    private final ArrayList<String> allData = scoutingFormPresenter.qrGenerator();

    public QrGeneratorPresenter(Activity activity) {
        this.mActivity = activity;
        drawerManager = DrawerManager.getInstance(mActivity);
    }

    public void makeDrawer(Toolbar toolbar) {
        drawerManager.makeDrawer(toolbar);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String fetchAllData(){
        return String.join(", ", allData);
    }

    public String fetchTeamAndMatch(){
            return "Team: " + allData.get(0) + "; Match: " + allData.get(1);
    }
}
