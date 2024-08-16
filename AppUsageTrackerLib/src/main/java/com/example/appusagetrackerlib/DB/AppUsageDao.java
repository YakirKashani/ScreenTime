package com.example.appusagetrackerlib.DB;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AppUsageDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insert(AppUsageInfo appUsageInfo);

    @Query("SELECT * FROM AppUsageInfo")
    List<AppUsageInfo> getall();

    @Query("UPDATE AppUsageInfo SET usageTime = usageTime + :usageTime WHERE packageName= :packageName")
    void updateUsageTime(String packageName, long usageTime);
}
