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
import com.example.obdenergy.obdenergy.Utilities.Console;
import com.example.obdenergy.obdenergy.Utilities.GridAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * Created by sumayyah on 5/31/14.
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

    private int adapterNum = 0;
    private String adapterType = "CARBON";
    private ArrayList<Integer> imagelist;

    private boolean cloud = true;
    private boolean leaf = false;
    private boolean dayPressed = true;
    private boolean weekPressed = false;
    private boolean monthPressed = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstancestate){

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

        currentTime = System.currentTimeMillis()/1000;

        dayStartRange = currentTime - secsInDay;
        weekStartRange = currentTime - secsInWeek;

        imagelist = new ArrayList<Integer>();

//        Console.log(classID+"Timings: Current: "+currentTime+" day from "+dayStartRange+" week from "+weekStartRange);


//        RelativeLayout imageLayout = (RelativeLayout)(view.findViewById(R.id.imageLayout));
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50,50);
//        params.leftMargin = 100;
//        params.rightMargin = 100;
//
//        ImageView imgView = new ImageView(getActivity());
//        imgView.setBackgroundResource(R.drawable.launcher_icon1);
//
//        imageLayout.addView(imgView, params);

        /*If the user has paths in the current session*/
        if(Profile.pathArray.size() > 0){
            Console.log(classID+"Paths in current session");
            for(Path p: Profile.pathArray){
                dayFuelNum += p.gallonsUsed;
                dayCarbonNum += p.carbonUsed;
                dayTreesNum += p.treesKilled;

                weekFuelNum += p.gallonsUsed;
                weekCarbonNum += p.carbonUsed;
                weekTreesNum += p.treesKilled;

                monthFuelNum += p.gallonsUsed;
                monthCarbonNum += p.carbonUsed;
                monthTreesNum += p.treesKilled;
            }

            Console.log(classID+"Before parsing, day: "+dayFuelNum+" "+dayCarbonNum+" "+dayTreesNum+" week "+weekFuelNum+" "+weekCarbonNum+" "+weekTreesNum+" month "+monthFuelNum+" "+monthCarbonNum+" "+monthTreesNum);
        }

        try { //Go ahead and parse all the previous paths
            parseJSON(new JSONArray(String.valueOf(Profile.pathHistoryJSON)));

        } catch (JSONException e) {
            Console.log(classID+" failed to get JSON array");
            e.printStackTrace();
        }

//        Console.log(classID+"Pieces are today: "+todayJSONArray);
//        Console.log(classID+"Historical "+Profile.pathHistoryJSON);
//        String holderString = "[{\"initTimestamp\":\"1402414587670\", \"finalMAF\":655.35,\"treesKilled\":7, \"gallonsUsed\":3, \"carbonUsed\":61},{\"initTimestamp\":\"1401896187867\", \"finalMAF\":655.35,\"treesKilled\":1,\"carbonUsed\":5,\"initFuel\":0,\"gallonsUsed\":7,\"initMAF\":406.65,\"averageSpeed\":55.5,\"finalTimestamp\":\"1402365290\",\"finalFuel\":0}, {\"initTimestamp\":\"1402417236395\",\"carbonUsed\":9, \"initFuel\":0,\"initMAF\":406.65,\"finalFuel\":0,\"treesKilled\":3,\"finalMAF\":655.35,\"gallonsUsed\":6}]";

        setDefaults();
        treeList = new Integer[]{R.drawable.tree1leaf, R.drawable.tree2leaves,  R.drawable.tree3leaves,  R.drawable.tree4leaves,  R.drawable.tree5leaves,  R.drawable.tree6leaves,  R.drawable.tree7leaves,  R.drawable.tree8leaves,  R.drawable.tree9leaves,  R.drawable.tree10leaves};
