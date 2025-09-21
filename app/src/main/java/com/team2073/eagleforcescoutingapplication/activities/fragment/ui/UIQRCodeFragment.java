package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.team2073.eagleforcescoutingapplication.framework.manager.ConfigurationManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.WriterException;
import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentQrcodeBinding;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;
import com.team2073.eagleforcescoutingapplication.util.Match;

import java.util.ArrayList;

import timber.log.Timber;

public class UIQRCodeFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "QRCode";
    private ScoutingFormPresenter scoutingFormPresenter;
    private UiFragmentQrcodeBinding fragmentQrcodeBinding;
    private ConfigurationManager configurationManager;


    public static UIQRCodeFragment newInstance(int index) {
        UIQRCodeFragment fragment = new UIQRCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PageViewModel pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = getArguments().getInt(ARG_SECTION_NUMBER);
        pageViewModel.setIndex(index);
        scoutingFormPresenter = new ScoutingFormPresenter(this.getActivity());
        configurationManager = ConfigurationManager.getInstance(this.getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentQrcodeBinding = UiFragmentQrcodeBinding.inflate(inflater, container, false);
        return fragmentQrcodeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        finishScan();
        setupImportQR();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentQrcodeBinding = null;
    }

    public void generateQRCode() {
        try {
            fragmentQrcodeBinding.QROutput.setImageBitmap(scoutingFormPresenter.createQR());
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    private void finishScan() {
        fragmentQrcodeBinding.FinishScan.setOnClickListener(finishScan -> {
            boolean nameFieldUnfilled = scoutingFormPresenter.readData("name").equals("") || scoutingFormPresenter.readData("name").equals("0");
            boolean matchFieldUnfilled = scoutingFormPresenter.readData("teamNumber").equals("") || scoutingFormPresenter.readData("teamNumber").equals("0");
            boolean teamFieldUnfilled = scoutingFormPresenter.readData("matchNumber").equals("") || scoutingFormPresenter.readData("matchNumber").equals("0");


            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
            if (!nameFieldUnfilled && !matchFieldUnfilled && !teamFieldUnfilled) {
                builder.setTitle("Confirm Submit?");
            } else {
                Toast.makeText(this.getActivity(),   "Make sure Name, Team, and Match are Filled", Toast.LENGTH_SHORT).show();
                builder.setTitle("Either Name, Team, or Match isn't filled. \nClear Anyway?");
                builder.setIcon(R.drawable.warning_icon);
            }
            builder.setPositiveButton("Yes", (dialog, which) -> {
                try {
                    scoutingFormPresenter.saveQR(scoutingFormPresenter.createQR());
                } catch (WriterException e) {
                    throw new RuntimeException(e);
                }
                Intent intent = getActivity().getIntent();
                getActivity().overridePendingTransition(0, 0);
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
                startActivity(intent);

                String matchNum = scoutingFormPresenter.readData("matchNumber");
                ArrayList <Match> scheduleList = scoutingFormPresenter.getScheduleList();
                String position = scoutingFormPresenter.getPosition();
                scoutingFormPresenter.advanceOnSubmit(matchNum, scheduleList, position);
            }).setNegativeButton("No", (dialog, which) -> {
            });
            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(buttons -> {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.primaryTextColor));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.primaryTextColor));
            });
            dialog.show();
        });
    }

    private void setupImportQR() {
        fragmentQrcodeBinding.ImportQR.setOnClickListener(importQR -> {
            IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            integrator.setPrompt("Scan QR Code to Import Data");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(true);
            integrator.setBarcodeImageEnabled(true);
            integrator.initiateScan();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "Scan cancelled", Toast.LENGTH_SHORT).show();
            } else {
                importQRData(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void importQRData(String qrContent) {
        try {
            JSONObject jsonData = new JSONObject(qrContent);
            
            // Check if this is configuration data or scouting data
            if (jsonData.has("config_type") && jsonData.getString("config_type").equals("app_settings")) {
                // Import configuration data
                configurationManager.importConfiguration(jsonData);
                Toast.makeText(getActivity(), "Configuration imported successfully!", Toast.LENGTH_LONG).show();
            } else {
                // Import scouting data
                Iterator<String> keys = jsonData.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String value = jsonData.getString(key);
                    scoutingFormPresenter.saveData(key, value);
                }
                Toast.makeText(getActivity(), "Scouting data imported successfully!", Toast.LENGTH_LONG).show();
            }
            
            // Refresh the current view to show imported data
            Intent intent = getActivity().getIntent();
            getActivity().finish();
            startActivity(intent);
            
        } catch (JSONException e) {
            Toast.makeText(getActivity(), "Invalid QR code format", Toast.LENGTH_SHORT).show();
            Timber.e("Error parsing QR data: %s", e.getMessage());
        }
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            try {
                InputMethodManager mImm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mImm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                generateQRCode(); // Generate and display QR code when fragment becomes visible
            } catch (Exception e) {
                Timber.d("setUserVisibleHint: QRCode ");
            }
        }
    }

}
