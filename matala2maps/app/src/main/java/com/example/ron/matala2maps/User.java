package com.example.ron.matala2maps;

/**
 * Created by Vitaligever on 24/12/2016.
 */

public class User {
    private boolean isAdmin;
    private String androidID;
    private UserLocation location=null;
    private float speed;

    public User()
    {}

    public User(String androidID, boolean isAdmin) {
        this.androidID = androidID;
        this.isAdmin = isAdmin;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getAndroidID() {
        return androidID;
    }

    public void setAndroidID(String androidID) {
        this.androidID = androidID;
    }

    public UserLocation getLocation() {
        return location;
    }

    public void setLocation(UserLocation location) {
        this.location = location;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}

