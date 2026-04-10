package com.team2073.eagleforcescoutingapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.adapters.ScheduleRecyclerViewAdapter;
import com.team2073.eagleforcescoutingapplication.framework.manager.FileManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.PrefsDataManager;
import com.team2073.eagleforcescoutingapplication.framework.manager.ReplayServerManager;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ChooseFilePresenter;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ViewSchedulePresenter;
import com.team2073.eagleforcescoutingapplication.framework.view.ViewScheduleView;
import com.team2073.eagleforcescoutingapplication.util.Match;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import timber.log.Timber;

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
    private FileManager fileManager;
    private ReplayServerManager replayServerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        viewSchedulePresenter.makeDrawer(toolbar);
        prefsDataManager = PrefsDataManager.getInstance(this);
        chooseFilePresenter = new ChooseFilePresenter(this);
        fileManager = FileManager.getInstance(this);
        replayServerManager = ReplayServerManager.getInstance(this);

        Button removeSchedule = findViewById(R.id.remove_schedule);
        removeSchedule.setOnClickListener(v -> fileManager.setScheduleFile(null));

        Button scanQr = findViewById(R.id.scan_schedule_qr);
        scanQr.setOnClickListener(v -> launchScheduleQrScanner());

        Button retryDownload = findViewById(R.id.retry_download_schedule);
        retryDownload.setOnClickListener(v -> retryScheduleDownload());

        loadScheduleIfAvailable();
    }

    private void launchScheduleQrScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt(getString(R.string.scan_schedule_qr_prompt));
        integrator.setBeepEnabled(false);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    private void loadScheduleIfAvailable() {
        if (viewSchedulePresenter.getScheduleFile() == null) {
            Toast.makeText(this, "Select or scan a Schedule", Toast.LENGTH_SHORT).show();
            return;
        }
        matchList = viewSchedulePresenter.getAllTeamsPerMatch();
        scheduleRecyclerView = this.findViewById(R.id.schedule_recycler_view);
        adapter = new ScheduleRecyclerViewAdapter(getBaseContext(), matchList, prefsDataManager, this);
        scheduleRecyclerView.setAdapter(adapter);
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
    }

    private void retryScheduleDownload() {
        String url = replayServerManager.buildScheduleUrl();
        if (url == null) {
            Toast.makeText(this, "No server config yet. Scan a Schedule QR first.", Toast.LENGTH_LONG).show();
            return;
        }
        downloadScheduleFromServer();
    }

    private void handleScheduleQrResult(String qrContent) {
        try {
            JSONObject json = new JSONObject(qrContent);
            replayServerManager.saveScheduleQrData(json);

            String compCode = replayServerManager.getCompCode();
            String year = replayServerManager.getYear();

            if (compCode.isEmpty() || year.isEmpty()) {
                Toast.makeText(this,
                        getString(R.string.schedule_qr_incomplete_config),
                        Toast.LENGTH_LONG).show();
                return;
            }

            downloadScheduleFromServer();
        } catch (JSONException e) {
            Toast.makeText(this, getString(R.string.schedule_qr_parse_error), Toast.LENGTH_LONG).show();
            Timber.e(e, "Failed to parse schedule QR JSON");
        }
    }

    private void downloadScheduleFromServer() {
        Toast.makeText(this, getString(R.string.schedule_downloading), Toast.LENGTH_SHORT).show();

        replayServerManager.downloadSchedule(new ReplayServerManager.ScheduleDownloadCallback() {
            @Override
            public void onSuccess(File csvFile) {
                fileManager.setScheduleFile(csvFile);
                Toast.makeText(ViewScheduleActivity.this,
                        getString(R.string.schedule_downloaded_ok), Toast.LENGTH_SHORT).show();
                loadScheduleIfAvailable();
            }

            @Override
            public void onError(int httpCode, String message) {
                String msg;
                switch (httpCode) {
                    case 403:
                        msg = getString(R.string.schedule_error_403);
                        break;
                    case 404:
                        msg = getString(R.string.schedule_error_404);
                        break;
                    default:
                        msg = getString(R.string.schedule_error_generic, httpCode);
                }
                Toast.makeText(ViewScheduleActivity.this, msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNetworkError(String message) {
                Toast.makeText(ViewScheduleActivity.this,
                        getString(R.string.schedule_error_network),
                        Toast.LENGTH_LONG).show();
                Timber.e("Schedule network error: %s", message);
            }
        });
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
        // Handle ZXing QR scan result
        IntentResult qrResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (qrResult != null) {
            if (qrResult.getContents() != null) {
                handleScheduleQrResult(qrResult.getContents());
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
        chooseFilePresenter.saveScheduleFile(requestCode, resultCode, data);
    }
}
