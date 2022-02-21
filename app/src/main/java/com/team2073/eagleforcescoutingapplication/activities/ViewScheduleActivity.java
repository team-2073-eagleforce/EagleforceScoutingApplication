package com.team2073.eagleforcescoutingapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.adapters.ScheduleRecyclerViewAdapter;
import com.team2073.eagleforcescoutingapplication.framework.manager.PrefsDataManager;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ChooseFilePresenter;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ViewSchedulePresenter;
import com.team2073.eagleforcescoutingapplication.framework.view.ViewScheduleView;
import com.team2073.eagleforcescoutingapplication.util.Match;

import java.util.List;

/**
 * An activity is basically a window for you to place your User Interface.
 * This is for holding the UI of the match schedule.
 */
public class ViewScheduleActivity extends BaseActivity implements ViewScheduleView {

    private ViewSchedulePresenter viewSchedulePresenter;

    private RecyclerView scheduleRecyclerView;
    private ScheduleRecyclerViewAdapter adapter;
    private List<Match> matchList;
    private PrefsDataManager prefsDataManager;
    private ChooseFilePresenter chooseFilePresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        viewSchedulePresenter.makeDrawer(toolbar);
        prefsDataManager = PrefsDataManager.getInstance(this);
        chooseFilePresenter = new ChooseFilePresenter(this);

        if(viewSchedulePresenter.getScheduleFile() == null) {
            Toast.makeText(this, "Select a Schedule File", Toast.LENGTH_SHORT).show();
            chooseFilePresenter.chooseFile(ViewScheduleActivity.class);
            return;
        }

        //Initialize RecyclerView related variables
        matchList = viewSchedulePresenter.getAllTeamsPerMatch();
        scheduleRecyclerView = this.findViewById(R.id.schedule_recycler_view);
        adapter = new ScheduleRecyclerViewAdapter(getBaseContext(), matchList, prefsDataManager, this);
        scheduleRecyclerView.setAdapter(adapter);
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        matchList = viewSchedulePresenter.getAllTeamsPerMatch();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_view_schedule;
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void bindView() {
        viewSchedulePresenter = new ViewSchedulePresenter(this);
        viewSchedulePresenter.bindView(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        chooseFilePresenter.saveScheduleFile(requestCode, resultCode, data);
    }
}
