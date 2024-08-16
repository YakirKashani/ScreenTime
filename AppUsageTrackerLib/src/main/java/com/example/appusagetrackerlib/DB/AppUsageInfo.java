package com.example.appusagetrackerlib.DB;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AppUsageInfo {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String packageName;
    public long usageTime;

    public AppUsageInfo(String packageName,long usageTime){
        this.packageName=packageName;
        this.usageTime=usageTime;
    }
}
