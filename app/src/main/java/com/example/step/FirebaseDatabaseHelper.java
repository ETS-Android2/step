package com.example.step;


import android.location.Location;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirebaseDatabaseHelper {
    private FirebaseDatabase db;
    private DatabaseReference referenceUsers;
    private DatabaseReference referenceDays;
    private DatabaseReference referenceWeeks;
    private DatabaseReference referenceMonths;
    private DatabaseReference referenceDirectories;

    private String key;


    public FirebaseDatabaseHelper() {
        db = FirebaseDatabase.getInstance();
        referenceUsers = db.getReference("users");

    }


    public FirebaseDatabaseHelper(String id){
        db = FirebaseDatabase.getInstance();
        referenceUsers = db.getReference("users");
        referenceDays = referenceUsers.child(id).child("days");
        referenceWeeks = referenceUsers.child(id).child("weeks");
        referenceMonths = referenceUsers.child(id).child("months");
        referenceDirectories = referenceUsers.child(id).child("directories");
        key=id;
    }

    public FirebaseDatabase getDb() {
        return db;
    }

    public DatabaseReference getReferenceDays() {
        return referenceDays;
    }

    public DatabaseReference getReferenceWeeks() {
        return referenceWeeks;
    }

    public DatabaseReference getReferenceMonths() {
        return referenceMonths;
    }

    public DatabaseReference getReferenceDirectories(){return referenceDirectories;}

    public String getKey() {
        return key;
    }

    public void addUser(String id, String email){
        User currUser = new User(id,email);
        referenceUsers.child(id).setValue(currUser);
        key =id;

    }
    public void addDay(String date,String name,float kilometers){
        Day day = new Day(date,kilometers,name);
        referenceDays.child(date).setValue(day);
    }

    public void addWeek(float kilometers){
        Week week = new Week(kilometers);
        referenceWeeks.push().setValue(week);
    }
    public void addMonth(String name,float kilometers){
        Month month = new Month(name,kilometers);
        referenceMonths.push().setValue(month);
    }
    public void addDirectory(String name, ArrayList<Location> locations){
        for(Location location:locations) {
            MapPoint point= new MapPoint(location);
            referenceDirectories.child(name).push().setValue(point);
        }
    }
    public void deleteDirectory(String name){
        referenceDirectories.child(name).setValue(null);
    }

}
