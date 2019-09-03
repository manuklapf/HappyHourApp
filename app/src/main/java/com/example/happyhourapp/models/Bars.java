package com.example.happyhourapp.models;


import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.happyhourapp.BarLocation;

@Entity(tableName = "bars")
public class Bars {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int bar_id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @Embedded
    private BarLocation location;

    @ColumnInfo(name = "opening_hours")
    private String opening_hours;

    //Getters
    public int getBar_id() {
        return bar_id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BarLocation getLocation() {
        return location;
    }

    public String getOpening_hours() {
        return opening_hours;
    }

    //Setters
    public void setBar_id(int bar_id) {
        this.bar_id = bar_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(BarLocation location) {
        this.location = location;
    }

    public void setOpening_hours(String opening_hours) {
        this.opening_hours = opening_hours;
    }
}
