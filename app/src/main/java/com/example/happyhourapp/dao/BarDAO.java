package com.example.happyhourapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

import com.example.happyhourapp.models.Bar;

@Dao
public interface BarDAO {

    @Insert
    public void insert(Bar... bars);
    @Update
    public void update(Bar... bars);
    @Delete
    public void delete(Bar bar);
}