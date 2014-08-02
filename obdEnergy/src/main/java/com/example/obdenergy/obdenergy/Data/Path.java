package com.example.obdenergy.obdenergy.Data;

import com.example.obdenergy.obdenergy.Utilities.Calculations;
import com.example.obdenergy.obdenergy.Utilities.Console;
import com.example.obdenergy.obdenergy.Utilities.DataLogger;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by sumayyah on 5/13/14.
 */
public class Path implements Comparable<Path>{


    public static Double initFuel = (double) 0.0;
    public static Double initMAF = (double) 0.0;
    public static Double finalFuel = (double) 0.0;
    public static Double finalMAF = (double) 0.0;
    public static Double gallonsUsed = (double) 0.0;
    public static Double carbonUsed = (double)0.0;
    public static Double treesKilled = (double) 0.0;
    public static Double averageSpeed = (double) 0.0;
    public static Double milesTravelled = (double) 0.0;
    public static String initTimestamp = "";
    public static String finalTimestamp = "";
    public static String username = "";
    public static ArrayList<Integer> speedArray = new ArrayList<Integer>();
    public static ArrayList<Double> timeArray = new ArrayList<Double>();
    public static ArrayList<Double> MAFarray = new ArrayList<Double>();

    public Path(){}

    public static void setInitFuel(String val){
        int temp1 = Calculations.hexToInt(val);
        double temp = Double.parseDouble(String.valueOf(temp1));
        initFuel = temp;
    }

    public static void setFinalFuel(String val){
        int temp1 = Calculations.hexToInt(val);
        double temp = Double.parseDouble(String.valueOf(temp1));
        finalFuel = temp;
    }

    public static void setInitTimestamp(String val){ initTimestamp = val;}
    public static void setFinalTimestamp(String val){ finalTimestamp = val;}
    public static void addToSpeedArray(String val){
        int speedInt = Calculations.hexToInt(val);
        speedArray.add(speedInt);
    }
    public static void addToMAFarray(String val1, String val2){
        double value = Calculations.getMAF(val1, val2);
        MAFarray.add(value);
    }

    /*Takes time in milliseconds, converts to seconds, and stores in array*/
    public static void addToTimeArray(String val){
        timeArray.add(Double.parseDouble(val));
    }

    public static boolean isHighway(){

        if(speedArray.size() < 20) return false;
        for(int i=10;i<speedArray.size()-10;i++){
            if(speedArray.get(i) > 80 && speedArray.get(i-10) > 80 && speedArray.get(i+10) > 80)
                return true;
        }
        return false;
    }

    public static double getInitFuel(){return initFuel; }
    public static double getFinalFuel(){return finalFuel;}
    public static String getInitTime(){return initTimestamp;}


    public void printData() {
        DataLogger.writeConsoleData("Name " + username);
        DataLogger.writeConsoleData("Init fuel "+initFuel+" finalFuel "+finalFuel+" initMAF "+initMAF+" finalMAF "+finalMAF+" initTime "+initTimestamp+" finalTime "+finalTimestamp);
        DataLogger.writeConsoleData("Gallons "+gallonsUsed+", Carbon "+carbonUsed+", Trees "+treesKilled);
        DataLogger.writeConsoleData("Miles travelled "+milesTravelled+" average speed "+averageSpeed);
        DataLogger.writeConsoleData("Speed array is: "+printArray(speedArray));
        DataLogger.writeConsoleData("MAF array is: "+printArray(MAFarray));
    }

    public String returnData(){
        String finalString = "";

        String name = "Name: "+username;
        String constants = "\nInit fuel: "+initFuel+"\nFinalFuel: "+finalFuel+"\nAverage speed: "+averageSpeed+"\nMiles driven: "+milesTravelled;
        String calculations ="\nGallons: "+gallonsUsed+"\nCarbon: "+carbonUsed+"\nTrees: "+treesKilled;
        String speeds = "\nSpeeds are "+printArray(speedArray);
        String mafs = "\nMAF values are "+printArray(MAFarray);
        String times = "\n Time values are "+printArray(timeArray);

        finalString = finalString + name+constants+calculations+speeds+mafs+times;

        return finalString;
    }

    public String printArray(ArrayList<?> list){
        String returnString = "";

        for(int i=0;i<list.size()-1;i++){
            returnString += (" "+list.get(i));
        }
        return returnString;
    }

    @Override
    public int compareTo(Path another) {

        long othertime = Long.parseLong(((Path) another).getInitTime());
        long thistime = Long.parseLong(this.getInitTime());

        return (int)(thistime-othertime);
    }
}
