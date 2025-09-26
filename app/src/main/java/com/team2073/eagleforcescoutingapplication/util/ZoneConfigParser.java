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
            
            // Determine starting index (account for image hash and scale)
            int startIndex = 4;
            if (parts.length > 4 && parts[4].startsWith("IMAGE_HASH:")) {
                startIndex = 5;
            }
            if (parts.length > startIndex && parts[startIndex].startsWith("SCALE:")) {
                startIndex++; // Skip scale factor
            }
            
            // Parse zones (adjust step size for potential scale factor)
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
    
    public static void parseFieldConfigToFieldConfig(FieldConfig fieldConfig, String config) {
        if (config == null || config.isEmpty() || !config.startsWith("FIELD_CONFIG|")) {
            return;
        }
        
        try {
            String[] parts = config.split("\\|");
            
            // Parse field boundaries
            if (parts.length > 1) {
                String[] boundaries = parts[1].split(",");
                if (boundaries.length == 4) {
                    float fieldLeft = Float.parseFloat(boundaries[0]);
                    float fieldTop = Float.parseFloat(boundaries[1]);
                    float fieldRight = Float.parseFloat(boundaries[2]);
                    float fieldBottom = Float.parseFloat(boundaries[3]);
                    float fieldWidth = fieldRight - fieldLeft;
                    float fieldHeight = fieldBottom - fieldTop;
                    
                    // Clear existing zones in FieldConfig
                    // fieldConfig.clearZones(); // Assuming this method exists
                    
                    // Determine starting index for zones
                    int startIndex = 4;
                    if (parts.length > 4 && parts[4].startsWith("IMAGE_HASH:")) {
                        startIndex = 5;
                    }
                    if (parts.length > startIndex && parts[startIndex].startsWith("SCALE:")) {
                        startIndex++;
                    }
                    
                    // Parse zones and add to FieldConfig
                    for (int i = startIndex; i < parts.length; i += 6) {
                        if (i + 5 < parts.length && "ZONE".equals(parts[i])) {
                            String zoneName = parts[i + 1];
                            String zoneType = parts[i + 2];
                            String pointsStr = parts[i + 3];
                            String actionsStr = parts[i + 4];
                            
                            if ("Unnamed".equals(zoneName)) zoneName = "Zone";
                            
                            // Calculate zone center from points
                            float centerX = fieldLeft + fieldWidth / 2;
                            float centerY = fieldTop + fieldHeight / 2;
                            
                            if (!pointsStr.isEmpty()) {
                                String[] pointPairs = pointsStr.split(";");
                                float sumX = 0, sumY = 0;
                                int pointCount = 0;
                                
                                for (String pointPair : pointPairs) {
                                    if (!pointPair.trim().isEmpty()) {
                                        String[] coords = pointPair.split(",");
                                        if (coords.length == 2) {
                                            float relativeX = Float.parseFloat(coords[0]);
                                            float relativeY = Float.parseFloat(coords[1]);
                                            
                                            float absoluteX = fieldLeft + (relativeX * fieldWidth);
                                            float absoluteY = fieldTop + (relativeY * fieldHeight);
                                            
                                            sumX += absoluteX;
                                            sumY += absoluteY;
                                            pointCount++;
                                        }
                                    }
                                }
                                
                                if (pointCount > 0) {
                                    centerX = sumX / pointCount;
                                    centerY = sumY / pointCount;
                                }
                            }
                            
                            // Add zone to FieldConfig
                            float radius = Math.min(fieldWidth, fieldHeight) * 0.1f; // Default radius
                            fieldConfig.addZone(zoneName, centerX, centerY, radius, actionsStr);
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            android.util.Log.e("ZoneConfigParser", "Error parsing field config: " + e.getMessage());
        }
    }
}