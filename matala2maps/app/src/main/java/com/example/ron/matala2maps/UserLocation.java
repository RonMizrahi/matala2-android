package com.example.ron.matala2maps;

/**
 * Created by Vitaligever on 24/12/2016.
 */

public class UserLocation {
    private double latitude,longitude;

    UserLocation(){
        latitude = 0;
        longitude = 0;
    }
    UserLocation(double decimalDegree1,double decimalDegree2){
        this.longitude = decimalDegree2;
        this.latitude = decimalDegree1;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
