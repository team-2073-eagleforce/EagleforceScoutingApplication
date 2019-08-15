package com.example.eagleforcescoutingapplication.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.example.eagleforcescoutingapplication.R;
import com.example.eagleforcescoutingapplication.activities.fragment.SectionsPagerAdapter;
import com.example.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;
import com.example.eagleforcescoutingapplication.framework.view.ScoutingFormView;
import com.google.android.material.tabs.TabLayout;

public class ScoutingFormActivity extends BaseActivity implements ScoutingFormView {
    private final String LOG = "ScoutingFormActivity";
    private ScoutingFormPresenter scoutingFormPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scoutingFormPresenter.makeDrawer();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_scouting_form;
    }

    @Override
    protected void initEvent() {
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void bindView() {
        scoutingFormPresenter = new ScoutingFormPresenter(this);
        scoutingFormPresenter.bindView(this);
    }

    @Override
    public void onSubmitSuccessful() {
        Toast.makeText(this, "Failed to Submit", Toast.LENGTH_SHORT).show();;
    }

    @Override
    public void onSubmitFailed(String errorMessage) {
        Toast.makeText(this, "Submit Success", Toast.LENGTH_SHORT).show();
    }
}