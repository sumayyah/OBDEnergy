package com.example.obdenergy.obdenergy;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by sumayyah on 5/8/14.
 */
public class Data implements Parcelable{


    private String gallons;
    private String miles;
    private String street;
    private String timestamp;
    private ArrayList<String> speed;


    public Data(String gallons, String miles, String timestamp){
        this.gallons = gallons;
        this.miles = miles;
        this.timestamp = timestamp;
    }

    public Data(Parcel in){
        gallons = in.readString();
        miles = in.readString();
        timestamp = in.readString();
    }

    public String getGallons() {return gallons;}
    public String getMiles(){return miles;}
    public String getStreet() {return street;}
    public String getTimestamp() {return timestamp;}

    public void setStreet(String street){
        this.street = street;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(gallons);
        out.writeString(miles);
        out.writeString(street);
        out.writeString(timestamp);
    }
}
