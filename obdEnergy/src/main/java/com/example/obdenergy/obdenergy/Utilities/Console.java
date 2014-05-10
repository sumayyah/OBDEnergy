package com.example.obdenergy.obdenergy.Utilities;

import android.util.Log;

import com.example.obdenergy.obdenergy.Utilities.DataLogger;

/**
 * Created by sumayyah on 5/7/14.
 */
public class Console {
    //Log to IDE console
    public static void log(String message){
        Log.d("Console", message);
        DataLogger.writeData("\n" + "Console: " + message);

    }
}
