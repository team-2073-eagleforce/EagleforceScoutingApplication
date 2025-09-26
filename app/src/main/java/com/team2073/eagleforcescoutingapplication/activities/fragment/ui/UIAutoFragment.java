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
import java.util.List;
import timber.log.Timber;
import java.util.HashMap;
import android.graphics.PointF;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import androidx.viewpager.widget.ViewPager;
import com.team2073.eagleforcescoutingapplication.util.FieldConfig;
import com.team2073.eagleforcescoutingapplication.util.AutoPathManager;
import com.team2073.eagleforcescoutingapplication.util.ZoneConfigParser;
import com.team2073.eagleforcescoutingapplication.util.ZoneDrawingView;
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
    private ZoneDrawingView autoZoneView;
    private PathDrawingView pathDrawingView;
    private ImageView fieldBackground;
    private LinearLayout actionBar;
    private float pendingActionX, pendingActionY;
    private String pendingSnapTarget;
    private FieldConfig fieldConfig;
    private long autoStartTime;
    private boolean autoActive = false;
    private ViewPager parentViewPager;
    private String currentConfigName = null;
    private Button undoBtn, redoBtn, unlockTabsBtn;
    private TextView noConfigOverlay;

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
        autoZoneView = fragmentAutoBinding.drawingCanvas;
        pathDrawingView = fragmentAutoBinding.pathDrawingCanvas;
        actionBar = fragmentAutoBinding.actionBar;
        undoBtn = fragmentAutoBinding.btnUndo;
        redoBtn = fragmentAutoBinding.btnRedo;
        unlockTabsBtn = fragmentAutoBinding.btnUnlockTabs;
        noConfigOverlay = fragmentAutoBinding.noConfigOverlay;
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
        setupSimplePathDrawing();
        
        // Find parent ViewPager
        parentViewPager = getActivity().findViewById(R.id.view_pager);
        
        // Timer will start when fragment becomes visible
    }

    public void flipLayout() {
        // Layout flipping no longer needed with simplified UI
    }



    private void initTextFields() {
        // Removed old counter UI elements
    }

    public void initFieldViews() {
        // Simplified - no longer using field layout
    }
    private void initClickListeners() {
        fragmentAutoBinding.autoLeave.setOnClickListener(autoLeave -> toggleLeave());
        
        // Field editor button
        fragmentAutoBinding.btnFieldEditor.setOnClickListener(v -> openFieldEditor());
        
        // Add zone alignment button (long press field editor for alignment)
        fragmentAutoBinding.btnFieldEditor.setOnLongClickListener(v -> {
            showZoneAlignmentDialog();
            return true;
        });
        
        // Undo/Redo buttons
        undoBtn.setOnClickListener(v -> undoLastAction());
        redoBtn.setOnClickListener(v -> redoLastAction());
        
        // Unlock tabs button
        unlockTabsBtn.setOnClickListener(v -> unlockTabs());
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

    private void setupSimplePathDrawing() {
        // Set correct field background based on position setting
        String position = readData("position");
        if (position.toLowerCase().startsWith("blue")) {
            fieldBackground.setImageResource(R.drawable.field_blue_side);
            saveData("field_side", "0");
        } else if (position.toLowerCase().startsWith("red")) {
            fieldBackground.setImageResource(R.drawable.field_red_side);
            saveData("field_side", "1");
        } else {
            // Default to red if no position set
            fieldBackground.setImageResource(R.drawable.field_red_side);
            saveData("field_side", "1");
        }
        
        autoStartTime = System.currentTimeMillis();
        autoActive = true;
        
        // Lock ViewPager during auto period
        if (parentViewPager != null) {
            try {
                java.lang.reflect.Method method = parentViewPager.getClass().getMethod("setUserInputEnabled", boolean.class);
                method.invoke(parentViewPager, false);
            } catch (Exception e) {
                // Fallback for older ViewPager
                parentViewPager.setOnTouchListener((v, event) -> true);
            }
        }
        
        // Also lock during drawing
        if (pathDrawingView != null) {
            pathDrawingView.setOnTouchListener((v, event) -> {
                // Consume touch events to prevent ViewPager swiping
                if (parentViewPager != null) {
                    parentViewPager.requestDisallowInterceptTouchEvent(true);
                }
                return false; // Let PathDrawingView handle the touch
            });
        }
        
        // Setup zone drawing for auto mode - zones visible but not editable
        if (autoZoneView != null) {
            autoZoneView.setEditEnabled(false);
            autoZoneView.setDrawingMode(false);
        }
        
        // Setup path drawing for auto mode
        if (pathDrawingView != null) {
            pathDrawingView.setDrawingEnabled(true);
            
            // Set canvas boundaries to match field image
            fieldBackground.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    fieldBackground.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    setPathDrawingBoundaries();
                }
            });
            
            pathDrawingView.setPathDrawingListener(new PathDrawingView.PathDrawingListener() {
                @Override
                public void onPathPoint(PointF point, ZoneDrawingView.Zone snappedZone) {
                    // Record path point
                }
                
                @Override
                public void onPathCompleted(List<PointF> path) {
                    // Path drawing completed
                    updateActionHistory("Path drawn", "Field");
                }
                
                @Override
                public void onZoneSnapped(ZoneDrawingView.Zone zone, PointF point) {
                    pendingActionX = point.x;
                    pendingActionY = point.y;
                    pendingSnapTarget = zone.name;
                    
                    if ("Reef".equals(zone.type)) {
                        setupReefActionBar(zone.name);
                    } else {
                        setupDynamicActionBar(zone.name);
                    }
                    actionBar.setVisibility(View.VISIBLE);
                }
            });
        }
    }
    
    private void setupPathDrawing() {
        // Set correct field background
        if (readData("field_side").equals("0")) {
            fieldBackground.setImageResource(R.drawable.field_blue_side);
        } else {
            fieldBackground.setImageResource(R.drawable.field_red_side);
        }
        
        // Load field editor configuration
        loadFieldEditorConfig();
        
        // Update field boundaries for PathDrawingView after layout
        fieldBackground.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                fieldBackground.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                updatePathDrawingBoundaries();
            }
        });
        
        // Disable scrolling to allow drawing
        ScrollView scrollView = getView().findViewById(R.id.scrollView);
        if (scrollView != null) {
            scrollView.setOnTouchListener((v, event) -> {
                // Check if touch is within field container
                View fieldContainer = getView().findViewById(R.id.field_container);
                if (fieldContainer != null) {
                    int[] location = new int[2];
                    fieldContainer.getLocationOnScreen(location);
                    float touchX = event.getRawX();
                    float touchY = event.getRawY();
                    
                    if (touchX >= location[0] && touchX <= location[0] + fieldContainer.getWidth() &&
                        touchY >= location[1] && touchY <= location[1] + fieldContainer.getHeight()) {
                        // Touch is within field - disable scrolling
                        return false;
                    }
                }
                // Allow normal scrolling outside field
                return false;
            });
        }
        
        // PathDrawingView functionality not available with ZoneDrawingView
        
        // This method is kept for compatibility but not used
    }
    
    private void updatePathDrawingBoundaries() {
        // PathDrawingView functionality not available with ZoneDrawingView
    }
    
    private void selectAction(String actionType) {
        // Record action with timestamp
        updateActionHistory(actionType, pendingSnapTarget);
        updatePathDisplay();
    }
    
    private void cancelAction() {
        actionBar.setVisibility(View.GONE);
    }
    
    private void updatePathDisplay() {
        LinearLayout historyContainer = fragmentAutoBinding.actionHistory;
        int actionCount = historyContainer.getChildCount();
        // Path info removed from layout, just update history count internally
    }
    
    private void updateActionHistory(String actionType, String zoneName) {
        LinearLayout historyContainer = fragmentAutoBinding.actionHistory;
        
        TextView actionText = new TextView(getContext());
        long elapsed = System.currentTimeMillis() - autoStartTime;
        String timeStr = String.format("%.1fs", elapsed / 1000.0);
        actionText.setText(timeStr + ": " + actionType.replace("_", " ") + " @ " + zoneName);
        actionText.setTextColor(0xFF000000);
        actionText.setTextSize(10f);
        actionText.setPadding(2, 2, 2, 2);
        
        historyContainer.addView(actionText, 0); // Add to top
        
        // Limit history to 10 items
        while (historyContainer.getChildCount() > 10) {
            historyContainer.removeViewAt(historyContainer.getChildCount() - 1);
        }
    }
    
    private void setupDynamicActionBar(String snapTarget) {
        actionBar.removeAllViews();
        
        // Add title
        TextView titleText = new TextView(getContext());
        titleText.setText("Actions");
        titleText.setTextColor(0xFF000000);
        titleText.setTextSize(14f);
        titleText.setTypeface(null, android.graphics.Typeface.BOLD);
        titleText.setPadding(0, 0, 0, 8);
        actionBar.addView(titleText);
        
        // Find the actual zone and get its actions
        ZoneDrawingView.Zone targetZone = null;
        if (autoZoneView != null) {
            for (ZoneDrawingView.Zone zone : autoZoneView.getZones()) {
                if (snapTarget.equals(zone.name)) {
                    targetZone = zone;
                    break;
                }
            }
        }
        
        if (targetZone != null && !targetZone.actions.isEmpty()) {
            // Use zone-specific actions
            for (String action : targetZone.actions) {
                android.widget.CheckBox actionCheck = new android.widget.CheckBox(getContext());
                actionCheck.setText(action.replace("_", " "));
                actionCheck.setTextColor(0xFF000000);
                actionCheck.setTextSize(12f);
                actionCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        selectAction(action);
                        actionBar.setVisibility(View.GONE);
                    }
                });
                
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(5, 2, 5, 2);
                actionCheck.setLayoutParams(params);
                
                actionBar.addView(actionCheck);
            }
        } else {
            // Fallback to default actions
            String[] actions = fieldConfig.getActionsForZone(snapTarget);
            for (String action : actions) {
                android.widget.CheckBox actionCheck = new android.widget.CheckBox(getContext());
                actionCheck.setText(action.replace("_", " "));
                actionCheck.setTextColor(0xFF000000);
                actionCheck.setTextSize(12f);
                actionCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        selectAction(action);
                        actionBar.setVisibility(View.GONE);
                    }
                });
                
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(5, 2, 5, 2);
                actionCheck.setLayoutParams(params);
                
                actionBar.addView(actionCheck);
            }
        }
        
        // Always add cancel button
        Button cancelBtn = new Button(getContext());
        cancelBtn.setText("Cancel");
        cancelBtn.setTextColor(0xFFFFFFFF);
        cancelBtn.setTextSize(12f);
        cancelBtn.setBackgroundTintList(getColorStateList("#FF5722"));
        cancelBtn.setOnClickListener(v -> cancelAction());
        
        LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cancelParams.setMargins(5, 8, 5, 0);
        cancelBtn.setLayoutParams(cancelParams);
        
        actionBar.addView(cancelBtn);
    }
    
    private void setupReefActionBar(String snapTarget) {
        actionBar.removeAllViews();
        
        // Add title
        TextView titleText = new TextView(getContext());
        titleText.setText("Reef Actions");
        titleText.setTextColor(0xFF000000);
        titleText.setTextSize(14f);
        titleText.setTypeface(null, android.graphics.Typeface.BOLD);
        titleText.setPadding(0, 0, 0, 8);
        actionBar.addView(titleText);
        
        // Add reef image
        ImageView reefImage = new ImageView(getContext());
        reefImage.setImageResource(R.drawable.hexagon_corner);
        reefImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
            200, 150);
        imageParams.setMargins(0, 5, 0, 10);
        reefImage.setLayoutParams(imageParams);
        actionBar.addView(reefImage);
        
        // Add level selection buttons in 2x2 grid
        LinearLayout gridLayout = new LinearLayout(getContext());
        gridLayout.setOrientation(LinearLayout.VERTICAL);
        
        // Top row: L4, L3
        LinearLayout topRow = new LinearLayout(getContext());
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        
        Button l4Btn = createReefLevelButton("L4");
        Button l3Btn = createReefLevelButton("L3");
        topRow.addView(l4Btn);
        topRow.addView(l3Btn);
        
        // Bottom row: L2, L1
        LinearLayout bottomRow = new LinearLayout(getContext());
        bottomRow.setOrientation(LinearLayout.HORIZONTAL);
        
        Button l2Btn = createReefLevelButton("L2");
        Button l1Btn = createReefLevelButton("L1");
        bottomRow.addView(l2Btn);
        bottomRow.addView(l1Btn);
        
        gridLayout.addView(topRow);
        gridLayout.addView(bottomRow);
        actionBar.addView(gridLayout);
        
        // Find the actual zone and get its actions
        ZoneDrawingView.Zone targetZone = null;
        if (autoZoneView != null) {
            for (ZoneDrawingView.Zone zone : autoZoneView.getZones()) {
                if (snapTarget.equals(zone.name)) {
                    targetZone = zone;
                    break;
                }
            }
        }
        
        if (targetZone != null && !targetZone.actions.isEmpty()) {
            // Use zone-specific actions
            for (String action : targetZone.actions) {
                android.widget.CheckBox actionCheck = new android.widget.CheckBox(getContext());
                actionCheck.setText(action.replace("_", " "));
                actionCheck.setTextColor(0xFF000000);
                actionCheck.setTextSize(12f);
                actionCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        selectAction(action);
                        actionBar.setVisibility(View.GONE);
                    }
                });
                
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(5, 2, 5, 2);
                actionCheck.setLayoutParams(params);
                
                actionBar.addView(actionCheck);
            }
        }
        
        // Cancel button
        Button cancelBtn = new Button(getContext());
        cancelBtn.setText("Cancel");
        cancelBtn.setTextColor(0xFFFFFFFF);
        cancelBtn.setTextSize(12f);
        cancelBtn.setBackgroundTintList(getColorStateList("#FF5722"));
        cancelBtn.setOnClickListener(v -> cancelAction());
        
        LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cancelParams.setMargins(5, 8, 5, 0);
        cancelBtn.setLayoutParams(cancelParams);
        
        actionBar.addView(cancelBtn);
    }
    
    private Button createReefLevelButton(String level) {
        Button levelBtn = new Button(getContext());
        levelBtn.setText(level);
        levelBtn.setTextColor(0xFF000000);
        levelBtn.setTextSize(10f);
        levelBtn.setBackgroundTintList(getColorStateList("#4CAF50"));
        levelBtn.setOnClickListener(v -> {
            selectReefAction(level);
            actionBar.setVisibility(View.GONE);
        });
        
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
            60, 40);
        btnParams.setMargins(2, 2, 2, 2);
        levelBtn.setLayoutParams(btnParams);
        
        return levelBtn;
    }
    
    private void selectReefAction(String position) {
        // Record reef position selection
        updateActionHistory("reef_" + position, pendingSnapTarget);
        updatePathDisplay();
    }
    
    public void openFieldEditor() {
        Intent intent = new Intent(getActivity(), com.team2073.eagleforcescoutingapplication.activities.FieldEditorActivity.class);
        // Pass field side based on position
        String position = readData("position");
        String fieldSide = position.toLowerCase().startsWith("blue") ? "0" : "1";
        intent.putExtra("field_side", fieldSide);
        startActivityForResult(intent, 1001);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            // Reload configuration when returning from field editor
            loadFieldEditorConfig();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint() && !autoActive) {
            autoStartTime = System.currentTimeMillis();
            autoActive = true;
            startAutoTimer();
        }
        loadFieldEditorConfig();
    }
    
    private void loadFieldEditorConfig() {
        // Load field editor configuration for specific side
        android.content.SharedPreferences prefs = getActivity().getSharedPreferences("field_editor", android.content.Context.MODE_PRIVATE);
        
        String fieldSide = readData("field_side");
        String configKey = "auto_save_config_" + ("0".equals(fieldSide) ? "blue" : "red");
        String config = prefs.getString(configKey, "");
        
        // Fallback to general config if side-specific doesn't exist
        if (config.isEmpty()) {
            config = prefs.getString("auto_save_config", "");
        }
        
        if (!config.isEmpty() && autoZoneView != null) {
            // Parse and apply zones to auto view
            try {
                ZoneConfigParser.parseAndRestoreZonesRelative(autoZoneView, config);
                
                // Pass zones to path drawing view for snapping
                if (pathDrawingView != null) {
                    pathDrawingView.setZones(autoZoneView.getZones());
                }
                
                // Hide no config overlay and show zone count
                noConfigOverlay.setVisibility(View.GONE);
                int zoneCount = autoZoneView.getZones().size();
                updateActionHistory("Loaded " + zoneCount + " zones", "Config");
                
                // Auto-detect and lock boundaries
                fieldBackground.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        fieldBackground.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        autoDetectAndLockBoundaries();
                        // Apply any saved zone adjustments after boundaries are set
                        loadZoneAdjustment();
                    }
                });
                
            } catch (Exception e) {
                android.util.Log.e("UIAutoFragment", "Error loading field config: " + e.getMessage());
                showNoConfigOverlay();
            }
        } else {
            showNoConfigOverlay();
        }
    }
    
    private android.os.Handler timerHandler = new android.os.Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (autoActive) {
                long elapsed = System.currentTimeMillis() - autoStartTime;
                long remaining = Math.max(0, 15000 - elapsed);
                
                if (remaining > 0) {
                    fragmentAutoBinding.timerDisplay.setText(String.format("%.1fs", remaining / 1000.0));
                    // Change color as time runs out
                    if (remaining < 5000) {
                        fragmentAutoBinding.timerDisplay.setTextColor(0xFFFF0000); // Red
                    } else if (remaining < 10000) {
                        fragmentAutoBinding.timerDisplay.setTextColor(0xFFFF8800); // Orange
                    }
                    timerHandler.postDelayed(this, 100);
                } else {
                    fragmentAutoBinding.timerDisplay.setText("AUTO ENDED");
                    fragmentAutoBinding.timerDisplay.setTextColor(0xFF888888);
                    autoActive = false;
                    actionBar.setVisibility(View.GONE);
                    
                    // Re-enable ViewPager and auto-scroll to teleop (index 2)
                    if (parentViewPager != null) {
                        try {
                            java.lang.reflect.Method method = parentViewPager.getClass().getMethod("setUserInputEnabled", boolean.class);
                            method.invoke(parentViewPager, true);
                        } catch (Exception e) {
                            parentViewPager.setOnTouchListener(null);
                        }
                        parentViewPager.setCurrentItem(2, true);
                    }
                    
                    // Disable path drawing when auto ends
                    if (pathDrawingView != null) {
                        pathDrawingView.setDrawingEnabled(false);
                    }
                    unlockTabsBtn.setVisibility(View.GONE);
                }
            }
        }
    };
    
    private void startAutoTimer() {
        if (getUserVisibleHint() && isResumed()) {
            timerHandler.post(timerRunnable);
        }
    }
    
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed() && !autoActive) {
            autoStartTime = System.currentTimeMillis();
            autoActive = true;
            startAutoTimer();
        }
    }
    

    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timerHandler.removeCallbacks(timerRunnable);
        fragmentAutoBinding = null;
    }
    
    private void autoDetectAndLockBoundaries() {
        if (fieldBackground == null || fieldBackground.getDrawable() == null || autoZoneView == null) return;
        
        // Get container dimensions
        int viewWidth = fieldBackground.getWidth();
        int viewHeight = fieldBackground.getHeight();
        
        if (viewWidth == 0 || viewHeight == 0) return;
        
        // With centerInside scaling, calculate actual image bounds
        float[] bounds = calculateImageBounds();
        autoZoneView.setFieldBoundaries(bounds[0], bounds[1], bounds[2], bounds[3]);
        autoZoneView.lockBoundaries(true);
        
        android.util.Log.d("UIAutoFragment", String.format("Auto boundaries set: %.1f,%.1f to %.1f,%.1f", bounds[0], bounds[1], bounds[2], bounds[3]));
    }
    
    private void setPathDrawingBoundaries() {
        if (pathDrawingView != null && fieldBackground != null) {
            float[] bounds = calculateImageBounds();
            pathDrawingView.setBoundaries(bounds[0], bounds[1], bounds[2], bounds[3]);
            android.util.Log.d("UIAutoFragment", "Path drawing boundaries set");
        }
    }
    
    private float[] calculateImageBounds() {
        if (fieldBackground == null || fieldBackground.getDrawable() == null) {
            return new float[]{0, 0, 100, 100};
        }
        
        int viewWidth = fieldBackground.getWidth();
        int viewHeight = fieldBackground.getHeight();
        int drawableWidth = fieldBackground.getDrawable().getIntrinsicWidth();
        int drawableHeight = fieldBackground.getDrawable().getIntrinsicHeight();
        
        if (viewWidth == 0 || viewHeight == 0) {
            return new float[]{0, 0, 100, 100};
        }
        
        // Calculate centerInside scaling to match actual image bounds
        float scaleX = (float) viewWidth / drawableWidth;
        float scaleY = (float) viewHeight / drawableHeight;
        float scale = Math.min(scaleX, scaleY);
        
        float scaledWidth = drawableWidth * scale;
        float scaledHeight = drawableHeight * scale;
        
        // Apply refined universal boundary correction
        float left = (viewWidth - scaledWidth) / 2f;
        float top = (viewHeight - scaledHeight) / 2f;
        float right = left + scaledWidth;
        float bottom = top + scaledHeight;
        
        android.util.Log.d("UIAutoFragment", String.format("Image bounds: %.1f,%.1f to %.1f,%.1f (scale=%.3f)", left, top, right, bottom, scale));
        
        return new float[]{left, top, right, bottom};
    }
    
    private void undoLastAction() {
        if (pathDrawingView != null) {
            pathDrawingView.undoLastPath();
            updateActionHistory("Undo", "Action");
        }
    }
    
    private void redoLastAction() {
        // Redo functionality would need to be implemented in PathDrawingView
        updateActionHistory("Redo", "Action");
    }
    
    private void showNoConfigOverlay() {
        if (noConfigOverlay != null) {
            noConfigOverlay.setVisibility(View.VISIBLE);
        }
    }
    
    private void unlockTabs() {
        autoActive = false;
        if (parentViewPager != null) {
            try {
                java.lang.reflect.Method method = parentViewPager.getClass().getMethod("setUserInputEnabled", boolean.class);
                method.invoke(parentViewPager, true);
            } catch (Exception e) {
                parentViewPager.setOnTouchListener(null);
            }
        }
        unlockTabsBtn.setVisibility(View.GONE);
        updateActionHistory("Tabs unlocked manually", "System");
    }
    
    private void showZoneAlignmentDialog() {
        // Create vertical layout for action bar
        LinearLayout controlPanel = new LinearLayout(getActivity());
        controlPanel.setOrientation(LinearLayout.VERTICAL);
        controlPanel.setBackgroundColor(0xEE000000);
        controlPanel.setPadding(8, 8, 8, 8);
        
        // X control row
        LinearLayout xRow = new LinearLayout(getActivity());
        xRow.setOrientation(LinearLayout.HORIZONTAL);
        
        TextView xLabel = new TextView(getActivity());
        xLabel.setText("H:");
        xLabel.setTextColor(0xFFFFFFFF);
        xLabel.setTextSize(14f);
        xLabel.setLayoutParams(new LinearLayout.LayoutParams(25, LinearLayout.LayoutParams.WRAP_CONTENT));
        
        Button xMinus = new Button(getActivity());
        xMinus.setText("-");
        xMinus.setLayoutParams(new LinearLayout.LayoutParams(30, 30));
        xMinus.setTextSize(10f);
        
        android.widget.SeekBar xSeek = new android.widget.SeekBar(getActivity());
        xSeek.setMax(600);
        xSeek.setProgress(300);
        xSeek.setLayoutParams(new LinearLayout.LayoutParams(80, 40));
        
        Button xPlus = new Button(getActivity());
        xPlus.setText("+");
        xPlus.setLayoutParams(new LinearLayout.LayoutParams(30, 30));
        xPlus.setTextSize(10f);
        
        TextView xVal = new TextView(getActivity());
        xVal.setText("0");
        xVal.setTextColor(0xFFFFFFFF);
        xVal.setTextSize(14f);
        xVal.setMinWidth(40);
        
        xRow.addView(xLabel);
        xRow.addView(xMinus);
        xRow.addView(xSeek);
        xRow.addView(xPlus);
        
        // X value on separate line
        TextView xValLine = new TextView(getActivity());
        xValLine.setText("H: 0");
        xValLine.setTextColor(0xFFFFFFFF);
        xValLine.setTextSize(16f);
        xValLine.setGravity(android.view.Gravity.CENTER);
        xValLine.setTypeface(null, android.graphics.Typeface.BOLD);
        
        // Y control row
        LinearLayout yRow = new LinearLayout(getActivity());
        yRow.setOrientation(LinearLayout.HORIZONTAL);
        
        TextView yLabel = new TextView(getActivity());
        yLabel.setText("V:");
        yLabel.setTextColor(0xFFFFFFFF);
        yLabel.setTextSize(14f);
        yLabel.setLayoutParams(new LinearLayout.LayoutParams(25, LinearLayout.LayoutParams.WRAP_CONTENT));
        
        Button yMinus = new Button(getActivity());
        yMinus.setText("-");
        yMinus.setLayoutParams(new LinearLayout.LayoutParams(30, 30));
        yMinus.setTextSize(10f);
        
        android.widget.SeekBar ySeek = new android.widget.SeekBar(getActivity());
        ySeek.setMax(600);
        ySeek.setProgress(300);
        ySeek.setLayoutParams(new LinearLayout.LayoutParams(80, 40));
        
        Button yPlus = new Button(getActivity());
        yPlus.setText("+");
        yPlus.setLayoutParams(new LinearLayout.LayoutParams(30, 30));
        yPlus.setTextSize(10f);
        
        TextView yVal = new TextView(getActivity());
        yVal.setText("0");
        yVal.setTextColor(0xFFFFFFFF);
        yVal.setTextSize(14f);
        yVal.setMinWidth(40);
        
        yRow.addView(yLabel);
        yRow.addView(yMinus);
        yRow.addView(ySeek);
        yRow.addView(yPlus);
        
        // Y value on separate line
        TextView yValLine = new TextView(getActivity());
        yValLine.setText("V: 0");
        yValLine.setTextColor(0xFFFFFFFF);
        yValLine.setTextSize(16f);
        yValLine.setGravity(android.view.Gravity.CENTER);
        yValLine.setTypeface(null, android.graphics.Typeface.BOLD);
        
        // Done button
        Button done = new Button(getActivity());
        done.setText("DONE");
        done.setTextSize(12f);
        done.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 50));
        
        controlPanel.addView(xRow);
        controlPanel.addView(xValLine);
        controlPanel.addView(yRow);
        controlPanel.addView(yValLine);
        controlPanel.addView(done);
        
        // Add to action bar temporarily
        actionBar.removeAllViews();
        actionBar.addView(controlPanel);
        actionBar.setVisibility(View.VISIBLE);
        
        // Load current values
        android.content.SharedPreferences prefs = getActivity().getSharedPreferences("zone_adjustment", android.content.Context.MODE_PRIVATE);
        String fieldSide = readData("field_side");
        int currentX = prefs.getInt("x_offset_" + fieldSide, 0);
        int currentY = prefs.getInt("y_offset_" + fieldSide, 0);
        xSeek.setProgress(currentX + 300);
        ySeek.setProgress(currentY + 300);
        xVal.setText("H:" + currentX);
        yVal.setText("V:" + currentY);
        
        xSeek.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int offset = progress - 300;
                    xValLine.setText("H: " + offset);
                    previewZoneAdjustment(offset, ySeek.getProgress() - 300);
                }
            }
            @Override
            public void onStartTrackingTouch(android.widget.SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {}
        });
        
        ySeek.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int xOffset = xSeek.getProgress() - 300;
                    int yOffset = progress - 300;
                    yValLine.setText("V: " + yOffset);
                    previewZoneAdjustment(xOffset, yOffset);
                }
            }
            @Override
            public void onStartTrackingTouch(android.widget.SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {}
        });
        
        // Button click listeners for fine adjustment
        xMinus.setOnClickListener(v -> {
            int current = xSeek.getProgress();
            if (current > 0) {
                xSeek.setProgress(current - 1);
            }
        });
        xPlus.setOnClickListener(v -> {
            int current = xSeek.getProgress();
            if (current < 600) {
                xSeek.setProgress(current + 1);
            }
        });
        yMinus.setOnClickListener(v -> {
            int current = ySeek.getProgress();
            if (current > 0) {
                ySeek.setProgress(current - 1);
            }
        });
        yPlus.setOnClickListener(v -> {
            int current = ySeek.getProgress();
            if (current < 600) {
                ySeek.setProgress(current + 1);
            }
        });
        
        done.setOnClickListener(v -> {
            saveZoneAdjustment(xSeek.getProgress() - 300, ySeek.getProgress() - 300);
            actionBar.setVisibility(View.GONE);
            updateActionHistory("Zones aligned", "Config");
        });
    }
    
    private void previewZoneAdjustment(int xOffset, int yOffset) {
        if (autoZoneView != null) {
            // Reload config first to get original positions
            loadFieldEditorConfig();
            
            // Then apply offset and make zones semi-transparent for better visibility
            List<ZoneDrawingView.Zone> zones = autoZoneView.getZones();
            for (ZoneDrawingView.Zone zone : zones) {
                // Make zones more transparent during adjustment
                int currentColor = zone.fillPaint.getColor();
                int transparentColor = (currentColor & 0x00FFFFFF) | 0x30000000; // 30% opacity
                zone.fillPaint.setColor(transparentColor);
                
                for (PointF point : zone.points) {
                    point.x += xOffset;
                    point.y += yOffset;
                }
            }
            autoZoneView.invalidate();
        }
    }
    
    private void saveZoneAdjustment(int xOffset, int yOffset) {
        android.content.SharedPreferences prefs = getActivity().getSharedPreferences("zone_adjustment", android.content.Context.MODE_PRIVATE);
        String fieldSide = readData("field_side");
        prefs.edit()
            .putInt("x_offset_" + fieldSide, xOffset)
            .putInt("y_offset_" + fieldSide, yOffset)
            .apply();
    }
    
    private void loadZoneAdjustment() {
        android.content.SharedPreferences prefs = getActivity().getSharedPreferences("zone_adjustment", android.content.Context.MODE_PRIVATE);
        String fieldSide = readData("field_side");
        int xOffset = prefs.getInt("x_offset_" + fieldSide, 0);
        int yOffset = prefs.getInt("y_offset_" + fieldSide, 0);
        
        if (xOffset != 0 || yOffset != 0) {
            if (autoZoneView != null) {
                List<ZoneDrawingView.Zone> zones = autoZoneView.getZones();
                for (ZoneDrawingView.Zone zone : zones) {
                    for (PointF point : zone.points) {
                        point.x += xOffset;
                        point.y += yOffset;
                    }
                }
                autoZoneView.invalidate();
            }
        }
    }
    
    public String readData(String key) { return scoutingFormPresenter.readData(key); }
    public void saveData(String key, String data) { scoutingFormPresenter.saveData(key, data);}
}