package com.example.obdenergy.obdenergy.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.obdenergy.obdenergy.Data.DisplayData;
import com.example.obdenergy.obdenergy.R;
import com.example.obdenergy.obdenergy.Utilities.Calculations;
import com.example.obdenergy.obdenergy.Utilities.Console;
import com.example.obdenergy.obdenergy.Utilities.DataLogger;

/**
 * Created by Sumayyah on 5/11/2014.
 */
public class MetricActivity extends Activity{

    private TextView fuelData;
    private TextView carbonData;
    private TextView treesData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Console.log("Metric activity launched");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metric_activity_layout);

        fuelData = (TextView)(findViewById(R.id.fuelData));
        carbonData = (TextView)(findViewById(R.id.carbonData));
        treesData = (TextView)(findViewById(R.id.metricData));

        Bundle extras = getIntent().getExtras();
        DisplayData displayData = (DisplayData) extras.getParcelable("DATAPOINT");

        String gallons = displayData.getGallons();
        //TODO: get miles and mpg and calculate data

        String carbonUsed = Calculations.getCarbon(Double.parseDouble(gallons));
        String treesKilled = Calculations.getTrees(Double.parseDouble(gallons));
        String displayMiles = displayData.getMiles();
//        String miles = displayMiles.length() > 4 ? displayMiles.substring(0,3): displayMiles;
        DataLogger.writeData("Data calculated: " + "\n" + " Carbon: " + carbonUsed + " Trees killed: " + treesKilled);


        fuelData.setText(gallons);
        carbonData.setText(carbonUsed);
        treesData.setText(treesKilled);


    }
}
