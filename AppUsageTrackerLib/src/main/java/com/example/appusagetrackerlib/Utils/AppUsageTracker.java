package com.example.appusagetrackerlib.Utils;

import android.content.Context;
import android.content.Intent;

import com.example.appusagetrackerlib.Services.AppUsageService;

public class AppUsageTracker {
    public static void startTracking(Context context){
        Intent intent = new Intent(context, AppUsageService.class);
        context.startService(intent);
    }
    public static void stopTracking(Context context){
        Intent intent = new Intent(context, AppUsageService.class);
        context.stopService(intent);
    }
}