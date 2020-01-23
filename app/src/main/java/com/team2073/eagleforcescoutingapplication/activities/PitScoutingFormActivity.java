package com.team2073.eagleforcescoutingapplication.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.adapters.PitScoutingRecyclerViewAdapter;
import com.team2073.eagleforcescoutingapplication.framework.presenter.PitScoutingFormPresenter;
import com.team2073.eagleforcescoutingapplication.framework.view.ScoutingFormView;

import java.util.ArrayList;

import timber.log.Timber;

public class PitScoutingFormActivity extends BaseActivity implements ScoutingFormView, View.OnClickListener{

    private PitScoutingFormPresenter pitScoutingFormPresenter;

    private ArrayList<String> fieldNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pit_scouting_form);

        pitScoutingFormPresenter.makeDrawer();

        pitScoutingFormPresenter.clearPreferences();

        initFieldNames();
        initRecyclerView();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_pit_scouting_form;
    }

    @Override
    protected void initEvent() {
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void bindView() {
        pitScoutingFormPresenter = new PitScoutingFormPresenter(this);
        pitScoutingFormPresenter.bindView(this);
    }

    private void initFieldNames() {
        fieldNames = pitScoutingFormPresenter.getPitScoutingForm().getFieldNames();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.pitScoutingRecyclerView);
        PitScoutingRecyclerViewAdapter pitScoutingRecyclerViewAdapter = new PitScoutingRecyclerViewAdapter(fieldNames, this, this);
        recyclerView.setAdapter(pitScoutingRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pitScoutingSubmit:
                pitScoutingFormPresenter.createCSV();

                BluetoothSend bluetoothSend = new BluetoothSend(pitScoutingFormPresenter);
                bluetoothSend.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

                Timber.d("Pit Scouting Form Submitted");

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        pitScoutingFormPresenter.clearPreferences();
        startActivity(new Intent(this, PitScoutingFormActivity.class));
    }

    private static class BluetoothSend extends AsyncTask<Void, Void, Void> {

        private PitScoutingFormPresenter pitScoutingFormPresenter;

        public BluetoothSend(PitScoutingFormPresenter pitScoutingFormPresenter){
            this.pitScoutingFormPresenter = pitScoutingFormPresenter;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            pitScoutingFormPresenter.sendOverBluetooth();
            return null;
        }
    }
}
