package com.example.happyhourapp.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class BarAndAllHappyHours {

    @Embedded
    public Bar bar;

    @Relation(parentColumn = "id", entityColumn = "barId", entity = HappyHour.class)
    public List<HappyHour> happyHours;

    public BarAndAllHappyHours(Bar bar, List<HappyHour> happyHours) {
        this.bar = bar;
        this.happyHours = happyHours;
    }

    //Getters
    public Bar getBar() {
        return bar;
    }

    public List<HappyHour> getHappyHours() {
        return happyHours;
    }

}
