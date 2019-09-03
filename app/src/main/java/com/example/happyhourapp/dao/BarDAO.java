package com.example.happyhourapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

import com.example.happyhourapp.models.Bars;

@Dao
public interface BarDAO {

    @Insert
    public void insert(Bars... bars);
    @Update
    public void update(Bars... bars);
    @Delete
    public void delete(Bars bars);
}