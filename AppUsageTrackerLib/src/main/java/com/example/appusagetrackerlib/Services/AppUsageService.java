package com.example.appusagetrackerlib.Services;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.appusagetrackerlib.DB.AppUsageDao;
import com.example.appusagetrackerlib.DB.AppUsageDatabase;
import com.example.appusagetrackerlib.DB.AppUsageInfo;

import java.util.Calendar;
import java.util.List;

public class AppUsageService extends Service {
    public static final String TAG = "AppUsageService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(this::trackAppUsage).start();
        return START_STICKY;
    }

    private void trackAppUsage(){
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        long startTime = calendar.getTimeInMillis();
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,startTime,endTime);
        AppUsageDatabase db = AppUsageDatabase.getDatabase(getApplicationContext());
        AppUsageDao appUsageDao = db.appUsageDao();
        for(UsageStats usageStats : usageStatsList){
            String packageName = usageStats.getPackageName();
            long totalTime = usageStats.getTotalTimeInForeground();
            if(totalTime>0){
                List<AppUsageInfo> existingRecords = appUsageDao.getall();
                boolean isExisting = false;
                for(AppUsageInfo info: existingRecords){
                    if(info.packageName.equals(packageName)){
                        isExisting = true;
                        appUsageDao.updateUsageTime(packageName,totalTime);
                        break;
                    }
                }
                if(!isExisting) {
                    AppUsageInfo appUsageInfo = new AppUsageInfo(packageName, totalTime);
                    appUsageDao.insert(appUsageInfo);
                }
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
