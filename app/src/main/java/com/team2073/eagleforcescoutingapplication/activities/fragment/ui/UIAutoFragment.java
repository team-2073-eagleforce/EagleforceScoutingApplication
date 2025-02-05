package com.team2073.eagleforcescoutingapplication.activities.fragment.ui;

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
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.ColorStateList;
import android.graphics.Color;
import com.team2073.eagleforcescoutingapplication.activities.fragment.PageViewModel;

import com.team2073.eagleforcescoutingapplication.adapters.AutoPathRecyclerViewAdapter;
import com.team2073.eagleforcescoutingapplication.databinding.FieldLayoutBinding;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesNetBinding;
import com.team2073.eagleforcescoutingapplication.databinding.AddSubtractValuesRemovedBinding;
import com.team2073.eagleforcescoutingapplication.databinding.UiFragmentAutoBinding;
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import java.util.ArrayList;

import timber.log.Timber;
import java.util.HashMap;

public class UIAutoFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "Auto";
    private ScoutingFormPresenter scoutingFormPresenter;
    private UiFragmentAutoBinding fragmentAutoBinding;

    private ArrayList<String> autoPath;
    private AddSubtractValuesNetBinding autoNet;
    private AddSubtractValuesRemovedBinding autoRemoved;
    private HashMap<ImageButton, String> reefLoc;
    private HashMap<Button, String> otherLoc;
    private String level;
    private HashMap<String, HashMap<ImageButton, ColorStateList>> levelButtons  = new HashMap<>();
    private final ColorStateList cyan = getColorStateList("#009688");
    private final ColorStateList brown = getColorStateList("#a77b7b");
    private final ColorStateList black = getColorStateList("#000000");
    private final ColorStateList green = getColorStateList("#4BB543");
    private final ColorStateList red = getColorStateList("#cf0404");

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
        autoPath = new ArrayList<String>();
        level = "4";
        return fragmentAutoBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initTextFields();
        initFieldViews();
        initClickListeners();
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

    public void initFieldViews() {
        FieldLayoutBinding field = fragmentAutoBinding.autoField; ;
        try {
            reefLoc = new HashMap<ImageButton, String>();
            reefLoc.put(field.a, "A");
            reefLoc.put(field.b, "B");
            reefLoc.put(field.c, "C");
            reefLoc.put(field.d, "D");
            reefLoc.put(field.e, "E");
            reefLoc.put(field.f, "F");
            reefLoc.put(field.g, "G");
            reefLoc.put(field.h, "H");
            reefLoc.put(field.i, "I");
            reefLoc.put(field.j, "J");
            reefLoc.put(field.k, "K");
            reefLoc.put(field.l, "L");
            otherLoc = new HashMap<Button, String>();
            otherLoc.put(field.groundA, "groundA");
            otherLoc.put(field.groundB, "groundB");
            otherLoc.put(field.groundC, "groundC");
            otherLoc.put(field.sourceA, "sourceA");
            otherLoc.put(field.sourceB, "sourceB");
            otherLoc.put(field.processor, "processor");
            levelButtons.put("4", new HashMap<ImageButton, ColorStateList>());
            levelButtons.put("3", new HashMap<ImageButton, ColorStateList>());
            levelButtons.put("2", new HashMap<ImageButton, ColorStateList>());
            levelButtons.put("1", new HashMap<ImageButton, ColorStateList>());
        } catch (Exception e) {
            Timber.d("Error with initFieldViews %s", e.toString());
        }
    }
    private void initClickListeners() {
        autoNet.formAdd.setOnClickListener(autoNetAdd -> addTransportValue(autoNet.formScore, "autoNet"));
        autoRemoved.formAdd.setOnClickListener(autoRemovedAdd -> addTransportValue(autoRemoved.formScore, "autoRemoved"));
        autoNet.formSubtract.setOnClickListener(autoNetSubtract -> subtractTransportValue(autoNet.formScore, "autoNet"));
        autoRemoved.formSubtract.setOnClickListener(autoRemovedSubtract -> subtractTransportValue(autoRemoved.formScore, "autoRemoved"));
        try {
            for (ImageButton b : reefLoc.keySet()) {
                b.setOnClickListener(pos -> togglePlace(b, reefLoc.get(b)));
            }
            for (Button b : otherLoc.keySet()) {
                b.setOnClickListener(button -> togglePlace(b, otherLoc.get(b)));
            }
            fragmentAutoBinding.autoLeave.setOnClickListener(autoLeave -> toggleLeave());
        } catch (Exception e) {
            Timber.d("Error with click Listeners %s", e.toString());
        }
    }

    public void initLevel() {
        try {
            for (ImageButton b : reefLoc.keySet()) {
                b.setBackgroundTintList(black);
            }
            for (ImageButton b : levelButtons.get(level).keySet()) {
                b.setBackgroundTintList(levelButtons.get(level).get(b));
            }
        } catch (Exception e) {
            Timber.d("Error with initLevel: %s", e.toString());
        }

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
        initLevel();
        fragmentAutoBinding.autoReef.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = fragmentAutoBinding.autoReef.findViewById(checkedId);
                level = radioButton.getText().toString();
                Timber.d("level %s", level);
                initLevel();
            }
        });
    }

    private void togglePlace(Button button, String location) {
        ColorStateList bkgColor = button.getBackgroundTintList();
        try{
            Timber.d(autoPath.toString());
            if (location.indexOf("ground") > -1) {
                    if (bkgColor.equals(brown)) {
                        button.setBackgroundTintList(cyan);
                        autoPath.remove(autoPath.indexOf(location));
                        button.setText("");
                    } else {
                        button.setBackgroundTintList(brown);
                        autoPath.add(location);
                        button.setText("X");
                    }
            } else {
                autoPath.add(location);
                String s = button.getText().toString();
                int n = 0;
                try {
                    n = Integer.parseInt(s.substring(s.length() - 2));
                    button.setText(s.substring(0, s.length() - 2) + (n+1));
                } catch (Exception e) {
                    n = Integer.parseInt(s.substring(s.length() - 1));
                    button.setText(s.substring(0, s.length() - 1) + (n+1));
                }

                if (location.indexOf("processor") > -1){
                    saveData("autoProcessor", String.valueOf(n+1));
                }
            }
            saveAutoPath();
        } catch (Exception e) {
            Timber.d("Error with toggle place other %s", e.toString());
        }
    }

    private void togglePlace(ImageButton button, String location) {
        try {
            location = level + location;
            ColorStateList bkgColor = button.getBackgroundTintList();
            int n = Integer.valueOf(readData("autoL" + level));

            if (bkgColor.equals(black)) {
                button.setBackgroundTintList(green);
                autoPath.add(location);
                saveData("autoL" + level, String.valueOf(n+1));
                levelButtons.get(level).put(button,green);
            } else if (bkgColor.equals(green)) {
                button.setBackgroundTintList(red);
                saveData("autoL" + level, String.valueOf(n-2));
                levelButtons.get(level).put(button,red);
            } else if (bkgColor.equals(red)) {
                button.setBackgroundTintList(black);
                autoPath.remove(autoPath.indexOf(location));
                levelButtons.get(level).remove(button);
            }
            saveAutoPath();
        } catch (Exception e) {
            Timber.d("Error with toggle place reef: %s", e.toString());
        }
    }
    private void saveAutoPath() {
        String list = "";
        for (String loc: autoPath) {
            list += loc + ", ";
        }
        saveData("autoPath", list.substring(0,list.length()-2));
    }
    private ColorStateList getColorStateList(String hexCode) { return ColorStateList.valueOf(Color.parseColor(hexCode)); }

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

    public String readData(String key) { return scoutingFormPresenter.readData(key); }
    public void saveData(String key, String data) { scoutingFormPresenter.saveData(key, data);}
}

