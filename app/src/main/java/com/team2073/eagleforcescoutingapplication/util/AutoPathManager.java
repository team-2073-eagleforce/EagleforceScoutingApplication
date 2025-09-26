package com.team2073.eagleforcescoutingapplication.util;

import android.graphics.PointF;
import java.util.ArrayList;
import java.util.List;

public class AutoPathManager {
    
    public static class PathAction {
        public float x, y;
        public String actionType;
        public String zoneName;
        public long timestamp;
        public long duration; // Time from previous action
        
        public PathAction(float x, float y, String actionType, String zoneName, long timestamp, long duration) {
            this.x = x;
            this.y = y;
            this.actionType = actionType;
            this.zoneName = zoneName;
            this.timestamp = timestamp;
            this.duration = duration;
        }
    }
    
    public static class PathSegment {
        public List<PointF> points;
        public long startTime;
        public long endTime;
        
        public PathSegment() {
            points = new ArrayList<>();
        }
    }
    
    private List<PathAction> actions;
    private List<PathSegment> pathSegments;
    private long autoStartTime;
    private long lastActionTime;
    private PathSegment currentSegment;
    
    public AutoPathManager() {
        actions = new ArrayList<>();
        pathSegments = new ArrayList<>();
        reset();
    }
    
    public void startAuto() {
        autoStartTime = System.currentTimeMillis();
        lastActionTime = autoStartTime;
        currentSegment = new PathSegment();
        currentSegment.startTime = autoStartTime;
    }
    
    public void addPathPoint(float x, float y) {
        if (currentSegment != null) {
            currentSegment.points.add(new PointF(x, y));
        }
    }
    
    public void addAction(float x, float y, String actionType, String zoneName) {
        long currentTime = System.currentTimeMillis();
        long duration = currentTime - lastActionTime;
        
        PathAction action = new PathAction(x, y, actionType, zoneName, currentTime, duration);
        actions.add(action);
        
        // End current segment and start new one
        if (currentSegment != null) {
            currentSegment.endTime = currentTime;
            pathSegments.add(currentSegment);
        }
        
        currentSegment = new PathSegment();
        currentSegment.startTime = currentTime;
        lastActionTime = currentTime;
    }
    
    public void endAuto() {
        if (currentSegment != null) {
            currentSegment.endTime = System.currentTimeMillis();
            pathSegments.add(currentSegment);
        }
    }
    
    public void reset() {
        actions.clear();
        pathSegments.clear();
        autoStartTime = 0;
        lastActionTime = 0;
        currentSegment = null;
    }
    
    public List<PathAction> getActions() {
        return new ArrayList<>(actions);
    }
    
    public List<PathSegment> getPathSegments() {
        return new ArrayList<>(pathSegments);
    }
    
    public long getElapsedTime() {
        if (autoStartTime == 0) return 0;
        return System.currentTimeMillis() - autoStartTime;
    }
    
    public long getRemainingTime() {
        long elapsed = getElapsedTime();
        return Math.max(0, 15000 - elapsed); // 15 second auto period
    }
    
    public String exportToJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"autoStartTime\":").append(autoStartTime).append(",");
        json.append("\"actions\":[");
        
        for (int i = 0; i < actions.size(); i++) {
            PathAction action = actions.get(i);
            json.append("{");
            json.append("\"x\":").append(action.x).append(",");
            json.append("\"y\":").append(action.y).append(",");
            json.append("\"actionType\":\"").append(action.actionType).append("\",");
            json.append("\"zoneName\":\"").append(action.zoneName).append("\",");
            json.append("\"timestamp\":").append(action.timestamp).append(",");
            json.append("\"duration\":").append(action.duration);
            json.append("}");
            if (i < actions.size() - 1) json.append(",");
        }
        
        json.append("],");
        json.append("\"pathSegments\":[");
        
        for (int i = 0; i < pathSegments.size(); i++) {
            PathSegment segment = pathSegments.get(i);
            json.append("{");
            json.append("\"startTime\":").append(segment.startTime).append(",");
            json.append("\"endTime\":").append(segment.endTime).append(",");
            json.append("\"points\":[");
            
            for (int j = 0; j < segment.points.size(); j++) {
                PointF point = segment.points.get(j);
                json.append("{\"x\":").append(point.x).append(",\"y\":").append(point.y).append("}");
                if (j < segment.points.size() - 1) json.append(",");
            }
            
            json.append("]}");
            if (i < pathSegments.size() - 1) json.append(",");
        }
        
        json.append("]}");
        return json.toString();
    }
}