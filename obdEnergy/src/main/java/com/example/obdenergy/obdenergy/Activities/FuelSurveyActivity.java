package com.example.obdenergy.obdenergy.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.obdenergy.obdenergy.Data;
import com.example.obdenergy.obdenergy.R;
import com.example.obdenergy.obdenergy.Utilities.Console;

/**
 * Created by sumayyah on 5/10/14.
 */
public class FuelSurveyActivity extends Activity implements View.OnClickListener{

    private EditText milesField;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fuelsurvey_activity_layout);

        milesField = (EditText)(findViewById(R.id.milesField));
        radioGroup = (RadioGroup)(findViewById(R.id.radioGroup));
        doneButton = (Button)(findViewById(R.id.doneButtonFSA));

        Intent intent = new Intent();
        Data dataPoint = (Data)intent.getParcelableExtra("DATAPOINT");

//        Console.log("Getting data from Main! "+dataPoint.getGallons()+" gallons "+dataPoint.getMiles()+" miles");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.doneButtonFSA:
                Console.log("Done Button clicked");
                int selectedRadioButton = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton)(findViewById(selectedRadioButton));

                String text = (String) radioButton.getText();
                Console.log("Got button! It's "+text);
                break;
            default:
                break;
        }
    }
}
