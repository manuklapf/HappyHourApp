package com.example.happyhourapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.happyhourapp.models.Bar;

@Dao
public interface BarDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Bar... bars);
    @Update
    public void update(Bar... bars);
    @Delete
    public void delete(Bar bar);

    @Query("SELECT * FROM Bar")
    public Bar[] loadAllBars();
}