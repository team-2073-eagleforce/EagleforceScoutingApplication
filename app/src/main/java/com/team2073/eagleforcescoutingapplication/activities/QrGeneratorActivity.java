package com.team2073.eagleforcescoutingapplication.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.zxing.WriterException;
import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.framework.presenter.QrGeneratorPresenter;
import com.team2073.eagleforcescoutingapplication.framework.view.QrGeneratorView;


public class QrGeneratorActivity extends BaseActivity implements QrGeneratorView {

    ImageView qrOutput;
    TextView matchAndTeam;
    Button finishedScan;
    private QrGeneratorPresenter qrGeneratorPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        qrGeneratorPresenter.makeDrawer(toolbar);

        qrOutput = findViewById(R.id.QROutput);
        matchAndTeam = findViewById(R.id.MatchAndTeam);
        finishedScan = findViewById(R.id.FinishScan);

        matchAndTeam.setText(qrGeneratorPresenter.fetchTeamAndMatch());

        try {
            qrOutput.setImageBitmap(qrGeneratorPresenter.createQR());
        } catch (WriterException writerException) {
            writerException.printStackTrace();
        }

        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(qrOutput.getApplicationWindowToken(), 0);

        finishedScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrGeneratorPresenter.advanceOnSubmit();
                qrGeneratorPresenter.saveQRCode(qrOutput);

                Intent intent = new Intent(getApplicationContext(), ScoutingFormActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_qr_generator;
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void bindView() {
        qrGeneratorPresenter = new QrGeneratorPresenter(this);
        qrGeneratorPresenter.bindView(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        startActivity(new Intent(this, QrGeneratorActivity.class));
        finish();
    }

}