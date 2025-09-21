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

public class PathDrawingView extends View {
    
    private Paint pathPaint;
    private Paint waypointPaint;
    private Path currentPath;
    private List<PointF> pathPoints;
    private List<PointF> waypoints;
    private List<PointF> actionPoints;
    private long lastTouchTime;
    private PointF lastPoint;
    private float lastVelocity;
    private PathDrawingListener listener;
    private boolean isWaitingForAction = false;
    private PointF pendingWaypoint;
    private String pendingSnapTarget;
    private FieldConfig fieldConfig;
    
    public interface PathDrawingListener {
        void onWaypointCreated(float x, float y, String snapTarget);
        void onActionBarRequested(float x, float y, String snapTarget);
        void onPathContinued();
    }
    
    public PathDrawingView(Context context, AttributeSet attrs) {
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
        
        // Draw action points
        waypointPaint.setColor(0xFF0000FF);
        for (PointF actionPoint : actionPoints) {
            canvas.drawCircle(actionPoint.x, actionPoint.y, 16f, waypointPaint);
        }
        waypointPaint.setColor(0xFFFF0000);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        long currentTime = System.currentTimeMillis();
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Check if touching existing waypoint for action
                for (PointF waypoint : waypoints) {
                    if (isNearPoint(x, y, waypoint, 30f)) {
                        if (listener != null) {
                            listener.onActionBarRequested(waypoint.x, waypoint.y, getSnapTarget(waypoint.x, waypoint.y));
                        }
                        return true;
                    }
                }
                
                // If continuing from waypoint, hide action bar
                if (isWaitingForAction && pendingWaypoint != null && 
                    isNearPoint(x, y, pendingWaypoint, 40f)) {
                    isWaitingForAction = false;
                    if (listener != null) {
                        listener.onPathContinued();
                    }
                }
                
                // Start new path segment
                currentPath.moveTo(x, y);
                lastPoint = new PointF(x, y);
                lastTouchTime = currentTime;
                pathPoints.add(new PointF(x, y));
                break;
                
            case MotionEvent.ACTION_MOVE:
                currentPath.lineTo(x, y);
                
                // Calculate velocity
                if (lastPoint != null && lastTouchTime > 0) {
                    float distance = (float) Math.sqrt(Math.pow(x - lastPoint.x, 2) + Math.pow(y - lastPoint.y, 2));
                    float timeDiff = (currentTime - lastTouchTime) / 1000f;
                    lastVelocity = timeDiff > 0 ? distance / timeDiff : 0;
                }
                
                lastPoint = new PointF(x, y);
                lastTouchTime = currentTime;
                pathPoints.add(new PointF(x, y));
                invalidate();
                break;
                
            case MotionEvent.ACTION_UP:
                // Create waypoint if velocity was low (indicating a stop)
                if (lastVelocity < 50f && pathPoints.size() > 5) {
                    // Apply proximity snapping
                    PointF snappedPoint = applyProximitySnapping(x, y);
                    String snapTarget = getSnapTarget(snappedPoint.x, snappedPoint.y);
                    
                    waypoints.add(snappedPoint);
                    pendingWaypoint = snappedPoint;
                    pendingSnapTarget = snapTarget;
                    isWaitingForAction = true;
                    
                    if (listener != null) {
                        listener.onWaypointCreated(snappedPoint.x, snappedPoint.y, snapTarget);
                    }
                    invalidate();
                }
                break;
        }
        
        return true;
    }
    
    private boolean isNearPoint(float x, float y, PointF point, float threshold) {
        float distance = (float) Math.sqrt(Math.pow(x - point.x, 2) + Math.pow(y - point.y, 2));
        return distance <= threshold;
    }
    
    public void clearPath() {
        currentPath.reset();
        pathPoints.clear();
        waypoints.clear();
        actionPoints.clear();
        invalidate();
    }
    
    public void setPathDrawingListener(PathDrawingListener listener) {
        this.listener = listener;
    }
    
    public List<PointF> getPathPoints() {
        return new ArrayList<>(pathPoints);
    }
    
    public List<PointF> getWaypoints() {
        return new ArrayList<>(waypoints);
    }
    
    public List<PointF> getActionPoints() {
        return new ArrayList<>(actionPoints);
    }
    
    private PointF applyProximitySnapping(float x, float y) {
        FieldConfig.ActionZone nearestZone = fieldConfig.getZoneAt(x, y);
        if (nearestZone != null) {
            return new PointF(nearestZone.x, nearestZone.y);
        }
        return new PointF(x, y);
    }
    
    private String getSnapTarget(float x, float y) {
        FieldConfig.ActionZone zone = fieldConfig.getZoneAt(x, y);
        return zone != null ? zone.name : "field";
    }
    
    public void addActionPoint(float x, float y, String actionType) {
        actionPoints.add(new PointF(x, y));
        isWaitingForAction = false;
        invalidate();
    }
    
    public void cancelAction() {
        isWaitingForAction = false;
    }
}