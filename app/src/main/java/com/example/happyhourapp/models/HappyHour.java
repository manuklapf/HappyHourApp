package com.example.happyhourapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "happyhour")
public class HappyHour {

    @PrimaryKey
    private int happyHourId;
    private int barId;
    private String happyHourDay;
    private String happyHourTime;
    private String happyHourDesc;

    public HappyHour(int happyHourId, int barId, String happyHourDay, String happyHourTime, String happyHourDesc) {
        this.happyHourId = happyHourId;
        this.barId = barId;
        this.happyHourDay = happyHourDay;
        this.happyHourTime = happyHourTime;
        this.happyHourDesc = happyHourDesc;
    }


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

    //populate on first run
    public static HappyHour[] populateData() {
        return new HappyHour[]{
                new HappyHour(0, 0, "Monday", "20:00 Uhr - 22:00 Uhr", "Vodka-E 2€"),
                new HappyHour(1, 0, "Tuesday", "21:00 Uhr - 22:00 Uhr", "Vodka-Orange 2€"),
                new HappyHour(2, 0, "Thursday", "21:00 Uhr - 23:00 Uhr", "Pfeffi 1€"),
                new HappyHour(3, 1, "Wednesday", "20:00 Uhr - 22:00 Uhr", "Caipi 2€"),
                new HappyHour(4, 1, "Saturday", "21:00 Uhr - 22:00 Uhr", "Whisky Cola 2€"),
                new HappyHour(5, 1, "Sunday", "21:00 Uhr - 23:00 Uhr", "Berliner Luft 1€")
        };
    }


}
