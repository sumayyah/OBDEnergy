package com.example.obdenergy.obdenergy.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.obdenergy.obdenergy.R;

/**
 * Created by Sumayyah on 6/21/2014.
 */
public class InfoActivity extends Activity implements View.OnClickListener{

    private TextView calculationsInfo;
    private Button closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.info_layout);

        calculationsInfo = (TextView)(findViewById(R.id.calculationsInfo));
        closeButton = (Button)(findViewById(R.id.closeButton));
        closeButton.setOnClickListener(this);

        String text = "We calculate carbon emissions and trees used based on the EPA's mulipliers per gallon of gas.";
        calculationsInfo.setText(text);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.closeButton:
                finish();
                break;
            default:
                break;
        }
    }
}
