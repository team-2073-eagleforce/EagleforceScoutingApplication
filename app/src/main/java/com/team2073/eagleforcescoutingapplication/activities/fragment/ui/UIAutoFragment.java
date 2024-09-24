package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesAmpBinding;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesSpeakerMakeBinding;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesSpeakerMissBinding;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentAutoBinding;
import com.team2073.eagleforcescoutingapplication.framework.form.ChargedUpScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.form.CrescendoScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.form.ScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import timber.log.Timber;

public class UIAutoFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "Auto";
    private final ScoutingForm scoutingForm = new CrescendoScoutingForm();
    private ScoutingFormPresenter scoutingFormPresenter;
    private UiFragmentAutoBinding fragmentAutoBinding;
    private AddSubtractValuesAmpBinding autoAmpBinding;
    private AddSubtractValuesSpeakerMakeBinding autoSpeakerMakeBinding;
    private AddSubtractValuesSpeakerMissBinding autoSpeakerMissBinding;

    public static UIAutoFragment newInstance(int index) {
        UIAutoFragment fragment = new UIAutoFragment();
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
        scoutingFormPresenter.checkReadPermissions();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentAutoBinding = UiFragmentAutoBinding.inflate(inflater, container, false);
        autoAmpBinding = fragmentAutoBinding.autoAmp;
        autoSpeakerMakeBinding = fragmentAutoBinding.autoSpeakerMake;
        autoSpeakerMissBinding = fragmentAutoBinding.autoSpeakerMiss;
        return fragmentAutoBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initDataFields();
        initTextFields();
        initViewImageButtons();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentAutoBinding = null;
    }

    private void initDataFields() {
        boolean matchFieldEmpty = scoutingFormPresenter.readData("teamNumber").equals("") || scoutingFormPresenter.readData("teamNumber").equals("0");
        boolean teamFieldEmpty = scoutingFormPresenter.readData("matchNumber").equals("") || scoutingFormPresenter.readData("matchNumber").equals("0");
        if (matchFieldEmpty && teamFieldEmpty){
            Timber.d("initAutoFields 0 ");
            for (String autoField : scoutingForm.getAutoFieldNames()) {
                scoutingFormPresenter.saveData(autoField, "0");
            }
        }
    }

    private void initTextFields() {
        Timber.d("Display Auto Fields");
        autoAmpBinding.formScore.setText(readData("autoAmp"));
        autoSpeakerMakeBinding.formScore.setText(readData("autoSpeakerMake"));
        autoSpeakerMissBinding.formScore.setText(readData("autoSpeakerMiss"));
    }

    private void initViewImageButtons() {
        if (readData("autoLeave").equals("1")) {
            fragmentAutoBinding.autoLeave.setImageResource(R.drawable.auto_leave);
        }
        // Toggles Auto Leave
        fragmentAutoBinding.autoLeave.setOnClickListener(autoLeave -> {
            if (readData("autoLeave").equals("0")) {
                fragmentAutoBinding.autoLeave.setImageResource(R.drawable.auto_leave);
                saveData("autoLeave", "1");
            } else {
                fragmentAutoBinding.autoLeave.setImageResource(R.drawable.auto_none);
                saveData("autoLeave", "0");
            }
        });

        // Calls add and subtract values for Amp, SpeakerMake, and SpeakerMiss respectively
        autoAmpBinding.formAdd.setOnClickListener(autoAmpAdd -> addTransportValue(autoAmpBinding.formScore, "autoAmp"));
        autoAmpBinding.formSubtract.setOnClickListener(autoAmpSubtract -> subtractTransportValue(autoAmpBinding.formScore, "autoAmp"));

        autoSpeakerMakeBinding.formAdd.setOnClickListener(autoSpeakerMakeAdd -> addTransportValue(autoSpeakerMakeBinding.formScore, "autoSpeakerMake"));
        autoSpeakerMakeBinding.formSubtract.setOnClickListener(autoSpeakerMakeSubtract -> subtractTransportValue(autoSpeakerMakeBinding.formScore, "autoSpeakerMake"));

        autoSpeakerMissBinding.formAdd.setOnClickListener(autoSpeakerMissAdd -> addTransportValue(autoSpeakerMissBinding.formScore, "autoSpeakerMiss"));
        autoSpeakerMissBinding.formSubtract.setOnClickListener(autoSpeakerMissSubtract -> subtractTransportValue(autoSpeakerMissBinding.formScore, "autoSpeakerMiss"));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            try {
                InputMethodManager mImm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mImm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            } catch (Exception e) {
                Timber.d("setUserVisibleHint: Auto ");
            }
        }
    }

    private void addTransportValue(TextView formScore, String transportType) {
        int value = Integer.parseInt(readData(transportType)) + 1;
        if (value >= 100) {
            value = 99;
        }
        saveData(transportType, String.valueOf(value));
        formScore.setText(String.valueOf(value));
        Timber.d("%s:%s", transportType, scoutingFormPresenter.readData(transportType));
    }

    private void subtractTransportValue(TextView formScore, String transportType) {
        int value = Integer.parseInt(readData(transportType)) - 1;
        if (value < 0) {
            value = 0;
        }
        saveData(transportType, String.valueOf(value));
        formScore.setText(String.valueOf(value));
        Timber.d("%s:%s", transportType, scoutingFormPresenter.readData(transportType));
    }

    public String readData(String key) {
        return scoutingFormPresenter.readData(key);
    }

    public void saveData(String key, String data) {
        scoutingFormPresenter.saveData(key, data);
    }

}

