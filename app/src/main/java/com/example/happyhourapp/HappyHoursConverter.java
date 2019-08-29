package com.example.happyhourapp;

import android.os.Build;
import android.util.ArrayMap;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import com.example.happyhourapp.objects.HappyHour;

import java.util.ArrayList;

public class HappyHoursConverter {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @TypeConverter
        public ArrayList<HappyHour> toHappyHours(HappyHour happyHour) {
            ArrayList<String> happyHours = new ArrayList<>();
            happyHours.add(happyHour);
            return happyHours;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @TypeConverter
        public HappyHour toHappyHour(ArrayMap<String, HappyHour> happyHours, String key) {
            HappyHour happyHour = happyHours.get(key);
            return happyHour;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @TypeConverter
        public String happyHourTimestoString(HappyHour happyHour) {
            return happyHour.getHappyHourTimes().toString();
        }
}
