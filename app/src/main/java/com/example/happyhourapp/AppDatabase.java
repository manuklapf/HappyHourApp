package com.example.happyhourapp;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.happyhourapp.dao.BarDAO;
import com.example.happyhourapp.dao.BarsHappyHoursDAO;
import com.example.happyhourapp.models.Bars;
import com.example.happyhourapp.models.HappyHour;

@Database(entities = {Bars.class, HappyHour.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BarDAO getBarDAO();
    public abstract BarsHappyHoursDAO getBarsHappyHoursDAO();
}
