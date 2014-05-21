package com.example.obdenergy.obdenergy.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.obdenergy.obdenergy.Data.DisplayData;
import com.example.obdenergy.obdenergy.Data.Profile;
import com.example.obdenergy.obdenergy.R;
import com.example.obdenergy.obdenergy.Utilities.Calculations;

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

        String gallons = displayData.getGallons();

        String carbonUsed = Calculations.getCarbon(Double.parseDouble(gallons));
        String treesKilled = Calculations.getTrees(Double.parseDouble(gallons));

        fuelData.setText(gallons+" Gals used");
        carbonData.setText(displayData.getMiles()+" miles driven on "+ displayData.getStreet()+" using up "+carbonUsed+" kilos carbon");
        treesData.setText(treesKilled+" Trees killed");


    }
}
