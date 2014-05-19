package com.example.obdenergy.obdenergy.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

/**
 * Created by sumayyah on 5/7/14.
 */
public class Console {
    //Log to IDE console
    public static void log(String message){
        Log.d("Console", message);
        DataLogger.writeData("\n" + "Console: " + message);

    }
    public static void showAlert(Activity act, String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(act);

        alertDialogBuilder.setTitle("ERROR");

        alertDialogBuilder.setMessage(message)
//        alertDialogBuilder.setMessage("No Bluetooth device connected. Please connect some Bluetooth device and retry.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    //If the user clicks Ok, shut down alert
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
