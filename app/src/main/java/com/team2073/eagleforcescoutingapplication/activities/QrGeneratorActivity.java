package com.team2073.eagleforcescoutingapplication.activities;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.framework.presenter.QrGeneratorPresenter;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;
import com.team2073.eagleforcescoutingapplication.framework.view.QrGeneratorView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;


public class QrGeneratorActivity extends BaseActivity implements QrGeneratorView {

    private QrGeneratorPresenter qrGeneratorPresenter;

    ImageView qrOutput;
    TextView matchAndTeam;
    Button finishedScan;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        qrGeneratorPresenter.makeDrawer(toolbar);

        qrOutput = findViewById(R.id.QROutput);
        matchAndTeam = findViewById(R.id.MatchAndTeam);
        finishedScan = findViewById(R.id.FinishScan);

        matchAndTeam.setText(qrGeneratorPresenter.fetchTeamAndMatch());

        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(qrGeneratorPresenter.fetchAllData(), BarcodeFormat.QR_CODE, 150, 150);
            BarcodeEncoder encoder  = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            qrOutput.setImageBitmap(bitmap);

            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(qrOutput.getApplicationWindowToken(), 0);
        } catch (WriterException e){
            e.printStackTrace();
        }

        finishedScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ScoutingFormActivity.class);
                qrGeneratorPresenter.advanceOnSubmit();
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