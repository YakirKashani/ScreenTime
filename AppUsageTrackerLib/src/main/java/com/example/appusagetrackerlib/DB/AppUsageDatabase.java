package com.example.appusagetrackerlib.DB;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {AppUsageInfo.class}, version = 1)
public abstract class AppUsageDatabase extends RoomDatabase {
    public abstract AppUsageDao appUsageDao();
    private static volatile AppUsageDatabase INSTANCE;

    public static AppUsageDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (AppUsageDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppUsageDatabase.class,
                            "app-usage-db"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
