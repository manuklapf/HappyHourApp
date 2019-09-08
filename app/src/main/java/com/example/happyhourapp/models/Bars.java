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
    private int barId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @Embedded
    private BarLocation location;

    @ColumnInfo(name = "openingHours")
    private String openingHours;

    //Getters
    public int getBarId() {
        return barId;
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

    public String getOpeningHours() {
        return openingHours;
    }

    //Setters
    public void setBarId(int barId) {
        this.barId = barId;
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

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }
}
