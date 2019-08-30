package com.example.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;

import com.example.eagleforcescoutingapplication.framework.manager.CSVManager;
import com.example.eagleforcescoutingapplication.framework.manager.DrawerManager;
import com.example.eagleforcescoutingapplication.framework.view.ScoutingFormView;

import java.util.HashMap;
import java.util.Map;

public class ScoutingFormPresenter extends BasePresenter<ScoutingFormView> {
    private Activity mActivity;
    private CSVManager csvManager;
    private DrawerManager drawerManager;

    private String matchNumber;
    private String position;
    private String teamNumber;
    private String name;

    private Map<String, String> formMap = new HashMap<>();

    public ScoutingFormPresenter(Activity activity){
        this.mActivity = activity;
        csvManager = CSVManager.getInstance(mActivity);
        drawerManager = DrawerManager.getInstance(mActivity);
    }

    public void makeDrawer(){
        drawerManager.makeDrawer();
    }

    public void saveData(String key, String data){
        formMap.put(key, data);
    }

    public String readData(String key){
        return formMap.get(key);
    }

    public void writeCSV(){
        csvManager.writeData();
    }
}
