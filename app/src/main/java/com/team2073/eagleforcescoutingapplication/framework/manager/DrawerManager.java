package com.team2073.eagleforcescoutingapplication.framework.manager;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.ScoutingFormActivity;
import com.team2073.eagleforcescoutingapplication.activities.SettingsActivity;
import com.team2073.eagleforcescoutingapplication.activities.ViewScheduleActivity;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DrawerManager {
    private static final String TAG = "DrawerManager";
    public static DrawerManager INSTANCE;
    private Activity activity;

    public static DrawerManager getInstance(Activity activity) {
        if (INSTANCE == null) {
            return new DrawerManager(activity);
        }
        return INSTANCE;
    }

    public DrawerManager(Activity activity) {
        this.activity = activity;
    }

    public void makeDrawer() {
        PrimaryDrawerItem scoutingForm = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.drawerScoutingFormText);
        PrimaryDrawerItem schedule = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.drawerScheduleText);
        PrimaryDrawerItem settings = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.drawerSettingsText);

        new DrawerBuilder()
                .withActivity(activity)
                .addDrawerItems(scoutingForm, new DividerDrawerItem())
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    activity.startActivity(new Intent(activity, ScoutingFormActivity.class));
                    return false;
                })
                .addDrawerItems(schedule, new DividerDrawerItem())
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    activity.startActivity(new Intent(activity, ViewScheduleActivity.class));
                    return false;
                })
                .addDrawerItems(settings, new DividerDrawerItem())
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    activity.startActivity(new Intent(activity, SettingsActivity.class));
                    return false;
                })
                .build();
    }
}
