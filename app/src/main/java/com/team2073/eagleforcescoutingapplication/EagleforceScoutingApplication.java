package com.team2073.eagleforcescoutingapplication;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.ArrayList;

import timber.log.Timber;

public class EagleforceScoutingApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private ArrayList<Activity> listActivity;

    private static EagleforceScoutingApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        listActivity = new ArrayList<>();
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        listActivity.add(activity);
    }

    public static synchronized EagleforceScoutingApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        listActivity.remove(activity);
    }

    public ArrayList<Activity> getListActivity() {
        return listActivity;
    }
}
