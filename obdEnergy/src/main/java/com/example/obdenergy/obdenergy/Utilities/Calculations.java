package com.example.obdenergy.obdenergy.Utilities;

/**
 * Created by sumayyah on 5/13/14.
 */
public class Calculations {

    public String getGallons(String initFuel, String finalFuel, String tankCapacity){
        String finalGallonString = "";
        int initFuelNum = Integer.parseInt(initFuel);
        int finalFuelNum = Integer.parseInt(finalFuel);
        int tankCapacityNum = Integer.parseInt(tankCapacity);

        int fuelVal = finalFuelNum - initFuelNum;
        int percentage = (fuelVal/255);
        int gallons = percentage*tankCapacityNum;

        finalGallonString = String.valueOf(gallons);

        return finalGallonString;
    }

    public String getGallons(String mpg, String miles){
        String finalGallonString = "";

        int mpgNum = Integer.parseInt(mpg);
        int milesNum = Integer.parseInt(miles);
        float multiplier = 1/mpgNum; //Should this be double?

        float finalGallons = milesNum*multiplier;

        finalGallonString = String.valueOf(finalGallons);

        return finalGallonString;
    }

    public String getCarbon(int gallonsUsed){
        String finalCarbon = "";

        double multiplier = 8.85; //Kilos of carbon per gallon of gas
        double carbon = multiplier*gallonsUsed;

        finalCarbon = String.valueOf(carbon);

        return finalCarbon;
    }

    public String getTrees(int gallonsUsed){
        String finalTreesKilled = "";

        double multiplier = 0.228; //Tree seedlings grown for 10 years, per gallon of gas
        double treesKilled = multiplier*gallonsUsed;

        finalTreesKilled = String.valueOf(treesKilled);

        return finalTreesKilled;
    }

    private int hexToInt(String hexString){

        int value = Integer.parseInt(hexString, 16);
        return value;
    }

}
