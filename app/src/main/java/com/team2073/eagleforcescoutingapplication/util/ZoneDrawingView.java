package com.team2073.eagleforcescoutingapplication.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class ZoneDrawingView extends View {
    
    private Paint zonePaint;
    private Paint dotPaint;
    private Paint linePaint;
    private List<Zone> zones;
    private Zone currentZone;
    private boolean isDrawingMode = false;
    private boolean isEditEnabled = false;
    private boolean isBoundaryEditMode = false;
    private boolean isScaleMode = false;
    private PointF selectedDot;
    private Zone selectedZone;
    private boolean isDraggingZone = false;
    private PointF zoneDragStart = new PointF();
    private float panX = 0f, panY = 0f;
    private boolean isPanning = false;
    private PointF lastPanPoint = new PointF();
    private ZoneDrawingListener listener;
    private ScaleGestureDetector scaleGestureDetector;
    private ScaleModeListener scaleModeListener;
    private ImageView backgroundImage;
    private float fieldLeft = 50f, fieldTop = 50f, fieldRight = 450f, fieldBottom = 300f;
    private float fieldRotation = 0f;
    private float imageWidth = 1000f, imageHeight = 700f;
    private boolean autoScale = true;
    private boolean isDraggingBoundary = false;
    private float dragStartX, dragStartY;
    private String dragHandle = ""; // "move", "tl", "tr", "bl", "br"
    private List<List<Zone>> undoHistory = new ArrayList<>();
    private List<List<Zone>> redoHistory = new ArrayList<>();
    private String errorMessage = null;
    private long errorMessageTime = 0;
    
    public interface ZoneDrawingListener {
        void onZoneCompleted(Zone zone);
        void onZoneSelected(Zone zone);
        void onHistoryChanged(boolean canUndo, boolean canRedo);
        void showZoneOptions(Zone zone, float x, float y);
    }
    
    public interface ScaleModeListener {
        void onScaleChanged(float scaleFactor);
    }
    
    public static class Zone {
        public String name;
        public String type; // "normal", "reef", etc.
        public List<PointF> points;
        public List<String> actions;
        public Paint fillPaint;
        public boolean isLocked = false;
        
        public Zone() {
            points = new ArrayList<>();
            actions = new ArrayList<>();
            fillPaint = new Paint();
            fillPaint.setStyle(Paint.Style.FILL);
            fillPaint.setAlpha(100);
        }
        
        public boolean containsPoint(float x, float y) {
            // Simple point-in-polygon test
            int intersections = 0;
            for (int i = 0; i < points.size(); i++) {
                PointF p1 = points.get(i);
                PointF p2 = points.get((i + 1) % points.size());
                
                if (((p1.y > y) != (p2.y > y)) &&
                    (x < (p2.x - p1.x) * (y - p1.y) / (p2.y - p1.y) + p1.x)) {
                    intersections++;
                }
            }
            return (intersections % 2) == 1;
        }
        
        public boolean overlapsWithZone(Zone other) {
            if (points.size() < 3 || other.points.size() < 3) return false;
            
            // Check if any points of this zone are inside the other zone
            for (PointF point : points) {
                if (other.containsPoint(point.x, point.y)) {
                    return true;
                }
            }
            
            // Check if any points of the other zone are inside this zone
            for (PointF point : other.points) {
                if (containsPoint(point.x, point.y)) {
                    return true;
                }
            }
            
            return false;
        }
    }
    
    public ZoneDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    private void init() {
        zonePaint = new Paint();
        zonePaint.setColor(0x4400FF00);
        zonePaint.setStyle(Paint.Style.FILL);
        
        dotPaint = new Paint();
        dotPaint.setColor(0xFFFF0000);
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setAntiAlias(true);
        
        linePaint = new Paint();
        linePaint.setColor(0xFF0000FF);
        linePaint.setStrokeWidth(3f);
        linePaint.setStyle(Paint.Style.STROKE);
        
        zones = new ArrayList<>();
        
        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (isScaleMode && scaleModeListener != null) {
                    float scaleFactor = detector.getScaleFactor();
                    scaleModeListener.onScaleChanged(scaleFactor);
                    return true;
                }
                return false;
            }
        });
    }
    
    public void setFieldBackground(ImageView imageView) {
        this.backgroundImage = imageView;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        canvas.save();
        
        // Apply pan only
        canvas.translate(panX, panY);
        
        if (fieldRotation != 0) {
            canvas.rotate(fieldRotation, imageWidth / 2, imageHeight / 2);
        }
        
        // Draw field boundary
        Paint boundaryPaint = new Paint();
        boundaryPaint.setColor(isEditEnabled ? 0x88FF0000 : 0x44000000);
        boundaryPaint.setStyle(Paint.Style.STROKE);
        boundaryPaint.setStrokeWidth(isEditEnabled ? 3f : 1f);
        canvas.drawRect(fieldLeft, fieldTop, fieldRight, fieldBottom, boundaryPaint);
        
        // Draw boundary handles only in boundary edit mode and if not locked
        if (isEditEnabled && isBoundaryEditMode && !boundariesLocked) {
            Paint handlePaint = new Paint();
            handlePaint.setColor(0xFFFF0000);
            handlePaint.setStyle(Paint.Style.FILL);
            
            // Corner resize handles
            canvas.drawCircle(fieldLeft, fieldTop, 8f, handlePaint);
            canvas.drawCircle(fieldRight, fieldTop, 8f, handlePaint);
            canvas.drawCircle(fieldLeft, fieldBottom, 8f, handlePaint);
            canvas.drawCircle(fieldRight, fieldBottom, 8f, handlePaint);
            
            // Center move handle
            float centerX = (fieldLeft + fieldRight) / 2;
            float centerY = (fieldTop + fieldBottom) / 2;
            handlePaint.setColor(0xFF00FF00);
            canvas.drawCircle(centerX, centerY, 12f, handlePaint);
            
            // Labels for handles
            Paint textPaint = new Paint();
            textPaint.setColor(0xFF000000);
            textPaint.setTextSize(12f);
            canvas.drawText("MOVE", centerX - 20f, centerY + 4f, textPaint);
            canvas.drawText("RESIZE", fieldLeft - 30f, fieldTop + 20f, textPaint);
        }
        
        // Draw mode notifications
        Paint notificationPaint = new Paint();
        notificationPaint.setStyle(Paint.Style.FILL);
        Paint notificationTextPaint = new Paint();
        notificationTextPaint.setColor(0xFF000000);
        notificationTextPaint.setTextSize(16f);
        notificationTextPaint.setAntiAlias(true);
        
        float yOffset = 10;
        
        // Scale mode notification
        if (isScaleMode) {
            notificationPaint.setColor(0xEEFF9800);
            String scaleNotification = "SCALE MODE - All zones selected";
            float textWidth = notificationTextPaint.measureText(scaleNotification);
            canvas.drawRect(10, yOffset, textWidth + 20, yOffset + 30, notificationPaint);
            canvas.drawText(scaleNotification, 15, yOffset + 20, notificationTextPaint);
            yOffset += 35;
        }
        
        // Selected zone notification
        if (selectedZone != null && !isScaleMode) {
            notificationPaint.setColor(0xEEFFAA00);
            String zoneName = selectedZone.name != null ? selectedZone.name : "Unnamed Zone";
            String lockStatus = selectedZone.isLocked ? " (Locked)" : " (Unlocked)";
            String notification = "Selected: " + zoneName + lockStatus;
            
            float textWidth = notificationTextPaint.measureText(notification);
            canvas.drawRect(10, yOffset, textWidth + 20, yOffset + 30, notificationPaint);
            canvas.drawText(notification, 15, yOffset + 20, notificationTextPaint);
        }
        
        // Draw error message notification
        if (errorMessage != null && (System.currentTimeMillis() - errorMessageTime) < 3000) {
            Paint errorPaint = new Paint();
            errorPaint.setColor(0xEEFF0000);
            errorPaint.setStyle(Paint.Style.FILL);
            
            Paint textPaint = new Paint();
            textPaint.setColor(0xFFFFFFFF);
            textPaint.setTextSize(16f);
            textPaint.setAntiAlias(true);
            
            float textWidth = textPaint.measureText(errorMessage);
            float yPos = selectedZone != null ? 50 : 10;
            canvas.drawRect(10, yPos, textWidth + 20, yPos + 30, errorPaint);
            canvas.drawText(errorMessage, 15, yPos + 20, textPaint);
        } else if (errorMessage != null) {
            errorMessage = null;
        }
        
        // Continue with zones drawing
        
        // Draw existing zones
        for (Zone zone : zones) {
            if (zone.points.size() > 2) {
                Path path = createPath(zone.points);
                canvas.drawPath(path, zone.fillPaint);
                
                // Use different line style for locked zones or selected zone
                Paint currentLinePaint = new Paint(linePaint);
                if (zone == selectedZone) {
                    currentLinePaint.setColor(0xFFFFAA00);
                    currentLinePaint.setStrokeWidth(5f);
                } else if (zone.isLocked) {
                    currentLinePaint.setColor(0xFF888888);
                    currentLinePaint.setPathEffect(new android.graphics.DashPathEffect(new float[]{10, 5}, 0));
                }
                canvas.drawPath(path, currentLinePaint);
            }
            
            // Draw zone dots with numbers (highlight all in scale mode)
            Paint dotTextPaint = new Paint();
            dotTextPaint.setColor(0xFFFFFFFF);
            dotTextPaint.setTextSize(12f);
            dotTextPaint.setAntiAlias(true);
            dotTextPaint.setTextAlign(Paint.Align.CENTER);
            
            for (int i = 0; i < zone.points.size(); i++) {
                PointF point = zone.points.get(i);
                
                Paint currentDotPaint = new Paint(dotPaint);
                if (isScaleMode) {
                    currentDotPaint.setColor(0xFFFF9800); // Orange for scale mode
                } else if (i == 0) {
                    currentDotPaint.setColor(0xFF00FF00); // Green for start
                }
                
                float radius = (isScaleMode || i == 0) ? 12f : 8f;
                canvas.drawCircle(point.x, point.y, radius, currentDotPaint);
                
                // Draw point number
                canvas.drawText(String.valueOf(i + 1), point.x, point.y + 3f, dotTextPaint);
            }
        }
        
        // Draw current zone being created
        if (currentZone != null && currentZone.points.size() > 0) {
            Paint textPaint = new Paint();
            textPaint.setColor(0xFFFFFFFF);
            textPaint.setTextSize(16f);
            textPaint.setAntiAlias(true);
            textPaint.setTextAlign(Paint.Align.CENTER);
            
            for (int i = 0; i < currentZone.points.size(); i++) {
                PointF point = currentZone.points.get(i);
                
                // Highlight start point
                if (i == 0) {
                    Paint startPaint = new Paint(dotPaint);
                    startPaint.setColor(0xFF00FF00); // Green for start
                    canvas.drawCircle(point.x, point.y, 20f, startPaint);
                } else {
                    canvas.drawCircle(point.x, point.y, 15f, dotPaint);
                }
                
                // Draw point number
                canvas.drawText(String.valueOf(i + 1), point.x, point.y + 5f, textPaint);
                
                if (i > 0) {
                    PointF prevPoint = currentZone.points.get(i - 1);
                    canvas.drawLine(prevPoint.x, prevPoint.y, point.x, point.y, linePaint);
                }
            }
            
            // Close the shape if we have 3+ points
            if (currentZone.points.size() >= 3) {
                PointF first = currentZone.points.get(0);
                PointF last = currentZone.points.get(currentZone.points.size() - 1);
                canvas.drawLine(last.x, last.y, first.x, first.y, linePaint);
            }
        }
        
        canvas.restore();
    }
    
    private Path createPath(List<PointF> points) {
        Path path = new Path();
        if (points.size() > 0) {
            path.moveTo(points.get(0).x, points.get(0).y);
            for (int i = 1; i < points.size(); i++) {
                path.lineTo(points.get(i).x, points.get(i).y);
            }
            path.close();
        }
        return path;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEditEnabled) {
            return false; // Don't handle touches when edit is disabled
        }
        
        // Handle scale gestures in scale mode
        if (isScaleMode) {
            scaleGestureDetector.onTouchEvent(event);
            return true;
        }
        
        // Use simple coordinates
        float x = event.getX() - panX;
        float y = event.getY() - panY;
        
        // Handle boundary editing only if not locked
        if (isEditEnabled && isBoundaryEditMode && !boundariesLocked) {
            if (handleBoundaryTouch(event.getX(), event.getY(), event.getAction())) {
                return true;
            }
        }
        
        // Skip zone editing in boundary mode
        if (isBoundaryEditMode) {
            return false;
        }
        
        android.util.Log.d("ZoneDrawing", "Touch event - Drawing mode: " + isDrawingMode + ", Edit enabled: " + isEditEnabled);
        
        // Handle zone drawing/editing FIRST
        boolean zoneHandled = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isDrawingMode) {
                    android.util.Log.d("ZoneDrawing", "Handling drawing touch");
                    handleDrawingTouch(x, y);
                    zoneHandled = true;
                } else {
                    android.util.Log.d("ZoneDrawing", "Handling selection touch");
                    boolean dotSelected = handleSelectionTouch(x, y);
                    if (dotSelected) zoneHandled = true;
                }
                break;
                
            case MotionEvent.ACTION_MOVE:
                if (selectedDot != null) {
                    // Constrain dot movement to field boundaries
                    selectedDot.x = Math.max(fieldLeft, Math.min(fieldRight, x));
                    selectedDot.y = Math.max(fieldTop, Math.min(fieldBottom, y));
                    invalidate();
                    zoneHandled = true;
                } else if (isDraggingZone && selectedZone != null && !selectedZone.isLocked) {
                    // Move entire zone
                    float deltaX = x - zoneDragStart.x;
                    float deltaY = y - zoneDragStart.y;
                    
                    // Check if zone would stay within boundaries
                    boolean canMove = true;
                    for (PointF point : selectedZone.points) {
                        float newX = point.x + deltaX;
                        float newY = point.y + deltaY;
                        if (newX < fieldLeft || newX > fieldRight || newY < fieldTop || newY > fieldBottom) {
                            canMove = false;
                            break;
                        }
                    }
                    
                    if (canMove) {
                        for (PointF point : selectedZone.points) {
                            point.x += deltaX;
                            point.y += deltaY;
                        }
                        zoneDragStart.set(x, y);
                        invalidate();
                    }
                    zoneHandled = true;
                }
                break;
                
            case MotionEvent.ACTION_UP:
                if (selectedDot != null) {
                    selectedDot = null;
                    zoneHandled = true;
                } else if (isDraggingZone) {
                    isDraggingZone = false;
                    zoneHandled = true;
                }
                break;
        }
        
        // If zone drawing handled it, don't do panning
        if (zoneHandled) {
            return true;
        }
        
        // Allow panning only if zone drawing didn't handle the touch
        return handlePanTouch(event);
    }
    
    private void handleDrawingTouch(float x, float y) {
        android.util.Log.d("ZoneDrawing", "Drawing touch at: " + x + ", " + y);
        
        if (currentZone == null) {
            currentZone = new Zone();
            android.util.Log.d("ZoneDrawing", "Created new zone");
        }
        
        // Check if clicking near first point to close zone
        if (currentZone.points.size() >= 3) {
            PointF firstPoint = currentZone.points.get(0);
            float distance = (float) Math.sqrt(Math.pow(x - firstPoint.x, 2) + Math.pow(y - firstPoint.y, 2));
            if (distance < 20f) {
                // Check for overlap with existing zones before closing
                for (Zone existingZone : zones) {
                    if (currentZone.overlapsWithZone(existingZone)) {
                        showError("Zone overlaps with existing zone");
                        return;
                    }
                }
                
                // Close the zone
                saveToHistory();
                zones.add(currentZone);
                if (listener != null) {
                    listener.onZoneCompleted(currentZone);
                }
                currentZone = null;
                isDrawingMode = false; // Exit draw mode automatically
                invalidate();
                return;
            }
        }
        
        // Check if point is within field boundaries
        if (x < fieldLeft || x > fieldRight || y < fieldTop || y > fieldBottom) {
            showError("Point outside field boundaries");
            return;
        }
        
        // Check for overlap with existing zones
        PointF newPoint = new PointF(x, y);
        for (Zone existingZone : zones) {
            if (existingZone.containsPoint(x, y)) {
                showError("Point overlaps with existing zone");
                return;
            }
        }
        
        // Add new point with haptic feedback
        currentZone.points.add(newPoint);
        android.util.Log.d("ZoneDrawing", "Added point " + currentZone.points.size() + " at: " + x + ", " + y);
        
        // Haptic feedback
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP);
        }
        
        invalidate();
        notifyHistoryChanged();
    }
    
    private void showError(String message) {
        errorMessage = message;
        errorMessageTime = System.currentTimeMillis();
        invalidate();
    }
    
    private boolean handleSelectionTouch(float x, float y) {
        // Check if touching existing dot for fine-tuning (skip locked zones)
        for (Zone zone : zones) {
            if (!zone.isLocked) {
                for (PointF point : zone.points) {
                    float distance = (float) Math.sqrt(Math.pow(x - point.x, 2) + Math.pow(y - point.y, 2));
                    if (distance < 30f) {
                        selectedDot = point;
                        selectedZone = zone;
                        return true;
                    }
                }
            }
        }
        
        // Check if touching inside zone
        for (Zone zone : zones) {
            if (zone.containsPoint(x, y)) {
                selectedZone = zone;
                if (!zone.isLocked) {
                    // Start zone dragging
                    isDraggingZone = true;
                    zoneDragStart.set(x, y);
                }
                
                // Show zone options menu
                if (listener != null) {
                    listener.showZoneOptions(zone, x, y);
                }
                return true;
            }
        }
        
        // Clear selection if touching empty area
        selectedZone = null;
        invalidate();
        return false;
    }
    
    public void setDrawingMode(boolean drawingMode) {
        android.util.Log.d("ZoneDrawing", "Setting drawing mode to: " + drawingMode);
        this.isDrawingMode = drawingMode;
        if (!drawingMode) {
            currentZone = null;
        }
        invalidate();
    }
    
    public void setEditEnabled(boolean enabled) {
        this.isEditEnabled = enabled;
        if (!enabled) {
            isDrawingMode = false;
            isBoundaryEditMode = false;
            currentZone = null;
            selectedDot = null;
            selectedZone = null;
            isDraggingZone = false;
        }
        invalidate();
    }
    
    private boolean boundariesLocked = false;
    
    public void setBoundaryEditMode(boolean enabled) {
        if (boundariesLocked) return;
        
        this.isBoundaryEditMode = enabled;
        if (enabled) {
            isDrawingMode = false;
            isScaleMode = false;
            currentZone = null;
            selectedDot = null;
            selectedZone = null;
            isDraggingZone = false;
        }
        invalidate();
    }
    
    public void lockBoundaries(boolean locked) {
        this.boundariesLocked = locked;
        if (locked) {
            isBoundaryEditMode = false;
        }
        invalidate();
    }
    
    public void setScaleMode(boolean enabled) {
        this.isScaleMode = enabled;
        if (enabled) {
            isDrawingMode = false;
            isBoundaryEditMode = false;
            currentZone = null;
            selectedDot = null;
            isDraggingZone = false;
            // Select all zones visually
            selectedZone = null; // Clear individual selection
        }
        invalidate();
    }
    
    public void setScaleModeListener(ScaleModeListener listener) {
        this.scaleModeListener = listener;
    }
    
    public void setFieldBoundaries(float left, float top, float right, float bottom) {
        this.fieldLeft = left;
        this.fieldTop = top;
        this.fieldRight = right;
        this.fieldBottom = bottom;
        invalidate();
    }
    
    public float[] getFieldBoundaries() {
        return new float[]{fieldLeft, fieldTop, fieldRight, fieldBottom};
    }
    
    public void setImageDimensions(float width, float height) {
        this.imageWidth = width;
        this.imageHeight = height;
        invalidate();
    }
    
    public void setFieldRotation(float rotation) {
        this.fieldRotation = rotation;
        invalidate();
    }
    
    public float getFieldRotation() {
        return fieldRotation;
    }
    
    public String getOrientationInfo() {
        String orientation = "";
        switch ((int) fieldRotation % 360) {
            case 0: orientation = "Normal (Alliance side view)"; break;
            case 90: orientation = "90° CW"; break;
            case 180: orientation = "180° (Flipped)"; break;
            case 270: orientation = "270° CW"; break;
        }
        return String.format("Rotation: %.0f° - %s", fieldRotation, orientation);
    }
    
    public void setAutoScale(boolean autoScale) {
        this.autoScale = autoScale;
        invalidate();
    }
    
    public String exportConfiguration() {
        return exportConfiguration("");
    }
    
    public String exportConfiguration(String imageHash) {
        return exportConfiguration(imageHash, 1.0f);
    }
    
    public String exportConfiguration(String imageHash, float scaleFactor) {
        StringBuilder config = new StringBuilder();
        config.append("FIELD_CONFIG|");
        config.append(fieldLeft).append(",").append(fieldTop).append(",");
        config.append(fieldRight).append(",").append(fieldBottom).append("|");
        config.append(fieldRotation).append("|");
        config.append(imageWidth).append(",").append(imageHeight).append("|");
        
        // Add image hash if provided
        if (!imageHash.isEmpty()) {
            config.append("IMAGE_HASH:").append(imageHash).append("|");
        } else {
            config.append("|"); // Empty hash slot
        }
        
        // Add scale factor
        config.append("SCALE:").append(scaleFactor).append("|");
        
        // Calculate field dimensions for relative coordinates
        float fieldWidth = fieldRight - fieldLeft;
        float fieldHeight = fieldBottom - fieldTop;
        
        for (Zone zone : zones) {
            config.append("ZONE|");
            config.append(zone.name != null ? zone.name : "Unnamed").append("|");
            config.append(zone.type != null ? zone.type : "Normal").append("|");
            
            // Zone points as relative coordinates (0.0 to 1.0)
            for (PointF point : zone.points) {
                float relativeX = (point.x - fieldLeft) / fieldWidth;
                float relativeY = (point.y - fieldTop) / fieldHeight;
                config.append(relativeX).append(",").append(relativeY).append(";");
            }
            config.append("|");
            
            // Zone actions
            for (String action : zone.actions) {
                config.append(action).append(",");
            }
            config.append("|");
            
            // Zone lock state
            config.append(zone.isLocked ? "1" : "0").append("|");
        }
        
        return config.toString();
    }
    
    private boolean handleBoundaryTouch(float x, float y, int action) {
        float fieldX = x - panX;
        float fieldY = y - panY;
        float centerX = (fieldLeft + fieldRight) / 2;
        float centerY = (fieldTop + fieldBottom) / 2;
        
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // Check move handle (center)
                if (isNearPoint(fieldX, fieldY, new PointF(centerX, centerY), 20f)) {
                    isDraggingBoundary = true;
                    dragHandle = "move";
                    dragStartX = fieldX;
                    dragStartY = fieldY;
                    return true;
                }
                
                // Check corner handles
                if (isNearPoint(fieldX, fieldY, new PointF(fieldLeft, fieldTop), 15f)) {
                    isDraggingBoundary = true;
                    dragHandle = "tl";
                    return true;
                } else if (isNearPoint(fieldX, fieldY, new PointF(fieldRight, fieldTop), 15f)) {
                    isDraggingBoundary = true;
                    dragHandle = "tr";
                    return true;
                } else if (isNearPoint(fieldX, fieldY, new PointF(fieldLeft, fieldBottom), 15f)) {
                    isDraggingBoundary = true;
                    dragHandle = "bl";
                    return true;
                } else if (isNearPoint(fieldX, fieldY, new PointF(fieldRight, fieldBottom), 15f)) {
                    isDraggingBoundary = true;
                    dragHandle = "br";
                    return true;
                }
                break;
                
            case MotionEvent.ACTION_MOVE:
                if (isDraggingBoundary) {
                    fieldX = x - panX;
                    fieldY = y - panY;
                    
                    if (dragHandle.equals("move")) {
                        // Move entire boundary
                        float deltaX = fieldX - dragStartX;
                        float deltaY = fieldY - dragStartY;
                        fieldLeft += deltaX;
                        fieldRight += deltaX;
                        fieldTop += deltaY;
                        fieldBottom += deltaY;
                        dragStartX = fieldX;
                        dragStartY = fieldY;
                    } else {
                        // Resize boundary
                        switch (dragHandle) {
                            case "tl": fieldLeft = fieldX; fieldTop = fieldY; break;
                            case "tr": fieldRight = fieldX; fieldTop = fieldY; break;
                            case "bl": fieldLeft = fieldX; fieldBottom = fieldY; break;
                            case "br": fieldRight = fieldX; fieldBottom = fieldY; break;
                        }
                    }
                    invalidate();
                    return true;
                }
                break;
                
            case MotionEvent.ACTION_UP:
                if (isDraggingBoundary) {
                    isDraggingBoundary = false;
                    dragHandle = "";
                    return true;
                }
                break;
        }
        
        return false;
    }
    
    private boolean handlePanTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isPanning = true;
                lastPanPoint.set(event.getX(), event.getY());
                return true;
                
            case MotionEvent.ACTION_MOVE:
                if (isPanning) {
                    float deltaX = event.getX() - lastPanPoint.x;
                    float deltaY = event.getY() - lastPanPoint.y;
                    panX += deltaX;
                    panY += deltaY;
                    lastPanPoint.set(event.getX(), event.getY());
                    invalidate();
                    return true;
                }
                break;
                
            case MotionEvent.ACTION_UP:
                isPanning = false;
                break;
        }
        return false;
    }
    
    private void drawOrientationIndicators(Canvas canvas) {
        Paint textPaint = new Paint();
        textPaint.setColor(0xFF000000);
        textPaint.setTextSize(16f);
        textPaint.setAntiAlias(true);
        
        Paint arrowPaint = new Paint();
        arrowPaint.setColor(0xFF0000FF);
        arrowPaint.setStrokeWidth(3f);
        arrowPaint.setStyle(Paint.Style.STROKE);
        
        // Draw "UP" arrow at top center
        float centerX = (fieldLeft + fieldRight) / 2;
        float topY = fieldTop - 20f;
        canvas.drawLine(centerX, topY, centerX, topY - 30f, arrowPaint);
        canvas.drawLine(centerX, topY - 30f, centerX - 10f, topY - 20f, arrowPaint);
        canvas.drawLine(centerX, topY - 30f, centerX + 10f, topY - 20f, arrowPaint);
        canvas.drawText("UP", centerX - 15f, topY - 35f, textPaint);
        
        // Draw coordinate system origin (0,0) at top-left
        canvas.drawText("(0,0)", fieldLeft - 30f, fieldTop - 5f, textPaint);
        
        // Draw field dimensions
        float fieldWidth = fieldRight - fieldLeft;
        float fieldHeight = fieldBottom - fieldTop;
        canvas.drawText(String.format("W:%.0f", fieldWidth), fieldLeft, fieldBottom + 20f, textPaint);
        canvas.drawText(String.format("H:%.0f", fieldHeight), fieldRight + 10f, fieldTop + 20f, textPaint);
        
        // Draw alliance indicator for current field side
        Paint alliancePaint = new Paint();
        alliancePaint.setTextSize(20f);
        alliancePaint.setAntiAlias(true);
        
        // Show which alliance side this field represents
        alliancePaint.setColor(0xFF000000);
        canvas.drawText("ALLIANCE SIDE", centerX - 50f, fieldTop - 50f, alliancePaint);
        canvas.drawText("(Half Field View)", centerX - 45f, fieldTop - 30f, alliancePaint);
        
        // Draw grid lines for reference
        Paint gridPaint = new Paint();
        gridPaint.setColor(0x33000000);
        gridPaint.setStrokeWidth(1f);
        
        // Vertical grid lines every 50 units
        for (float x = fieldLeft; x <= fieldRight; x += 50f) {
            canvas.drawLine(x, fieldTop, x, fieldBottom, gridPaint);
            if (x > fieldLeft) {
                canvas.drawText(String.format("%.0f", x - fieldLeft), x - 10f, fieldTop - 10f, textPaint);
            }
        }
        
        // Horizontal grid lines every 50 units
        for (float y = fieldTop; y <= fieldBottom; y += 50f) {
            canvas.drawLine(fieldLeft, y, fieldRight, y, gridPaint);
            if (y > fieldTop) {
                canvas.drawText(String.format("%.0f", y - fieldTop), fieldLeft - 25f, y + 5f, textPaint);
            }
        }
    }
    
    public void clearZones() {
        zones.clear();
        currentZone = null;
        invalidate();
    }
    
    public void removeZone(Zone zone) {
        saveToHistory();
        zones.remove(zone);
        invalidate();
    }
    
    public void setZoneDrawingListener(ZoneDrawingListener listener) {
        this.listener = listener;
    }
    
    public List<Zone> getZones() {
        return new ArrayList<>(zones);
    }
    
    public List<Zone> getZonesReference() {
        return zones;
    }
    
    public void clearZoneSelection() {
        selectedZone = null;
        selectedDot = null;
        isDraggingZone = false;
        invalidate();
    }
    
    private boolean isNearPoint(float x, float y, PointF point, float threshold) {
        float distance = (float) Math.sqrt(Math.pow(x - point.x, 2) + Math.pow(y - point.y, 2));
        return distance <= threshold;
    }
    
    private void saveToHistory() {
        // Deep copy current zones state
        List<Zone> currentState = new ArrayList<>();
        for (Zone zone : zones) {
            Zone copy = new Zone();
            copy.name = zone.name;
            copy.type = zone.type;
            copy.points = new ArrayList<>(zone.points);
            copy.actions = new ArrayList<>(zone.actions);
            copy.fillPaint.setColor(zone.fillPaint.getColor());
            currentState.add(copy);
        }
        
        undoHistory.add(currentState);
        redoHistory.clear(); // Clear redo when new action is performed
        
        // Limit history size
        if (undoHistory.size() > 20) {
            undoHistory.remove(0);
        }
        
        notifyHistoryChanged();
    }
    
    public void undo() {
        // In drawing mode, undo last point of current zone
        if (isDrawingMode && currentZone != null && !currentZone.points.isEmpty()) {
            currentZone.points.remove(currentZone.points.size() - 1);
            invalidate();
            return;
        }
        
        // Normal undo for completed zones
        if (!undoHistory.isEmpty()) {
            // Save current state to redo
            List<Zone> currentState = new ArrayList<>(zones);
            redoHistory.add(currentState);
            
            // Restore previous state
            zones = undoHistory.remove(undoHistory.size() - 1);
            invalidate();
            notifyHistoryChanged();
        }
    }
    
    public void redo() {
        if (!redoHistory.isEmpty()) {
            // Save current state to undo
            saveToHistory();
            
            // Restore next state
            zones = redoHistory.remove(redoHistory.size() - 1);
            invalidate();
            notifyHistoryChanged();
        }
    }
    
    public boolean canUndo() {
        // Can undo if in drawing mode with points, or if there's history
        return (isDrawingMode && currentZone != null && !currentZone.points.isEmpty()) || !undoHistory.isEmpty();
    }
    
    public boolean canRedo() {
        return !redoHistory.isEmpty();
    }
    
    private void notifyHistoryChanged() {
        if (listener != null) {
            listener.onHistoryChanged(canUndo(), canRedo());
        }
    }
    
    // Zoom feature removed for stability
    
    private void updateBackgroundTransform() {
        // Simplified - removed background sync
    }
}