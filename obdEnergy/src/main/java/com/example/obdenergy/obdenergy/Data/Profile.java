package com.example.obdenergy.obdenergy.Data;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.obdenergy.obdenergy.MainActivity;
import com.example.obdenergy.obdenergy.Utilities.Console;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sumayyah on 5/8/14.
 */
public class Profile{

    private static String make;
    private static String model;
    private static String year;
    private static String capacity;
    private static String citympg;
    private static String highwaympg;
    public static ArrayList<Path> pathArray = new ArrayList<Path>();
    public static JSONArray pathHistoryJSON;

    public Profile(){};

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


    public static void addToPathArray(Path p){
        pathArray.add(p);
        Console.log("Profile added path to array, size is "+pathArray.size());
    }

    public static String checkContents(){
        String returnString = "Make "+make+" Model "+model+" Year "+year+" Capacity "+capacity+" CityMPG "+citympg+" HighwayMPG"+highwaympg;
        return returnString;
    }

    public static boolean checkPath(Path p){
        Console.log("profile checking path");
        if(p == null) {
            Console.log("Profile: current Path is null");
            return false;
        } else if (pathArray.contains(p)){
            Console.log("Profile: current Path is duplicate");
            return false;
        }
        else return true;
    }
    public static void printPathArray(){
        int counter = 0;
        for(Path p: pathArray){
            Console.log("Path "+(++counter));
            p.printData();
        }
    }


}
