package com.example.obdenergy.obdenergy.Data;

import com.example.obdenergy.obdenergy.Utilities.Calculations;
import com.example.obdenergy.obdenergy.Utilities.Console;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by sumayyah on 5/13/14.
 */
public class Path {

    public static double initFuel = (double) 0.0;
    public static double initMAF = (double) 0.0;
    public static double finalFuel = (double) 0.0;
    public static double finalMAF = (double) 0.0;
    public String initDistance = "";
    public String finalDistance = "";
    public static String initTimestamp = "";
    public static String finalTimestamp = "";
    public static StorageDate storageTime;
    public static ArrayList<Integer> speedArray = new ArrayList<Integer>();
    public static ArrayList<Double> MAFArray = new ArrayList<Double>();

    public void setInitFuel(String val){
        int temp1 = Calculations.hexToInt(val);
        double temp = Double.parseDouble(String.valueOf(temp1));
        initFuel = temp;
    }
    public void setInitMAF(){
        initMAF = MAFArray.get(0);
    }
    public void addToMAFArray(String val1, String val2){
        String strtemp = Calculations.getMAF(val1, val2);
        double temp = Double.parseDouble(String.valueOf(strtemp));
        MAFArray.add(temp);
    }
    public void setFinalFuel(String val){
        int temp1 = Calculations.hexToInt(val);
        double temp = Double.parseDouble(String.valueOf(temp1));
        finalFuel = temp;
    }
    public void setFinalMAF(){
        finalMAF = MAFArray.get(MAFArray.size()-1);
    }
    public void setInitDistance(String val){ initDistance = val;}
    public void setFinalDistance(String val){ finalDistance = val;}
    public static void setInitTimestamp(String val){ initTimestamp = val;
        Console.log("Set init timestamp "+val);}
    public static void setFinalTimestamp(String val){ finalTimestamp = val;
        Console.log("Set final timestamp "+val);}
    public static void setStorageTime(Calendar val){storageTime = new StorageDate(val);}
    public static void addToSpeedArray(String val){
        int speedInt = Calculations.hexToInt(val);
        speedArray.add(speedInt);
    }

    public static double getInitFuel(){return initFuel; }
    public static double getFinalFuel(){return finalFuel;}
    public static double getInitMAF(){return initMAF;}
    public static double getFinalMAF(){return finalMAF;}
    public String getInitDistance(){ return initDistance; }
    public String getFinalDistance(){ return finalDistance; }
    public String getInitTimestamp(){ return initTimestamp; }
    public static String getfinalTime(){ return finalTimestamp; }
    public static String getInitTime(){return initTimestamp;}

    public static String printSpeeds(){
        String returnString = "";
        for (Integer s: speedArray) returnString += (" "+s);
        return returnString;
    }
    public static String printMAFs(){
        String returnString = "";
        for (Double d: MAFArray) returnString += (" "+d);
        return returnString;
    }
}
