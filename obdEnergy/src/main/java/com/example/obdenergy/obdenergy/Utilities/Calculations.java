package com.example.obdenergy.obdenergy.Utilities;

/**
 * Created by sumayyah on 5/13/14.
 */
public class Calculations {

    public static String getGallons(double initFuel, double finalFuel, String tankCapacity){
        String finalGallonString = "";
        Console.log("Calculations - initfuel, final fuel, tankcapacity"+initFuel+" "+finalFuel+" "+tankCapacity);
        int tankCapacityNum = Integer.parseInt(tankCapacity);

        double fuelVal = finalFuel - initFuel;
        double percentage = (fuelVal/255);
        double gallons = percentage*tankCapacityNum;

        finalGallonString = String.valueOf(gallons);

        return finalGallonString;
    }

    public static String getGallons(double initMAF, double finalMAF, String initTime, String finalTime){
        String finalGallonString = "";

        //TODO: calculate time + redo System.gettime with java timestamps.
        int timeTaken = 3; //In seconds - TODO: double check time in formula - time taken per MAF reading?
        double initFuel = 1/(14.75*6.26*initMAF+timeTaken);
        double finalFuel = 1/(14.75*6.26*finalMAF+timeTaken);

        double fuelUsed = finalFuel - initFuel;

        finalGallonString = String.valueOf(fuelUsed);

        return finalGallonString; //TODO: double check - does formula give back gallons?
    }

    public static String getGallons(String mpg, String miles){
        String finalGallonString = "";

        int mpgNum = Integer.parseInt(mpg);
        int milesNum = Integer.parseInt(miles);
        double multiplier = 1/mpgNum; //Should this be double?

        double finalGallons = milesNum*multiplier;

        finalGallonString = String.valueOf(finalGallons);

        return finalGallonString;
    }

    public static String getCarbon(double gallonsUsed){
        String finalCarbon = "";

        double multiplier = 8.85; //Kilos of carbon per gallon of gas
        double carbon = multiplier*gallonsUsed;

        finalCarbon = String.valueOf(carbon);

        return finalCarbon;
    }

    public static String getTrees(int gallonsUsed){
        String finalTreesKilled = "";

        double multiplier = 0.228; //Tree seedlings grown for 10 years, per gallon of gas
        double treesKilled = multiplier*gallonsUsed;

        finalTreesKilled = String.valueOf(treesKilled);

        return finalTreesKilled;
    }

    public static String getFuel(String hexString){
        String finalString = "";

        int intFuel = hexToInt(hexString);
        double finalFuel = (intFuel/255);
        finalString = String.valueOf(finalFuel);

        return finalString;
    }

    public static String getMAF(String val1, String val2){
        String finalString = "";

        double byte1 = hexToInt(val1);
        double byte2 = hexToInt(val2);

        double value = ((byte1*256)+byte2)/100;
        finalString = String.valueOf(value);
        return finalString;
    }

    public static int hexToInt(String hexString){

        int value = Integer.parseInt(hexString, 16);
        return value;
    }

}
