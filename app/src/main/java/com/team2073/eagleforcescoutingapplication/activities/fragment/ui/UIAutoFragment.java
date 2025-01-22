package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.res.ColorStateList;
import android.graphics.Color;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesCoralBinding;

import com.team2073.eagleforcescoutingapplication.databinding.FieldLayoutBinding;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesNetBinding;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesProcessorBinding;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesRemovedBinding;
import com.team2073.eagleforcescoutingapplication.databinding.ReefLayoutBinding;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentAutoBinding;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import java.util.ArrayList;

import timber.log.Timber;

public class UIAutoFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "Auto";
    private ScoutingFormPresenter scoutingFormPresenter;
    private UiFragmentAutoBinding fragmentAutoBinding;
    private FieldLayoutBinding field;
    private ArrayList<String> autoPath = new ArrayList<String>();
    private AddSubtractValuesNetBinding autoNet;
    private AddSubtractValuesProcessorBinding autoProcessor;
    private AddSubtractValuesRemovedBinding autoRemoved;
    private RadioGroup reef;
    private String level;
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
        autoNet = fragmentAutoBinding.autoNet;
        autoRemoved = fragmentAutoBinding.autoRemoved;
        field = fragmentAutoBinding.autoField;
        reef = fragmentAutoBinding.autoReef;
        return fragmentAutoBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initTextFields();
        initViewImageButtons();
        selectLevel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentAutoBinding = null;
    }

    private void initTextFields() {
        Timber.d("Display Auto Fields");
        autoNet.formScore.setText(readData("autoNet"));
        autoRemoved.formScore.setText(readData("autoRemoved"));
    }

    private void initViewImageButtons() {
        autoNet.formAdd.setOnClickListener(autoNetAdd -> addTransportValue(autoNet.formScore, "autoNet"));
        autoRemoved.formAdd.setOnClickListener(autoRemovedAdd -> addTransportValue(autoRemoved.formScore, "autoRemoved"));
        autoNet.formSubtract.setOnClickListener(autoNetSubtract -> subtractTransportValue(autoNet.formScore, "autoNet"));
        autoRemoved.formSubtract.setOnClickListener(autoRemovedSubtract -> subtractTransportValue(autoRemoved.formScore, "autoRemoved"));
        ImageButton[] locations = {field.a, field.b, field.c, field.d, field.e, field.f, field.g, field.h, field.i, field.j, field.k, field.l};
        for (int i = 0; i < 12; i++) {
            String s = "" + (char) (i + 65);
            ImageButton button = locations[i];
            locations[i].setOnClickListener(pos -> togglePlace(button, s));
        }

        Button leave = fragmentAutoBinding.autoLeave;
        if (readData("autoLeave").equals("1")) { leave.setText("Leave"); leave.setBackgroundTintList(getColorStateList("#4BB543")); }
        else if (readData("autoLeave").equals("0")) { leave.setText("None"); leave.setBackgroundTintList(getColorStateList("#C1C1C1")); }
        fragmentAutoBinding.autoLeave.setOnClickListener(autoLeave -> toggleLeave());
    }

    private void toggleLeave() {
        Button leave = fragmentAutoBinding.autoLeave;
        if (readData("autoLeave").equals("0")) {
            saveData("autoLeave", "1");
            leave.setText("Leave");
            leave.setBackgroundTintList(getColorStateList("#4BB543"));
        } else if (readData("autoLeave").equals("1")) {
            saveData("autoLeave", "0");
            leave.setText("None");
            leave.setBackgroundTintList(getColorStateList("#C1C1C1"));
        }
    }

    private void selectLevel() {
        reef.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = reef.findViewById(checkedId);
                level = radioButton.getText().toString();
                Timber.d("level %s", level);
            }
        });
    }

    private void toggleOther(String fieldType) {

    }

    private void togglePlace(ImageButton button, String location) {

        ColorStateList bkgColor = button.getBackgroundTintList();
        ColorStateList black = getColorStateList("#000000");
        ColorStateList green = getColorStateList("#4BB543");
        if (bkgColor.equals(black)) {
            button.setBackgroundTintList(green);
            autoPath.add(location);
        } else if (bkgColor.equals(green)) {
            try {
                button.setBackgroundTintList(black);
                autoPath.remove(autoPath.indexOf(location));
            } catch(Exception e) {
                Timber.d(e.toString());
            }
        }
        String list = "";
        for (String loc: autoPath) {
            list += loc + ", ";
        }
        Timber.d(list);
    }

    private ColorStateList getColorStateList(String hexCode) {
        return ColorStateList.valueOf(Color.parseColor(hexCode));
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

