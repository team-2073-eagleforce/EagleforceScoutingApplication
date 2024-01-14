package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesAmpBinding;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesSpeakerMakeBinding;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesSpeakerMissBinding;
import com.team2073.eagleforcescoutingapplication.databinding.TransportDisplayLayoutBinding;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentTeleopBinding;
import com.team2073.eagleforcescoutingapplication.framework.form.ChargedUpScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.form.CrescendoScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.form.ScoutingForm;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import timber.log.Timber;

public class UITeleopFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "TeleOp";
    private final ScoutingForm scoutingForm = new CrescendoScoutingForm();
    private Context context;
    private ScoutingFormPresenter scoutingFormPresenter;
    private UiFragmentTeleopBinding fragmentTeleopBinding;
    private AddSubtractValuesAmpBinding teleopAmpBinding;
    private AddSubtractValuesSpeakerMakeBinding teleopSpeakerMakeBinding;
    private AddSubtractValuesSpeakerMissBinding teleopSpeakerMissBinding;

    public static UITeleopFragment newInstance(int index) {
        UITeleopFragment fragment = new UITeleopFragment();
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentTeleopBinding = UiFragmentTeleopBinding.inflate(inflater, container, false);
        teleopAmpBinding = fragmentTeleopBinding.teleopAmp;
        teleopSpeakerMakeBinding = fragmentTeleopBinding.teleopSpeakerMake;
        teleopSpeakerMissBinding = fragmentTeleopBinding.teleopSpeakerMiss;
        return fragmentTeleopBinding.getRoot();
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
        fragmentTeleopBinding = null;
    }

    private void initDataFields() {
        for (String teleopField: scoutingForm.getTeleFieldNames()) {
            scoutingFormPresenter.saveData(teleopField, "0");
        }
    }

    private void initTextFields() {
        teleopAmpBinding.formScore.setText("0");
        teleopSpeakerMakeBinding.formScore.setText("0");
        teleopSpeakerMissBinding.formScore.setText("0");
    }

    private void initViewImageButtons() {
        teleopAmpBinding.formAdd.setOnClickListener(teleopAmpAdd -> addTransportValue(teleopAmpBinding.formScore, "teleopAmp"));
        teleopAmpBinding.formSubtract.setOnClickListener(teleopAmpSubtract -> subtractTransportValue(teleopAmpBinding.formScore, "teleopAmp"));

        teleopSpeakerMakeBinding.formAdd.setOnClickListener(teleopSpeakerMakeAdd -> addTransportValue(teleopSpeakerMakeBinding.formScore, "teleopSpeakerMake"));
        teleopSpeakerMakeBinding.formSubtract.setOnClickListener(teleopSpeakerMakeSubtract -> subtractTransportValue(teleopSpeakerMakeBinding.formScore, "teleopSpeakerMake"));

        teleopSpeakerMissBinding.formAdd.setOnClickListener(teleopSpeakerMissAdd -> addTransportValue(teleopSpeakerMissBinding.formScore, "teleopSpeakerMiss"));
        teleopSpeakerMissBinding.formSubtract.setOnClickListener(teleopSpeakerMissSubtract -> subtractTransportValue(teleopSpeakerMissBinding.formScore, "teleopSpeakerMiss"));

        fragmentTeleopBinding.teleopTrap.trapOne.setOnClickListener(teleopTrapOne -> toggle_trap(fragmentTeleopBinding.teleopTrap.trapOne, "trapOne"));
        fragmentTeleopBinding.teleopTrap.trapTwo.setOnClickListener(teleopTrapTwo -> toggle_trap(fragmentTeleopBinding.teleopTrap.trapTwo, "trapTwo"));
        fragmentTeleopBinding.teleopTrap.trapThree.setOnClickListener(teleopTrapThree -> toggle_trap(fragmentTeleopBinding.teleopTrap.trapThree, "trapThree"));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            try {
                InputMethodManager mImm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mImm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            } catch (Exception e) {
                Timber.d("setUserVisibleHint: Teleop ");
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

    private void toggle_trap(ImageButton trapButtonTeleop, String trapNumber) {
        if (readData(trapNumber).equals("0")) {
            trapButtonTeleop.setImageResource(R.drawable.filled_trap_box);
//            trapButtonEndgame.setImageResource(R.drawable.filled_trap_box);
            saveData(trapNumber, "1");
        } else {
            trapButtonTeleop.setImageResource(R.drawable.empty_trap_box);
//            trapButtonEndgame.setImageResource(R.drawable.empty_trap_box);
            saveData(trapNumber, "0");
        }
        Timber.d("%s:%s", trapNumber, scoutingFormPresenter.readData(trapNumber));
    }

    public String readData(String key) {
        return scoutingFormPresenter.readData(key);
    }

    public void saveData(String key, String data) {
        scoutingFormPresenter.saveData(key, data);
    }

}

