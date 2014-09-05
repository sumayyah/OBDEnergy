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

import com.example.obdenergy.obdenergy.Data.Path;
import com.example.obdenergy.obdenergy.Data.Profile;
import com.example.obdenergy.obdenergy.MainActivity;
import com.example.obdenergy.obdenergy.R;
import com.example.obdenergy.obdenergy.Utilities.BluetoothChatService;
import com.example.obdenergy.obdenergy.Utilities.Console;
import com.example.obdenergy.obdenergy.Utilities.DataLogger;
import com.example.obdenergy.obdenergy.Utilities.GridAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * Created by sumayyah on 5/31/14.
 *
 *
 * This activity is meant to allow the user to see their data across a range of times. It receives
 * the user's accumulated data in JSON form from the SharedPreferences file, parses it, and stores it in local
 * variables. These variables supply the data to a Gridview that displays icons representing fuel usage.
 */

public class GraphsFragment extends Fragment implements View.OnClickListener{

    private MainActivity mainActivity;
    private GridAdapter gridAdapter;
    private GridView gridView;
    private TextView fuelUsed;
    private TextView carbonUsed;
    private TextView treesUsed;
    private TextView scale;

    private Button today;
    private Button week;
    private Button month;
    private ImageView cloudClicker;
    private ImageView leafClicker;

    private Integer[] treeList;

    private final String classID="GraphsFragment ";

    private long secsInWeek = 604800;
    private long secsInDay = 86400;

    private long dayStartRange;
    private long currentTime;
    private long weekStartRange;

    private double dayFuelNum = 0.0;
    private double dayCarbonNum = 0.0;
    private double dayTreesNum = 0.0;

    private double weekFuelNum = 0.0;
    private double weekCarbonNum = 0.0;
    private double weekTreesNum = 0.0;

    private double monthFuelNum = 0.0;
    private double monthCarbonNum = 0.0;
    private double monthTreesNum = 0.0;

    private double adapterNum = 0;
    private ArrayList<Integer> imagelist;

