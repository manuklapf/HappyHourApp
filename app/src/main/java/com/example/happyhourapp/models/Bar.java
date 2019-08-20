package com.example.happyhourapp.models;


import android.location.Location;
import android.support.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bars")
public class Bar {
    @PrimaryKey
    @NonNull  private Long id;
    private String name;
    private String description;
    private Location location;
    private Long opening_hours;
    private Long happy_hours;
}
