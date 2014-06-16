package com.example.obdenergy.obdenergy.Activities;


import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.obdenergy.obdenergy.Data.Profile;
import com.example.obdenergy.obdenergy.MainActivity;
import com.example.obdenergy.obdenergy.R;
import com.example.obdenergy.obdenergy.Utilities.Console;
import com.example.obdenergy.obdenergy.Utilities.GridAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by sumayyah on 5/31/14.
 */

public class GraphsFragment extends Fragment implements View.OnClickListener{

    private MainActivity mainActivity;
    private GridAdapter gridAdapter;
    private GridView gridView;
    private TextView fuelUsed;
    private TextView avgSpeed;
    private TextView carbonUsed;
    private TextView scale;
    private Button today;
    private Button week;
    private Button month;
    private ImageView cloudClicker;
    private ImageView leafClicker;

    private final String classID="GraphsFragment ";

    private long millisInWeek = 604800000;
    private long millisInDay = 86400000;

    private long dayStartRange;
    private long dayStopRange;
    private long currentTime;

    private long weekStartRange;
    private long weekStopRange;

    private double dayFuelNum = 0.0;
    private double dayCarbonNum = 0.0;
    private double dayTreesNum = 0.0;

    private double weekFuelNum = 0.0;
    private double weekCarbonNum = 0.0;
    private double weekTreesNum = 0.0;

    private double monthFuelNum = 0.0;
    private double monthCarbonNum = 0.0;
    private double monthTreesNum = 0.0;

    private int adapterNum = 0;
    private String adapterType = "CARBON";

