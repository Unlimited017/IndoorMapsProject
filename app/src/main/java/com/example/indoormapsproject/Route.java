package com.example.indoormapsproject;

import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.indoormapsproject.Driver.selectCount;

public class Route {
    public ArrayList<Store> stores = new ArrayList<Store>();
    public Route(ArrayList<Store> stores) {
        this.stores.addAll(stores);
    }
    public ArrayList<Store> getStores(){
        return stores;
    }
    public double calculateTotalDistance(){
        int storesSize = this.getStores().size();

        Log.i("STORE SIZE IS  "," " + (this.getStores()));
         double ret = (double)(this.getStores().stream().mapToDouble(x -> {
            int storeIndex = this.getStores().indexOf(x);
            double returnValue = 0;
            if(storeIndex < storesSize-1) {
                returnValue = x.measureDistance(this.getStores().get(storeIndex+1));}
                Log.i("Return Value : " ,Double.toString(returnValue));
            return returnValue;
        }).sum() );
        Log.i("RETURN VALUE IS  "," " + (ret));
        return ret;
    }
    public double calculateTotalDuration(){
        // 1.4 = Average walk time (1.4 m/s)
        double time = calculateTotalDistance() / 1.4 ; //second
        Log.i("Time : " ,Double.toString(time));
        return time;
    }

    public String toString() {
        return Arrays.toString(stores.toArray());
    }
}
