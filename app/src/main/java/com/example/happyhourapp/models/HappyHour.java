package com.example.happyhourapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "happy_hour")
public class HappyHour {

    @PrimaryKey(autoGenerate = true)
    private int happyHourId;
    private int barId;
    private String happyHourDay;
    private String happyHourTime;
    private String happyHourDesc;


    //Getters
    public int getHappyHourId() {
        return happyHourId;
    }

    public int getBarId() {
        return barId;
    }

    public String getHappyHourDay() {
        return happyHourDay;
    }

    public String getHappyHourTime() {
        return happyHourTime;
    }

    public String getHappyHourDesc() {
        return happyHourDesc;
    }


    //Setters
    public void setHappyHourId(int happyHourId) {
        this.happyHourId = happyHourId;
    }

    public void setBarId(int barId) {
        this.barId = barId;
    }

    public void setHappyHourDay(String happyHourDay) {
        this.happyHourDay = happyHourDay;
    }

    public void setHappyHourTime(String happyHourTime) {
        this.happyHourTime = happyHourTime;
    }

    public void setHappyHourDesc(String happyHourDesc) {
        this.happyHourDesc = happyHourDesc;
    }

}
