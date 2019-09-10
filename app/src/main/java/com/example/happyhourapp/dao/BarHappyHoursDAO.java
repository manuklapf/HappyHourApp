package com.example.happyhourapp.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.happyhourapp.models.BarAndAllHappyHours;

@Dao
public interface BarHappyHoursDAO {

    @Transaction
    @Query("SELECT * FROM Bar WHERE id = :barId")
    public BarAndAllHappyHours loadBarAndAllHappyHours(int barId);

    @Query("SELECT COUNT(id) FROM Bar WHERE id != null")
    int getCount();
}
