package com.example.indoormapsproject;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;

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

            return returnValue;
        }).sum() );
        Log.i("RETURN VALUE IS  "," " + (ret));
        return ret;
    }

    public String toString() {
        return Arrays.toString(stores.toArray());
    }
}
