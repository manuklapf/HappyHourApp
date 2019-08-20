package com.example.happyhourapp;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.happyhourapp.dao.BarDAO;
import com.example.happyhourapp.models.Bar;

@Database(entities = {Bar.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BarDAO getItemDAO();
}
