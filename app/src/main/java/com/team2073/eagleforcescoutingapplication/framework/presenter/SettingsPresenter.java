package com.team2073.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.FragmentManager;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.SettingsActivity;
import com.team2073.eagleforcescoutingapplication.framework.manager.DrawerManager;
import com.team2073.eagleforcescoutingapplication.framework.view.SettingsView;

public class SettingsPresenter extends BasePresenter<SettingsView> {
    private Activity mActivity;
    private DrawerManager drawerManager;

    public SettingsPresenter(Activity activity){
        this.mActivity = activity;
        drawerManager = DrawerManager.getInstance(activity);
    }

    public void makeSettings(FragmentManager fragmentManager, ActionBar actionBar){
        mActivity.setContentView(R.layout.activity_settings);
        fragmentManager
                .beginTransaction()
                .replace(R.id.settings , new SettingsActivity.SettingsFragment())
                .commit();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void makeDrawer(){
        drawerManager.makeDrawer();
    }
}
