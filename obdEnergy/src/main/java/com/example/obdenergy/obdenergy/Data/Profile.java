package com.example.obdenergy.obdenergy.Data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sumayyah on 5/8/14.
 */
public class Profile implements Parcelable{

    private String make;
    private String model;
    private String year;
    private String capacity;
    private String citympg;
    private String highwaympg;

    public Profile(String make, String model, String year, String capacity, String citympg, String highwaympg){
        this.make = make;
        this.model = model;
        this.year = year;
        this.capacity = capacity;
        this.citympg = citympg;
        this.highwaympg = highwaympg;
    }

    public Profile(Parcel in){
        make = in.readString();
        model = in.readString();
        year = in.readString();
        capacity = in.readString();
        citympg = in.readString();
        highwaympg = in.readString();
    }

    public String getMake(){return make;}
    public String getModel(){return model;}
    public String getYear(){return year;}
    public String getCapacity(){return capacity;}
    public String getCitympg(){return citympg;}
    public String getHighwaympg(){return highwaympg;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(make);
        out.writeString(model);
        out.writeString(year);
        out.writeString(capacity);
        out.writeString(citympg);
        out.writeString(highwaympg);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };
}
