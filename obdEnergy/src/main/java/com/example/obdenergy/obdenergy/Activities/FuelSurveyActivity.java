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
    private Button doneButtonFSA;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fuelsurvey_activity_layout);

        milesField = (EditText)(findViewById(R.id.milesField));
        radioGroup = (RadioGroup)(findViewById(R.id.radioGroup));
        doneButtonFSA = (Button)(findViewById(R.id.doneButtonFSA));
        doneButtonFSA.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.doneButtonFSA:
                Console.log("Done Button clicked");
                int selectedRadioButton = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton)(findViewById(selectedRadioButton));

                Long time = System.currentTimeMillis()/1000;
                String timeString = time.toString();

                String text = (String) radioButton.getText();
                Console.log("Got button! It's "+text);

                String miles = milesField.getText().toString();

                //TODO: get mpg baed on radio button
                //TODO: getGallons(mpg, miles)

                Data datapoint = new Data("0 gals", miles, timeString);
                datapoint.setStreet(text.toString());

                Intent intent = new Intent(this, MetricActivity.class);
                intent.putExtra("DATAPOINT", datapoint);
                startActivity(intent);

                finish();
                break;
            default:
                break;
        }
    }
}
