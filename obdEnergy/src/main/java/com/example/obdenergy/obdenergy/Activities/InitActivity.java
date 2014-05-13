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

import com.example.obdenergy.obdenergy.Profile;
import com.example.obdenergy.obdenergy.R;

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

    private final String USER_DATA_FILE = "MyCarData";
    SharedPreferences settings;
    Profile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_activity_layout);

        settings = getSharedPreferences(USER_DATA_FILE, 0);

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

        //Check that all fields are filled in
        if( checkNull(makeField) == false || checkNull(modelField) == false || checkNull(yearField) == false || checkNull(capacityField) == false || checkNull(citympg) == false || checkNull(highwaympg) == false){

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

            settings.edit().putString("car_make", makeField.getText().toString()).commit();
            settings.edit().putString("car_model", modelField.getText().toString()).commit();
            settings.edit().putString("car_year", yearField.getText().toString()).commit();
            settings.edit().putString("tank_capacity", capacityField.getText().toString()).commit();
            settings.edit().putString("city_mpg", citympg.getText().toString()).commit();
            settings.edit().putString("highway_mpg", highwaympg.getText().toString()).commit();

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
}
