package com.example.obdenergy.obdenergy.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.obdenergy.obdenergy.R;
import com.example.obdenergy.obdenergy.Utilities.Console;
import com.example.obdenergy.obdenergy.MainActivity;


/**
 * Created by sumayyah on 5/31/14.
 */
public class MetricFragment extends Fragment {

    private MainActivity mainActivity;
    private TextView activityTitle;
    private TextView fuelData;
    private TextView carbonData;
    private TextView treesData;

    private String gallons = "0.0";
    private String carbonUsed = "0.0";
    private String treesKilled = "0.0";

    private String classID = "MetricFragment ";
    private String messageFromMain = "nothing";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstancestate){

        Console.log(classID + " creation");

        View view = inflater.inflate(R.layout.metrics_fragment, container, false);

        activityTitle = (TextView)(view.findViewById(R.id.activityTitle));
        fuelData = (TextView)(view.findViewById(R.id.fuelData));
        carbonData = (TextView)(view.findViewById(R.id.carbonData));
        treesData = (TextView)(view.findViewById(R.id.metricData));
        activityTitle.setText(messageFromMain);

        fuelData.setText(gallons);
        carbonData.setText(carbonUsed);
        treesData.setText(treesKilled);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mainActivity = (MainActivity) activity;
    }

    /*Data from Main is given before onCreateView - so it needs to be stored in a global for onCreateView to access later*/
    public void MetricFragmentDataComm(String message){
        Console.log(classID+" Main sent: "+message);
        messageFromMain = message;
    }
    public void MetricFragmentDataComm(String gallons, String carbonUsed, String treesKilled){
        this.gallons = gallons;
        this.carbonUsed = carbonUsed;
        this.treesKilled = treesKilled;
        Console.log(classID+"Got gallons carbon trees "+this.gallons+" "+this.carbonUsed+" "+this.treesKilled);
    }
}
