package com.example.indoormapsproject;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class NearestNeighbor {
    public Route findShortestRoute(ArrayList<Store> stores){
        ArrayList<Store> shortestRouteStores = new ArrayList<Store>(stores.size());
//        System.out.println("--------------------------------------------------------");
//        System.out.println("Initial Route  => " + Arrays.toString(stores.toArray()));
//        System.out.println("w/ total distance: "+ new Route(stores).calculateTotalDistance());
//        System.out.println("--------------------------------------------------------");
        Store store = stores.get((int)(stores.size() * Math.random()));
        updateRoutes(shortestRouteStores,stores,store);
        while(stores.size() >= 1){
            store = getNextStore(stores,store);
            Log.i("Store ",store.getName());
            updateRoutes(shortestRouteStores,stores,store);
        }
        return new Route(shortestRouteStores);
    }

    public void updateRoutes(ArrayList<Store> shortestRouteStores, ArrayList<Store> stores, Store store) {
        shortestRouteStores.add(store);
        stores.remove(store);
//        System.out.println("Stores In Shortest Route ==> "+ Arrays.toString(shortestRouteStores.toArray()));
//        System.out.println("Remaining Stores         ==> "+ Arrays.toString(stores.toArray())+ "\n");
    }

    public Store getNextStore(ArrayList<Store> stores, Store store) {
        return stores.stream().min((store1, store2) -> {
            int flag = 0;
            if (store1.measureDistance(store) < store2.measureDistance(store)) flag = -1;
            else if (store1.measureDistance(store) > store2.measureDistance(store)) flag = 1;
            return flag;
        }).get();
    }
}
