package com.team2073.eagleforcescoutingapplication.framework;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;

import com.team2073.eagleforcescoutingapplication.activities.fragment.RecyclerPagerAdapter;
import com.team2073.eagleforcescoutingapplication.activities.fragment.UIPagerAdapter;

public class PagerAdapterFactory {

    private RecyclerPagerAdapter recyclerPagerAdapter;
    private UIPagerAdapter uiPagerAdapter;

    public FragmentPagerAdapter getAdapter(String pagerAdapter, FragmentActivity activity) {
        switch (pagerAdapter) {
            case "List":
                if(recyclerPagerAdapter == null) {
                    recyclerPagerAdapter = new RecyclerPagerAdapter(activity, activity.getSupportFragmentManager());
                }
                return recyclerPagerAdapter;
            case "Icons":
                if(uiPagerAdapter == null) {
                    uiPagerAdapter = new UIPagerAdapter(activity, activity.getSupportFragmentManager());
                }
                return uiPagerAdapter;
        }
        return null;
    }
}
