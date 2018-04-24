package com.example.indoormapsproject;


import android.util.Log;

public class Store {
    public static final double EARTH_EQUATORIAL_RADIUS = 6378.1370D;
    public static final double CONVERT_DEGREES_TO_RADIANS = Math.PI/180D;
    public double longitude;
    public double latitude;
    private String name;

    public Store(String name,double latitude,double longitude){
        this.name = name;
        this.latitude = latitude * CONVERT_DEGREES_TO_RADIANS;
        this.longitude = longitude * CONVERT_DEGREES_TO_RADIANS;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double measureDistance(Store store){
        double deltaLatitude = (store.getLatitude() - this.getLatitude());
        double deltaLongitude = (store.getLongitude() - this.getLongitude());
        double a = Math.pow(Math.sin(deltaLatitude / 2D), 2D) +
                   Math.cos(this.getLatitude()) * Math.cos(store.getLatitude()) * Math.pow(Math.sin(deltaLongitude / 2D), 2D);
        double ret = (double) (EARTH_EQUATORIAL_RADIUS * 2D * Math.atan2(Math.sqrt(a),Math.sqrt(1D-a))*1000);
        Log.i("measure distance"+store.getName(),Double.toString(ret));
        return ret;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
