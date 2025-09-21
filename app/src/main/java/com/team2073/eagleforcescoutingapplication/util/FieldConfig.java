package com.team2073.eagleforcescoutingapplication.util;

import android.content.Context;
import android.content.SharedPreferences;


import java.util.ArrayList;
import java.util.List;

public class FieldConfig {
    
    private static final String PREF_NAME = "field_config";
    private static final String KEY_ZONES = "action_zones";
    
    private SharedPreferences prefs;
    private List<ActionZone> zones;
    
    public static class ActionZone {
        public String name;
        public float x, y;
        public float radius;
        public String actions; // Comma-separated action types
        
        public ActionZone(String name, float x, float y, float radius, String actions) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.actions = actions;
        }
    }
    
    public FieldConfig(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        zones = new ArrayList<>();
        loadConfig();
    }
    
    public void addZone(String name, float x, float y, float radius, String actions) {
        zones.add(new ActionZone(name, x, y, radius, actions));
    }
    
    public void removeZone(String name) {
        zones.removeIf(zone -> zone.name.equals(name));
    }
    
    public List<ActionZone> getZones() {
        return new ArrayList<>(zones);
    }
    
    public ActionZone getZoneAt(float x, float y) {
        for (ActionZone zone : zones) {
            float distance = (float) Math.sqrt(Math.pow(x - zone.x, 2) + Math.pow(y - zone.y, 2));
            if (distance <= zone.radius) {
                return zone;
            }
        }
        return null;
    }
    
    public void saveConfig() {
        StringBuilder sb = new StringBuilder();
        for (ActionZone zone : zones) {
            sb.append(zone.name).append("|");
            sb.append(zone.x).append("|");
            sb.append(zone.y).append("|");
            sb.append(zone.radius).append("|");
            sb.append(zone.actions).append(";");
        }
        prefs.edit().putString(KEY_ZONES, sb.toString()).apply();
    }
    
    public void loadConfig() {
        String data = prefs.getString(KEY_ZONES, "");
        zones.clear();
        
        if (!data.isEmpty()) {
            String[] zoneStrings = data.split(";");
            for (String zoneString : zoneStrings) {
                if (!zoneString.trim().isEmpty()) {
                    String[] parts = zoneString.split("\\|");
                    if (parts.length == 5) {
                        zones.add(new ActionZone(
                            parts[0],
                            Float.parseFloat(parts[1]),
                            Float.parseFloat(parts[2]),
                            Float.parseFloat(parts[3]),
                            parts[4]
                        ));
                    }
                }
            }
        }
    }
    
    public String[] getActionsForZone(String zoneName) {
        for (ActionZone zone : zones) {
            if (zone.name.equals(zoneName)) {
                return zone.actions.split(",");
            }
        }
        return new String[]{"pickup_coral", "pickup_algae", "score_coral", "score_algae", "remove"};
    }
}