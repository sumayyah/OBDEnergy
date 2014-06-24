package com.example.obdenergy.obdenergy.Data;

import com.example.obdenergy.obdenergy.Utilities.Calculations;
import com.example.obdenergy.obdenergy.Utilities.Console;
import com.google.gson.Gson;

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
//        calculateAvgSpeed();
    }
    public static void addToMAFarray(String val1, String val2){
        double value = Calculations.getMAF(val1, val2);
        MAFarray.add(value);
    }
    public static void calculateAvgSpeed(){

        DecimalFormat df = new DecimalFormat("#.00");
        double total = 0.0;
        for(double d: speedArray){
            total+=d;
        }
        double temp = 0.621371*(total/speedArray.size());
        averageSpeed = Double.valueOf(df.format(temp));
        Console.log("Path calculated average speed is "+averageSpeed);
    }
    /*Takes time in milliseconds, converts to seconds, and stores in array*/
    public static void addToTimeArray(String val){
        timeArray.add(Double.parseDouble(val));
    }
    public static Double getMiles(){
        Double finalMiles = 0.0;
        double secondsPassed = 0.0;
        DecimalFormat df = new DecimalFormat("#.00");

        Console.log("Path speed array size, time array size "+speedArray.size()+" "+timeArray.size());
        for(int i=0;i<=speedArray.size()-1;i++){
            if(i==0) secondsPassed = 0; /*Discard initial reading since speed at time 0 is negligible*/
            else secondsPassed = timeArray.get(i) -  timeArray.get(i-1);
            double hoursPassed = secondsPassed/3600;
            double kilometers = speedArray.get(i)*hoursPassed;
            finalMiles += (0.621371*kilometers);
//            Console.log("Path seconds, hours, speed, km, miles "+secondsPassed+" "+hoursPassed+" "+kilometers+" "+finalMiles);
        }
        Console.log("Path returning miles travelled "+finalMiles);
        finalMiles = Double.valueOf(df.format(finalMiles)); //TODO; test this
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

    public String printSpeeds(){
        String returnString = "";
        for (Integer s: speedArray) returnString += (" "+s);
        return returnString;
    }

    public String printTimes(){
        String returnString = "";
        for (Double t: timeArray) returnString += (" "+t);
        return returnString;
    }

    public void printData() {
        Console.log("Init fuel "+initFuel+" finalFuel "+finalFuel+" initMAF "+initMAF+" finalMAF "+finalMAF+" average speed "+averageSpeed+" initTime "+initTimestamp+" finalTime "+finalTimestamp);
        Console.log("Gallons "+gallonsUsed+", Carbon "+carbonUsed+", Trees "+treesKilled);
        Console.log("Speed array is: "+printSpeeds());
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
