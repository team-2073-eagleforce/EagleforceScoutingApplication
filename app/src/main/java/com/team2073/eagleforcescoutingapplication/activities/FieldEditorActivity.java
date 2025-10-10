package com.team2073.eagleforcescoutingapplication.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import androidx.appcompat.app.AlertDialog;

import com.team2073.eagleforcescoutingapplication.R;
import com.team2073.eagleforcescoutingapplication.util.FieldConfig;
import com.team2073.eagleforcescoutingapplication.util.ZoneDrawingView;
import com.team2073.eagleforcescoutingapplication.util.ZoneConfigParser;

import java.util.List;
import java.util.ArrayList;
import android.graphics.PointF;

public class FieldEditorActivity extends BaseActivity {
    
    private ImageView fieldBackground;
    private ZoneDrawingView zoneDrawingView;
    private FieldConfig fieldConfig;
    private boolean isEditMode = false;
    private boolean isBoundaryEditMode = false;
    private boolean isDrawingMode = false;
    private boolean isScaleMode = false;
    private ZoneDrawingView.Zone selectedZone;
    private Button editModeBtn, drawZoneBtn, boundaryModeBtn, scaleModeBtn, helpBtn;
    private TextView orientationInfo;
    private float currentRotation = 0f;
    private Button undoBtn, redoBtn;
    private List<List<PointF>> originalZonePoints = new ArrayList<>();
    private float currentScaleFactor = 1.0f;
    private android.widget.SeekBar scaleSeekBar;
    private TextView scaleValueText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fieldConfig = new FieldConfig(this);
        super.onCreate(savedInstanceState);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        saveConfiguration();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadConfiguration();
        checkForConfigurationUpdates();
    }
    
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_field_editor;
    }
    
    @Override
    protected void initView() {
        fieldBackground = findViewById(R.id.field_background_editor);
        zoneDrawingView = findViewById(R.id.zone_drawing_view);
        
        if (fieldBackground != null) {
            updateFieldImage();
            

        }
        
        if (zoneDrawingView != null) {
            // Auto-detect image boundaries after layout is complete
            if (fieldBackground != null) {
                fieldBackground.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        fieldBackground.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        autoDetectFieldBoundaries();
                    }
                });
            }
            
            // Removed background sync
            zoneDrawingView.setZoneDrawingListener(new ZoneDrawingView.ZoneDrawingListener() {
                @Override
                public void onZoneCompleted(ZoneDrawingView.Zone zone) {
                    isDrawingMode = false; // Sync with ZoneDrawingView state
                    updateEditModeUI(); // Update button text
                    showZoneConfigDialog(zone);
                }
                
                @Override
                public void onZoneSelected(ZoneDrawingView.Zone zone) {
                    selectedZone = zone;
                    showZoneConfigDialog(zone);
                }
                
                @Override
                public void onHistoryChanged(boolean canUndo, boolean canRedo) {
                    updateUndoRedoButtons(canUndo, canRedo);
                }
                
                @Override
                public void showZoneOptions(ZoneDrawingView.Zone zone, float x, float y) {
                    selectedZone = zone;
                    showZoneOptionsMenu(zone);
                }
            });
            
            zoneDrawingView.setScaleModeListener(new ZoneDrawingView.ScaleModeListener() {
                @Override
                public void onScaleChanged(float scaleFactor) {
                    // Apply cumulative scaling from pinch gestures
                    float newScale = currentScaleFactor * scaleFactor;
                    newScale = Math.max(0.1f, Math.min(2.0f, newScale)); // Clamp to valid range
                    
                    if (scaleSeekBar != null) {
                        scaleSeekBar.setProgress((int)(newScale * 100) - 10);
                    }
                    applyRealtimeScale(newScale);
                    currentScaleFactor = newScale;
                    saveScaleForCurrentField(newScale);
                    
                    if (scaleValueText != null) {
                        scaleValueText.setText(String.format("%.0f%%", newScale * 100));
                    }
                }
            });
        }
        
        drawExistingZones();
    }
    
    @Override
    protected void initEvent() {
        editModeBtn = findViewById(R.id.btn_edit_mode);
        drawZoneBtn = findViewById(R.id.btn_draw_zone);
        undoBtn = findViewById(R.id.btn_undo);
        redoBtn = findViewById(R.id.btn_redo);
        orientationInfo = findViewById(R.id.orientation_info);
        
        Button rotateBtn = findViewById(R.id.btn_rotate_field);
        Button cropBtn = findViewById(R.id.btn_crop_field);
        Button saveBtn = findViewById(R.id.btn_save_config);
        Button loadBtn = findViewById(R.id.btn_load_config);
        Button exportBtn = findViewById(R.id.btn_export_config);
        Button setBoundaryBtn = findViewById(R.id.btn_set_boundary);
        Button scaleBtn = findViewById(R.id.btn_scale_field);
        scaleModeBtn = findViewById(R.id.btn_scale_mode);
        boundaryModeBtn = findViewById(R.id.btn_boundary_mode);
        helpBtn = findViewById(R.id.btn_help);
        scaleSeekBar = findViewById(R.id.scale_seekbar);
        scaleValueText = findViewById(R.id.scale_value_text);
        Button customImageBtn = findViewById(R.id.btn_custom_image);
        
        if (editModeBtn != null) editModeBtn.setOnClickListener(v -> toggleEditMode());
        if (drawZoneBtn != null) drawZoneBtn.setOnClickListener(v -> toggleDrawMode());
        if (undoBtn != null) undoBtn.setOnClickListener(v -> undo());
        if (redoBtn != null) redoBtn.setOnClickListener(v -> redo());
        if (rotateBtn != null) rotateBtn.setOnClickListener(v -> rotateField());
        if (cropBtn != null) cropBtn.setOnClickListener(v -> showCropDialog());
        if (saveBtn != null) saveBtn.setOnClickListener(v -> saveCompleteConfig());
        if (loadBtn != null) loadBtn.setOnClickListener(v -> loadCompleteConfig());
        if (exportBtn != null) exportBtn.setOnClickListener(v -> exportConfigToQR());
        if (setBoundaryBtn != null) setBoundaryBtn.setOnClickListener(v -> showBoundaryDialog());
        if (scaleBtn != null) scaleBtn.setOnClickListener(v -> showScaleDialog());
        if (scaleModeBtn != null) scaleModeBtn.setOnClickListener(v -> toggleScaleMode());
        if (boundaryModeBtn != null) boundaryModeBtn.setOnClickListener(v -> toggleBoundaryMode());
        if (helpBtn != null) helpBtn.setOnClickListener(v -> showHelpDialog());
        if (customImageBtn != null) customImageBtn.setOnClickListener(v -> showCustomImageDialog());
        
        setupScaleControls();
        
        // Add debug button for testing boundary calculation
        Button debugBtn = findViewById(R.id.btn_debug_boundaries);
        if (debugBtn != null) debugBtn.setOnClickListener(v -> {
            recalculateBoundaries();
            android.widget.Toast.makeText(this, "Boundaries recalculated", android.widget.Toast.LENGTH_SHORT).show();
        });
        
        updateEditModeUI();
        updateOrientationDisplay();
        updateLoadedConfigIndicator();
    }
    
    @Override
    protected void bindView() {
        // No presenter needed
    }
    

    
    private void showZoneOptionsMenu(ZoneDrawingView.Zone zone) {
        String zoneName = zone.name != null ? zone.name : "Unnamed Zone";
        String lockStatus = zone.isLocked ? "🔒 Locked" : "🔓 Unlocked";
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Zone: " + zoneName + " (" + lockStatus + ")");
        
        String[] options = {"Configure Zone", "Delete Zone", "Cancel"};
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Configure
                    showZoneConfigDialog(zone);
                    break;
                case 1: // Delete
                    showDeleteConfirmation(zone);
                    break;
                case 2: // Cancel
                    break;
            }
        });
        
        builder.show();
    }
    
    private void showDeleteConfirmation(ZoneDrawingView.Zone zone) {
        String zoneName = zone.name != null ? zone.name : "Unnamed Zone";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Zone")
            .setMessage("Are you sure you want to delete \"" + zoneName + "\"?")
            .setPositiveButton("Delete", (dialog, which) -> {
                if (zoneDrawingView != null) {
                    zoneDrawingView.removeZone(zone);
                    drawExistingZones();
                }
            })
            .setNegativeButton("Cancel", null);
        
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xFF000000);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xFF000000);
        });
        dialog.show();
    }
    
    private void showZoneConfigDialog(ZoneDrawingView.Zone zone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(zone.name != null ? "Edit Zone: " + zone.name : "Configure Zone");
        
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_zone_config, null);
        EditText nameEdit = dialogView.findViewById(R.id.edit_zone_name);
        Spinner typeSpinner = dialogView.findViewById(R.id.spinner_zone_type);
        LinearLayout reefContainer = dialogView.findViewById(R.id.reef_layout_container);
        LinearLayout actionPreview = dialogView.findViewById(R.id.action_preview_container);
        
        // Setup zone types
        String[] zoneTypes = {"Normal", "Reef", "Source", "Barge", "Processor"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, zoneTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
        
        // Set existing values
        if (zone.name != null) {
            nameEdit.setText(zone.name);
        }
        if (zone.type != null) {
            int position = adapter.getPosition(zone.type);
            if (position >= 0) typeSpinner.setSelection(position);
        }
        
        // Remove duplicate listener - handled in auto-save section
        
        // Setup color buttons
        setupColorButtons(dialogView, zone);
        
        // Setup lock button
        setupLockButton(dialogView, zone);
        
        // Setup action buttons
        setupActionButtons(dialogView, zone, actionPreview);
        
        // Auto-save changes on text/spinner changes
        nameEdit.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(android.text.Editable s) {
                zone.name = s.toString();
                drawExistingZones();
            }
        });
        
        typeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedType = zoneTypes[position];
                zone.type = selectedType;
                reefContainer.setVisibility("Reef".equals(selectedType) ? View.VISIBLE : View.GONE);
                drawExistingZones();
                
                // Haptic feedback for zone type selection
                hapticFeedback();
            }
            
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        
        // Remove delete button from config dialog since it's now in options menu
        
        builder.show();
    }
    
    private void toggleEditMode() {
        isEditMode = !isEditMode;
        if (isEditMode) {
            isBoundaryEditMode = false;
            isScaleMode = false;
        } else {
            isDrawingMode = false;
            isBoundaryEditMode = false;
            isScaleMode = false;
        }
        updateEditModeUI();
        
        if (zoneDrawingView != null) {
            zoneDrawingView.setEditEnabled(isEditMode);
            zoneDrawingView.setBoundaryEditMode(isBoundaryEditMode);
            zoneDrawingView.setScaleMode(isScaleMode);
            if (!isEditMode) {
                zoneDrawingView.setDrawingMode(false);
                saveConfiguration();
            }
        }
    }
    
    private void toggleScaleMode() {
        isScaleMode = !isScaleMode;
        if (isScaleMode) {
            isEditMode = true;
            isBoundaryEditMode = false;
            isDrawingMode = false;
            // Always refresh original positions when entering scale mode
            originalZonePoints.clear();
            saveOriginalZonePositions();
        }
        updateEditModeUI();
        
        if (zoneDrawingView != null) {
            zoneDrawingView.setEditEnabled(isEditMode);
            zoneDrawingView.setScaleMode(isScaleMode);
            zoneDrawingView.setBoundaryEditMode(isBoundaryEditMode);
            if (isScaleMode) {
                zoneDrawingView.setDrawingMode(false);
            }
        }
    }
    
    private void toggleBoundaryMode() {
        if (!isEditMode) return;
        isBoundaryEditMode = !isBoundaryEditMode;
        if (isBoundaryEditMode) {
            isDrawingMode = false;
        }
        updateEditModeUI();
        
        if (zoneDrawingView != null) {
            zoneDrawingView.setBoundaryEditMode(isBoundaryEditMode);
            if (isBoundaryEditMode) {
                zoneDrawingView.setDrawingMode(false);
            }
        }
    }
    
    private void updateEditModeUI() {
        if (editModeBtn != null) {
            editModeBtn.setText(isEditMode ? "Exit Edit Mode" : "Enter Edit Mode");
        }
        
        if (drawZoneBtn != null) {
            drawZoneBtn.setEnabled(isEditMode && !isBoundaryEditMode && !isScaleMode);
            drawZoneBtn.setText(isDrawingMode ? "Exit Draw Mode" : "Draw New Zone");
        }
        
        if (boundaryModeBtn != null) {
            boundaryModeBtn.setEnabled(isEditMode && !isScaleMode);
            boundaryModeBtn.setText(isBoundaryEditMode ? "Exit Boundary Mode" : "Set Boundaries");
        }
        
        if (scaleModeBtn != null) {
            scaleModeBtn.setText(isScaleMode ? "Exit Scale Mode" : "Scale Mode");
            scaleModeBtn.setBackgroundTintList(getColorStateList(isScaleMode ? android.R.color.holo_orange_light : android.R.color.system_accent1_100));
        }
        
        if (scaleSeekBar != null && scaleValueText != null) {
            scaleSeekBar.setVisibility(isScaleMode ? android.view.View.VISIBLE : android.view.View.GONE);
            scaleValueText.setVisibility(isScaleMode ? android.view.View.VISIBLE : android.view.View.GONE);
        }
        
        if (undoBtn != null && redoBtn != null) {
            undoBtn.setEnabled(isEditMode && !isScaleMode && zoneDrawingView != null && zoneDrawingView.canUndo());
            redoBtn.setEnabled(isEditMode && !isScaleMode && zoneDrawingView != null && zoneDrawingView.canRedo());
        }
    }
    
    private void toggleDrawMode() {
        isDrawingMode = !isDrawingMode;
        if (zoneDrawingView != null) {
            zoneDrawingView.setDrawingMode(isDrawingMode);
        }
        updateEditModeUI();
    }
    
    private void drawExistingZones() {
        LinearLayout zonesList = findViewById(R.id.zones_list);
        if (zonesList == null) return;
        
        zonesList.removeAllViews();
        
        List<ZoneDrawingView.Zone> zones = zoneDrawingView != null ? zoneDrawingView.getZones() : new ArrayList<>();
        for (ZoneDrawingView.Zone zone : zones) {
            final ZoneDrawingView.Zone currentZone = zone;
            TextView zoneView = new TextView(this);
            zoneView.setText(String.format("%s%s (%s) - %d actions", 
                currentZone.isLocked ? "🔒 " : "",
                currentZone.name != null ? currentZone.name : "Unnamed", 
                currentZone.type != null ? currentZone.type : "Normal",
                currentZone.actions.size()));
            zoneView.setPadding(16, 8, 16, 8);
            zoneView.setOnLongClickListener(v -> {
                if (zoneDrawingView != null) {
                    zoneDrawingView.removeZone(currentZone);
                    drawExistingZones();
                }
                return true;
            });
            zonesList.addView(zoneView);
        }
    }
    
    private void setupLockButton(View dialogView, ZoneDrawingView.Zone zone) {
        Button lockBtn = dialogView.findViewById(R.id.btn_lock_zone);
        if (lockBtn != null) {
            lockBtn.setText(zone.isLocked ? "Unlock Zone" : "Lock Zone");
            lockBtn.setOnClickListener(v -> {
                zone.isLocked = !zone.isLocked;
                lockBtn.setText(zone.isLocked ? "Unlock Zone" : "Lock Zone");
                zoneDrawingView.invalidate();
                drawExistingZones();
            });
        }
    }
    
    private void setupColorButtons(View dialogView, ZoneDrawingView.Zone zone) {
        Button redBtn = dialogView.findViewById(R.id.btn_color_red);
        Button blueBtn = dialogView.findViewById(R.id.btn_color_blue);
        Button greenBtn = dialogView.findViewById(R.id.btn_color_green);
        Button yellowBtn = dialogView.findViewById(R.id.btn_color_yellow);
        
        // Set initial visual state
        updateColorButtonStates(dialogView, zone.fillPaint.getColor());
        
        redBtn.setOnClickListener(v -> {
            zone.fillPaint.setColor(0x44FF0000);
            updateColorButtonStates(dialogView, 0x44FF0000);
            zoneDrawingView.invalidate();
            hapticFeedback();
        });
        blueBtn.setOnClickListener(v -> {
            zone.fillPaint.setColor(0x440000FF);
            updateColorButtonStates(dialogView, 0x440000FF);
            zoneDrawingView.invalidate();
            hapticFeedback();
        });
        greenBtn.setOnClickListener(v -> {
            zone.fillPaint.setColor(0x4400FF00);
            updateColorButtonStates(dialogView, 0x4400FF00);
            zoneDrawingView.invalidate();
            hapticFeedback();
        });
        yellowBtn.setOnClickListener(v -> {
            zone.fillPaint.setColor(0x44FFFF00);
            updateColorButtonStates(dialogView, 0x44FFFF00);
            zoneDrawingView.invalidate();
            hapticFeedback();
        });
    }
    
    private void updateColorButtonStates(View dialogView, int selectedColor) {
        Button redBtn = dialogView.findViewById(R.id.btn_color_red);
        Button blueBtn = dialogView.findViewById(R.id.btn_color_blue);
        Button greenBtn = dialogView.findViewById(R.id.btn_color_green);
        Button yellowBtn = dialogView.findViewById(R.id.btn_color_yellow);
        
        // Reset all to grayed out
        redBtn.setAlpha(0.5f);
        blueBtn.setAlpha(0.5f);
        greenBtn.setAlpha(0.5f);
        yellowBtn.setAlpha(0.5f);
        
        // Highlight selected
        switch (selectedColor) {
            case 0x44FF0000: redBtn.setAlpha(1.0f); break;
            case 0x440000FF: blueBtn.setAlpha(1.0f); break;
            case 0x4400FF00: greenBtn.setAlpha(1.0f); break;
            case 0x44FFFF00: yellowBtn.setAlpha(1.0f); break;
        }
    }
    
    private void hapticFeedback() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            findViewById(android.R.id.content).performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP);
        }
    }
    
    private void setupActionButtons(View dialogView, ZoneDrawingView.Zone zone, LinearLayout actionPreview) {
        Button addBtn = dialogView.findViewById(R.id.btn_add_action);
        Button removeBtn = dialogView.findViewById(R.id.btn_remove_action);
        
        addBtn.setOnClickListener(v -> showAddActionDialog(zone, actionPreview));
        removeBtn.setOnClickListener(v -> {
            if (!zone.actions.isEmpty()) {
                zone.actions.remove(zone.actions.size() - 1);
                updateActionPreview(zone, actionPreview);
            }
        });
        
        updateActionPreview(zone, actionPreview);
    }
    
    private void showAddActionDialog(ZoneDrawingView.Zone zone, LinearLayout actionPreview) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Action Button");
        
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(50, 20, 50, 20);
        
        TextView label = new TextView(this);
        label.setText("Action Name:");
        label.setTypeface(null, android.graphics.Typeface.BOLD);
        dialogLayout.addView(label);
        
        EditText actionEdit = new EditText(this);
        actionEdit.setHint("e.g. pickup_coral, score_algae, remove_algae");
        dialogLayout.addView(actionEdit);
        
        TextView helpText = new TextView(this);
        helpText.setText("\nThis will appear as a button in the action bar when scouts tap this zone.");
        helpText.setTextSize(12f);
        helpText.setTextColor(0xFF666666);
        dialogLayout.addView(helpText);
        
        builder.setView(dialogLayout);
        
        builder.setPositiveButton("Add Action", (dialog, which) -> {
            String action = actionEdit.getText().toString().trim();
            if (!action.isEmpty() && !action.matches("\\s*")) {
                zone.actions.add(action);
                updateActionPreview(zone, actionPreview);
            }
        });
        builder.setNegativeButton("Cancel", null);
        
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xFF000000);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xFF000000);
        });
        builder.show();
    }
    
    private void updateActionPreview(ZoneDrawingView.Zone zone, LinearLayout actionPreview) {
        actionPreview.removeAllViews();
        
        if (zone.actions.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("No actions configured\nTap 'Add Action' to create buttons");
            emptyText.setTextSize(12f);
            emptyText.setTextColor(0xFF666666);
            emptyText.setPadding(8, 16, 8, 16);
            actionPreview.addView(emptyText);
            return;
        }
        
        for (int i = 0; i < zone.actions.size(); i++) {
            String action = zone.actions.get(i);
            final int currentIndex = i;
            
            LinearLayout actionRow = new LinearLayout(this);
            actionRow.setOrientation(LinearLayout.HORIZONTAL);
            actionRow.setPadding(4, 4, 4, 4);
            
            // Up button
            Button upBtn = new Button(this);
            upBtn.setText("↑");
            upBtn.setTextSize(12f);
            upBtn.setEnabled(i > 0);
            LinearLayout.LayoutParams upParams = new LinearLayout.LayoutParams(60, 60);
            upBtn.setLayoutParams(upParams);
            upBtn.setOnClickListener(v -> {
                if (currentIndex > 0) {
                    String temp = zone.actions.get(currentIndex);
                    zone.actions.set(currentIndex, zone.actions.get(currentIndex - 1));
                    zone.actions.set(currentIndex - 1, temp);
                    updateActionPreview(zone, actionPreview);
                }
            });
            
            // Down button
            Button downBtn = new Button(this);
            downBtn.setText("↓");
            downBtn.setTextSize(12f);
            downBtn.setEnabled(i < zone.actions.size() - 1);
            LinearLayout.LayoutParams downParams = new LinearLayout.LayoutParams(60, 60);
            downBtn.setLayoutParams(downParams);
            downBtn.setOnClickListener(v -> {
                if (currentIndex < zone.actions.size() - 1) {
                    String temp = zone.actions.get(currentIndex);
                    zone.actions.set(currentIndex, zone.actions.get(currentIndex + 1));
                    zone.actions.set(currentIndex + 1, temp);
                    updateActionPreview(zone, actionPreview);
                }
            });
            
            CheckBox actionCheck = new CheckBox(this);
            actionCheck.setText(action.replace("_", " "));
            actionCheck.setTextSize(14f);
            actionCheck.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            
            Button removeBtn = new Button(this);
            removeBtn.setText("×");
            removeBtn.setTextSize(16f);
            removeBtn.setBackgroundTintList(getColorStateList(android.R.color.holo_red_light));
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(60, 60);
            btnParams.setMargins(4, 0, 0, 0);
            removeBtn.setLayoutParams(btnParams);
            
            final String currentAction = action;
            removeBtn.setOnClickListener(v -> {
                zone.actions.remove(currentAction);
                updateActionPreview(zone, actionPreview);
            });
            
            actionRow.addView(upBtn);
            actionRow.addView(downBtn);
            actionRow.addView(actionCheck);
            actionRow.addView(removeBtn);
            actionPreview.addView(actionRow);
        }
    }
    
    private void showBoundaryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Field Boundaries");
        
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_field_boundary, null);
        EditText leftEdit = dialogView.findViewById(R.id.edit_left);
        EditText topEdit = dialogView.findViewById(R.id.edit_top);
        EditText rightEdit = dialogView.findViewById(R.id.edit_right);
        EditText bottomEdit = dialogView.findViewById(R.id.edit_bottom);
        
        // Set current values
        leftEdit.setText("50");
        topEdit.setText("50");
        rightEdit.setText("450");
        bottomEdit.setText("300");
        
        builder.setView(dialogView);
        builder.setPositiveButton("Set", (dialog, which) -> {
            try {
                float left = Float.parseFloat(leftEdit.getText().toString());
                float top = Float.parseFloat(topEdit.getText().toString());
                float right = Float.parseFloat(rightEdit.getText().toString());
                float bottom = Float.parseFloat(bottomEdit.getText().toString());
                
                if (zoneDrawingView != null) {
                    zoneDrawingView.setFieldBoundaries(left, top, right, bottom);
                }
            } catch (NumberFormatException e) {
                new AlertDialog.Builder(this)
                    .setTitle("Invalid Input")
                    .setMessage("Please enter valid numeric values for all boundary fields.")
                    .setPositiveButton("OK", null)
                    .show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xFF000000);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xFF000000);
        });
        dialog.show();
    }
    
    private void rotateField() {
        if (zoneDrawingView != null) {
            currentRotation = (currentRotation + 90f) % 360f;
            zoneDrawingView.setFieldRotation(currentRotation);
            updateOrientationDisplay();
        }
    }
    
    private void updateOrientationDisplay() {
        if (orientationInfo != null && zoneDrawingView != null) {
            orientationInfo.setText(zoneDrawingView.getOrientationInfo());
        }
    }
    
    private void undo() {
        if (zoneDrawingView != null) {
            zoneDrawingView.undo();
            drawExistingZones();
        }
    }
    
    private void redo() {
        if (zoneDrawingView != null) {
            zoneDrawingView.redo();
            drawExistingZones();
        }
    }
    
    private void updateUndoRedoButtons(boolean canUndo, boolean canRedo) {
        if (undoBtn != null) {
            undoBtn.setEnabled(isEditMode && canUndo);
        }
        if (redoBtn != null) {
            redoBtn.setEnabled(isEditMode && canRedo);
        }
    }
    
    private void showCropDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Crop Field Area");
        builder.setMessage("Drag the red corner handles to define the field area, then tap OK.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }
    
    private void saveConfiguration() {
        if (zoneDrawingView != null) {
            // Save zones at their original 1.0 scale positions
            String imageHash = generateImageHash();
            String config;
            
            if (currentScaleFactor != 1.0f && !originalZonePoints.isEmpty()) {
                // Temporarily restore zones to 1.0 scale for saving
                restoreOriginalPositions();
                config = zoneDrawingView.exportConfiguration(imageHash, currentScaleFactor);
                // Reapply current scale after saving
                applyRealtimeScale(currentScaleFactor);
            } else {
                config = zoneDrawingView.exportConfiguration(imageHash, currentScaleFactor);
            }
            
            // Save configuration for specific field side
            String position = getSharedPreferences("EagleforceScoutingApplication", MODE_PRIVATE)
                .getString("position", "red1");
            boolean isBlue = position.toLowerCase().startsWith("blue");
            String configKey = "auto_save_config_" + (isBlue ? "blue" : "red");
            
            getSharedPreferences("field_editor", MODE_PRIVATE)
                .edit()
                .putString("auto_save_config", config) // Keep general config for compatibility
                .putString(configKey, config) // Save side-specific config
                .putLong("config_timestamp", System.currentTimeMillis())
                .apply();
                
            saveFieldConfigRelative();
        }
    }
    
    private void saveFieldConfigRelative() {
        if (fieldConfig != null && zoneDrawingView != null) {
            float[] boundaries = zoneDrawingView.getFieldBoundaries();
            float fieldWidth = boundaries[2] - boundaries[0];
            float fieldHeight = boundaries[3] - boundaries[1];
            fieldConfig.saveConfigRelative(boundaries[0], boundaries[1], fieldWidth, fieldHeight);
        }
    }
    
    private void loadConfiguration() {
        // Load configuration for specific field side
        String position = getSharedPreferences("EagleforceScoutingApplication", MODE_PRIVATE)
            .getString("position", "red1");
        boolean isBlue = position.toLowerCase().startsWith("blue");
        String configKey = "auto_save_config_" + (isBlue ? "blue" : "red");
        
        String config = getSharedPreferences("field_editor", MODE_PRIVATE)
            .getString(configKey, "");
            
        // Fallback to general config if side-specific doesn't exist
        if (config.isEmpty()) {
            config = getSharedPreferences("field_editor", MODE_PRIVATE)
                .getString("auto_save_config", "");
        }
        
        if (!config.isEmpty()) {
            parseAndApplyConfig(config);
            drawExistingZones();
            
            // Set the loaded config name for auto-loaded configurations
            String autoPosition = getSharedPreferences("EagleforceScoutingApplication", MODE_PRIVATE)
                .getString("position", "red1");
            boolean autoIsBlue = autoPosition.toLowerCase().startsWith("blue");
            currentLoadedConfig = "auto_" + (autoIsBlue ? "blue" : "red");
            updateMainConfigurationManager();
            updateLoadedConfigIndicator();
        }
    }
    
    private void saveCompleteConfig() {
        showSaveConfigDialog();
    }
    
    private void loadCompleteConfig() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Load Configuration");
        
        String[] options = {"Load Saved Config", "Import from QR Code", "New Field", "Scale Field", "Clear All", "Cancel"};
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Load Saved Config
                    showSavedConfigsDialog();
                    break;
                case 1: // Import from QR Code
                    importConfigFromQR();
                    break;
                case 2: // New Field
                    createNewField();
                    break;
                case 3: // Scale Field
                    showScaleDialog();
                    break;
                case 4: // Clear All
                    clearAllFields();
                    break;
                case 5: // Cancel
                    break;
            }
        });
        
        builder.show();
    }
    

    
    private void checkForConfigurationUpdates() {
        // Check if configuration was updated from settings
        long lastUpdate = getSharedPreferences("field_editor", MODE_PRIVATE)
            .getLong("config_timestamp", 0);
        long lastCheck = getSharedPreferences("field_editor", MODE_PRIVATE)
            .getLong("last_check_timestamp", 0);
            
        if (lastUpdate > lastCheck) {
            // Configuration was updated, reload it
            loadConfiguration();
            getSharedPreferences("field_editor", MODE_PRIVATE)
                .edit()
                .putLong("last_check_timestamp", System.currentTimeMillis())
                .apply();
        }
    }
    
    private void showHelpDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Field Editor Help")
            .setMessage("• Enter Edit Mode: Enable zone editing\n" +
                       "• Draw New Zone: Click to enter drawing mode, then click points to create polygon\n" +
                       "• Edit Existing Zone: Click existing zone (when NOT in draw mode)\n" +
                       "• Set Boundaries: Toggle boundary mode, drag corners/center to adjust\n" +
                       "• Scale Mode: Select all zones, use slider or pinch to scale in real-time\n" +
                       "• Scale Dialog: Quick percentage scaling with preset buttons\n" +
                       "• Boundary stays fixed after exiting boundary mode\n" +
                       "• Drag to pan field\n" +
                       "• Drag zone points to fine-tune shapes\n" +
                       "• Configuration auto-saves on exit\n\n" +
                       "SCALING: Use Scale Mode for real-time adjustment with pinch gestures and slider")
            .setPositiveButton("OK", null)
            .show();
    }
    
    private void exportConfigToQR() {
        if (zoneDrawingView != null) {
            String imageHash = generateImageHash();
            String config = zoneDrawingView.exportConfiguration(imageHash, currentScaleFactor);
            
            try {
                // Generate QR code using same method as scouting form
                com.google.zxing.MultiFormatWriter multiFormatWriter = new com.google.zxing.MultiFormatWriter();
                com.google.zxing.common.BitMatrix bitMatrix = multiFormatWriter.encode(config, com.google.zxing.BarcodeFormat.QR_CODE, 400, 400);
                com.journeyapps.barcodescanner.BarcodeEncoder barcodeEncoder = new com.journeyapps.barcodescanner.BarcodeEncoder();
                android.graphics.Bitmap qrBitmap = barcodeEncoder.createBitmap(bitMatrix);
                
                // Show QR code in dialog
                ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(qrBitmap);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setPadding(20, 20, 20, 20);
                
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Field Configuration QR Code")
                       .setView(imageView)
                       .setPositiveButton("Close", null)
                       .setNegativeButton("Import QR", (dialog, which) -> importConfigFromQR())
                       .show();
                       
            } catch (com.google.zxing.WriterException e) {
                android.widget.Toast.makeText(this, "Error generating QR code", android.widget.Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void importConfigFromQR() {
        com.google.zxing.integration.android.IntentIntegrator integrator = new com.google.zxing.integration.android.IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(com.google.zxing.integration.android.IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan Field Configuration QR Code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        if (requestCode == 2001 && resultCode == RESULT_OK && data != null) {
            // Handle custom image selection
            android.net.Uri imageUri = data.getData();
            if (imageUri != null) {
                String position = getSharedPreferences("EagleforceScoutingApplication", MODE_PRIVATE)
                    .getString("position", "red1");
                
                com.team2073.eagleforcescoutingapplication.util.FieldImageManager fieldImageManager = 
                    com.team2073.eagleforcescoutingapplication.util.FieldImageManager.getInstance(this);
                
                fieldImageManager.setCustomImage(position, imageUri);
                updateFieldImage();
                android.widget.Toast.makeText(this, "Custom field image set!", android.widget.Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle QR code scanning
            com.google.zxing.integration.android.IntentResult result = 
                com.google.zxing.integration.android.IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    android.widget.Toast.makeText(this, "Scan cancelled", android.widget.Toast.LENGTH_SHORT).show();
                } else {
                    importFieldConfig(result.getContents());
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
    
    private void importFieldConfig(String configData) {
        try {
            // Save imported config
            getSharedPreferences("field_editor", MODE_PRIVATE)
                .edit()
                .putString("auto_save_config", configData)
                .putLong("config_timestamp", System.currentTimeMillis())
                .apply();
                
            // Apply the configuration
            parseAndApplyConfig(configData);
            drawExistingZones();
            
            int zoneCount = zoneDrawingView != null ? zoneDrawingView.getZones().size() : 0;
            android.widget.Toast.makeText(this, "Field configuration imported with " + zoneCount + " zones!", android.widget.Toast.LENGTH_LONG).show();
            
        } catch (Exception e) {
            android.widget.Toast.makeText(this, "Invalid field configuration QR code", android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    
    private void autoDetectFieldBoundaries() {
        if (fieldBackground == null || fieldBackground.getDrawable() == null) return;
        
        // Get ImageView dimensions
        int viewWidth = fieldBackground.getWidth();
        int viewHeight = fieldBackground.getHeight();
        
        if (viewWidth == 0 || viewHeight == 0) return;
        
        // Get drawable dimensions
        float drawableWidth = fieldBackground.getDrawable().getIntrinsicWidth();
        float drawableHeight = fieldBackground.getDrawable().getIntrinsicHeight();
        
        if (drawableWidth == 0 || drawableHeight == 0) return;
        
        // Calculate the actual displayed image bounds (centerInside scaling)
        float scaleX = viewWidth / drawableWidth;
        float scaleY = viewHeight / drawableHeight;
        float scale = Math.min(scaleX, scaleY); // centerInside uses the smaller scale
        
        float scaledWidth = drawableWidth * scale;
        float scaledHeight = drawableHeight * scale;
        
        // Calculate the actual image position (centered in ImageView)
        float imageLeft = (viewWidth - scaledWidth) / 2f;
        float imageTop = (viewHeight - scaledHeight) / 2f;
        float imageRight = imageLeft + scaledWidth;
        float imageBottom = imageTop + scaledHeight;
        
        // Use exact image bounds without margin for consistency with auto view
        if (zoneDrawingView != null) {
            zoneDrawingView.setImageDimensions(scaledWidth, scaledHeight);
            zoneDrawingView.setFieldBoundaries(imageLeft, imageTop, imageRight, imageBottom);
            zoneDrawingView.lockBoundaries(true);
            
            // Debug logging
            android.util.Log.d("FieldEditor", String.format("Image boundaries locked - View: %dx%d, Drawable: %.0fx%.0f, Scale: %.3f, Exact bounds: (%.1f,%.1f) to (%.1f,%.1f)", 
                viewWidth, viewHeight, drawableWidth, drawableHeight, scale, imageLeft, imageTop, imageRight, imageBottom));
        }
    }
    
    private String generateImageHash() {
        if (fieldBackground == null || fieldBackground.getDrawable() == null) return "";
        
        try {
            android.graphics.drawable.Drawable drawable = fieldBackground.getDrawable();
            android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(
                drawable.getIntrinsicWidth(), 
                drawable.getIntrinsicHeight(), 
                android.graphics.Bitmap.Config.ARGB_8888
            );
            android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            
            // Convert to PNG for consistent hashing
            java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream);
            
            // Generate MD5 hash
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(stream.toByteArray());
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
        } catch (Exception e) {
            android.util.Log.e("FieldEditor", "Error generating image hash: " + e.getMessage());
            return "";
        }
    }
    
    private void parseAndApplyConfig(String config) {
        try {
            if (config.isEmpty() || !config.startsWith("FIELD_CONFIG|")) {
                return;
            }
            
            String[] parts = config.split("\\|");
            if (parts.length < 4) {
                return;
            }
            
            // Check for image hash validation and extract scale factor
            String importedImageHash = "";
            float importedScale = 1.0f;
            
            if (parts.length > 4 && parts[4].startsWith("IMAGE_HASH:")) {
                importedImageHash = parts[4].substring(11);
                String currentImageHash = generateImageHash();
                
                if (!importedImageHash.equals(currentImageHash) && !importedImageHash.isEmpty()) {
                    showImageMismatchDialog(config, importedImageHash);
                    return;
                }
            }
            
            // Extract scale factor if present
            if (parts.length > 5 && parts[5].startsWith("SCALE:")) {
                try {
                    importedScale = Float.parseFloat(parts[5].substring(6));
                } catch (NumberFormatException e) {
                    importedScale = 1.0f;
                }
            }
            
            // Auto-detect current boundaries instead of using imported ones
            recalculateBoundaries();
            
            // Parse rotation
            currentRotation = Float.parseFloat(parts[2]);
            if (zoneDrawingView != null) {
                zoneDrawingView.setFieldRotation(currentRotation);
            }
            
            // Parse and restore zones with relative positioning
            if (zoneDrawingView != null) {
                ZoneConfigParser.parseAndRestoreZonesRelative(zoneDrawingView, config);
                
                // Apply imported scale factor
                currentScaleFactor = importedScale;
                saveScaleForCurrentField(currentScaleFactor);
                
                if (scaleSeekBar != null) {
                    scaleSeekBar.setProgress((int)(currentScaleFactor * 100) - 10);
                }
                if (scaleValueText != null) {
                    scaleValueText.setText(String.format("%.0f%%", currentScaleFactor * 100));
                }
                
                // Apply scale from loaded positions (they're already at 1.0 scale from relative conversion)
                if (currentScaleFactor != 1.0f) {
                    originalZonePoints.clear();
                    saveOriginalZonePositions();
                    applyRealtimeScale(currentScaleFactor);
                }
            }
            
            updateOrientationDisplay();
            
        } catch (Exception e) {
            android.util.Log.e("FieldEditor", "Error parsing config: " + e.getMessage());
        }
    }
    
    private void showImageMismatchDialog(String config, String importedImageHash) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Different Field Image Detected")
               .setMessage("This configuration was created for a different field image. Import anyway?")
               .setPositiveButton("Import Anyway", (dialog, which) -> {
                   // Force import with scaling
                   forceImportConfig(config);
               })
               .setNegativeButton("Cancel", null)
               .show();
    }
    
    private void forceImportConfig(String config) {
        try {
            // Recalculate boundaries to ensure they match current image display
            recalculateBoundaries();
            
            String[] parts = config.split("\\|");
            if (parts.length >= 3) {
                currentRotation = Float.parseFloat(parts[2]);
                if (zoneDrawingView != null) {
                    zoneDrawingView.setFieldRotation(currentRotation);
                }
            }
            
            if (zoneDrawingView != null) {
                ZoneConfigParser.parseAndRestoreZonesRelative(zoneDrawingView, config);
            }
            
            updateOrientationDisplay();
            drawExistingZones();
            
            android.widget.Toast.makeText(this, "Configuration imported with scaling", android.widget.Toast.LENGTH_LONG).show();
            
        } catch (Exception e) {
            android.widget.Toast.makeText(this, "Error importing configuration", android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    
    private void recalculateBoundaries() {
        if (fieldBackground != null && fieldBackground.getWidth() > 0 && fieldBackground.getHeight() > 0) {
            autoDetectFieldBoundaries();
        } else {
            // If layout isn't ready, wait for it
            if (fieldBackground != null) {
                fieldBackground.post(() -> autoDetectFieldBoundaries());
            }
        }
    }
    
    private void showSaveConfigDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        if (currentLoadedConfig != null) {
            // Update existing config
            builder.setTitle("Update Configuration")
                   .setMessage("Update existing configuration '" + currentLoadedConfig + "'?")
                   .setPositiveButton("Update", (dialog, which) -> {
                       saveConfigWithName(currentLoadedConfig);
                       // Also update the main configuration manager
                       updateMainConfigurationManager();
                   })
                   .setNeutralButton("Save As New", (dialog, which) -> {
                       showNewConfigDialog();
                   })
                   .setNegativeButton("Cancel", null);
        } else {
            // Create new config
            showNewConfigDialog();
            return;
        }
        
        builder.show();
    }
    
    private void showNewConfigDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Field Configuration");
        
        EditText nameEdit = new EditText(this);
        nameEdit.setHint("Enter configuration name");
        nameEdit.setText("Field_" + System.currentTimeMillis());
        builder.setView(nameEdit);
        
        builder.setPositiveButton("Save", (dialog, which) -> {
            String configName = nameEdit.getText().toString().trim();
            if (!configName.isEmpty()) {
                saveConfigWithName(configName);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private String currentLoadedConfig = null;
    
    private void saveConfigWithName(String configName) {
        if (zoneDrawingView != null) {
            // Save zones at their original 1.0 scale positions
            String imageHash = generateImageHash();
            String config;
            
            if (currentScaleFactor != 1.0f && !originalZonePoints.isEmpty()) {
                // Temporarily restore zones to 1.0 scale for saving
                restoreOriginalPositions();
                config = zoneDrawingView.exportConfiguration(imageHash, currentScaleFactor);
                // Reapply current scale after saving
                applyRealtimeScale(currentScaleFactor);
            } else {
                config = zoneDrawingView.exportConfiguration(imageHash, currentScaleFactor);
            }
            
            getSharedPreferences("field_configs", MODE_PRIVATE)
                .edit()
                .putString(configName, config)
                .putLong(configName + "_timestamp", System.currentTimeMillis())
                .apply();
                
            // Also save as auto-save config
            getSharedPreferences("field_editor", MODE_PRIVATE)
                .edit()
                .putString("auto_save_config", config)
                .putLong("config_timestamp", System.currentTimeMillis())
                .apply();
                
            currentLoadedConfig = configName;
            updateLoadedConfigIndicator();
            
            String message = currentLoadedConfig != null ? 
                "Configuration '" + configName + "' updated!" :
                "Configuration '" + configName + "' saved!";
            android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showSavedConfigsDialog() {
        android.content.SharedPreferences prefs = getSharedPreferences("field_configs", MODE_PRIVATE);
        java.util.Map<String, ?> allConfigs = prefs.getAll();
        
        java.util.List<String> configNames = new java.util.ArrayList<>();
        for (String key : allConfigs.keySet()) {
            if (!key.endsWith("_timestamp")) {
                configNames.add(key);
            }
        }
        
        if (configNames.isEmpty()) {
            android.widget.Toast.makeText(this, "No saved configurations found", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        
        String[] configArray = configNames.toArray(new String[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Manage Configurations");
        builder.setItems(configArray, (dialog, which) -> {
            String selectedConfig = configArray[which];
            showConfigOptionsDialog(selectedConfig);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void showConfigOptionsDialog(String configName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Config: " + configName);
        String[] options = {"Load", "Delete", "Cancel"};
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Load
                    loadConfigByName(configName);
                    break;
                case 1: // Delete
                    showDeleteConfigDialog(configName);
                    break;
                case 2: // Cancel
                    break;
            }
        });
        builder.show();
    }
    
    private void showDeleteConfigDialog(String configName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Configuration")
               .setMessage("Delete \"" + configName + "\"? This cannot be undone.")
               .setPositiveButton("Delete", (dialog, which) -> {
                   getSharedPreferences("field_configs", MODE_PRIVATE)
                       .edit()
                       .remove(configName)
                       .remove(configName + "_timestamp")
                       .apply();
                   android.widget.Toast.makeText(this, "Configuration deleted", android.widget.Toast.LENGTH_SHORT).show();
               })
               .setNegativeButton("Cancel", null)
               .show();
    }
    
    private void loadConfigByName(String configName) {
        String config = getSharedPreferences("field_configs", MODE_PRIVATE)
            .getString(configName, "");
        if (!config.isEmpty()) {
            parseAndApplyConfig(config);
            loadFieldConfigRelative();
            drawExistingZones();
            currentLoadedConfig = configName;
            
            // Update the main configuration manager to track this loaded config
            updateMainConfigurationManager();
            updateLoadedConfigIndicator();
            
            android.widget.Toast.makeText(this, "Configuration '" + configName + "' loaded!", android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    
    private void loadFieldConfigRelative() {
        if (fieldConfig != null && zoneDrawingView != null) {
            float[] boundaries = zoneDrawingView.getFieldBoundaries();
            float fieldWidth = boundaries[2] - boundaries[0];
            float fieldHeight = boundaries[3] - boundaries[1];
            fieldConfig.loadConfigRelative(boundaries[0], boundaries[1], fieldWidth, fieldHeight);
        }
    }
    
    private void createNewField() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Field")
               .setMessage("This will clear the current field. Continue?")
               .setPositiveButton("Yes", (dialog, which) -> {
                   if (zoneDrawingView != null) {
                       zoneDrawingView.clearZones();
                       drawExistingZones();
                   }
                   android.widget.Toast.makeText(this, "New field created", android.widget.Toast.LENGTH_SHORT).show();
               })
               .setNegativeButton("Cancel", null)
               .show();
    }
    
    private void clearAllFields() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Clear All Configurations")
               .setMessage("This will delete ALL saved field configurations. This cannot be undone!")
               .setPositiveButton("Delete All", (dialog, which) -> {
                   getSharedPreferences("field_configs", MODE_PRIVATE)
                       .edit()
                       .clear()
                       .apply();
                   if (zoneDrawingView != null) {
                       zoneDrawingView.clearZones();
                       drawExistingZones();
                   }
                   android.widget.Toast.makeText(this, "All configurations cleared", android.widget.Toast.LENGTH_SHORT).show();
               })
               .setNegativeButton("Cancel", null)
               .show();
    }
    
    private void showScaleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scale Field Configuration");
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);
        
        TextView label = new TextView(this);
        label.setText("Scale Factor (0.1 - 2.0):");
        layout.addView(label);
        
        EditText scaleEdit = new EditText(this);
        scaleEdit.setText(String.format("%.2f", currentScaleFactor));
        scaleEdit.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(scaleEdit);
        
        TextView currentScaleText = new TextView(this);
        currentScaleText.setText("Current Scale: " + String.format("%.2f", currentScaleFactor));
        currentScaleText.setTextSize(14f);
        currentScaleText.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(currentScaleText);
        
        TextView helpText = new TextView(this);
        helpText.setText("\n< 1.0 = Scale Down (Fix oversized QR imports)\n> 1.0 = Scale Up\n1.0 = No Change\n\nScales entire field boundaries and all zones");
        helpText.setTextSize(12f);
        layout.addView(helpText);
        
        // Add quick scale buttons
        LinearLayout quickScaleLayout = new LinearLayout(this);
        quickScaleLayout.setOrientation(LinearLayout.HORIZONTAL);
        
        Button scale50 = new Button(this);
        scale50.setText("50%");
        scale50.setOnClickListener(v -> scaleEdit.setText("0.5"));
        
        Button scale75 = new Button(this);
        scale75.setText("75%");
        scale75.setOnClickListener(v -> scaleEdit.setText("0.75"));
        
        Button scale100 = new Button(this);
        scale100.setText("100%");
        scale100.setOnClickListener(v -> scaleEdit.setText("1.0"));
        
        quickScaleLayout.addView(scale50);
        quickScaleLayout.addView(scale75);
        quickScaleLayout.addView(scale100);
        layout.addView(quickScaleLayout);
        
        builder.setView(layout);
        builder.setPositiveButton("Apply Scale", (dialog, which) -> {
            try {
                float scale = Float.parseFloat(scaleEdit.getText().toString());
                if (scale > 0.1f && scale <= 2.0f) {
                    scaleEntireField(scale);
                } else {
                    android.widget.Toast.makeText(this, "Scale must be between 0.1 and 2.0", android.widget.Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                android.widget.Toast.makeText(this, "Invalid scale value", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNeutralButton("Reset (1.0)", (dialog, which) -> {
            scaleEntireField(1.0f);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void scaleAllZones(float scaleFactor) {
        if (zoneDrawingView == null) return;
        
        // Save original positions if not already saved
        if (originalZonePoints.isEmpty()) {
            saveOriginalZonePositions();
        }
        
        float[] boundaries = zoneDrawingView.getFieldBoundaries();
        float centerX = (boundaries[0] + boundaries[2]) / 2f;
        float centerY = (boundaries[1] + boundaries[3]) / 2f;
        
        List<ZoneDrawingView.Zone> zones = zoneDrawingView.getZonesReference();
        for (int i = 0; i < zones.size() && i < originalZonePoints.size(); i++) {
            ZoneDrawingView.Zone zone = zones.get(i);
            List<PointF> originalPoints = originalZonePoints.get(i);
            
            for (int j = 0; j < zone.points.size() && j < originalPoints.size(); j++) {
                PointF originalPoint = originalPoints.get(j);
                PointF currentPoint = zone.points.get(j);
                
                // Scale from original position, not current
                float deltaX = (originalPoint.x - centerX) * scaleFactor;
                float deltaY = (originalPoint.y - centerY) * scaleFactor;
                currentPoint.x = centerX + deltaX;
                currentPoint.y = centerY + deltaY;
            }
        }
        
        currentScaleFactor = scaleFactor;
        saveScaleForCurrentField(scaleFactor);
        
        zoneDrawingView.invalidate();
        drawExistingZones();
        android.widget.Toast.makeText(this, "Zones scaled to " + String.format("%.2f", scaleFactor) + "x", android.widget.Toast.LENGTH_SHORT).show();
    }
    
    private void scaleEntireField(float scaleFactor) {
        if (zoneDrawingView == null) return;
        
        // Get current view dimensions for centering
        int viewWidth = getWindow().getDecorView().getWidth();
        int viewHeight = getWindow().getDecorView().getHeight();
        float centerX = viewWidth / 2f;
        float centerY = viewHeight / 2f;
        
        // Scale field boundaries from center
        float[] boundaries = zoneDrawingView.getFieldBoundaries();
        float currentCenterX = (boundaries[0] + boundaries[2]) / 2f;
        float currentCenterY = (boundaries[1] + boundaries[3]) / 2f;
        float currentWidth = boundaries[2] - boundaries[0];
        float currentHeight = boundaries[3] - boundaries[1];
        
        float newWidth = currentWidth * scaleFactor;
        float newHeight = currentHeight * scaleFactor;
        
        float newLeft = currentCenterX - (newWidth / 2f);
        float newTop = currentCenterY - (newHeight / 2f);
        float newRight = currentCenterX + (newWidth / 2f);
        float newBottom = currentCenterY + (newHeight / 2f);
        
        // Apply new boundaries
        zoneDrawingView.setFieldBoundaries(newLeft, newTop, newRight, newBottom);
        
        // Scale all zones relative to field boundaries
        List<ZoneDrawingView.Zone> zones = zoneDrawingView.getZonesReference();
        for (ZoneDrawingView.Zone zone : zones) {
            for (PointF point : zone.points) {
                // Scale point relative to field center
                float deltaX = (point.x - currentCenterX) * scaleFactor;
                float deltaY = (point.y - currentCenterY) * scaleFactor;
                point.x = currentCenterX + deltaX;
                point.y = currentCenterY + deltaY;
            }
        }
        
        currentScaleFactor = scaleFactor;
        saveScaleForCurrentField(scaleFactor);
        
        // Clear original positions to use new scaled positions as baseline
        originalZonePoints.clear();
        
        zoneDrawingView.invalidate();
        drawExistingZones();
        android.widget.Toast.makeText(this, "Entire field scaled to " + String.format("%.0f", scaleFactor * 100) + "%", android.widget.Toast.LENGTH_LONG).show();
    }
    
    private void saveOriginalZonePositions() {
        originalZonePoints.clear();
        for (ZoneDrawingView.Zone zone : zoneDrawingView.getZonesReference()) {
            List<PointF> originalPoints = new ArrayList<>();
            for (PointF point : zone.points) {
                originalPoints.add(new PointF(point.x, point.y));
            }
            originalZonePoints.add(originalPoints);
        }
    }
    
    private String getCurrentFieldKey() {
        String fieldSide = getIntent().getStringExtra("field_side");
        return "scale_" + ("0".equals(fieldSide) ? "blue" : "red");
    }
    
    private void saveScaleForCurrentField(float scale) {
        getSharedPreferences("field_scales", MODE_PRIVATE)
            .edit()
            .putFloat(getCurrentFieldKey(), scale)
            .apply();
    }
    
    private float getScaleForCurrentField() {
        return getSharedPreferences("field_scales", MODE_PRIVATE)
            .getFloat(getCurrentFieldKey(), 1.0f);
    }
    
    private void setupScaleControls() {
        if (scaleSeekBar != null) {
            scaleSeekBar.setMax(190); // 0.1 to 2.0 scale (10-200, offset by 10)
            scaleSeekBar.setProgress((int)(currentScaleFactor * 100) - 10);
            scaleSeekBar.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && isScaleMode) {
                        float scale = (progress + 10) / 100f; // Convert back to 0.1-2.0
                        applyRealtimeScale(scale);
                        if (scaleValueText != null) {
                            scaleValueText.setText(String.format("%.0f%%", scale * 100));
                        }
                    }
                }
                @Override
                public void onStartTrackingTouch(android.widget.SeekBar seekBar) {}
                @Override
                public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
                    float scale = (seekBar.getProgress() + 10) / 100f;
                    currentScaleFactor = scale;
                    saveScaleForCurrentField(scale);
                }
            });
        }
        
        if (scaleValueText != null) {
            scaleValueText.setText(String.format("%.0f%%", currentScaleFactor * 100));
        }
    }
    
    private void applyRealtimeScale(float scaleFactor) {
        if (zoneDrawingView == null || originalZonePoints.isEmpty()) return;
        
        float[] boundaries = zoneDrawingView.getFieldBoundaries();
        float centerX = (boundaries[0] + boundaries[2]) / 2f;
        float centerY = (boundaries[1] + boundaries[3]) / 2f;
        
        List<ZoneDrawingView.Zone> zones = zoneDrawingView.getZonesReference();
        for (int i = 0; i < zones.size() && i < originalZonePoints.size(); i++) {
            ZoneDrawingView.Zone zone = zones.get(i);
            List<PointF> originalPoints = originalZonePoints.get(i);
            
            for (int j = 0; j < zone.points.size() && j < originalPoints.size(); j++) {
                PointF originalPoint = originalPoints.get(j);
                PointF currentPoint = zone.points.get(j);
                
                float deltaX = (originalPoint.x - centerX) * scaleFactor;
                float deltaY = (originalPoint.y - centerY) * scaleFactor;
                currentPoint.x = centerX + deltaX;
                currentPoint.y = centerY + deltaY;
            }
        }
        
        zoneDrawingView.invalidate();
    }
    
    private void restoreOriginalPositions() {
        if (zoneDrawingView == null || originalZonePoints.isEmpty()) return;
        
        List<ZoneDrawingView.Zone> zones = zoneDrawingView.getZonesReference();
        for (int i = 0; i < zones.size() && i < originalZonePoints.size(); i++) {
            ZoneDrawingView.Zone zone = zones.get(i);
            List<PointF> originalPoints = originalZonePoints.get(i);
            
            for (int j = 0; j < zone.points.size() && j < originalPoints.size(); j++) {
                PointF originalPoint = originalPoints.get(j);
                PointF currentPoint = zone.points.get(j);
                currentPoint.x = originalPoint.x;
                currentPoint.y = originalPoint.y;
            }
        }
        
        zoneDrawingView.invalidate();
    }
    
    private void updateMainConfigurationManager() {
        // Update the main configuration manager to know about the currently loaded field config
        try {
            com.team2073.eagleforcescoutingapplication.framework.manager.ConfigurationManager configManager = 
                com.team2073.eagleforcescoutingapplication.framework.manager.ConfigurationManager.getInstance(this);
            configManager.setCurrentLoadedConfigName(currentLoadedConfig);
        } catch (Exception e) {
            // Ignore if configuration manager is not available
        }
    }
    
    private void updateFieldImage() {
        String position = getSharedPreferences("EagleforceScoutingApplication", MODE_PRIVATE)
            .getString("position", "red1");
        
        com.team2073.eagleforcescoutingapplication.util.FieldImageManager fieldImageManager = 
            com.team2073.eagleforcescoutingapplication.util.FieldImageManager.getInstance(this);
        
        int resourceId = fieldImageManager.getFieldImageResource(position);
        if (resourceId == -1) {
            // Load custom image
            String customPath = fieldImageManager.getCustomImagePath(position);
            if (customPath != null) {
                android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeFile(customPath);
                if (bitmap != null) {
                    fieldBackground.setImageBitmap(bitmap);
                }
            }
        } else {
            fieldBackground.setImageResource(resourceId);
        }
        
        // Update field side indicator
        TextView fieldSideIndicator = findViewById(R.id.field_side_indicator);
        if (fieldSideIndicator != null) {
            String displayName = fieldImageManager.getFieldDisplayName(position);
            fieldSideIndicator.setText(displayName);
        }
    }
    
    private void showCustomImageDialog() {
        String position = getSharedPreferences("EagleforceScoutingApplication", MODE_PRIVATE)
            .getString("position", "red1");
        
        com.team2073.eagleforcescoutingapplication.util.FieldImageManager fieldImageManager = 
            com.team2073.eagleforcescoutingapplication.util.FieldImageManager.getInstance(this);
        
        String displayName = fieldImageManager.getFieldDisplayName(position);
        boolean hasCustom = fieldImageManager.getCustomImagePath(position) != null;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Custom Field Image: " + displayName);
        
        String[] options = hasCustom ? 
            new String[]{"Upload New Image", "Remove Custom Image", "Cancel"} :
            new String[]{"Upload Custom Image", "Cancel"};
            
        builder.setItems(options, (dialog, which) -> {
            if (hasCustom) {
                switch (which) {
                    case 0: // Upload New
                        selectCustomImage();
                        break;
                    case 1: // Remove Custom
                        fieldImageManager.clearCustomImage(position);
                        updateFieldImage();
                        android.widget.Toast.makeText(this, "Custom image removed", android.widget.Toast.LENGTH_SHORT).show();
                        break;
                }
            } else {
                if (which == 0) { // Upload Custom
                    selectCustomImage();
                }
            }
        });
        
        builder.show();
    }
    
    private void selectCustomImage() {
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(android.content.Intent.createChooser(intent, "Select Field Image"), 2001);
    }
    

    
    private void updateLoadedConfigIndicator() {
        TextView loadedConfigText = findViewById(R.id.loaded_config_indicator);
        if (loadedConfigText != null) {
            if (currentLoadedConfig != null) {
                String displayName = currentLoadedConfig.startsWith("auto_") ? 
                    "Auto-loaded (" + currentLoadedConfig.substring(5) + " side)" : 
                    currentLoadedConfig;
                loadedConfigText.setText("Loaded: " + displayName);
                loadedConfigText.setVisibility(android.view.View.VISIBLE);
            } else {
                loadedConfigText.setVisibility(android.view.View.GONE);
            }
        }
    }
}