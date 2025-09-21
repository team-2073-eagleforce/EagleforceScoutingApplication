package com.team2073.eagleforcescoutingapplication.util;

import android.graphics.PointF;
import java.util.ArrayList;
import java.util.List;

public class ZoneConfigParser {
    
    public static void parseAndRestoreZones(ZoneDrawingView zoneDrawingView, String config) {
        parseAndRestoreZonesRelative(zoneDrawingView, config);
    }
    
    public static void parseAndRestoreZonesRelative(ZoneDrawingView zoneDrawingView, String config) {
        if (config == null || config.isEmpty() || !config.startsWith("FIELD_CONFIG|")) {
            return;
        }
        
        try {
            String[] parts = config.split("\\|");
            
            // Clear existing zones
            zoneDrawingView.clearZones();
            
            // Get current field boundaries for relative coordinate conversion
            float[] boundaries = zoneDrawingView.getFieldBoundaries();
            float fieldLeft = boundaries[0];
            float fieldTop = boundaries[1];
            float fieldRight = boundaries[2];
            float fieldBottom = boundaries[3];
            float fieldWidth = fieldRight - fieldLeft;
            float fieldHeight = fieldBottom - fieldTop;
            
            // Determine starting index (account for image hash)
            int startIndex = 5; // Default with image hash
            if (parts.length > 4 && !parts[4].startsWith("IMAGE_HASH:")) {
                startIndex = 4; // No image hash
            }
            
            // Parse zones
            for (int i = startIndex; i < parts.length; i += 6) {
                if (i + 5 < parts.length && "ZONE".equals(parts[i])) {
                    ZoneDrawingView.Zone zone = new ZoneDrawingView.Zone();
                    
                    // Parse zone name
                    zone.name = parts[i + 1];
                    if ("Unnamed".equals(zone.name)) {
                        zone.name = null;
                    }
                    
                    // Parse zone type
                    zone.type = parts[i + 2];
                    if ("Normal".equals(zone.type)) {
                        zone.type = null;
                    }
                    
                    // Parse zone points (convert from relative to absolute)
                    String pointsStr = parts[i + 3];
                    if (!pointsStr.isEmpty()) {
                        String[] pointPairs = pointsStr.split(";");
                        for (String pointPair : pointPairs) {
                            if (!pointPair.trim().isEmpty()) {
                                String[] coords = pointPair.split(",");
                                if (coords.length == 2) {
                                    float relativeX = Float.parseFloat(coords[0]);
                                    float relativeY = Float.parseFloat(coords[1]);
                                    
                                    // Convert relative coordinates to absolute
                                    float absoluteX = fieldLeft + (relativeX * fieldWidth);
                                    float absoluteY = fieldTop + (relativeY * fieldHeight);
                                    
                                    zone.points.add(new PointF(absoluteX, absoluteY));
                                }
                            }
                        }
                    }
                    
                    // Parse zone actions
                    String actionsStr = parts[i + 4];
                    if (!actionsStr.isEmpty()) {
                        String[] actions = actionsStr.split(",");
                        for (String action : actions) {
                            if (!action.trim().isEmpty()) {
                                zone.actions.add(action.trim());
                            }
                        }
                    }
                    
                    // Parse lock state
                    String lockStr = parts[i + 5];
                    zone.isLocked = "1".equals(lockStr);
                    
                    // Set default color
                    zone.fillPaint.setColor(0x4400FF00);
                    
                    // Add zone to view if it has valid points
                    if (zone.points.size() >= 3) {
                        zoneDrawingView.getZonesReference().add(zone);
                    }
                }
            }
            
            zoneDrawingView.invalidate();
            
        } catch (Exception e) {
            android.util.Log.e("ZoneConfigParser", "Error parsing zones: " + e.getMessage());
        }
    }
}