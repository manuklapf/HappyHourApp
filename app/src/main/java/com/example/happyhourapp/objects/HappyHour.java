package com.example.happyhourapp.objects;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class HappyHour {

    private ArrayList<String> happyHourTimes;
    private String happyHourDesc;

    public HappyHour(ArrayList<String> happyHourTimes, String happyHourDesc) {
        this.happyHourTimes = happyHourTimes;
        this.happyHourDesc = happyHourDesc;
    }

    /**
     * Get Happy Hour Times as ArrayList. Two values, one for "start", one for "end".
     * @return ArrayList<String>
     */
    public ArrayList<String> getHappyHourTimes() {
       return this.happyHourTimes;
    }

    /**
     * Get Happy Hour Description as String.
     * @return String
     */
    public String getHappyHourDescription() {
        return this.happyHourDesc;
    }
}
