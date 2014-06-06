package com.example.obdenergy.obdenergy.Data;

import com.example.obdenergy.obdenergy.Utilities.Calculations;
import com.example.obdenergy.obdenergy.Utilities.Console;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sumayyah on 5/13/14.
 */
public class Path implements Serializable{


    public static double initFuel = (double) 0.0;
    public static double initMAF = (double) 0.0;
    public static double finalFuel = (double) 0.0;
    public static double finalMAF = (double) 0.0;
    public static double gallonsUsed = (double) 0.0;
    public static double carbonUsed = (double)0.0;
    public static double treesKilled = (double) 0.0;
    public static String initTimestamp = "";
    public static String finalTimestamp = "";
    public static ArrayList<Integer> speedArray = new ArrayList<Integer>();
    public static ArrayList<Double> timeArray = new ArrayList<Double>();

    //TODO: calculate average speed and miles, add to Serializable
    public void setInitFuel(String val){
        int temp1 = Calculations.hexToInt(val);
        double temp = Double.parseDouble(String.valueOf(temp1));
        initFuel = temp;
    }
    public void setInitMAF(String val1, String val2){
        String strtemp = Calculations.getMAF(val1, val2);
        double temp = Double.parseDouble(String.valueOf(strtemp));
        initMAF = temp;
    }
    public void setFinalFuel(String val){
        int temp1 = Calculations.hexToInt(val);
        double temp = Double.parseDouble(String.valueOf(temp1));
        finalFuel = temp;
    }
    public void setFinalMAF(String val1, String val2){
        String strtemp = Calculations.getMAF(val1, val2);
        double temp = Double.parseDouble(String.valueOf(strtemp));
        finalMAF = temp;
    }
    public static void setInitTimestamp(String val){ initTimestamp = val;
        Console.log("Set init timestamp " + val);}
    public static void setFinalTimestamp(String val){ finalTimestamp = val;
        Console.log("Set final timestamp "+val);}
    //        public static void setStorageTime(Calendar val){storageTime = new StorageDate(val);}
    public static void addToSpeedArray(String val){
        int speedInt = Calculations.hexToInt(val);
        speedArray.add(speedInt);
    }
    /*Takes time in milliseconds, converts to seconds, and stores in array*/
    public static void addToTimeArray(String val){
        timeArray.add(Double.parseDouble(val));
    }
    public static Double getMiles(){
        Double finalMiles = 0.0;

        for(int i=0;i<speedArray.size();i++){
            double secondsPassed = timeArray.get(i) - (i==0 ? 0: timeArray.get(i-1));
            double hoursPassed = secondsPassed/3600;

            finalMiles += speedArray.get(i)*hoursPassed;
        }

        return finalMiles;
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

    public static String printSpeeds(){
        String returnString = "";
        for (Integer s: speedArray) returnString += (" "+s);
        return returnString;
    }

    public static String printTimes(){
        String returnString = "";
        for (Double t: timeArray) returnString += (" "+t);
        return returnString;
    }

    public void printData() {
        Console.log("Init fuel "+initFuel+" finalFuel "+finalFuel+" initMAF "+initMAF+" finalMAF "+finalMAF);
        Console.log("Speed array is: "+printSpeeds());
    }

    @Override
    public String toString(){
        return "Path [initFuel="+initFuel+", initMAF="+initMAF+", finalFuel="+finalFuel+", finalMAF="+finalMAF+", gallonsUsed="+gallonsUsed+", carbonUsed="+carbonUsed+", treesKilled="+treesKilled+", initTimestamp="+initTimestamp+", finalTimestamp="+finalTimestamp+"]";
    }
}
