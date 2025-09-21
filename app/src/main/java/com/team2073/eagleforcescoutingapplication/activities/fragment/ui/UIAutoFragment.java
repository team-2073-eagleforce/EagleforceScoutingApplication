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
import com.team2073.eagleforcescoutingapplication.util.PathDrawingView;

import java.util.ArrayList;
import timber.log.Timber;
import java.util.HashMap;
import android.graphics.PointF;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.team2073.eagleforcescoutingapplication.util.FieldConfig;
import android.content.Intent;

public class UIAutoFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "Auto";
    private ScoutingFormPresenter scoutingFormPresenter;
    private UiFragmentAutoBinding fragmentAutoBinding;
    private ArrayList<String> autoPath = new ArrayList<String>();

    private HashMap<ImageButton, String> reefLoc;
    private String level;
    private HashMap<String, HashMap<ImageButton, ColorStateList>> levelButtons  = new HashMap<>();
    private final ColorStateList cyan = getColorStateList("#009688"); private final ColorStateList brown = getColorStateList("#a77b7b"); private final ColorStateList black = getColorStateList("#000000"); private final ColorStateList green = getColorStateList("#4BB543"); private final ColorStateList red = getColorStateList("#cf0404");
    private PathDrawingView pathDrawingView;
    private ImageView fieldBackground;
    private LinearLayout actionBar;
    private float pendingActionX, pendingActionY;
    private String pendingSnapTarget;
    private FieldConfig fieldConfig;

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
        // Removed old UI element bindings
        level = "4";
        
        // Setup field background and drawing canvas
        fieldBackground = fragmentAutoBinding.fieldBackground;
        pathDrawingView = (PathDrawingView) fragmentAutoBinding.drawingCanvas;
        actionBar = fragmentAutoBinding.actionBar;
        fieldConfig = new FieldConfig(getContext());
        
        return fragmentAutoBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (readData("field_side").equals("0")) flipLayout();
        initTextFields();
        initFieldViews();
        initClickListeners();
        selectLevel();
        setupPathDrawing();
    }

    public void flipLayout() {
        // Layout flipping no longer needed with simplified UI
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentAutoBinding = null;
    }

    private void initTextFields() {
        // Removed old counter UI elements
    }

    public void initFieldViews() {
        FieldLayoutBinding field = fragmentAutoBinding.autoField;
        try {
            reefLoc = new HashMap<ImageButton, String>();
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
            }
            levelButtons.put("4", new HashMap<ImageButton, ColorStateList>());
            levelButtons.put("3", new HashMap<ImageButton, ColorStateList>());
            levelButtons.put("2", new HashMap<ImageButton, ColorStateList>());
            levelButtons.put("1", new HashMap<ImageButton, ColorStateList>());
        } catch (Exception e) {
            Timber.d("Error with initFieldViews %s", e.toString());
        }
    }
    private void initClickListeners() {
        fragmentAutoBinding.autoLeave.setOnClickListener(autoLeave -> toggleLeave());
        
        // Action bar listeners
        fragmentAutoBinding.actionPickupCoral.setOnClickListener(v -> selectAction("pickup_coral"));
        fragmentAutoBinding.actionPickupAlgae.setOnClickListener(v -> selectAction("pickup_algae"));
        fragmentAutoBinding.actionScoreCoral.setOnClickListener(v -> selectAction("score_coral"));
        fragmentAutoBinding.actionScoreAlgae.setOnClickListener(v -> selectAction("score_algae"));
        fragmentAutoBinding.actionRemove.setOnClickListener(v -> selectAction("remove"));
        fragmentAutoBinding.actionCancel.setOnClickListener(v -> cancelAction());
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
        } catch (Exception e) {
            Timber.d("Error with toggle place reef: %s", e.toString());
        }
        Timber.d(autoPath.toString());
    }

    private ColorStateList getColorStateList(String hexCode) { return ColorStateList.valueOf(Color.parseColor(hexCode)); }

    // Removed old transport value methods

    private void setupPathDrawing() {
        // Set correct field background
        if (readData("field_side").equals("0")) {
            fieldBackground.setImageResource(R.drawable.field_blue_side);
        } else {
            fieldBackground.setImageResource(R.drawable.field_red_side);
        }
        
        // Setup path drawing listener
        pathDrawingView.setPathDrawingListener(new PathDrawingView.PathDrawingListener() {
            @Override
            public void onWaypointCreated(float x, float y, String snapTarget) {
                // Show dynamic action bar based on zone
                pendingActionX = x;
                pendingActionY = y;
                pendingSnapTarget = snapTarget;
                setupDynamicActionBar(snapTarget);
                actionBar.setVisibility(View.VISIBLE);
            }
            
            @Override
            public void onActionBarRequested(float x, float y, String snapTarget) {
                // Show dynamic action bar for existing waypoint
                pendingActionX = x;
                pendingActionY = y;
                pendingSnapTarget = snapTarget;
                setupDynamicActionBar(snapTarget);
                actionBar.setVisibility(View.VISIBLE);
            }
            
            @Override
            public void onPathContinued() {
                // Hide action bar when continuing path
                actionBar.setVisibility(View.GONE);
            }
        });
    }
    
    private void selectAction(String actionType) {
        // Add action point to path
        pathDrawingView.addActionPoint(pendingActionX, pendingActionY, actionType);
        
        // Save to path data
        String pathData = readData("autoDrawnPath");
        pathData += String.format("%.1f,%.1f,%s,%s|", pendingActionX, pendingActionY, actionType, pendingSnapTarget);
        saveData("autoDrawnPath", pathData);
        
        // Hide action bar
        actionBar.setVisibility(View.GONE);
        updatePathDisplay();
    }
    
    private void cancelAction() {
        pathDrawingView.cancelAction();
        actionBar.setVisibility(View.GONE);
    }
    
    private void updatePathDisplay() {
        String pathData = readData("autoDrawnPath");
        if (!pathData.isEmpty()) {
            String[] points = pathData.split("\\|");
            fragmentAutoBinding.pathInfo.setText("Actions: " + points.length);
        }
    }
    
    private void setupDynamicActionBar(String snapTarget) {
        actionBar.removeAllViews();
        
        String[] actions = fieldConfig.getActionsForZone(snapTarget);
        for (String action : actions) {
            Button actionBtn = new Button(getContext());
            actionBtn.setText(action.replace("_", " "));
            actionBtn.setTextSize(12f);
            actionBtn.setOnClickListener(v -> selectAction(action));
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, 100);
            params.setMargins(5, 0, 5, 0);
            actionBtn.setLayoutParams(params);
            
            actionBar.addView(actionBtn);
        }
        
        // Always add cancel button
        Button cancelBtn = new Button(getContext());
        cancelBtn.setText("Cancel");
        cancelBtn.setTextSize(12f);
        cancelBtn.setBackgroundTintList(getColorStateList("#FF5722"));
        cancelBtn.setOnClickListener(v -> cancelAction());
        actionBar.addView(cancelBtn);
    }
    
    public void openFieldEditor() {
        Intent intent = new Intent(getActivity(), com.team2073.eagleforcescoutingapplication.activities.FieldEditorActivity.class);
        intent.putExtra("field_side", readData("field_side"));
        startActivity(intent);
    }
    
    public String readData(String key) { return scoutingFormPresenter.readData(key); }
    public void saveData(String key, String data) { scoutingFormPresenter.saveData(key, data);}
}