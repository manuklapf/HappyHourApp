package com.example.happyhourapp.models;


import android.location.Location;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.happyhourapp.objects.HappyHour;

import java.util.ArrayList;

@Entity(tableName = "bars")
public class Bar {
    @PrimaryKey
    private Long id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "location")
    private Location location;

    @ColumnInfo(name = "opening_hours")
    private String opening_hours;

    private ArrayList<HappyHour> happy_hours;

    @ColumnInfo(name = "happy_hour")
    private String happy_hours_serialized;

    public ArrayList<HappyHour> getHappy_hours() {
        happy_hours_serialized.
        return happy_hours;
    }
}
