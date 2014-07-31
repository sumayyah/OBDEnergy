package com.example.obdenergy.obdenergy.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.obdenergy.obdenergy.R;
import com.example.obdenergy.obdenergy.Utilities.DataLogger;

/**
 * Created by sumayyah on 5/7/14.
 */
public class InitActivity extends Activity implements View.OnClickListener{

    private EditText nameField;
    private EditText makeField;
    private EditText modelField;
    private EditText yearField;
    private EditText capacityField;
    private EditText citympg;
    private EditText highwaympg;
    private Button doneButton;

    String name;
    String make;
    String model;
    String year;
    String tank;
    String city;
    String highway;

    private final String USER_DATA_FILE = "MyCarData";
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_activity_layout);

        settings = getSharedPreferences(USER_DATA_FILE, 0);

        nameField = (EditText) (findViewById(R.id.nameField));
        makeField = (EditText)(findViewById(R.id.makeField));
        modelField = (EditText)(findViewById(R.id.modelField));
        yearField = (EditText)(findViewById(R.id.yearField));
        capacityField = (EditText)(findViewById(R.id.capacityField));
        citympg = (EditText)(findViewById(R.id.cityField));
        highwaympg = (EditText)(findViewById(R.id.highwayField));
        doneButton = (Button)(findViewById(R.id.doneButton));

        doneButton.setOnClickListener(this);

//        setDefaults();

    }

    private void setDefaults() {
        nameField.setText("Sumayyah");
        makeField.setText("Honda");
        modelField.setText("Accord");
        yearField.setText("1996");
        capacityField.setText("15");
        citympg.setText("22");
        highwaympg.setText("33");
    }

    @Override
    public void onClick(View v) {

        name = nameField.getText().toString();
        make = makeField.getText().toString();
        model = modelField.getText().toString();
        year = yearField.getText().toString();
        tank = capacityField.getText().toString();
        city = citympg.getText().toString();
        highway = highwaympg.getText().toString();

        /*Check that all fields are filled in*/
        if( !checkNull(nameField) ||!checkNull(makeField) || !checkNull(modelField) || !checkNull(yearField) || !checkNull(capacityField) || !checkNull(citympg) || !checkNull(highwaympg)){

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

            settings.edit().putString("name", name).commit();
            settings.edit().putString("car_make", make).commit();
            settings.edit().putString("car_model", model).commit();
            settings.edit().putString("car_year", year).commit();
            settings.edit().putString("tank_capacity", tank).commit();
            settings.edit().putString("City", city).commit();
            settings.edit().putString("Highway", highway).commit();

            DataLogger.writeData("Car information: "+printData());

            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);

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

    private String printData(){
        String finalString = "Name: "+name+"\nCar Make: "+make+"\nCar model: "+ model+"\nYear of manufacture: "+year+"\nTank capacity: "+tank+"\nCity mileage: "+city+"\nHighway mileage: "+highway+"\n";
        return finalString;
    }


}
