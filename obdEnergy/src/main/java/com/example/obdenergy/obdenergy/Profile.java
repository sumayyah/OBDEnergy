package com.example.obdenergy.obdenergy;

import android.content.SharedPreferences;

/**
 * Created by sumayyah on 5/8/14.
 */
public class Profile {

    private String make;
    private String model;
    private String year;
    private String capacity;
    private String citympg;
    private String highwaympg;

    public void Profile(String make, String model, String year, String capacity, String citympg, String highwaympg){
        this.make = make;
        this.model = model;
        this.year = year;
        this.capacity = capacity;
        this.citympg = citympg;
        this.highwaympg = highwaympg;
    }

    public String getMake(){return make;}
    public String getModel(){return model;}
    public String getYear(){return year;}
    public String getCapacity(){return capacity;}
    public String getCitympg(){return citympg;}
    public String getHighwaympg(){return highwaympg;}
}
