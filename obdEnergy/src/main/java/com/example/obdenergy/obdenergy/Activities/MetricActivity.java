package com.example.obdenergy.obdenergy.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.obdenergy.obdenergy.Data.DisplayData;
import com.example.obdenergy.obdenergy.R;

/**
 * Created by Sumayyah on 5/11/2014.
 */
public class MetricActivity extends Activity{

    private TextView fuelData;
    private TextView carbonData;
    private TextView treesData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metric_activity_layout);

        fuelData = (TextView)(findViewById(R.id.fuelData));
        carbonData = (TextView)(findViewById(R.id.carbonData));
        treesData = (TextView)(findViewById(R.id.metricData));

        Bundle extras = getIntent().getExtras();
        DisplayData displayData = (DisplayData) extras.getParcelable("DATAPOINT");

        //TODO: getCarbon(displayData.getGallons())
        //TODO: getTrees(displayData.getGallons())

        fuelData.setText(displayData.getGallons()+" Gals used");
        carbonData.setText(displayData.getMiles()+" miles driven on "+ displayData.getStreet());
    }
}
