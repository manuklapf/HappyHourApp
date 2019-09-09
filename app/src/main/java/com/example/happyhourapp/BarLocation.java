package com.example.happyhourapp;

public class BarLocation {

    private double latitude;
    private double longitude;

    public BarLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //Getters
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    //Setters
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
