package com.example.happyhourapp.models;


import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.happyhourapp.BarLocation;

@Entity(tableName = "bar")
public class Bar {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int barId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @Embedded
    private BarLocation location;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "openingHours")
    private String openingHours;

    public Bar(int barId, String name, String description, BarLocation location, String openingHours) {
        this.barId = barId;
        this.name = name;
        this.description = description;
        this.location = location;
        this.openingHours = openingHours;
    }

    //Getters
    public int getBarId() { return barId; }

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

    public String getAddress() {
        return address;
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

    public void setAddress(String address) {
        this.address = address;
    }

    //populate on first run
    public static Bar[] populateData() {
        return new Bar[] {
        new Bar(0, "Coole Bar", "Gemütliches Ambiente in der Altstadt, mit toller Lage und guten Cocktails", new BarLocation(49.018178, 12.096371), "17:00 Uhr - 03:00 Uhr"),
        new Bar(1, "Coole Bar1", "Gemütliches Ambiente in der Altstadt, mit toller Lage und guten Cocktails 2", new BarLocation(49.018122, 12.105508), "17:00 Uhr - 03:00 Uhr"),
        new Bar(2, "Coole Bar2", "Gemütliches Ambiente in der Altstadt, mit toller Lage und guten Cocktails 3", new BarLocation(49.018122, 12.105008), "18:00 Uhr - 02:00 Uhr"),
        };
    }
}
