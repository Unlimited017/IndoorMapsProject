package com.example.indoormapsproject;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class Driver {
    static String[] nameStore = new String[100];
    static double[] selectLat = new double[100];
    static double[] selectLong = new double[100];
    static int selectCount;
    public static ArrayList<Store> initialStores = new ArrayList<Store>();

    public static void initStore(){

        for(int i = 1 ; i < selectCount ; i++ ){
            Store a = new Store(nameStore[i],selectLat[i],selectLong[i]);
            Log.i("LAT",Double.toString(a.getLatitude()));
            Log.i("LONG",Double.toString(a.getLongitude()));
            initialStores.add(a);
        }
        Store b = new Store(nameStore[0],selectLat[0],selectLong[0]);
        initialStores.add(b);
    }
    public void main(String[] args){

        Driver driver = new Driver();
        ArrayList<Store> stores = new ArrayList<Store>();
        initStore();
        stores.addAll(driver.initialStores);
        driver.printShortestRoute(new NearestNeighbor().findShortestRoute(stores));
    }

    public void printShortestRoute(Route shortestRoute) {
//        System.out.println("--------------------------------------------------------");
//        System.out.println("Shortest route found so far: " + shortestRoute);
//        System.out.println("w/ total distance: "+ shortestRoute.calculateTotalDistance());
//        System.out.println("--------------------------------------------------------");
        MapsActivityIndoor.distanceAll =  shortestRoute.calculateTotalDistance();
    }
}
