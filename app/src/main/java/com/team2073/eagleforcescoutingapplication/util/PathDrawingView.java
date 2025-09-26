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
    private Paint dotPaint;
    private Path currentPath;
    private List<PointF> pathPoints;
    private List<List<PointF>> completedPaths;
    private PathDrawingListener listener;
    private boolean isDrawingEnabled = true;
    private float snapRadius = 30f;
    private List<ZoneDrawingView.Zone> zones;
    private ZoneDrawingView.Zone lastSnappedZone;
    private float boundaryLeft = 0f, boundaryTop = 0f, boundaryRight = 1000f, boundaryBottom = 1000f;
    
    public interface PathDrawingListener {
        void onPathPoint(PointF point, ZoneDrawingView.Zone snappedZone);
        void onPathCompleted(List<PointF> path);
        void onZoneSnapped(ZoneDrawingView.Zone zone, PointF point);
    }
    
    public PathDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    private void init() {
        pathPaint = new Paint();
        pathPaint.setColor(0xFF0066CC);
        pathPaint.setStrokeWidth(6f);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setAntiAlias(true);
        
        dotPaint = new Paint();
        dotPaint.setColor(0xFF0066CC);
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setAntiAlias(true);
        
        currentPath = new Path();
        pathPoints = new ArrayList<>();
        completedPaths = new ArrayList<>();
        zones = new ArrayList<>();
    }
    
    public void setZones(List<ZoneDrawingView.Zone> zones) {
        this.zones = zones != null ? zones : new ArrayList<>();
        invalidate();
    }
    
    public void setPathDrawingListener(PathDrawingListener listener) {
        this.listener = listener;
    }
    
    public void setDrawingEnabled(boolean enabled) {
        this.isDrawingEnabled = enabled;
    }
    
    public void setBoundaries(float left, float top, float right, float bottom) {
        this.boundaryLeft = left;
        this.boundaryTop = top;
        this.boundaryRight = right;
        this.boundaryBottom = bottom;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Draw completed paths
        for (List<PointF> path : completedPaths) {
            if (path.size() > 1) {
                Path drawPath = new Path();
                drawPath.moveTo(path.get(0).x, path.get(0).y);
                for (int i = 1; i < path.size(); i++) {
                    drawPath.lineTo(path.get(i).x, path.get(i).y);
                }
                canvas.drawPath(drawPath, pathPaint);
            }
            
            // Draw path points
            for (int i = 0; i < path.size(); i++) {
                PointF point = path.get(i);
                float radius = i == 0 ? 12f : 8f;
                canvas.drawCircle(point.x, point.y, radius, dotPaint);
            }
        }
        
        // Draw current path
        if (!pathPoints.isEmpty()) {
            if (pathPoints.size() > 1) {
                canvas.drawPath(currentPath, pathPaint);
            }
            
            // Draw current path points
            for (int i = 0; i < pathPoints.size(); i++) {
                PointF point = pathPoints.get(i);
                Paint currentDotPaint = new Paint(dotPaint);
                if (i == 0) {
                    currentDotPaint.setColor(0xFF00FF00); // Green for start
                }
                float radius = i == 0 ? 12f : 8f;
                canvas.drawCircle(point.x, point.y, radius, currentDotPaint);
            }
        }
        
        // Draw zone outlines for snapping reference
        Paint zonePaint = new Paint();
        zonePaint.setColor(0x44FF0000);
        zonePaint.setStyle(Paint.Style.STROKE);
        zonePaint.setStrokeWidth(2f);
        
        for (ZoneDrawingView.Zone zone : zones) {
            if (zone.points.size() > 2) {
                Path zonePath = new Path();
                zonePath.moveTo(zone.points.get(0).x, zone.points.get(0).y);
                for (int i = 1; i < zone.points.size(); i++) {
                    zonePath.lineTo(zone.points.get(i).x, zone.points.get(i).y);
                }
                zonePath.close();
                canvas.drawPath(zonePath, zonePaint);
            }
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isDrawingEnabled) return false;
        
        float x = event.getX();
        float y = event.getY();
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startNewPath(x, y);
                return true;
                
            case MotionEvent.ACTION_MOVE:
                // Only add point if moved significantly
                if (!pathPoints.isEmpty()) {
                    PointF lastPoint = pathPoints.get(pathPoints.size() - 1);
                    float distance = (float) Math.sqrt(Math.pow(x - lastPoint.x, 2) + Math.pow(y - lastPoint.y, 2));
                    if (distance > 20f) {
                        addPathPoint(x, y);
                    }
                }
                return true;
                
            case MotionEvent.ACTION_UP:
                finishPath();
                return true;
        }
        
        return false;
    }
    
    private void startNewPath(float x, float y) {
        pathPoints.clear();
        currentPath.reset();
        addPathPoint(x, y);
    }
    
    private void addPathPoint(float x, float y) {
        // Constrain to boundaries
        x = Math.max(boundaryLeft, Math.min(boundaryRight, x));
        y = Math.max(boundaryTop, Math.min(boundaryBottom, y));
        
        PointF point = new PointF(x, y);
        
        // Check for zone snapping
        ZoneDrawingView.Zone snappedZone = findNearestZone(x, y);
        if (snappedZone != null) {
            // Snap to zone center
            PointF center = getZoneCenter(snappedZone);
            point.set(center.x, center.y);
            
            if (snappedZone != lastSnappedZone && listener != null) {
                listener.onZoneSnapped(snappedZone, point);
                lastSnappedZone = snappedZone;
            }
        }
        
        pathPoints.add(point);
        
        if (pathPoints.size() == 1) {
            currentPath.moveTo(point.x, point.y);
        } else {
            currentPath.lineTo(point.x, point.y);
        }
        
        if (listener != null) {
            listener.onPathPoint(point, snappedZone);
        }
        
        invalidate();
    }
    
    private void finishPath() {
        if (pathPoints.size() > 1) {
            completedPaths.add(new ArrayList<>(pathPoints));
            if (listener != null) {
                listener.onPathCompleted(new ArrayList<>(pathPoints));
            }
        }
        
        pathPoints.clear();
        currentPath.reset();
        lastSnappedZone = null;
        invalidate();
    }
    
    private ZoneDrawingView.Zone findNearestZone(float x, float y) {
        ZoneDrawingView.Zone nearestZone = null;
        float nearestDistance = Float.MAX_VALUE;
        
        for (ZoneDrawingView.Zone zone : zones) {
            if (zone.containsPoint(x, y)) {
                PointF center = getZoneCenter(zone);
                float distance = (float) Math.sqrt(Math.pow(x - center.x, 2) + Math.pow(y - center.y, 2));
                if (distance < nearestDistance && distance < snapRadius) {
                    nearestDistance = distance;
                    nearestZone = zone;
                }
            }
        }
        
        return nearestZone;
    }
    
    private PointF getZoneCenter(ZoneDrawingView.Zone zone) {
        float centerX = 0f, centerY = 0f;
        for (PointF point : zone.points) {
            centerX += point.x;
            centerY += point.y;
        }
        centerX /= zone.points.size();
        centerY /= zone.points.size();
        return new PointF(centerX, centerY);
    }
    
    public void clearPaths() {
        completedPaths.clear();
        pathPoints.clear();
        currentPath.reset();
        lastSnappedZone = null;
        invalidate();
    }
    
    public void undoLastPath() {
        if (!completedPaths.isEmpty()) {
            completedPaths.remove(completedPaths.size() - 1);
            invalidate();
        } else if (!pathPoints.isEmpty()) {
            pathPoints.clear();
            currentPath.reset();
            invalidate();
        }
    }
    
    public List<List<PointF>> getCompletedPaths() {
        return new ArrayList<>(completedPaths);
    }
    
    public boolean hasActivePath() {
        return !pathPoints.isEmpty();
    }
}