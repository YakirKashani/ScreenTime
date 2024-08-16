package com.example.screentime;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appusagetrackerlib.DB.AppUsageDatabase;
import com.example.appusagetrackerlib.DB.AppUsageInfo;
import com.example.appusagetrackerlib.Utils.AppUsageTracker;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private TableLayout ActivityMain_TL_tableLayout;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        if(!hasUsageStatsPremission())
            requestUsageStatsPremission();
        else {
            AppUsageTracker.startTracking(this);
            loadData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(hasUsageStatsPremission())
            loadData();
    }

    private void loadData(){
        executorService.execute(() -> {
            AppUsageDatabase db = AppUsageDatabase.getDatabase(MainActivity.this);
            List<AppUsageInfo> appUsageInfoList = db.appUsageDao().getall();
            runOnUiThread(() -> {
                ActivityMain_TL_tableLayout.removeAllViews();
                addHeaderRow();
                populateTable(appUsageInfoList);
            });
        });
    }

    private void addHeaderRow(){
        TableRow row = new TableRow(this);
        TextView headerPackageName = new TextView(this);
        headerPackageName.setText("Package Name");
        headerPackageName.setPadding(8,8,8,8);
        headerPackageName.setTypeface(null, Typeface.BOLD);

        TextView headerUsageTime = new TextView(this);
        headerUsageTime.setText("Usage Time");
        headerUsageTime.setPadding(8,8,8,8);
        headerUsageTime.setTypeface(null, Typeface.BOLD);

        row.addView(headerPackageName);
        row.addView(headerUsageTime);

        ActivityMain_TL_tableLayout.addView(row);
    }


    private void findViews(){
        ActivityMain_TL_tableLayout = findViewById(R.id.ActivityMain_TL_tableLayout);
    }

    private void populateTable(List<AppUsageInfo> appUsageInfoList){

        for(AppUsageInfo info : appUsageInfoList){
            TableRow row = new TableRow(this);
            TextView packageNameTextView = new TextView(this);
            packageNameTextView.setText(info.packageName);
            packageNameTextView.setPadding(8,8,8,8);

            TextView usageTimeTextView = new TextView(this);
            usageTimeTextView.setText(formatUsageTime(info.usageTime));
            usageTimeTextView.setPadding(8,8,8,8);

            row.addView(packageNameTextView);
            row.addView(usageTimeTextView);

            ActivityMain_TL_tableLayout.addView(row);
        }
    }

    private String formatUsageTime(long usageTime){
        long seconds = (usageTime/1000) % 60;
        long minutes = (usageTime/(1000*60))%60;
        long hours = (usageTime/(1000*60*60)) % 24;
        return String.format("%02d:%02d:%02d",hours,minutes,seconds);
    }

    private boolean hasUsageStatsPremission(){
        AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private void requestUsageStatsPremission(){
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
        Toast.makeText(this, "Please grant Usage Access Premission", Toast.LENGTH_SHORT).show();
    }
}