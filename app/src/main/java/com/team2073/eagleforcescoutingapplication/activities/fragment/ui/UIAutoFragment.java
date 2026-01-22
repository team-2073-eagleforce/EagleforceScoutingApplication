




package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.res.ColorStateList;
import android.graphics.Color;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.databinding.FieldLayoutBinding;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesNetBinding;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesRemovedBinding;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesProcessorBinding;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesSourceBinding;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentAutoBinding;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentTeleopBinding;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import java.util.ArrayList;
import timber.log.Timber;
import java.util.HashMap;

public class UIAutoFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "Auto";
    private ScoutingFormPresenter scoutingFormPresenter;
    private UiFragmentAutoBinding fragmentAutoBinding;
    private String autoMode = "autoScore";

    private boolean clickedMiddleClimb;
    private boolean clickedLeftClimb;
    private boolean clickedRightClimb;

    private int autoClimb = 0;

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
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentAutoBinding = UiFragmentAutoBinding.inflate(inflater, container, false);



        return fragmentAutoBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //initDataFields();
        initTextFields();
        initViewImageButtons();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentAutoBinding = null;
    }


    private void initTextFields() {
        fragmentAutoBinding.scoreCounterAuto.setText(readData("autoScore"));
        fragmentAutoBinding.passCounterAuto.setText(readData("autoPass"));
    }


    private void initViewImageButtons() {
        fragmentAutoBinding.largeMinusAuto.setOnClickListener(lm ->subtractTransportValue(20));
        fragmentAutoBinding.mediumMinusAuto.setOnClickListener(lm ->subtractTransportValue(10));
        fragmentAutoBinding.smallMinusAuto.setOnClickListener(lm ->subtractTransportValue(5));
        fragmentAutoBinding.oneMinusAuto.setOnClickListener(lm ->subtractTransportValue(1));
        fragmentAutoBinding.largePlusAuto.setOnClickListener(lm ->addTransportValue(20));
        fragmentAutoBinding.mediumPlusAuto.setOnClickListener(lm ->addTransportValue(10));
        fragmentAutoBinding.smallPlusAuto.setOnClickListener(lm ->addTransportValue(5));
        fragmentAutoBinding.onePlusAuto.setOnClickListener(lm ->addTransportValue(1));
        fragmentAutoBinding.passToggleAuto.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                autoMode = "autoPass";
            }
        });




        final boolean[] isClicked = {false};

        fragmentAutoBinding.passToggleAuto.setOnClickListener(v -> {
            if (!isClicked[0]) {
                autoMode = "autoPass";
                fragmentAutoBinding.passToggleAuto.setText("PASS");
                fragmentAutoBinding.passToggleAuto.setBackgroundColor(Color.rgb(150, 86, 224));
            } else {
                autoMode = "autoScore";
                fragmentAutoBinding.passToggleAuto.setText("SCORE");
                fragmentAutoBinding.passToggleAuto.setBackgroundColor(Color.rgb(86, 214, 120));
            }
            isClicked[0] = !isClicked[0];
        });

        fragmentAutoBinding.autoClimbLeft.setOnClickListener(v -> {
            if (!clickedLeftClimb) {
                notRightClimb();
                notMiddleClimb();
                scoutingFormPresenter.saveData("autoClimb", "1");
                clickedLeftClimb = true;
                fragmentAutoBinding.autoClimbLeft.setText("Left Climbed!");
                fragmentAutoBinding.autoClimbLeft.setBackgroundColor(Color.rgb(53, 203, 168));
            } else {
                scoutingFormPresenter.saveData("autoClimb", "0");
                notLeftClimb();
            }

        });
        fragmentAutoBinding.autoClimbMiddle.setOnClickListener(v -> {
            if (!clickedMiddleClimb) {
                notLeftClimb();
                notRightClimb();
                scoutingFormPresenter.saveData("autoClimb", "2");
                clickedMiddleClimb = true;
                fragmentAutoBinding.autoClimbMiddle.setText("Middle Climbed!");
                fragmentAutoBinding.autoClimbMiddle.setBackgroundColor(Color.rgb(53, 203, 168));
            } else {
                scoutingFormPresenter.saveData("autoClimb", "0");
                notMiddleClimb();
            }

        });
        fragmentAutoBinding.autoClimbRight.setOnClickListener(v -> {
            if (!clickedRightClimb) {
                notMiddleClimb();
                notLeftClimb();
                scoutingFormPresenter.saveData("autoClimb", "3");
                clickedRightClimb = true;
                fragmentAutoBinding.autoClimbRight.setText("Right Climbed!");
                fragmentAutoBinding.autoClimbRight.setBackgroundColor(Color.rgb(53, 203, 168));
            } else {
                scoutingFormPresenter.saveData("autoClimb", "0");
                notRightClimb();
            }


        });
    }


    private void addTransportValue(int amount) {
        int value = Integer.parseInt(readData(autoMode)) + amount;
        if (value >= 1000) {
            value = 999;
        }
        saveData(autoMode, String.valueOf(value));
        Timber.d("%s:%s", autoMode, scoutingFormPresenter.readData(autoMode));
        if (autoMode.equals("autoScore")) {
            fragmentAutoBinding.scoreCounterAuto.setText(String.valueOf(value));
            scoutingFormPresenter.saveData("autoScore", String.valueOf(value));
        } else {
            if (autoMode.equals("autoPass")) {
                fragmentAutoBinding.passCounterAuto.setText(String.valueOf(value));
                scoutingFormPresenter.saveData("autoPass", String.valueOf(value));

            }
        }
    }


    private void subtractTransportValue(int amount) {
        int value = Integer.parseInt(readData(autoMode)) - amount;
        if (value < 0) {
            value = 0;
        }
        saveData(autoMode, String.valueOf(value));
        Timber.d("%s:%s", autoMode, scoutingFormPresenter.readData(autoMode));
        if (autoMode.equals("autoScore")) {
            fragmentAutoBinding.scoreCounterAuto.setText(String.valueOf(value));
        } else {
            if (autoMode.equals("autoPass")) {
                fragmentAutoBinding.passCounterAuto.setText(String.valueOf(value));
            }
        }
    }

    private void notLeftClimb(){
        clickedLeftClimb = false;
        fragmentAutoBinding.autoClimbLeft.setText("Left CLimb?");
        fragmentAutoBinding.autoClimbLeft.setBackgroundColor(Color.rgb(142, 154, 175));
    }

    private void notMiddleClimb(){
        clickedMiddleClimb = false;
        fragmentAutoBinding.autoClimbMiddle.setText("Middle CLimb?");
        fragmentAutoBinding.autoClimbMiddle.setBackgroundColor(Color.rgb(142, 154, 175));
    }

    private void notRightClimb(){
        clickedRightClimb = false;
        fragmentAutoBinding.autoClimbRight.setText("Right CLimb?");
        fragmentAutoBinding.autoClimbRight.setBackgroundColor(Color.rgb(142, 154, 175));
    }

    public String readData(String key) {
        return scoutingFormPresenter.readData(key);
    }


    public void saveData(String key, String data) {
        scoutingFormPresenter.saveData(key, data);
    }
}