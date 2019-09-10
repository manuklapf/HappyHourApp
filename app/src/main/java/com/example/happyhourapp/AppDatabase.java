package com.example.happyhourapp;

import android.content.Context;
import android.support.annotation.NonNull;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.happyhourapp.dao.BarDAO;
import com.example.happyhourapp.dao.BarHappyHoursDAO;
import com.example.happyhourapp.dao.HappyHourDAO;
import com.example.happyhourapp.models.Bar;
import com.example.happyhourapp.models.HappyHour;

import java.util.concurrent.Executors;

@Database(entities = {Bar.class, HappyHour.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BarDAO barDAO();
    public abstract BarHappyHoursDAO barHappyHoursDAO();
    public abstract HappyHourDAO happyHourDAO();
    private static AppDatabase INSTANCE;

    public static AppDatabase getAppDatabase(final Context context) {
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context,
                    AppDatabase.class,
                    "Bar_Database")
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull final SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    getAppDatabase(context).barDAO().insert(Bar.populateData());
                                    getAppDatabase(context).happyHourDAO().insert(HappyHour.populateData());
                                }
                            });
                        }
                    })
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

}
