package com.example.obdenergy.obdenergy.Utilities;

import com.example.obdenergy.obdenergy.Data.Path;
import com.example.obdenergy.obdenergy.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by sumayyah on 5/13/14.
 */
public class Calculations {

    private final static String classID = "Calculations ";

    /*Gets gallons used from fuel level data*/
    public static Double getGallons(double initFuel, double finalFuel, String tankCapacity){
        double gallons = 0.0;
        DecimalFormat df = new DecimalFormat("#.00");

        Console.log("Calculations - initfuel, final fuel, tankcapacity"+initFuel+" "+finalFuel+" "+tankCapacity);
        int tankCapacityNum = Integer.parseInt(tankCapacity);

        double fuelVal = finalFuel - initFuel;
        double percentage = (fuelVal/255);
        gallons = percentage*tankCapacityNum;

        gallons = Double.valueOf(df.format(gallons));

        return gallons;
    }

    /*Calculates gallons with Mass Air Flow parameters*/
    public static Double getGallons(ArrayList<Double> MAFarray, double timeInterval){
        double gallons = 0.0;
        DecimalFormat df = new DecimalFormat("#.00");

        if(MAFarray == null){return gallons;}

        for(double d: MAFarray){
            double value = d*0.000024*timeInterval;
            gallons += value;
        }

        gallons = Double.valueOf(df.format(gallons));
        Console.log(classID+" MAF gallon calculation returns "+gallons);

        return gallons;
    }

    /*Calculate gallons with user input*/
    public static Double getGallons(String mpg, String miles){
        double gallons = 0.0;
        DecimalFormat df = new DecimalFormat("#.00");

        double mpgNum = Double.parseDouble(mpg);
        double milesNum = Double.parseDouble(miles);
        double finalGallons = milesNum/mpgNum;

        gallons = Double.parseDouble(df.format(finalGallons));

        return gallons;
    }

    /*Calculate gallons with array of instantaneous speed readings*/
    public static Double getGallons(double miles, String street){
        double gallons = 0.0;
        DecimalFormat df = new DecimalFormat("#.00");

        String mpg = MainActivity.userData.getString(street, "city");

        Double answer = miles*(1/(Double.parseDouble(mpg)));
        gallons = Double.parseDouble(df.format(answer));

        return gallons;
    }

    public static String getCarbon(double gallonsUsed){
        String finalCarbon = "0.0";
        DecimalFormat df = new DecimalFormat("#.00");

        double multiplier = 8.85; //Kilos of carbon per gallon of gas
        double carbon = multiplier*gallonsUsed;

        finalCarbon = String.valueOf(df.format(carbon));
//        finalCarbon = tempString.length() > 4 ? tempString.substring(0,3): tempString;

        return finalCarbon;
    }

    public static Double getTrees(double gallonsUsed){
        DecimalFormat df = new DecimalFormat("#.00");

        double multiplier = 0.228; //Tree seedlings grown for 10 years, per gallon of gas
        double treesKilled = multiplier*gallonsUsed;

       treesKilled = Double.parseDouble(df.format(treesKilled));
        Console.log(classID+"Trees calculated "+treesKilled);
        return treesKilled;
    }

    public static Double getMAF(String val1, String val2){
        double value = 0.0;

        double byte1 = hexToInt(val1);
        double byte2 = hexToInt(val2);

        value = ((byte1*256)+byte2)/100;
        Console.log(classID+" MAF is calculated "+value);
        return value;
    }

    public static Double getMiles(ArrayList<Integer> speedArray, ArrayList<Double> timeArray){
        Double finalMiles = 0.0;
        double secondsPassed = 0.0;
        DecimalFormat df = new DecimalFormat("#.00");

        for(int i=0;i<=speedArray.size()-1;i++){
            if(i==0) secondsPassed = 0; /*Discard initial reading since speed at time 0 is negligible*/
            else secondsPassed = timeArray.get(i) -  timeArray.get(i-1);
            double hoursPassed = secondsPassed/3600;
            double kilometers = speedArray.get(i)*hoursPassed;
            finalMiles += (0.621371*kilometers);
//            Console.log("Path seconds, hours, speed, km, miles "+secondsPassed+" "+hoursPassed+" "+kilometers+" "+finalMiles);
        }

        finalMiles = Double.valueOf(df.format(finalMiles));
        Console.log(classID+"Miles travelled "+finalMiles);
        return finalMiles;
    }

    public static Double getAvgSpeed(ArrayList<Integer> speedArray){

        double averageSpeed = 0.0;
        DecimalFormat df = new DecimalFormat("#.00");
        double total = 0.0;
        for(double d: speedArray){
            total+=d;
        }
        double temp = 0.621371*(total/speedArray.size());
        averageSpeed = Double.valueOf(df.format(temp));
        Console.log("Path calculated average speed is "+averageSpeed);
        return averageSpeed;
    }

    public static int hexToInt(String hexString){

        int value = Integer.parseInt(hexString, 16);
        return value;
    }

    public static JSONArray concatenateJSON(JSONArray array1, JSONArray array2){

        JSONArray jsonArray = new JSONArray();
        if(array1 == null && array2 == null) return jsonArray;
        else if(array1 == null) return array2; /*If there exists no master array, just return the newest array to be set as first*/
        else if(array2 == null) return array1;
        /*Do the actual concatenation*/
        else {
            for (int i = 0; i < array2.length(); i++) {
                try {
                    array1.put(array2.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Console.log(classID + "Failed to concatenate JSON arrays");
                }
            }
            return array1;
        }
    }

    public static void checkArray(ArrayList<Path> pathArray){
        int counter = 0;
        for(Path p: pathArray){
            Console.log("Path "+(++counter));
            p.printData();
        }
    }

}
