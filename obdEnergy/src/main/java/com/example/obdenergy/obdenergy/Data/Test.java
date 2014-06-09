package com.example.obdenergy.obdenergy.Data;

import com.example.obdenergy.obdenergy.Utilities.Calculations;
import com.example.obdenergy.obdenergy.Utilities.Console;

import java.util.ArrayList;

/**
 * Created by sumayyah on 6/8/14.
 */
public class Test {

    public Test(){}

    public static String authorFirstName = "Josephine";
    public static String authorLastName = "Tey";
    public int ID = 1;
    public String title = "The Franchise Affair";
    public ArrayList<String> array = new ArrayList<String>();
    public final String initFuel = "0.0";
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

    public void checkTest(){
        Console.log("Checking test");
        Console.log("Author "+authorFirstName+" "+authorLastName+" ID "+ID+" title "+title+" letters "+checkArray());
    }

    private String checkArray(){
        String finalString = "null";
        if(array.size() > 0){
            for(String s: array){
                finalString+=(" "+s);
            }
        }
        return finalString;
    }

    public static void addToSpeedArray(String val){
        int speedInt = Calculations.hexToInt(val);
//        calculateAvgSpeed();
    }
    public static void calculateAvgSpeed(){

    }
    /*Takes time in milliseconds, converts to seconds, and stores in array*/
    public static void addToTimeArray(String val){
    }
}
