package com.example.happyhourapp.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.happyhourapp.models.BarsAndAllHappyHours;

@Dao
public interface BarsHappyHoursDAO {

    @Transaction
    @Query("SELECT * FROM Bars WHERE id = :barId")
    BarsAndAllHappyHours loadBarsAndAllHappyHours(long barId);
}
