package com.example.obdenergy.obdenergy;

import java.util.ArrayList;

/**
 * Created by sumayyah on 5/8/14.
 */
public class Data {


    private String gallons;
    private String miles;
    private String street;
    private String timestamp;
    private ArrayList<String> speed;


    public void Data(String gallons, String miles, String street, String timestamp){
        this.gallons = gallons;
        this.miles = miles;
        this.street = street;
        this.timestamp = timestamp;
    };

    public String getGallons() {return gallons;}
    public String getMiles(){return miles;}
    public String getStreet() {return street;}
    public String getTimestamp() {return timestamp;}

}
