package com.team2073.eagleforcescoutingapplication.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class SimpleAutoPathView extends View {
    
    private Paint pathPaint;
    private Paint waypointPaint;
    private Paint actionPaint;
    private Path currentPath;
    private List<PointF> pathPoints;
    private List<PointF> waypoints;
    private List<ActionPoint> actionPoints;
    private AutoPathListener listener;
    private boolean isDrawing = false;
    private PointF lastPoint;
    private long lastTouchTime;
    private float velocity;
    private FieldConfig fieldConfig;
    
    public static class ActionPoint {
        public float x, y;
        public String action;
        public String zone;
        public long timestamp;
        
        public ActionPoint(float x, float y, String action, String zone) {
            this.x = x;
            this.y = y;
            this.action = action;
            this.zone = zone;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    public interface AutoPathListener {
        void onWaypointCreated(float x, float y, String zone);
        void onActionAdded(String action, String zone);
    }
    
    public SimpleAutoPathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    private void init() {
        pathPaint = new Paint();
        pathPaint.setColor(0xFF4BB543);
        pathPaint.setStrokeWidth(8f);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setAntiAlias(true);
        
        waypointPaint = new Paint();
        waypointPaint.setColor(0xFFFF0000);
        waypointPaint.setStyle(Paint.Style.FILL);
        waypointPaint.setAntiAlias(true);
        
        actionPaint = new Paint();
        actionPaint.setColor(0xFF0000FF);
        actionPaint.setStyle(Paint.Style.FILL);
        actionPaint.setAntiAlias(true);
        
        currentPath = new Path();
        pathPoints = new ArrayList<>();
        waypoints = new ArrayList<>();
        actionPoints = new ArrayList<>();
        fieldConfig = new FieldConfig(getContext());
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Draw path
        canvas.drawPath(currentPath, pathPaint);
        
        // Draw waypoints
        for (PointF waypoint : waypoints) {
            canvas.drawCircle(waypoint.x, waypoint.y, 12f, waypointPaint);
        }
        
        // Draw action points with labels
        Paint textPaint = new Paint();
        textPaint.setColor(0xFFFFFFFF);
        textPaint.setTextSize(10f);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        
        for (ActionPoint action : actionPoints) {
            canvas.drawCircle(action.x, action.y, 16f, actionPaint);
            canvas.drawText(action.action.substring(0, Math.min(3, action.action.length())), 
                          action.x, action.y + 3f, textPaint);
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        long currentTime = System.currentTimeMillis();
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Check if touching existing waypoint
                for (PointF waypoint : waypoints) {
                    if (isNearPoint(x, y, waypoint, 30f)) {
                        String zone = getZoneAt(x, y);
                        if (listener != null) {
                            listener.onWaypointCreated(waypoint.x, waypoint.y, zone);
                        }
                        return true;
                    }
                }
                
                // Start new path
                currentPath.moveTo(x, y);
                lastPoint = new PointF(x, y);
                lastTouchTime = currentTime;
                pathPoints.add(new PointF(x, y));
                isDrawing = true;
                break;
                
            case MotionEvent.ACTION_MOVE:
                if (isDrawing) {
                    currentPath.lineTo(x, y);
                    
                    // Calculate velocity
                    if (lastPoint != null && lastTouchTime > 0) {
                        float distance = (float) Math.sqrt(Math.pow(x - lastPoint.x, 2) + Math.pow(y - lastPoint.y, 2));
                        float timeDiff = (currentTime - lastTouchTime) / 1000f;
                        velocity = timeDiff > 0 ? distance / timeDiff : 0;
                    }
                    
                    lastPoint = new PointF(x, y);
                    lastTouchTime = currentTime;
                    pathPoints.add(new PointF(x, y));
                    invalidate();
                }
                break;
                
            case MotionEvent.ACTION_UP:
                if (isDrawing && pathPoints.size() > 5) {
                    // Check if line intersects any zone
                    boolean intersected = false;
                    for (PointF point : pathPoints) {
                        if (intersectsZone(point.x, point.y)) {
                            // Cut line at intersection point
                            PointF waypoint = new PointF(point.x, point.y);
                            waypoints.add(waypoint);
                            
                            String zone = getZoneAt(point.x, point.y);
                            if (listener != null) {
                                listener.onWaypointCreated(point.x, point.y, zone);
                            }
                            intersected = true;
                            break;
                        }
                    }
                    
                    // If no intersection but low velocity, create waypoint
                    if (!intersected && velocity < 50f) {
                        PointF waypoint = new PointF(x, y);
                        waypoints.add(waypoint);
                        
                        String zone = getZoneAt(x, y);
                        if (listener != null) {
                            listener.onWaypointCreated(x, y, zone);
                        }
                    }
                    
                    invalidate();
                }
                isDrawing = false;
                break;
        }
        
        return true;
    }
    
    private boolean isNearPoint(float x, float y, PointF point, float threshold) {
        float distance = (float) Math.sqrt(Math.pow(x - point.x, 2) + Math.pow(y - point.y, 2));
        return distance <= threshold;
    }
    
    private String getZoneAt(float x, float y) {
        // Enhanced zone detection with field config integration
        if (fieldConfig != null) {
            FieldConfig.ActionZone zone = fieldConfig.getZoneAt(x, y);
            if (zone != null) {
                return zone.name;
            }
        }
        
        // Fallback simple detection
        if (x < getWidth() / 3) return "Source";
        if (x > getWidth() * 2 / 3) return "Reef";
        return "Field";
    }
    
    private boolean intersectsZone(float x, float y) {
        if (fieldConfig != null) {
            return fieldConfig.getZoneAt(x, y) != null;
        }
        return false;
    }
    
    public void addAction(float x, float y, String action) {
        String zone = getZoneAt(x, y);
        ActionPoint actionPoint = new ActionPoint(x, y, action, zone);
        actionPoints.add(actionPoint);
        
        if (listener != null) {
            listener.onActionAdded(action, zone);
        }
        
        invalidate();
    }
    
    public void clearPath() {
        currentPath.reset();
        pathPoints.clear();
        waypoints.clear();
        actionPoints.clear();
        invalidate();
    }
    
    public void setAutoPathListener(AutoPathListener listener) {
        this.listener = listener;
    }
    
    public List<ActionPoint> getActionPoints() {
        return new ArrayList<>(actionPoints);
    }
    
    public void setFieldConfig(FieldConfig config) {
        this.fieldConfig = config;
    }
    
    public void updateFieldBoundaries(float left, float top, float width, float height) {
        if (fieldConfig != null) {
            fieldConfig.loadConfigRelative(left, top, width, height);
        }
    }
}