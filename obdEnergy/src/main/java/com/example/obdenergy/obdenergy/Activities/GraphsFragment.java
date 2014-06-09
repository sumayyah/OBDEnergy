package com.example.obdenergy.obdenergy.Activities;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;

import com.example.obdenergy.obdenergy.MainActivity;
import com.example.obdenergy.obdenergy.R;
import com.example.obdenergy.obdenergy.Utilities.Calculations;
import com.example.obdenergy.obdenergy.Utilities.Console;

import org.json.JSONArray;


/**
 * Created by sumayyah on 5/31/14.
 */

public class GraphsFragment extends Fragment implements View.OnClickListener{

    private MainActivity mainActivity;
    private TextView fuelUsed;
    private Button today;
    private Button week;
    private Button month;

    private final String classID="GraphsFragment";

    private JSONArray pathArray;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstancestate){

        View view = inflater.inflate(R.layout.graphs_fragment, container, false);

        fuelUsed = (TextView)(view.findViewById(R.id.fuelNumber));
        today = (Button)(view.findViewById(R.id.todayButton));
        week = (Button)(view.findViewById(R.id.weekButton));
        month = (Button)(view.findViewById(R.id.monthButton));

        today.setOnClickListener(this);
        week.setOnClickListener(this);
        month.setOnClickListener(this);


        fuelUsed.setText(mainActivity.path.gallonsUsed+"");

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mainActivity = (MainActivity) activity;
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.todayButton:
                Console.log(classID+"clicked Today");
                pathArray = Calculations.getPathArray(0);
                drawIcons(0);
                break;
            case R.id.weekButton:
                Console.log(classID+"clicked Week");
                pathArray = Calculations.getPathArray(1);
                drawIcons(0);
                break;
            case R.id.monthButton:
                Console.log(classID+"clicked Month");
                pathArray = Calculations.getPathArray(2);
                drawIcons(0);
                break;
            default:
                break;
        }
    }

    private void drawIcons(int num){
        
    }

}
