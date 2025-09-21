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

public class FieldEditorActivity extends BaseActivity {
    
    private ImageView fieldBackground;
    private ZoneDrawingView zoneDrawingView;
    private FieldConfig fieldConfig;
    private boolean isEditMode = false;
    private boolean isBoundaryEditMode = false;
    private boolean isDrawingMode = false;
    private ZoneDrawingView.Zone selectedZone;
    private Button editModeBtn, drawZoneBtn, boundaryModeBtn, helpBtn;
    private TextView orientationInfo;
    private float currentRotation = 0f;
    private Button undoBtn, redoBtn;
    
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
            // Set field side
            String fieldSide = getIntent().getStringExtra("field_side");
            if ("0".equals(fieldSide)) {
                fieldBackground.setImageResource(R.drawable.field_blue_side);
            } else {
                fieldBackground.setImageResource(R.drawable.field_red_side);
            }
        }
        
        if (zoneDrawingView != null) {
            // Auto-detect image dimensions and set boundaries
            if (fieldBackground != null) {
                fieldBackground.post(() -> {
                    if (fieldBackground.getDrawable() == null) return;
                    float imgWidth = fieldBackground.getDrawable().getIntrinsicWidth();
                    float imgHeight = fieldBackground.getDrawable().getIntrinsicHeight();
                    zoneDrawingView.setImageDimensions(imgWidth, imgHeight);
                    
                    // Set default boundaries to 90% of image
                    float margin = 0.05f;
                    zoneDrawingView.setFieldBoundaries(
                        imgWidth * margin, imgHeight * margin,
                        imgWidth * (1 - margin), imgHeight * (1 - margin)
                    );
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
        boundaryModeBtn = findViewById(R.id.btn_boundary_mode);
        helpBtn = findViewById(R.id.btn_help);
        
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
        if (boundaryModeBtn != null) boundaryModeBtn.setOnClickListener(v -> toggleBoundaryMode());
        if (helpBtn != null) helpBtn.setOnClickListener(v -> showHelpDialog());
        
        updateEditModeUI();
        updateOrientationDisplay();
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
        String[] zoneTypes = {"Normal", "Reef", "Source", "Barge"};
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
        } else {
            isDrawingMode = false;
            isBoundaryEditMode = false;
        }
        updateEditModeUI();
        
        if (zoneDrawingView != null) {
            zoneDrawingView.setEditEnabled(isEditMode);
            zoneDrawingView.setBoundaryEditMode(isBoundaryEditMode);
            if (!isEditMode) {
                zoneDrawingView.setDrawingMode(false);
                saveConfiguration();
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
            drawZoneBtn.setEnabled(isEditMode && !isBoundaryEditMode);
            drawZoneBtn.setText(isDrawingMode ? "Exit Draw Mode" : "Draw New Zone");
        }
        
        if (boundaryModeBtn != null) {
            boundaryModeBtn.setEnabled(isEditMode);
            boundaryModeBtn.setText(isBoundaryEditMode ? "Exit Boundary Mode" : "Set Boundaries");
        }
        
        if (undoBtn != null && redoBtn != null) {
            undoBtn.setEnabled(isEditMode && zoneDrawingView != null && zoneDrawingView.canUndo());
            redoBtn.setEnabled(isEditMode && zoneDrawingView != null && zoneDrawingView.canRedo());
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
        
        redBtn.setOnClickListener(v -> {
            zone.fillPaint.setColor(0x44FF0000);
            zoneDrawingView.invalidate();
        });
        blueBtn.setOnClickListener(v -> {
            zone.fillPaint.setColor(0x440000FF);
            zoneDrawingView.invalidate();
        });
        greenBtn.setOnClickListener(v -> {
            zone.fillPaint.setColor(0x4400FF00);
            zoneDrawingView.invalidate();
        });
        yellowBtn.setOnClickListener(v -> {
            zone.fillPaint.setColor(0x44FFFF00);
            zoneDrawingView.invalidate();
        });
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
            String config = zoneDrawingView.exportConfiguration();
            getSharedPreferences("field_editor", MODE_PRIVATE)
                .edit()
                .putString("auto_save_config", config)
                .putLong("config_timestamp", System.currentTimeMillis())
                .apply();
        }
    }
    
    private void loadConfiguration() {
        String config = getSharedPreferences("field_editor", MODE_PRIVATE)
            .getString("auto_save_config", "");
        if (!config.isEmpty()) {
            parseAndApplyConfig(config);
            drawExistingZones();
        }
    }
    
    private void saveCompleteConfig() {
        saveConfiguration();
        new AlertDialog.Builder(this)
            .setTitle("Configuration Saved")
            .setMessage("Field configuration saved successfully with " + 
                (zoneDrawingView != null ? zoneDrawingView.getZones().size() : 0) + " zones.")
            .setPositiveButton("OK", null)
            .show();
    }
    
    private void loadCompleteConfig() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Load Configuration");
        
        String[] options = {"Load from Tablet", "Import from QR Code", "Cancel"};
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Load from Tablet
                    loadFromTablet();
                    break;
                case 1: // Import from QR Code
                    importConfigFromQR();
                    break;
                case 2: // Cancel
                    break;
            }
        });
        
        builder.show();
    }
    
    private void loadFromTablet() {
        loadConfiguration();
        int zoneCount = zoneDrawingView != null ? zoneDrawingView.getZones().size() : 0;
        new AlertDialog.Builder(this)
            .setTitle("Configuration Loaded")
            .setMessage("Loaded saved field configuration with " + zoneCount + " zones.")
            .setPositiveButton("OK", null)
            .show();
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
                       "• Boundary stays fixed after exiting boundary mode\n" +
                       "• Pinch to zoom, drag to pan field\n" +
                       "• Drag zone points to fine-tune shapes\n" +
                       "• Configuration auto-saves on exit")
            .setPositiveButton("OK", null)
            .show();
    }
    
    private void exportConfigToQR() {
        if (zoneDrawingView != null) {
            String config = zoneDrawingView.exportConfiguration();
            
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
    
    private void parseAndApplyConfig(String config) {
        try {
            if (config.isEmpty() || !config.startsWith("FIELD_CONFIG|")) {
                return; // Silently ignore empty or invalid config
            }
            
            String[] parts = config.split("\\|");
            if (parts.length < 4) {
                return; // Silently ignore incomplete config
            }
            
            // Parse field boundaries
            String[] boundaries = parts[1].split(",");
            if (boundaries.length == 4) {
                float left = Float.parseFloat(boundaries[0]);
                float top = Float.parseFloat(boundaries[1]);
                float right = Float.parseFloat(boundaries[2]);
                float bottom = Float.parseFloat(boundaries[3]);
                if (zoneDrawingView != null) {
                    zoneDrawingView.setFieldBoundaries(left, top, right, bottom);
                }
            }
            
            // Parse rotation
            currentRotation = Float.parseFloat(parts[2]);
            if (zoneDrawingView != null) {
                zoneDrawingView.setFieldRotation(currentRotation);
            }
            
            // Parse image dimensions
            String[] dimensions = parts[3].split(",");
            if (dimensions.length == 2) {
                float width = Float.parseFloat(dimensions[0]);
                float height = Float.parseFloat(dimensions[1]);
                if (zoneDrawingView != null) {
                    zoneDrawingView.setImageDimensions(width, height);
                }
            }
            
            // Parse and restore zones
            if (zoneDrawingView != null) {
                ZoneConfigParser.parseAndRestoreZones(zoneDrawingView, config);
            }
            
            updateOrientationDisplay();
            
        } catch (Exception e) {
            // Silently handle parsing errors to avoid disrupting user experience
            android.util.Log.e("FieldEditor", "Error parsing config: " + e.getMessage());
        }
    }
}