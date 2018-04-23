package com.example.indoormapsproject;

import java.util.ArrayList;
import java.util.Arrays;

public class Driver {
    static String[] nameStore;
    static double[] selectLat;
    static double[] selectLong;
    static int selectCount;
    private ArrayList<Store> initialStores = new ArrayList<Store>(Arrays.asList(
            new Store("CurrentLocation", selectLat[0], selectLong[0])
    ));
    public void initStore(){
        for(int i = 1 ; i <= selectCount ; i++ ){
            Store a = new Store(nameStore[i],selectLat[i],selectLong[i]);
            initialStores.add(a);
        }
    }
    public void main(String[] args){
        Driver driver = new Driver();
        ArrayList<Store> stores = new ArrayList<Store>();
        initStore();
        stores.addAll(driver.initialStores);
        driver.printShortestRoute(new NearestNeighbor().findShortestRoute(stores));
    }

    private void printShortestRoute(Route shortestRoute) {
        System.out.println("--------------------------------------------------------");
        System.out.println("Shortest route found so far: " + shortestRoute);
        System.out.println("w/ total distance: "+ shortestRoute.calculateTotalDistance());
        System.out.println("--------------------------------------------------------");
    }
}
