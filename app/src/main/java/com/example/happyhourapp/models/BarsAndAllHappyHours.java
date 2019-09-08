package com.example.happyhourapp.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class BarsAndAllHappyHours {

    @Embedded
    public Bars bars;

    @Relation(parentColumn = "id", entityColumn = "barId", entity = HappyHour.class)
    public List<HappyHour> happyHours;

    //Getters
    public Bars getBars() {
        return bars;
    }

    public List<HappyHour> getHappyHours() {
        return happyHours;
    }
}
