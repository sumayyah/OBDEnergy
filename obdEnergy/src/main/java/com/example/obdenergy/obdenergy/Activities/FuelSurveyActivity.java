package com.example.obdenergy.obdenergy.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.obdenergy.obdenergy.Data.DisplayData;
import com.example.obdenergy.obdenergy.Data.Profile;
import com.example.obdenergy.obdenergy.R;
import com.example.obdenergy.obdenergy.Utilities.Calculations;
import com.example.obdenergy.obdenergy.Utilities.Console;

/**
 * Created by sumayyah on 5/10/14.
 */
public class FuelSurveyActivity extends Activity implements View.OnClickListener{

    private EditText milesField;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button doneButtonFSA;
    private final String USER_DATA_FILE = "MyCarData";

    Profile userProfile;
    SharedPreferences userData;

    private static final String classID = "FS Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        Console.log(classID+" created FUEL survey activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fuelsurvey_activity_layout);

        milesField = (EditText)(findViewById(R.id.milesField));
        radioGroup = (RadioGroup)(findViewById(R.id.radioGroup));
        doneButtonFSA = (Button)(findViewById(R.id.doneButtonFSA));
        doneButtonFSA.setOnClickListener(this);

        userData = getSharedPreferences(USER_DATA_FILE, 0);

    }

    @Override
    public void onClick(View v) {

            int selectedRadioButton = -1;

            Console.log(classID+" Done Button clicked");

            selectedRadioButton = radioGroup.getCheckedRadioButtonId();
            /*Check that all radio buttons are checked*/
            if(selectedRadioButton == -1) {
                Console.showAlert(this, "Please select either city or highway");
                return;
            }
            radioButton = (RadioButton)(findViewById(selectedRadioButton));

            /*Get parameters to pass to MetricActivity*/
            Long time = System.currentTimeMillis()/1000;
            String timeString = time.toString();
            String mpg = "";
            String miles = milesField.getText().toString();
            String street = "";
            String text = (String) radioButton.getText();

            if(text.equals("City")){
                Console.log(classID+"City MPG is "+Profile.getCitympg());
                mpg = Profile.getCitympg();
                street = "city";
            }
            else if(text.equals("Highway")){
                Console.log(classID+"Highway MPG is "+Profile.getHighwaympg());
                mpg = Profile.getHighwaympg();
                street = "highway";
            }
            else Console.log(classID+" wrong radio button data "+text);

            String gallons = Calculations.getGallons(mpg, miles);

            Console.log("User entered miles, mpg, gallons "+miles+" "+mpg+" "+gallons);
            miles = "10";

            DisplayData datapoint = new DisplayData(gallons, miles, timeString, street);
            datapoint.setStreet(text);

            Intent intent = new Intent(this, MetricActivity.class);
            intent.putExtra("DATAPOINT", datapoint);
            startActivity(intent);

            finish();

    }
}