    private boolean cloud = true;
    private boolean leaf = false;
    private boolean dayPressed = true;
    private boolean weekPressed = false;
    private boolean monthPressed = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstancestate){

        Console.log(classID+"State is "+ BluetoothChatService.getState());

        View view = inflater.inflate(R.layout.graphs_fragment, container, false);

        gridView = (GridView)(view.findViewById(R.id.gridView1));

        fuelUsed = (TextView)(view.findViewById(R.id.fuelNumber));
        carbonUsed = (TextView)(view.findViewById(R.id.carbonUsed));
        treesUsed = (TextView)(view.findViewById(R.id.treesUsed));
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
        treesUsed.setText(mainActivity.path.treesKilled+"");



        imagelist = new ArrayList<Integer>();
        treeList = new Integer[]{R.drawable.tree1leaf, R.drawable.tree2leaves,  R.drawable.tree3leaves,  R.drawable.tree4leaves,  R.drawable.tree5leaves,  R.drawable.tree6leaves,  R.drawable.tree7leaves,  R.drawable.tree8leaves,  R.drawable.tree9leaves,  R.drawable.tree10leaves};

        setDefaults();
        return view;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mainActivity = (MainActivity) activity;
    }

    public void GraphsFragmentDataComm(){

        currentTime = System.currentTimeMillis()/1000;

        dayStartRange = currentTime - secsInDay;
        weekStartRange = currentTime - secsInWeek;

        try { //Go ahead and parse all the previous paths
            parseJSON(new JSONArray(String.valueOf(Profile.pathHistoryJSON)));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void GraphsFragmentDataComm(Path p){

        monthFuelNum += p.gallonsUsed;
        monthCarbonNum += p.carbonUsed;
        monthTreesNum += p.treesKilled;

        weekFuelNum += p.gallonsUsed;
        weekCarbonNum += p.carbonUsed;
        weekTreesNum += p.treesKilled;

        dayFuelNum += p.gallonsUsed;
        dayCarbonNum += p.carbonUsed;
        dayTreesNum += p.treesKilled;
        Console.log(classID + "Path added, day: " + dayFuelNum + " " + dayCarbonNum + " " + dayTreesNum + " week " + weekFuelNum + " " + weekCarbonNum + " " + weekTreesNum + " month " + monthFuelNum + " " + monthCarbonNum + " " + monthTreesNum);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.todayButton: /*Get today's data collected to far - so this is all the paths stored now? no this won't work*/
                dayPressed =true;weekPressed = false;monthPressed = false;

                today.setTextColor(Color.parseColor("#A4C739"));
                week.setTextColor(Color.WHITE);
                month.setTextColor(Color.WHITE);
                displayData(dayPressed, weekPressed, monthPressed, cloud, leaf);
                break;
            case R.id.weekButton:
                dayPressed =false;weekPressed = true;monthPressed = false;

                week.setTextColor(Color.parseColor("#A4C739"));
                today.setTextColor(Color.WHITE);
                month.setTextColor(Color.WHITE);
                displayData(dayPressed, weekPressed, monthPressed, cloud, leaf);
                break;
            case R.id.monthButton:
                dayPressed =false;weekPressed = false;monthPressed = true;

                month.setTextColor(Color.parseColor("#A4C739"));
                today.setTextColor(Color.WHITE);
                week.setTextColor(Color.WHITE);
                displayData(dayPressed, weekPressed, monthPressed, cloud, leaf);
                break;

            case R.id.cloudClicker:
                cloud = true;leaf = false;

                cloudClicker.setImageDrawable(getResources().getDrawable(R.drawable.cloud_icon_green));
                carbonUsed.setTextColor(Color.parseColor("#A4C739"));
                leafClicker.setImageDrawable(getResources().getDrawable(R.drawable.leafcopy));
                treesUsed.setTextColor(Color.WHITE);
                displayData(dayPressed, weekPressed, monthPressed, cloud, leaf);
                break;
            case R.id.leafClicker:
                cloud = false;leaf = true;

                cloudClicker.setImageDrawable(getResources().getDrawable(R.drawable.cloud_icon));
                carbonUsed.setTextColor(Color.WHITE);
                leafClicker.setImageDrawable(getResources().getDrawable(R.drawable.leafgreen));
                treesUsed.setTextColor(Color.parseColor("#A4C739"));
                displayData(dayPressed, weekPressed, monthPressed, cloud, leaf);
                break;

            default:
                break;
        }
    }

    private ArrayList<Integer> setImageArray(double imagenumber, int type){
        ArrayList<Integer> finalImages = new ArrayList<Integer>();
        int wholenum = (int) imagenumber;
        double decimalportion = imagenumber%1;
        int finaldecimal = (int)(decimalportion*10);


        switch(type){
            case 1: /*If carbon*/
                Console.log(classID+"Carbon");
                for(int i=0;i<wholenum;i++){
                    finalImages.add(R.drawable.cloud_icon_green);
                }

                break;
            case 2: /*If trees*/

                Console.log(classID+"Trees");
                for(int i=0;i<wholenum;i++){
                    finalImages.add(treeList[9]);
                }
                if(decimalportion > 0 && finaldecimal > 0){
                    finalImages.add(treeList[finaldecimal - 1]);
                } else finalImages.add(treeList[0]);
                break;
            default:
                break;
        }
        return finalImages;
    }

    private void displayData(boolean day, boolean week, boolean month, boolean cloud, boolean leaf){

        /*If the user has selected carbon*/
        if(cloud && !leaf){
            if(day && !week && !month){
                fuelUsed.setText(dayFuelNum + "");

                carbonUsed.setText(dayCarbonNum + " kilos CO2");
                adapterNum = dayCarbonNum;
                scale.setText("1 cloud per kilo of carbon");
                imagelist = setImageArray(adapterNum, 1);

            }
            else if(!day && week && !month){
                fuelUsed.setText(weekFuelNum+"");

                carbonUsed.setText(weekCarbonNum + " kilos CO2");
                if(weekCarbonNum<=10) {adapterNum = (int)(weekCarbonNum); scale.setText("1 cloud per kilo of carbon");}
                else {adapterNum = (weekCarbonNum/10); scale.setText("1 cloud for every 10 kilos of carbon");}
                imagelist = setImageArray(adapterNum, 1);

            }
            else if(!day && !week && month){
                fuelUsed.setText(monthFuelNum+"");

                carbonUsed.setText(monthCarbonNum + " kilos CO2");
                if(monthCarbonNum<=10) {adapterNum = (int)(monthCarbonNum); scale.setText("1 cloud per kilo of carbon");}
                else {adapterNum = (monthCarbonNum/10); scale.setText("1 cloud for every 10 kilos of carbon");}
                imagelist = setImageArray(adapterNum, 1);

            }
            else;
        }

        /*If the user has selected carbon*/
        else if(leaf && !cloud){
            scale.setText("1 leaf per tree used");
            if(day && !week && !month){
                fuelUsed.setText(dayFuelNum + "");

                carbonUsed.setText(dayCarbonNum + " kilos CO2");
                treesUsed.setText(dayTreesNum + " trees used");
                scale.setText("1 leaf per 0.1 tree used");
                adapterNum = (dayTreesNum);
                imagelist = setImageArray(adapterNum, 2);

            }
            else if(!day && week && !month){
                fuelUsed.setText(weekFuelNum+"");

                carbonUsed.setText(weekCarbonNum + " kilos CO2");
                treesUsed.setText(weekTreesNum + " trees used");
                scale.setText("1 leaf per 0.1 tree used");
                adapterNum = (weekTreesNum);
                imagelist = setImageArray(adapterNum, 2);
            }
            else if(!day && !week && month){
                fuelUsed.setText(monthFuelNum+"");
                carbonUsed.setText(monthCarbonNum + " kilos CO2");
                treesUsed.setText(monthTreesNum + " trees used");
                scale.setText("1 leaf per 0.1 tree used");
                adapterNum = (monthTreesNum);
                imagelist = setImageArray(adapterNum, 2);
            }
            else Console.log(classID+"Wrong time bool for leaf");
        }

        else Console.log(classID+"wrong boolean somewhere");

        gridAdapter = new GridAdapter(mainActivity, imagelist);
        gridView.setAdapter(gridAdapter);
    }

    private void parseJSON(JSONArray jsonArray) throws JSONException {

        DecimalFormat df = new DecimalFormat("#.00");

        Long objTimestamp;
        double fuelNum;
        double carbonNum;
        double treesNum;

        for(int i=0;i<jsonArray.length();i++){

            if(jsonArray.isNull(i)){
                continue;
            }

            JSONObject obj = (JSONObject) jsonArray.get(i);
            objTimestamp = Long.parseLong(obj.getString("initTimestamp"));

            /*Get object's data*/
            fuelNum = obj.getDouble("gallonsUsed");
            carbonNum = obj.getDouble("carbonUsed");
            treesNum = obj.getDouble("treesKilled");

            monthFuelNum += fuelNum;
            monthCarbonNum += carbonNum;
            monthTreesNum += treesNum;

            /*If in range, calculate data for the week*/
            if(objTimestamp <= currentTime && objTimestamp >= weekStartRange){

                weekFuelNum += fuelNum;
                weekCarbonNum += carbonNum;
                weekTreesNum += treesNum;
            }

            /*If in range, calculate data for the day*/
            if(objTimestamp <= currentTime && objTimestamp >= dayStartRange){

                dayFuelNum += fuelNum;
                dayCarbonNum += carbonNum;
                dayTreesNum += treesNum;
            }
        }

        dayCarbonNum = Double.parseDouble(df.format(dayCarbonNum));
        dayTreesNum = Double.parseDouble(df.format(dayTreesNum));
        dayFuelNum = Double.parseDouble(df.format(dayFuelNum));
        weekCarbonNum = Double.parseDouble(df.format(weekCarbonNum));
        weekTreesNum = Double.parseDouble(df.format(weekTreesNum));
        weekFuelNum = Double.parseDouble(df.format(weekFuelNum));
        monthCarbonNum = Double.parseDouble(df.format(monthCarbonNum));
        monthTreesNum = Double.parseDouble(df.format(monthTreesNum));
        monthFuelNum = Double.parseDouble(df.format(monthFuelNum));

        printData();
    }

    private void setDefaults(){

        fuelUsed.setText(dayFuelNum+"");
        carbonUsed.setText(dayCarbonNum + " kilos CO2");
        treesUsed.setText(dayTreesNum + " trees used");

        dayPressed =true;weekPressed = false;monthPressed = false;
        today.setTextColor(Color.parseColor("#A4C739"));
        cloudClicker.setImageDrawable(getResources().getDrawable(R.drawable.cloud_icon_green));
        carbonUsed.setTextColor(Color.parseColor("#A4C739"));

        adapterNum = (int)dayCarbonNum;
        imagelist = setImageArray(adapterNum, 1);
        gridAdapter = new GridAdapter(mainActivity,imagelist); /*Call grid view when parsing is done*/
        gridView.setAdapter(gridAdapter);
    }

    private void printData(){
        Console.log(classID+"Printing data now at "+currentTime);
        Console.log("Day "+dayStartRange+" to "+currentTime+" fuel carbon trees "+dayFuelNum+" "+dayCarbonNum+" "+dayTreesNum);
        Console.log("Week "+weekStartRange+" to "+currentTime+" fuel carbon trees "+weekFuelNum+" "+weekCarbonNum+" "+weekTreesNum);
        Console.log("Month fuel carbon trees "+monthFuelNum+" "+monthCarbonNum+" "+monthTreesNum);

        DataLogger.writeConsoleData(classID+"Printing data now at "+currentTime);
        DataLogger.writeConsoleData("Day "+dayStartRange+" to "+currentTime+" fuel carbon trees "+dayFuelNum+" "+dayCarbonNum+" "+dayTreesNum);
        DataLogger.writeConsoleData("Week "+weekStartRange+" to "+currentTime+" fuel carbon trees "+weekFuelNum+" "+weekCarbonNum+" "+weekTreesNum);
        DataLogger.writeConsoleData("Month fuel carbon trees "+monthFuelNum+" "+monthCarbonNum+" "+monthTreesNum);


    }

}
