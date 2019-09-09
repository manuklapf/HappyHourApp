package com.example.happyhourapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.happyhourapp.models.HappyHour;

@Dao
public interface HappyHourDAO {
    @Insert
    public void insert(HappyHour... HappyHours);

    @Update
    public void update(HappyHour... HappyHours);

    @Delete
    public void delete(HappyHour HappyHour);

    @Query("SELECT * FROM HappyHour")
    public HappyHour[] loadAllHappyHours();
}
