package com.team2073.eagleforcescoutingapplication.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import com.team2073.eagleforcescoutingapplication.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FieldImageManager {
    
    private static FieldImageManager INSTANCE;
    private final Context context;
    private final SharedPreferences prefs;
    
    public static FieldImageManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new FieldImageManager(context);
        }
        return INSTANCE;
    }
    
    private FieldImageManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences("field_images", Context.MODE_PRIVATE);
    }
    
    public int getFieldImageResource(String position) {
        // Check for custom override first
        String fieldKey = getFieldKey(position);
        String customPath = prefs.getString("custom_" + fieldKey, null);
        if (customPath != null && new File(customPath).exists()) {
            return -1; // Indicates custom image should be loaded
        }
        
        // Return appropriate default resource based on position
        boolean isBlue = position.toLowerCase().startsWith("blue");
        return isBlue ? R.drawable.field_blue_side : R.drawable.field_red_side;
    }
    
    public String getCustomImagePath(String position) {
        String fieldKey = getFieldKey(position);
        return prefs.getString("custom_" + fieldKey, null);
    }
    
    public void setCustomImage(String position, Uri imageUri) {
        try {
            String fieldKey = getFieldKey(position);
            File customDir = new File(context.getFilesDir(), "custom_fields");
            if (!customDir.exists()) customDir.mkdirs();
            
            File customFile = new File(customDir, fieldKey + ".png");
            
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            
            FileOutputStream outputStream = new FileOutputStream(customFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
            
            prefs.edit().putString("custom_" + fieldKey, customFile.getAbsolutePath()).apply();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void clearCustomImage(String position) {
        String fieldKey = getFieldKey(position);
        String customPath = prefs.getString("custom_" + fieldKey, null);
        if (customPath != null) {
            new File(customPath).delete();
            prefs.edit().remove("custom_" + fieldKey).apply();
        }
    }
    
    private String getFieldKey(String position) {
        boolean isBlue = position.toLowerCase().startsWith("blue");
        return isBlue ? "blue" : "red";
    }
    
    public String getFieldDisplayName(String position) {
        boolean isBlue = position.toLowerCase().startsWith("blue");
        return isBlue ? "Blue Alliance Side" : "Red Alliance Side";
    }
}