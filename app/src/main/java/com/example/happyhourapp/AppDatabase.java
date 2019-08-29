package com.example.happyhourapp;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.happyhourapp.dao.BarDAO;
import com.example.happyhourapp.models.Bar;

@Database(entities = {Bar.class}, version = 1)
@TypeConverters({HappyHoursConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract BarDAO getBarDAO();
}
