package com.example.happyhourapp.models;


import android.location.Location;
import android.util.ArrayMap;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bars")
public class Bar {
    @PrimaryKey
    private Long id;
    private String name;
    private String description;
    private Location location;
    private String opening_hours;
    private ArrayMap<String, ArrayMap<String, String>> happy_hours;
}
