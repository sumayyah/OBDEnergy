package com.example.obdenergy.obdenergy.Utilities;

import com.example.obdenergy.obdenergy.Data.Path;
import com.example.obdenergy.obdenergy.MainActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by sumayyah on 5/13/14.
 */
public class Calculations {

    private final static String classID = "Calculations ";

    /*Gets gallons used from fuel level data*/
    public static String getGallons(double initFuel, double finalFuel, String tankCapacity){
        String finalGallonString = "";
        Console.log("Calculations - initfuel, final fuel, tankcapacity"+initFuel+" "+finalFuel+" "+tankCapacity);
        int tankCapacityNum = Integer.parseInt(tankCapacity);

        double fuelVal = finalFuel - initFuel;
        double percentage = (fuelVal/255);
        double gallons = percentage*tankCapacityNum;

        String tempString = String.valueOf(gallons);
        finalGallonString = tempString.length() > 4? (tempString.substring(0,3)): (tempString);

        return finalGallonString;
    }

    /*Calculates gallons with Mass Airflow parameters*/
    public static String getGallons(double initMAF, double finalMAF, String initTime, String finalTime){
        String finalGallonString = "";

        int timeTaken = (Integer.parseInt(finalTime))-(Integer.parseInt(initTime));
        double initFuel = 1/(14.75*6.26*initMAF*timeTaken);
        double finalFuel = 1/(14.75*6.26*finalMAF*timeTaken);

        double fuelUsed = finalFuel - initFuel;

        String tempString = String.valueOf(fuelUsed);
        finalGallonString = tempString.length() > 4? (tempString.substring(0,3)): (tempString);
        Console.log(classID+" init fuel "+initFuel+" - final fuel "+finalFuel+" equals "+finalGallonString+" from "+initTime+" to "+finalTime);

        return finalGallonString; //TODO: double check - does formula give back gallons?
    }

    /*Calculate gallons with user input*/
    public static String getGallons(String mpg, String miles){
        String finalGallonString = "";

        double mpgNum = Double.parseDouble(mpg);
        double milesNum = Double.parseDouble(miles);
        double finalGallons = milesNum/mpgNum;

        String tempString = String.valueOf(finalGallons);
        finalGallonString = tempString.length() > 4? (tempString.substring(0,3)): (tempString);

        return finalGallonString;
    }

    /*Calculate gallons with array of instantaneous speed readings*/
    public static String getGallons(double miles, String street){ //TODO: test this function
        String finalGallonString = "0.0";

        String mpg = MainActivity.userData.getString(street, "city");

        Double answer = miles*Double.parseDouble(mpg);
        finalGallonString = String.valueOf(answer);

        return finalGallonString;
    }

    public static String getCarbon(double gallonsUsed){
        String finalCarbon = "0.0";

        double multiplier = 8.85; //Kilos of carbon per gallon of gas
        double carbon = multiplier*gallonsUsed;

        String tempString = String.valueOf(carbon);
        finalCarbon = tempString.length() > 4 ? tempString.substring(0,3): tempString;

        return finalCarbon;
    }

    public static String getTrees(double gallonsUsed){
        String finalTreesKilled = "0.0";

        double multiplier = 0.228; //Tree seedlings grown for 10 years, per gallon of gas
        double treesKilled = multiplier*gallonsUsed;

        String tempString = String.valueOf(treesKilled);
        finalTreesKilled= tempString.length() > 4 ? tempString.substring(0,3): tempString;

        return finalTreesKilled;
    }

    public static String getMAF(String val1, String val2){
        String finalString = "";

        double byte1 = hexToInt(val1);
        double byte2 = hexToInt(val2);

        double value = ((byte1*256)+byte2)/100;
        finalString = String.valueOf(value);
        Console.log(classID+"MAF is calculated "+finalString);
        return finalString;
    }

    public static int hexToInt(String hexString){

        int value = Integer.parseInt(hexString, 16);
        return value;
    }

    public static JSONArray getPathArray(int id){
        JSONArray jsonArray = new JSONArray();

        switch (id){
            case 0:

                break;
            case 1:
                break;
            case 2:
                break;
            default:
                Console.log(classID+"Wrong id to get Path array");
                break;
        }

        return jsonArray;
    }

    public static JSONArray concatenateJSON(JSONArray array1, JSONArray array2){
        for(int i=0;i<array2.length();i++){
            try {
                array1.put(array2.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
                Console.log(classID+"Failed to concatenate JSON arrays");
            }
        }
        return array1;
    }

    public static ArrayList<Path> convert(JSONArray jsonArray){
        ArrayList<Path> patharray;
        if(jsonArray==null || jsonArray.toString().equals("[]")) {
            patharray = new ArrayList<Path>();
            return patharray;
        }
        else{
            Gson gson = new Gson();
            patharray = new Gson().fromJson(jsonArray.toString(), new TypeToken<ArrayList<Path>>(){}.getType());
            return patharray;
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
