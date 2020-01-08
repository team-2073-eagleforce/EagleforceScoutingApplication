package com.team2073.eagleforcescoutingapplication.activities.fragment.list;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.team2073.eagleforcescoutingapplication.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class RecyclerPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.recycler_tab_text_1, R.string.recycler_tab_text_2, R.string.recycler_tab_text_3, R.string.recycler_tab_text_4, R.string.recycler_tab_text_5};
    private final Context mContext;

    public RecyclerPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: {
                return RecyclerStartFragment.newInstance(position+1);
            } case 1: {
                return RecyclerAutoFragment.newInstance(position+1);
            } case 2: {
                return RecyclerTeleOpFragment.newInstance(position+1);
            }case 3: {
                return RecyclerDetailFragment.newInstance(position+1);
            }case 4: {
                return RecyclerSubmitFragment.newInstance(position+1);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 5;
    }
}