//        setImageArray();


        return view;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Console.log(classID+"onCreate");
        setHasOptionsMenu(true);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        Console.log(classID+"on attach");
        super.onAttach(activity);
        this.mainActivity = (MainActivity) activity;
    }

    public void GraphsFragmentDataComm(){}

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

    private ArrayList<Integer> setImageArray(int imagenumber, int type){
//        double imagenumber = 5.2;
        ArrayList<Integer> finalImages = new ArrayList<Integer>();
        int wholenum = (int) imagenumber;
        double decimalportion = imagenumber%1;
        int finaldecimal = (int)(decimalportion*10);

        switch(type){
            case 1: /*If carbon*/

                for(int i=0;i<wholenum;i++){
                    finalImages.add(R.drawable.cloud_icon);
                }

                break;
            case 2: /*If trees*/

                for(int i=0;i<wholenum;i++){
                    finalImages.add(treeList[9]);
                }
                if(decimalportion > 0){
                    finalImages.add(treeList[finaldecimal - 1]);
                }
                break;
            default:
                break;
        }

        Console.log(classID+"Number of whole images is "+wholenum+" and parts are "+decimalportion+" ie "+finaldecimal);
//        gridAdapter = new GridAdapter(mainActivity, finalImages, "TREE");
//        gridView.setAdapter(gridAdapter);

        return finalImages;
    }

    private void displayData(boolean day, boolean week, boolean month, boolean cloud, boolean leaf){

        if(cloud && !leaf){
            if(day && !week && !month){
                fuelUsed.setText(dayFuelNum + "");

                carbonUsed.setText(dayCarbonNum + " kilos CO2");
//                if(dayCarbonNum<=10) {adapterNum = (int)(dayCarbonNum); scale.setText("1 cloud per kilo of carbon");}
//                else {adapterNum = (int)(dayCarbonNum/10); scale.setText("1 cloud for every 10 kilos of carbon");}
                adapterNum = (int)dayCarbonNum;
                scale.setText("1 cloud per kilo of carbon");
//                adapterType = "CARBON";
                imagelist = setImageArray(adapterNum, 1);

//                Console.log(classID+"Cloud and day, send number and type "+adapterNum+" "+adapterType);
            }
            else if(!day && week && !month){
                fuelUsed.setText(weekFuelNum+"");

                carbonUsed.setText(weekCarbonNum + " kilos CO2");
                if(weekCarbonNum<=10) {adapterNum = (int)(weekCarbonNum); scale.setText("1 cloud per kilo of carbon");}
                else {adapterNum = (int)(weekCarbonNum/10); scale.setText("1 cloud for every 10 kilos of carbon");}
//                adapterType = "CARBON";
                imagelist = setImageArray(adapterNum, 1);

//                Console.log(classID+"Cloud and week send number and type "+adapterNum+" "+adapterType);
            }
            else if(!day && !week && month){
                fuelUsed.setText(monthFuelNum+"");

                carbonUsed.setText(monthCarbonNum + " kilos CO2");
                if(monthCarbonNum<=10) {adapterNum = (int)(monthCarbonNum); scale.setText("1 cloud per kilo of carbon");}
                else {adapterNum = (int)(monthCarbonNum/10); scale.setText("1 cloud for every 10 kilos of carbon");}
//                adapterType = "CARBON";
                imagelist = setImageArray(adapterNum, 1);

//                Console.log(classID+"Cloud and month, send number and type "+adapterNum+" "+adapterType);
            }
            else Console.log(classID+"Wrong time bool for cloud");
        }
        else if(leaf && !cloud){
            scale.setText("1 leaf per tree required");
            if(day && !week && !month){
                fuelUsed.setText(dayFuelNum + "");

                carbonUsed.setText(dayCarbonNum + " kilos CO2");
                treesUsed.setText(dayTreesNum + " trees required");
                scale.setText("1 leaf per 0.1 tree required");
                adapterNum = (int)(dayTreesNum*10);
//                adapterType = "TREE";
                imagelist = setImageArray(adapterNum, 2);

//                Console.log(classID+"Leaf and day, send number and type "+adapterNum+" "+adapterType);
            }
            else if(!day && week && !month){
                fuelUsed.setText(weekFuelNum+"");

                carbonUsed.setText(weekCarbonNum + " kilos CO2");
                treesUsed.setText(weekTreesNum + " trees required");
                scale.setText("1 leaf per 0.1 tree required");
                adapterNum = (int)(weekTreesNum*10);
//                adapterType = "TREE";
                imagelist = setImageArray(adapterNum, 2);

//                Console.log(classID+"Leaf and week, send number and type "+adapterNum+" "+adapterType);
            }
            else if(!day && !week && month){
                fuelUsed.setText(monthFuelNum+"");
                carbonUsed.setText(monthCarbonNum + " kilos CO2");
                treesUsed.setText(monthTreesNum + " trees required");
                scale.setText("1 leaf per 0.1 tree required");
                adapterNum = (int)(monthTreesNum*10);
//                adapterType = "TREE";
                imagelist = setImageArray(adapterNum, 2);
//                Console.log(classID+"Leaf and month, send number and type "+adapterNum+" "+adapterType);
            }
            else Console.log(classID+"Wrong time bool for leaf");
        }

        else Console.log(classID+"wrong boolean somewhere");

        gridAdapter = new GridAdapter(mainActivity, imagelist);
        gridView.setAdapter(gridAdapter);
    }

    private void parseJSON(JSONArray jsonArray) throws JSONException {

        Console.log(classID + "Parsing JSON "+jsonArray);

        DecimalFormat df = new DecimalFormat("#.00");

        Long objTimestamp;
        double fuelNum;
        double carbonNum;
        double treesNum;

        for(int i=0;i<jsonArray.length()-1;i++){

            if(jsonArray.isNull(i)){
                Console.log(classID+"Array is null at index "+i);
                continue;
            }

            JSONObject obj = (JSONObject) jsonArray.get(i);
            objTimestamp = Long.parseLong(obj.getString("initTimestamp"));

            /*Get object's data*/
            fuelNum = obj.getDouble("gallonsUsed");
            carbonNum = obj.getDouble("carbonUsed");
            treesNum = obj.getDouble("treesKilled");

            Console.log(classID+"Object "+i+" at "+objTimestamp+" Current time " + currentTime+" Data: fuel carbon trees "+fuelNum+" "+carbonNum+" "+treesNum);

            monthFuelNum += fuelNum;
            monthCarbonNum += carbonNum;
            monthTreesNum += treesNum;

            /*If in range, calculate data for the week*/
            if(objTimestamp <= currentTime && objTimestamp >= weekStartRange){

                weekFuelNum += fuelNum;
                weekCarbonNum += carbonNum;
                weekTreesNum += treesNum;
                Console.log(classID+" Week from "+weekStartRange+" to "+currentTime+" Data: fuel carbon trees "+weekFuelNum+" "+weekCarbonNum+" "+weekTreesNum);
            }

            /*If in range, calculate data for the day*/
            if(objTimestamp <= currentTime && objTimestamp >= dayStartRange){

                dayFuelNum += fuelNum;
                dayCarbonNum += carbonNum;
                dayTreesNum += treesNum;
                Console.log(classID+" Today from "+dayStartRange+" to "+currentTime+" Data: fuel carbon trees "+dayFuelNum+" "+dayCarbonNum+" "+dayTreesNum);
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
        treesUsed.setText(dayTreesNum + " trees required");

        dayPressed =true;weekPressed = false;monthPressed = false;
        today.setTextColor(Color.parseColor("#A4C739"));
        cloudClicker.setImageDrawable(getResources().getDrawable(R.drawable.cloud_icon_green));
        carbonUsed.setTextColor(Color.parseColor("#A4C739"));

        adapterNum = (int)dayCarbonNum;
//        adapterType = "CARBON";
        imagelist = setImageArray(adapterNum, 1);
        gridAdapter = new GridAdapter(mainActivity,imagelist); /*Call grid view when parsing is done*/
        gridView.setAdapter(gridAdapter);
    }

    private void printData(){
        Console.log(classID+"Printing data now at "+currentTime);
        Console.log("Day "+dayStartRange+" to "+currentTime+" fuel carbon trees "+dayFuelNum+" "+dayCarbonNum+" "+dayTreesNum);
        Console.log("Week "+weekStartRange+" to "+currentTime+" fuel carbon trees "+weekFuelNum+" "+weekCarbonNum+" "+weekTreesNum);
        Console.log("Month fuel carbon trees "+monthFuelNum+" "+monthCarbonNum+" "+monthTreesNum);
    }

}
