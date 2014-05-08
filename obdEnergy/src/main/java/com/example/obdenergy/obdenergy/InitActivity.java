package com.example.obdenergy.obdenergy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileWriter;

/**
 * Created by sumayyah on 5/7/14.
 */
public class InitActivity extends Activity implements View.OnClickListener{

    private EditText makeField;
    private EditText modelField;
    private EditText yearField;
    private EditText capacityField;
    private EditText citympg;
    private EditText highwaympg;
    private Button doneButton;

    private final String PREFS_NAME = "MyCarData";
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_activity_layout);
        Console.log("Creating INit");

        Intent intent = getIntent();

        settings = getSharedPreferences(PREFS_NAME, 0);

        String readMessage = settings.getString("Init message", "Default for null");
        Boolean boolVal = settings.getBoolean("my_first_time", false);

        makeField = (EditText)(findViewById(R.id.makeField));
        modelField = (EditText)(findViewById(R.id.modelField));
        yearField = (EditText)(findViewById(R.id.yearField));
        capacityField = (EditText)(findViewById(R.id.capacityField));
        citympg = (EditText)(findViewById(R.id.cityField));
        highwaympg = (EditText)(findViewById(R.id.highwayField));
        doneButton = (Button)(findViewById(R.id.doneButton));

        doneButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        Console.log("Clicked done");

        //Check that all fields are filled in
        if( checkNull(makeField) == false || checkNull(modelField) == false || checkNull(yearField) == false || checkNull(capacityField) == false || checkNull(citympg) == false || checkNull(highwaympg) == false){

            Console.log("one of the fields is null");

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setTitle("ERROR");

            alertDialogBuilder.setMessage("Please fill in all the fields")
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
        else {

            Console.log("nothing null");
            settings.edit().putString("car_make", makeField.getText().toString());
            settings.edit().putString("car_model", modelField.getText().toString());
            settings.edit().putString("car_year", yearField.getText().toString());
            settings.edit().putString("tank_capacity", capacityField.getText().toString());
            settings.edit().putString("city_mpg", citympg.getText().toString());
            settings.edit().putString("highway_mpg", highwaympg.getText().toString());

//        Intent intent = new Intent(this, MainActivity.class);
            finish();
        }

    }

    private boolean checkNull(EditText field){
        String value = field.getText().toString();

        if(value.matches("")){
            return false;
        }
        else return true;
    }
}
