package com.team2073.eagleforcescoutingapplication.activities;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.ui.UIQRCodeFragment;
import com.team2073.eagleforcescoutingapplication.databinding.ActivityScoutingFormBinding;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;
import com.team2073.eagleforcescoutingapplication.framework.view.ScoutingFormView;

public class ScoutingFormActivity extends BaseActivity implements ScoutingFormView {

    private ScoutingFormPresenter scoutingFormPresenter;
    private ActivityScoutingFormBinding activityScoutingFormBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityScoutingFormBinding = ActivityScoutingFormBinding.inflate(getLayoutInflater());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        scoutingFormPresenter.makeDrawer(toolbar);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_scouting_form;
    }

    @Override
    protected void initEvent() {
        scoutingFormPresenter.createTabs();

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Fragment fragmentInstance = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + viewPager.getCurrentItem());
                System.out.println(fragmentInstance);
                if (fragmentInstance instanceof UIQRCodeFragment) {
                    ((UIQRCodeFragment) fragmentInstance).generateQRCode();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void bindView() {
        scoutingFormPresenter = new ScoutingFormPresenter(this);
        scoutingFormPresenter.bindView(this);
    }
}