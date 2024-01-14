package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.team2073.eagleforcescoutingapplication.R;

public class UIPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.ui_tab_text_1, R.string.ui_tab_text_2, R.string.ui_tab_text_3, R.string.ui_tab_text_4, R.string.ui_tab_text_5, R.string.ui_tab_text_6};
    private final Context mContext;

    public UIPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return UIInfoFragment.newInstance(position+1);
            case 1:
                return UIAutoFragment.newInstance(position+1);
            case 2:
                return UITeleopFragment.newInstance(position+1);
            case 3:
                return UIEndGameFragment.newInstance(position+1);
            case 4:
                return UIDetailFragment.newInstance(position+1);
            case 5:
                return UIQRCodeFragment.newInstance(position+1);
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
        return 6;
    }
}
