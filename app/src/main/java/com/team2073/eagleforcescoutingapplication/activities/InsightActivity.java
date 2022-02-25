package com.team2073.eagleforcescoutingapplication.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.adapters.ScheduleRecyclerViewAdapter;
import com.team2073.eagleforcescoutingapplication.framework.manager.PrefsDataManager;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ChooseFilePresenter;
import com.team2073.eagleforcescoutingapplication.framework.presenter.InsightPresenter;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ViewSchedulePresenter;
import com.team2073.eagleforcescoutingapplication.framework.view.InsightView;
import com.team2073.eagleforcescoutingapplication.framework.view.ViewScheduleView;
import com.team2073.eagleforcescoutingapplication.util.Match;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

public class InsightActivity extends BaseActivity implements InsightView {

    private InsightPresenter insightPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        insightPresenter.makeDrawer(toolbar);

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_insight;
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void bindView() {
        insightPresenter = new InsightPresenter(this);
        insightPresenter.bindView(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}