    private boolean cloud = true;
    private boolean leaf = false;
    private boolean dayPressed = true;
    private boolean weekPressed = false;
    private boolean monthPressed = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstancestate){

        View view = inflater.inflate(R.layout.graphs_fragment, container, false);

        gridView = (GridView)(view.findViewById(R.id.gridView1));

        fuelUsed = (TextView)(view.findViewById(R.id.fuelNumber));
        avgSpeed = (TextView)(view.findViewById(R.id.avgSpeedNum));
        carbonUsed = (TextView)(view.findViewById(R.id.carbonUsed));
        scale = (TextView)(view.findViewById(R.id.carbonScale));
        today = (Button)(view.findViewById(R.id.todayButton));
        week = (Button)(view.findViewById(R.id.weekButton));
        month = (Button)(view.findViewById(R.id.monthButton));
        cloudClicker = (ImageView)(view.findViewById(R.id.cloudClicker));
        leafClicker = (ImageView)(view.findViewById(R.id.leafClicker));

        today.setOnClickListener(this);
        week.setOnClickListener(this);
        month.setOnClickListener(this);
        cloudClicker.setOnClickListener(this);
        leafClicker.setOnClickListener(this);

        fuelUsed.setText(mainActivity.path.gallonsUsed+"");

        currentTime = System.currentTimeMillis();

        dayStartRange = currentTime - millisInDay;
        dayStopRange = currentTime+millisInDay;
        weekStartRange = currentTime - millisInWeek;
        weekStopRange = currentTime + millisInWeek;

        String holderString = "[{\"initTimestamp\":\"1402414587670\", \"finalMAF\":655.35,\"treesKilled\":7, \"gallonsUsed\":3, \"carbonUsed\":61},{\"initTimestamp\":\"1401896187867\", \"finalMAF\":655.35,\"treesKilled\":1,\"carbonUsed\":5,\"initFuel\":0,\"gallonsUsed\":7,\"initMAF\":406.65,\"averageSpeed\":55.5,\"finalTimestamp\":\"1402365290\",\"finalFuel\":0}, {\"initTimestamp\":\"1402417236395\",\"carbonUsed\":9, \"initFuel\":0,\"initMAF\":406.65,\"finalFuel\":0,\"treesKilled\":3,\"finalMAF\":655.35,\"gallonsUsed\":6}]";

        try {
            parseJSON(new JSONArray(holderString));

        } catch (JSONException e) {
            Console.log(classID+" failed to get JSON array");
            e.printStackTrace();
        }

        setDefaults();

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mainActivity = (MainActivity) activity;
    }

    public void GraphsFragmentDataComm(){}

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.todayButton: /*Get today's data collected to far - so this is all the paths stored now? no this won't work*/
                dayPressed =true;weekPressed = false;monthPressed = false;
                today.setBackgroundColor(Color.BLACK);
                week.setBackgroundColor(Color.parseColor("#282828"));
                month.setBackgroundColor(Color.parseColor("#282828"));
                displayData(dayPressed, weekPressed, monthPressed, cloud, leaf);
                break;
            case R.id.weekButton:
                dayPressed =false;weekPressed = true;monthPressed = false;
                week.setBackgroundColor(Color.BLACK);
                month.setBackgroundColor(Color.parseColor("#282828"));
                today.setBackgroundColor(Color.parseColor("#282828"));
                displayData(dayPressed, weekPressed, monthPressed, cloud, leaf);
                break;
            case R.id.monthButton:
                dayPressed =false;
                weekPressed = false;
                monthPressed = true;
                month.setBackgroundColor(Color.BLACK);
                week.setBackgroundColor(Color.parseColor("#282828"));
                today.setBackgroundColor(Color.parseColor("#282828"));
                displayData(dayPressed, weekPressed, monthPressed, cloud, leaf);
                break;
            case R.id.cloudClicker:
                cloud = true;
                leaf = false;
                displayData(dayPressed, weekPressed, monthPressed, cloud, leaf);
                break;
            case R.id.leafClicker:
                cloud = false;
                leaf = true;
                displayData(dayPressed, weekPressed, monthPressed, cloud, leaf);
            default:
                break;
        }
    }

    private void displayData(boolean day, boolean week, boolean month, boolean cloud, boolean leaf){
        if(cloud && !leaf){
            if(day && !week && !month){
                fuelUsed.setText(dayFuelNum + "");

                carbonUsed.setText(dayCarbonNum + " kilos CO2");
                scale.setText("1 cloud for every kilo of carbon");
                adapterNum = (int)dayCarbonNum;
                adapterType = "CARBON";

//                Console.log(classID+"Cloud and day, send number and type "+adapterNum+" "+adapterType);
            }
            else if(!day && week && !month){
                fuelUsed.setText(weekFuelNum+"");

                carbonUsed.setText(weekCarbonNum + " kilos CO2");
                scale.setText("1 cloud for every 5 kilos of carbon");
                adapterNum = (int)(weekCarbonNum/5);
                adapterType = "CARBON";

//                Console.log(classID+"Cloud and week send number and type "+adapterNum+" "+adapterType);
            }
            else if(!day && !week && month){
                fuelUsed.setText(monthFuelNum+"");

                carbonUsed.setText(monthCarbonNum + " kilos CO2");
                scale.setText("1 cloud for every 10 kilos of carbon");
                adapterNum = (int)(monthCarbonNum/10);
                adapterType = "CARBON";

//                Console.log(classID+"Cloud and month, send number and type "+adapterNum+" "+adapterType);
            }
            else Console.log(classID+"Wrong time bool for cloud");
        }
        else if(leaf && !cloud){
            if(day && !week && !month){
                fuelUsed.setText(dayFuelNum + "");

                carbonUsed.setText(dayTreesNum + " trees killed");
                scale.setText("1 leaf per tree killed");
                adapterNum = (int)dayTreesNum;
                adapterType = "TREE";

//                Console.log(classID+"Leaf and day, send number and type "+adapterNum+" "+adapterType);
            }
            else if(!day && week && !month){
                fuelUsed.setText(weekFuelNum+"");

                carbonUsed.setText(weekTreesNum + " trees killed");
                scale.setText("1 leaf per tree killed");
                adapterNum = (int)(weekTreesNum);
                adapterType = "TREE";

//                Console.log(classID+"Leaf and week, send number and type "+adapterNum+" "+adapterType);
            }
            else if(!day && !week && month){
                fuelUsed.setText(monthFuelNum+"");

                carbonUsed.setText(monthTreesNum + " trees killed");
                scale.setText("1 leaf per tree killed");
                adapterNum = (int)(monthTreesNum);
                adapterType = "TREE";
//                Console.log(classID+"Leaf and month, send number and type "+adapterNum+" "+adapterType);
            }
            else Console.log(classID+"Wrong time bool for leaf");
        }

        else Console.log(classID+"wrong boolean somewhere");

        gridAdapter = new GridAdapter(mainActivity, adapterNum, adapterType); //TODO: replace with notifyDataSetChanged()
        gridView.setAdapter(gridAdapter);
    }

    private void parseJSON(JSONArray jsonArray) throws JSONException {//TODO: make sure this runs only once per session

        Long objTimestamp;
        double fuelNum;
        double carbonNum;
        double treesNum;

        for(int i=0;i<jsonArray.length();i++){

            JSONObject obj = (JSONObject) jsonArray.get(i);
            objTimestamp = Long.parseLong(obj.getString("initTimestamp"));

            /*Get object's data*/
            fuelNum = obj.getDouble("gallonsUsed");
            carbonNum = obj.getDouble("carbonUsed");
            treesNum = obj.getDouble("treesKilled");

            Console.log(classID+"Object "+i+" "+objTimestamp+" Data: fuel carbon trees "+fuelNum+" "+carbonNum+" "+treesNum);

            monthFuelNum += fuelNum;
            monthCarbonNum += carbonNum;
            monthTreesNum += treesNum;

            /*If in range, calculate data for the week*/
            if(objTimestamp <= weekStopRange && objTimestamp >= weekStartRange){
                weekFuelNum += fuelNum;
                weekCarbonNum += carbonNum;
                weekTreesNum += treesNum;
            }

            /*If in range, calculate data for the day*/
            if(objTimestamp <= dayStopRange && objTimestamp >= dayStartRange){
                dayFuelNum += fuelNum;
                dayCarbonNum += carbonNum;
                dayTreesNum += treesNum;
            }
        }

//        printData();
    }

    private void setDefaults(){
        if(mainActivity.path != null) {
            fuelUsed.setText(mainActivity.path.gallonsUsed + "");
            avgSpeed.setText(mainActivity.path.averageSpeed + "");
            carbonUsed.setText(mainActivity.path.carbonUsed + " kilos CO2");
        }
        else if(Profile.pathArray.size() > 0){
            fuelUsed.setText(Profile.pathArray.get(Profile.pathArray.size()-1).gallonsUsed + "");
            avgSpeed.setText(Profile.pathArray.get(Profile.pathArray.size()-1).averageSpeed + "");
            carbonUsed.setText(Profile.pathArray.get(Profile.pathArray.size() - 1).carbonUsed + " kilos CO2");
        }else{
            Console.log(classID + "Error getting data from path");
            fuelUsed.setText(dayFuelNum+"");
            carbonUsed.setText(dayCarbonNum + " kilos CO2");
        }

        dayPressed =true;weekPressed = false;monthPressed = false;
        today.setBackgroundColor(Color.BLACK);
        week.setBackgroundColor(Color.parseColor("#282828"));
        month.setBackgroundColor(Color.parseColor("#282828"));

        adapterNum = 5;
        adapterType = "CARBON";
        gridAdapter = new GridAdapter(mainActivity,adapterNum,adapterType); /*Call grid view when parsing is done*/
        gridView.setAdapter(gridAdapter);
    }

    private void printData(){
        Console.log(classID+"Printing data now at "+currentTime);
        Console.log("Day "+dayStartRange+" to "+dayStopRange+" fuel carbon trees "+dayFuelNum+" "+dayCarbonNum+" "+dayTreesNum);
        Console.log("Week "+weekStartRange+" to "+weekStopRange+" fuel carbon trees "+weekFuelNum+" "+weekCarbonNum+" "+weekTreesNum);
        Console.log("Month fuel carbon trees "+monthFuelNum+" "+monthCarbonNum+" "+monthTreesNum);
    }


        //TODO: store running totals in Profile
        //TODO: replace with Profile.pathHistoryJSON - pathArrayJSON = Profile.pathHistoryJSON


}
