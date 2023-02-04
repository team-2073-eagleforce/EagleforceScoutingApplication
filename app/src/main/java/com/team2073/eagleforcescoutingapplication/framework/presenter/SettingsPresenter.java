package com.team2073.eagleforcescoutingapplication.framework.presenter;

import android.app.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.SettingsActivity;
import com.team2073.eagleforcescoutingapplication.framework.manager.DrawerManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.PrefsDataManager;
import com.team2073.eagleforcescoutingapplication.framework.view.SettingsView;

public class SettingsPresenter extends BasePresenter<SettingsView> {
    private final Activity mActivity;
    private final DrawerManager drawerManager;
    private final PrefsDataManager prefsDataManager;

    public SettingsPresenter(Activity activity) {
        this.mActivity = activity;
        drawerManager = DrawerManager.getInstance(activity);
        prefsDataManager = PrefsDataManager.getInstance(activity);
    }

    public void makeSettings(FragmentManager fragmentManager, ActionBar actionBar) {
        mActivity.setContentView(R.layout.activity_settings);
        fragmentManager
                .beginTransaction()
                .replace(R.id.settings, new SettingsActivity.SettingsFragment(mActivity))
                .commit();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void writeToPreferences(String key, String value) {
        prefsDataManager.writeToPreferences(key, value);
    }

    public void makeDrawer(Toolbar toolbar) {
        drawerManager.makeDrawer(toolbar);
    }

}
