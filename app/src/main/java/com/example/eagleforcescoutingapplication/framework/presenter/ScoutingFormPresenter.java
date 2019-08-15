package com.example.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.example.eagleforcescoutingapplication.R;
import com.example.eagleforcescoutingapplication.activities.ScoutingFormActivity;
import com.example.eagleforcescoutingapplication.activities.SettingsActivity;
import com.example.eagleforcescoutingapplication.framework.manager.CSVManager;
import com.example.eagleforcescoutingapplication.framework.view.ScoutingFormView;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ScoutingFormPresenter extends BasePresenter<ScoutingFormView> {
    private Activity mActivity;
    private CSVManager csvManager;

    private Map<String, String> formMap = new HashMap<>();

    public ScoutingFormPresenter(Activity activity){
        this.mActivity = activity;
        csvManager = CSVManager.getInstance(mActivity);
    }

    public void makeDrawer(){
        PrimaryDrawerItem settings = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.drawerSettingsText);
        new DrawerBuilder()
                .withActivity(mActivity)
                .addDrawerItems(settings, new DividerDrawerItem())
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(@Nullable View view, int i, @NotNull IDrawerItem<?> iDrawerItem) {
                        mActivity.startActivity(new Intent(mActivity, SettingsActivity.class));
                        return false;
                    }
                })
                .build();

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
