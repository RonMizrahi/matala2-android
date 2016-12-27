package com.example.ron.matala2maps;

/**
 * Created by Ron on 27/12/2016.
 */

public class BleDevice {
    private String DeviceMac;
    private UserLocation loc;


    public BleDevice(){}
    public BleDevice(String DeviceMac,UserLocation loc)
    {
        this.DeviceMac=DeviceMac;
        this.loc=loc;
    }
    public String getDeviceMac() {
        return DeviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        DeviceMac = deviceMac;
    }

    public UserLocation getLoc() {
        return loc;
    }

    public void setLoc(UserLocation loc) {
        this.loc = loc;
    }
}
