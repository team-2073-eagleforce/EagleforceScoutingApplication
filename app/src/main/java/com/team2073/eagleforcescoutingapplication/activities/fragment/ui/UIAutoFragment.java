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
import com.team2073.eagleforcescoutingapplication.framework.presenter.ScoutingFormPresenter;

import java.util.ArrayList;
import timber.log.Timber;
import java.util.HashMap;

public class UIAutoFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "Auto";
    private ScoutingFormPresenter scoutingFormPresenter;
    private UiFragmentAutoBinding fragmentAutoBinding;
    private AddSubtractValuesSourceBinding sourceA;
    private AddSubtractValuesSourceBinding sourceB;
    private ArrayList<String> autoPath = new ArrayList<String>();
    private AddSubtractValuesNetBinding autoNet;
    private AddSubtractValuesRemovedBinding autoRemoved;
    private AddSubtractValuesProcessorBinding autoProcessor;
    private HashMap<Button, String> otherLoc;
    private HashMap<ImageButton, String> reefLoc;
    private String level;
    private HashMap<String, HashMap<ImageButton, ColorStateList>> levelButtons  = new HashMap<>();
    private final ColorStateList cyan = getColorStateList("#009688"); private final ColorStateList brown = getColorStateList("#a77b7b"); private final ColorStateList black = getColorStateList("#000000"); private final ColorStateList green = getColorStateList("#4BB543"); private final ColorStateList red = getColorStateList("#cf0404");

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
        sourceA = fragmentAutoBinding.sourceA;
        sourceB = fragmentAutoBinding.sourceB;
        autoNet = fragmentAutoBinding.autoNet;
        autoRemoved = fragmentAutoBinding.autoRemoved;
        autoProcessor = fragmentAutoBinding.autoProcessor;
        level = "4";
        return fragmentAutoBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (readData("field_side").equals("0")) flipLayout();
        initTextFields();
        initFieldViews();
        initClickListeners();
        selectLevel();
    }

    public void flipLayout() {
        RelativeLayout.LayoutParams sourceParams = (RelativeLayout.LayoutParams) fragmentAutoBinding.source.getLayoutParams();
        RelativeLayout.LayoutParams aAddParams = (RelativeLayout.LayoutParams) fragmentAutoBinding.sourceA.formAdd.getLayoutParams();
        RelativeLayout.LayoutParams aScoreParams = (RelativeLayout.LayoutParams) fragmentAutoBinding.sourceA.formScore.getLayoutParams();
        RelativeLayout.LayoutParams aSubtractParams = (RelativeLayout.LayoutParams) fragmentAutoBinding.sourceA.formSubtract.getLayoutParams();
        RelativeLayout.LayoutParams bAddParams = (RelativeLayout.LayoutParams) fragmentAutoBinding.sourceB.formAdd.getLayoutParams();
        RelativeLayout.LayoutParams bScoreParams = (RelativeLayout.LayoutParams) fragmentAutoBinding.sourceB.formScore.getLayoutParams();
        RelativeLayout.LayoutParams bSubtractParams = (RelativeLayout.LayoutParams) fragmentAutoBinding.sourceB.formSubtract.getLayoutParams();
        RelativeLayout.LayoutParams groundParams = (RelativeLayout.LayoutParams) fragmentAutoBinding.groundPickup.getRoot().getLayoutParams();
        RelativeLayout.LayoutParams fieldParams = (RelativeLayout.LayoutParams) fragmentAutoBinding.autoField.getRoot().getLayoutParams();
        RelativeLayout.LayoutParams[] layoutParams = {sourceParams, aAddParams, aScoreParams, aSubtractParams, bAddParams,bScoreParams,bSubtractParams};
        for (RelativeLayout.LayoutParams params : layoutParams) {
            params.removeRule(RelativeLayout.END_OF);
        }
        aScoreParams.addRule(RelativeLayout.END_OF, R.id.formSubtract);
        aAddParams.addRule(RelativeLayout.END_OF, R.id.formScore);
        bScoreParams.addRule(RelativeLayout.END_OF, R.id.formSubtract);
        bAddParams.addRule(RelativeLayout.END_OF, R.id.formScore);
        fieldParams.addRule(RelativeLayout.END_OF, R.id.auto_algae);
        groundParams.addRule(RelativeLayout.END_OF, R.id.auto_field);
        sourceParams.addRule(RelativeLayout.END_OF, R.id.groundPickup);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentAutoBinding = null;
    }

    private void initTextFields() {
        ImageButton[] views = {autoNet.formAdd, autoNet.formSubtract, autoProcessor.formAdd, autoProcessor.formSubtract, autoRemoved.formAdd, autoRemoved.formSubtract};
        for (ImageButton view : views) {
            view.getLayoutParams().width = 100;
            view.getLayoutParams().height = 100;
        }
        autoNet.formScore.setText(readData("autoNet"));
        autoRemoved.formScore.setText(readData("autoRemoved"));
        autoProcessor.formScore.setText(readData("autoProcessor"));
        sourceA.formScore.setText(readData("sourceA"));
        sourceB.formScore.setText(readData("sourceB"));
    }

    public void initFieldViews() {
        FieldLayoutBinding field = fragmentAutoBinding.autoField;
        try {
            reefLoc = new HashMap<ImageButton, String>();
            otherLoc = new HashMap<Button, String>();
            if (readData("field_side").equals("0")) {
                reefLoc.put(field.a, "G");
                reefLoc.put(field.b, "H");
                reefLoc.put(field.c, "I");
                reefLoc.put(field.d, "J");
                reefLoc.put(field.e, "K");
                reefLoc.put(field.f, "L");
                reefLoc.put(field.g, "A");
                reefLoc.put(field.h, "B");
                reefLoc.put(field.i, "C");
                reefLoc.put(field.j, "D");
                reefLoc.put(field.k, "E");
                reefLoc.put(field.l, "F");
                otherLoc.put(fragmentAutoBinding.groundPickup.groundA, "groundC");
                otherLoc.put(fragmentAutoBinding.groundPickup.groundC, "groundA");
            } else {
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
                otherLoc.put(fragmentAutoBinding.groundPickup.groundA, "groundA");
                otherLoc.put(fragmentAutoBinding.groundPickup.groundC, "groundC");
            }
            otherLoc.put(fragmentAutoBinding.groundPickup.groundB, "groundB");
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
        autoNet.formSubtract.setOnClickListener(autoNetSubtract -> subtractTransportValue(autoNet.formScore, "autoNet"));
        autoRemoved.formAdd.setOnClickListener(autoRemovedAdd -> addTransportValue(autoRemoved.formScore, "autoRemoved"));
        autoRemoved.formSubtract.setOnClickListener(autoRemovedSubtract -> subtractTransportValue(autoRemoved.formScore, "autoRemoved"));
        autoProcessor.formAdd.setOnClickListener(autoProcessorAdd -> addTransportValue(autoProcessor.formScore, "autoProcessor"));
        autoProcessor.formSubtract.setOnClickListener(autoProcessorSubtract -> subtractTransportValue(autoProcessor.formScore, "autoProcessor"));
        sourceA.formAdd.setOnClickListener(autoProcessorAdd -> addTransportValue(sourceA.formScore, "sourceA"));
        sourceA.formSubtract.setOnClickListener(autoProcessorSubtract -> subtractTransportValue(sourceA.formScore, "sourceA"));
        sourceB.formAdd.setOnClickListener(autoProcessorAdd -> addTransportValue(sourceB.formScore, "sourceB"));
        sourceB.formSubtract.setOnClickListener(autoProcessorSubtract -> subtractTransportValue(sourceB.formScore, "sourceB"));
        for (ImageButton b : reefLoc.keySet()) {
            b.setOnClickListener(pos -> togglePlace(b, reefLoc.get(b)));
        }
        for (Button b : otherLoc.keySet()) {
            b.setOnClickListener(button -> togglePlace(b, otherLoc.get(b)));
        }
        fragmentAutoBinding.autoLeave.setOnClickListener(autoLeave -> toggleLeave());
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
            if (location.contains("ground")) {
                    if (bkgColor.equals(brown)) {
                        button.setBackgroundTintList(cyan);
                        autoPath.remove(location);
                        button.setText("");
                    } else {
                        button.setBackgroundTintList(brown);
                        autoPath.add(location);
                        button.setText("X");
                    }
            }
            fragmentAutoBinding.list.setText(saveAutoPath());
        } catch (Exception e) {
            Timber.d("Error with toggle place other %s", e.toString());
        }
        Timber.d(autoPath.toString());
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
                saveData("autoL" + level, String.valueOf(n-1));
                saveData("missed_auto", String.valueOf(Integer.valueOf(readData("missed_auto")) + 1));
                levelButtons.get(level).put(button,red);
            } else if (bkgColor.equals(red)) {
                button.setBackgroundTintList(black);
                saveData("missed_auto", String.valueOf(Integer.valueOf(readData("missed_auto")) -1));
                autoPath.remove(location);
                levelButtons.get(level).remove(button);
            }
            fragmentAutoBinding.list.setText(saveAutoPath());
        } catch (Exception e) {
            Timber.d("Error with toggle place reef: %s", e.toString());
        }
        Timber.d(autoPath.toString());
    }
    private String saveAutoPath(){
        if (autoPath.size() == 0) {
            saveData("autoPath", "");
            return "";
        }
        String list = "";
        String text = "";
        for (int i = 0; i < autoPath.size(); i++) {
            String item = autoPath.get(i);
            list +=  item + ", ";
            if (autoPath.size() <= 7 || i > autoPath.size()-7) {
                if (i == autoPath.size() - 6) {
                    text = "...\n";
                }
                text += (i + 1) + ": " + item + "\n";
            }
        }
        saveData("autoPath", list.substring(0, list.length()-2));
        return text.substring(0, text.length() - 1);
    }
    private ColorStateList getColorStateList(String hexCode) { return ColorStateList.valueOf(Color.parseColor(hexCode)); }

    private void addTransportValue(TextView formScore, String transportType) {
        if (transportType.equals("autoProcessor")) {
            autoPath.add("processor");
            fragmentAutoBinding.list.setText(saveAutoPath());
        }
        if (transportType.equals("sourceA") || transportType.equals("sourceB")) {
            autoPath.add(transportType);
            fragmentAutoBinding.list.setText(saveAutoPath());
        }
        int value = Integer.parseInt(readData(transportType)) + 1;
        if (value >= 100) {
            value = 99;
        }
        saveData(transportType, String.valueOf(value));
        formScore.setText(String.valueOf(value));
        Timber.d("%s:%s", transportType, scoutingFormPresenter.readData(transportType));
    }

    private void subtractTransportValue(TextView formScore, String transportType) {
        if (transportType.equals("autoProcessor") &&  autoPath.contains("processor")) {
            autoPath.remove(autoPath.lastIndexOf("processor"));
            fragmentAutoBinding.list.setText(saveAutoPath());
        }
        if ((transportType.equals("sourceA") && autoPath.contains("sourceA")) || (transportType.equals("sourceB") &&  autoPath.contains("sourceB"))) {
            autoPath.remove(autoPath.lastIndexOf(transportType));
            fragmentAutoBinding.list.setText(saveAutoPath());
        }
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