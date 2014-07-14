package com.example.obdenergy.obdenergy.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.obdenergy.obdenergy.R;

/**
 * Created by Sumayyah on 6/21/2014.
 */
public class InfoActivity extends Activity implements View.OnClickListener{

    private TextView calculationsInfo;
    private TextView emissionsInfo;
    private TextView graphsInfo;
    private Button closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.info_layout);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        calculationsInfo = (TextView)(findViewById(R.id.calculationsInfo));
        emissionsInfo = (TextView)(findViewById(R.id.emissionsInfo));
        graphsInfo = (TextView)(findViewById(R.id.graphsInfo));


        closeButton = (Button)(findViewById(R.id.closeButton));
        closeButton.setOnClickListener(this);

        String emissionstext = "This app calculates instantaneous fuel usage throughout the duration of each drive, and uses a final figure in gallons to calculate carbon emissions (in kilograms of carbon dioxide) and an equivalent metric.";
        String calculationstext = "Fuel is calculated using three formulas:\n" +
                "\n1) Fuel levels in gas tank\n" +
                "\t2) Mass air flow in the intake valve, which is used in the formula to give gallons:\n" +
                "\t\t\n" +
                "\t  (MAF grams air/sec) * (grams gas/ 14.7 grams air)*(1 gal/ 2760 grams)*secs \n" +
                "\n" +
                "     = MAF* (1/ 14.75*2760 )* 5 seconds \n" +
                "\n" +
                "     = MAF*0.00012 gallons used in previous 5 second interval\n" +
                "\n" +
                "3) Speed, time and mileage calculations (this option is used when the other two options fail)\n" +
                "\n" +
                "Carbon emissions and equivalent metrics are based on calculations from the US Environmental Protection Agency’s multipliers. \n";
        String graphstext = "Our Graphs screen displays your historical accumulation in terms of daily data, weekly, or monthly. You have the option of seeing carbon or trees, and the time frame. Each icon that appears onscreen represents a certain multiplier - for example, weekly carbon usually shows up as 1 icon per 10 kilos of carbon dioxide. ";

        emissionsInfo.setText(emissionstext);
        calculationsInfo.setText(calculationstext);
        graphsInfo.setText(graphstext);
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
