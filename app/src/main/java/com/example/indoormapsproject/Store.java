package com.example.indoormapsproject;


public class Store {
    private static final double EARTH_EQUATORIAL_RADIUS = 6378.1370D;
    private static final double CONVERT_DEGREES_TO_RADIANS = Math.PI/180D;
    private double longitude;
    private double latitude;
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
        return EARTH_EQUATORIAL_RADIUS * 2D * Math.atan2(Math.sqrt(a),Math.sqrt(1D-a));
    }

    @Override
    public String toString() {
        return this.name;
    }
}
