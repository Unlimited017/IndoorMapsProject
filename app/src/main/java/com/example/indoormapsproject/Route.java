package com.example.indoormapsproject;

import android.os.Build;

import java.util.ArrayList;
import java.util.Arrays;

public class Route {
    private ArrayList<Store> stores = new ArrayList<Store>();
    public Route(ArrayList<Store> stores) {
        this.stores.addAll(stores);
    }
    public ArrayList<Store> getStores(){
        return stores;
    }
    public int calculateTotalDistance(){
        int storesSize = this.getStores().size();
        return (int)(this.getStores().stream().mapToDouble(x -> {
            int storeIndex = this.getStores().indexOf(x);
            double returnValue = 0;
            if(storeIndex < storesSize-1) {
                returnValue = x.measureDistance(this.getStores().get(storeIndex+1));}
            return returnValue;
        }).sum() + this.getStores().get(storesSize-1).measureDistance(this.getStores().get(0)));
    }

    public String toString() {
        return Arrays.toString(stores.toArray());
    }
}
