package com.example.happyhourapp.models;

import android.util.ArrayMap;

public class HappyHour {

    private ArrayMap<String, String> happyHourTime;
    private String happyHourDesc;

    public HappyHour(ArrayMap<String, String> happyHourTime, String happyHourDesc) {
        this.happyHourTime = happyHourTime;
        this.happyHourDesc = happyHourDesc;
    }

    /**
     * Get Happy Hour Times as ArrayMap. Two values, one for "start", one for "end".
     * @return ArrayMap<String, String>
     */
    public ArrayMap<String, String> getHappyHourTime() {
       return this.happyHourTime;
    };

    /**
     * Get Happy Hour Description as String.
     * @return String
     */
    public String getHappyHourDescription() {
        return this.happyHourDesc;
    }

    /**
     * Get Happy Hour Time as String.
     * @return String
     */
    public ArrayMap<String, String> getHappyTime() { return this.happyHourTime; }
}
