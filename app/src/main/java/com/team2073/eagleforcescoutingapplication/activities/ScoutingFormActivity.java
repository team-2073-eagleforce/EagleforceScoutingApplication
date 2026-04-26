package com.team2073.eagleforcescoutingapplication.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.ui.UIQRCodeFragment;
import com.team2073.eagleforcescoutingapplication.databinding.ActivityScoutingFormBinding;
import com.team2073.eagleforcescoutingapplication.framework.manager.ReplayServerManager;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;
import com.team2073.eagleforcescoutingapplication.framework.view.ScoutingFormView;
import com.team2073.eagleforcescoutingapplication.util.NonSwipeableViewPager;

public class ScoutingFormActivity extends BaseActivity implements ScoutingFormView {

    private ScoutingFormPresenter scoutingFormPresenter;
    private ActivityScoutingFormBinding activityScoutingFormBinding;
    private NonSwipeableViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityScoutingFormBinding = ActivityScoutingFormBinding.inflate(getLayoutInflater());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        scoutingFormPresenter.makeDrawer(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh lock state in case settings changed while paused
        if (viewPager != null) {
            viewPager.setLockInfoToAuto(
                    ReplayServerManager.getInstance(this).isRemoteStartEnabled());
        }
    }

    /** Programmatically navigate to the Auto tab (position 1). */
    public void navigateToAutoTab() {
        if (viewPager != null) {
            viewPager.setCurrentItem(1, true);
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_scouting_form;
    }

    @Override
    protected void initEvent() {
        scoutingFormPresenter.createTabs();

        viewPager = findViewById(R.id.view_pager);
        viewPager.setLockInfoToAuto(
                ReplayServerManager.getInstance(this).isRemoteStartEnabled());

        viewPager.addOnPageChangeListener(new androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                Fragment fragmentInstance = getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + viewPager.getCurrentItem());
                if (fragmentInstance instanceof UIQRCodeFragment) {
                    ((UIQRCodeFragment) fragmentInstance).generateQRCode();
                }
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