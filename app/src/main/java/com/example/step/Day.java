package com.example.step;

public class Day {
    private String date;
    private float kilometers;
    private String name;


    public Day(){
    }

    public Day(String date, float kilometers, String name) {
        this.date = date;
        this.kilometers = kilometers;
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public float getKilometers() {
        return kilometers;
    }

    public String getName() {
        return name;
    }
}
