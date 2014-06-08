package com.example.obdenergy.obdenergy.Data;

import com.example.obdenergy.obdenergy.Utilities.Console;

import java.util.Calendar;

/**
 * Created by sumayyah on 5/20/14.
 */
public class StorageDate {

    private static int year = 0;
    private static int month = 0;
    private static int dayOfMonth = 0;
    private static int hour = 0;
    private static int minute = 0;
    private static int second = 0;

    public StorageDate(Calendar calendar){
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);
    }

    public StorageDate(){};

    public static void printDate(){
        Console.log("Year "+year+" month "+month+" day "+dayOfMonth+" hour "+hour+" minute "+minute+" second "+second);
    }
    @Override
    public String toString(){
        return "Year ="+year+", month="+month+", day="+dayOfMonth+", hour="+hour+", minute="+minute+", second="+second;
    }
}
