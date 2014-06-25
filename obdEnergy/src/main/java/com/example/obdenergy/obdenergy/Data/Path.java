package com.example.obdenergy.obdenergy.Data;

import com.example.obdenergy.obdenergy.Utilities.Calculations;
import com.example.obdenergy.obdenergy.Utilities.Console;
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


    public static void setInitTimestamp(String val){ initTimestamp = val;
        Console.log("Set init timestamp " + val);}
    public static void setFinalTimestamp(String val){ finalTimestamp = val;
        Console.log("Set final timestamp "+val);}
    //        public static void setStorageTime(Calendar val){storageTime = new StorageDate(val);}
    public static void addToSpeedArray(String val){
        int speedInt = Calculations.hexToInt(val);
        speedArray.add(speedInt);
//        getAvgSpeed();
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

        if(speedArray.size() < 10) return false;
        for(int i=10;i<speedArray.size()-10;i++){
            if(speedArray.get(i) > 80 && speedArray.get(i-10) > 80 && speedArray.get(i+10) > 80)
                return true;
        }

        return false;
    }

    public static double getInitFuel(){return initFuel; }
    public static double getFinalFuel(){return finalFuel;}
    public static double getInitMAF(){return initMAF;}
    public static double getFinalMAF(){return finalMAF;}
    public static String getfinalTime(){ return finalTimestamp; }
    public static String getInitTime(){return initTimestamp;}


    public void printData() {
        Console.log("Init fuel "+initFuel+" finalFuel "+finalFuel+" initMAF "+initMAF+" finalMAF "+finalMAF+" average speed "+averageSpeed+" initTime "+initTimestamp+" finalTime "+finalTimestamp);
        Console.log("Gallons "+gallonsUsed+", Carbon "+carbonUsed+", Trees "+treesKilled);
        Console.log("Speed array is: "+printArray(speedArray));
        Console.log("MAF array is: "+printArray(MAFarray));
    }

    public String returnData(){
        String finalString = "";

        String constants = "Init fuel: "+initFuel+"\nFinalFuel: "+finalFuel+"\nAverage speed: "+averageSpeed+"\nMiles driven: "+milesTravelled;
        String calculations ="\nGallons: "+gallonsUsed+"\nCarbon: "+carbonUsed+"\nTrees: "+treesKilled;
        String speeds = "\nSpeeds are "+printArray(speedArray);
        String mafs = "\nMAF values are "+printArray(MAFarray);
        String times = "\n Time values are "+printArray(timeArray);

        finalString = finalString + constants+calculations+speeds+mafs+times;

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
    public String toString(){
        Gson gson = new Gson();
        String speedArrayFromJSON = gson.toJson(speedArray);
        String timeArrayFromJSON = gson.toJson(timeArray);

        return "Path: [initFuel="+initFuel+", initMAF="+initMAF+", finalFuel="+finalFuel+", finalMAF="+finalMAF+", gallonsUsed="+gallonsUsed+", carbonUsed="+carbonUsed+", treesKilled="+treesKilled+", initTimestamp="+initTimestamp+", finalTimestamp="+finalTimestamp+", gallonsUsed="+gallonsUsed+", carbonUsed="+carbonUsed+", treesKilled="+treesKilled+", averageSpeed="+averageSpeed+", speedArray="+speedArrayFromJSON+", timeArray="+timeArrayFromJSON+"]";
    }

    @Override
    public int compareTo(Path another) {

        long othertime = Long.parseLong(((Path) another).getInitTime());
        long thistime = Long.parseLong(this.getInitTime());

        return (int)(thistime-othertime);
    }
}
