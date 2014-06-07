package com.example.obdenergy.obdenergy.Data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by sumayyah on 5/8/14.
 */
public class Profile implements Parcelable{

    private static String make;
    private static String model;
    private static String year;
    private static String capacity;
    private static String citympg;
    private static String highwaympg;
    public static ArrayList<Path> pathArray;
    public static ArrayList<String> pathArr; //TODO: change all arrays to path types



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
    public static String getCapacity(){return capacity;}
    public static String getCitympg(){return citympg;}
    public static String getHighwaympg(){return highwaympg;}

    public static void setMake(String make) {
        Profile.make = make;}
    public static void setModel(String model) {
        Profile.model = model;}
    public static void setYear(String year){
        Profile.year = year;}
    public static void setCapacity(String capacity) {
        Profile.capacity = capacity;}
    public static void setCitympg(String citympg) {
        Profile.citympg = citympg;}
    public static void setHighwaympg(String highwaympg) {
        Profile.highwaympg = highwaympg;}

    public static void setPathArray(ArrayList<String> array){
        for(String s: array){
            pathArr.add(s);
        }
    }
    public static String checkContents(){
        String returnString = "Make "+make+" Model "+model+" Year "+year+" Capacity "+capacity+" CityMPG "+citympg+" HighwayMPG"+highwaympg;
        return returnString;
    }

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
