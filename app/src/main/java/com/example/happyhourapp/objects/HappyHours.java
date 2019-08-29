package com.example.happyhourapp.objects;

import java.util.ArrayList;

public class HappyHours {

    private ArrayList<HappyHour> happyHours;

    public HappyHours(ArrayList<HappyHour> happyHours) {
        this.happyHours = happyHours;
    }

    /**
     * Get Happy Hours.
     * @return ArrayMap<String, HappyHour>
     */
    public ArrayList<HappyHour> getHappyHours() { return this.happyHours; }
}
