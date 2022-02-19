package com.team2073.eagleforcescoutingapplication.framework.manager;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.InsightActivity;
import com.team2073.eagleforcescoutingapplication.activities.ScoutingFormActivity;
import com.team2073.eagleforcescoutingapplication.activities.SettingsActivity;
import com.team2073.eagleforcescoutingapplication.activities.ViewScheduleActivity;

public class DrawerManager {

    public static DrawerManager INSTANCE;
    private Activity activity;
    private Drawer drawer;

    public static DrawerManager getInstance(Activity activity) {
        if (INSTANCE == null) {
            return new DrawerManager(activity);
        }
        return INSTANCE;
    }

    public DrawerManager(Activity activity) {
        this.activity = activity;
    }


    /**
     * Creates the default material drawer for the application. Should be called in each activity
     */

    public void makeDrawer() {
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.accountheader)
                .addProfiles(
                        new ProfileDrawerItem().withName("EagleForce").withIcon(R.drawable.eagleforce_logo)
                )
                .build();

        PrimaryDrawerItem scoutingForm = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.drawerScoutingFormText);
        PrimaryDrawerItem schedule = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.drawerScheduleText);
        PrimaryDrawerItem insight = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.drawerInsightText);
        PrimaryDrawerItem settings = new PrimaryDrawerItem().withIdentifier(4).withName(R.string.drawerSettingsText);

        drawer = new DrawerBuilder()
                .withActivity(activity)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(headerResult)
                .withCloseOnClick(true)
                .withSelectedItem(-1)
                .addDrawerItems(scoutingForm, new DividerDrawerItem(),
                        schedule, new DividerDrawerItem(),
                        insight, new DividerDrawerItem(),
                        settings, new DividerDrawerItem())
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    if (scoutingForm.equals(drawerItem)) {
                        activity.startActivity(new Intent(activity, ScoutingFormActivity.class));
                    } else if (schedule.equals(drawerItem)) {
                        activity.startActivity(new Intent(activity, ViewScheduleActivity.class));
                    } else if (insight.equals(drawerItem)){
                        activity.startActivity(new Intent(activity, InsightActivity.class));
                    } else if (settings.equals(drawerItem)){
                        activity.startActivity(new Intent(activity, SettingsActivity.class));
                    }
                    return false;
                })
                .withSliderBackgroundColor(activity.getColor(R.color.white))
                .build();
    }

    public Drawer getDrawer() {
        return drawer;
    }
}
