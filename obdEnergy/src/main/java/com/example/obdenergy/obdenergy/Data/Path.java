package com.example.obdenergy.obdenergy.Data;

import com.example.obdenergy.obdenergy.Utilities.Calculations;

import java.util.ArrayList;

/**
 * Created by sumayyah on 5/13/14.
 */
public class Path {

    public static float initFuel = (float) 0.0;
    public static double initMAF = (double) 0.0;
    public static float finalFuel = (float) 0.0;
    public static double finalMAF = (double) 0.0;
    public String initDistance = "";
    public String finalDistance = "";
    public static String initTimestamp = "";
    public static String finalTimestamp = "";
    public Boolean city = false;
    public Boolean highway = false;
    public static ArrayList<Integer> speedArray = new ArrayList<Integer>();
    public static ArrayList<String> MAFArray = new ArrayList<String>();

//    public Path(){}

    public static void setInitFuel(String val){
        int temp1 = Calculations.hexToInt(val);
        float temp = Float.parseFloat(String.valueOf(temp1));
        initFuel = temp;
    }
    public static void setInitMAF(String val1, String val2){
        String strtemp = Calculations.getMAF(val1, val2);
        double temp = Double.parseDouble(String.valueOf(strtemp));
        initMAF = temp;
    }
    public static void setFinalFuel(String val){
        int temp1 = Calculations.hexToInt(val);
        float temp = Float.parseFloat(String.valueOf(temp1));
        finalFuel = temp;
    }
    public static void setFinalMAF(String val1, String val2){
        String strtemp = Calculations.getMAF(val1, val2);
        double temp = Double.parseDouble(String.valueOf(strtemp));
        finalMAF = temp;
    }
    public void setInitDistance(String val){ initDistance = val;}
    public void setFinalDistance(String val){ finalDistance = val;}
    public static void setInitTimestamp(String val){ initTimestamp = val;}
    public static void setFinalTimestamp(String val){ finalTimestamp = val;}
    public static void addToSpeedArray(String val){
        int speedInt = Calculations.hexToInt(val);
        speedArray.add(speedInt);
    }

    public static float getInitFuel(){return initFuel; }
    public static float getFinalFuel(){return finalFuel;}
    public static double getInitMAF(){return initMAF;}
    public static double getFinalMAF(){return finalMAF;}
    public String getInitDistance(){ return initDistance; }
    public String getFinalDistance(){ return finalDistance; }
    public String getInitTimestamp(){ return initTimestamp; }
    public static String getfinalTime(){ return finalTimestamp; }
    public static String getInitTime(){return initTimestamp;}
}
