package com.example.obdenergy.obdenergy.Activities;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.obdenergy.obdenergy.Data.Profile;
import com.example.obdenergy.obdenergy.MainActivity;
import com.example.obdenergy.obdenergy.R;
import com.example.obdenergy.obdenergy.Utilities.Console;

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
    private TextView treesKilled;
    private Button today;
    private Button week;
    private Button month;

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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstancestate){

        View view = inflater.inflate(R.layout.graphs_fragment, container, false);

        gridView = (GridView)(view.findViewById(R.id.gridView1));

        fuelUsed = (TextView)(view.findViewById(R.id.fuelNumber));
        avgSpeed = (TextView)(view.findViewById(R.id.avgSpeedNum));
        carbonUsed = (TextView)(view.findViewById(R.id.carbonUsed));
        treesKilled = (TextView)(view.findViewById(R.id.treesUsed));
        today = (Button)(view.findViewById(R.id.todayButton));
        week = (Button)(view.findViewById(R.id.weekButton));
        month = (Button)(view.findViewById(R.id.monthButton));

        today.setOnClickListener(this);
        week.setOnClickListener(this);
        month.setOnClickListener(this);


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

    public void GraphsFragmentDataComm(){

    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.todayButton: /*Get today's data collected to far - so this is all the paths stored now? no this won't work*/
                fuelUsed.setText(dayFuelNum+"");
                carbonUsed.setText(dayCarbonNum+" kilos CO2");
                treesKilled.setText(dayTreesNum+" trees killed");
                drawIcons(0);
                break;
            case R.id.weekButton:
                fuelUsed.setText(weekFuelNum+"");
                carbonUsed.setText(weekCarbonNum+" kilos CO2");
                treesKilled.setText(weekTreesNum+" trees killed");
                drawIcons(0);
                break;
            case R.id.monthButton:
                fuelUsed.setText(monthFuelNum+"");
                carbonUsed.setText(monthCarbonNum+" kilos CO2");
                treesKilled.setText(monthTreesNum+" trees killed");
                drawIcons(0);
                break;
            default:
                break;
        }
    }

    private void drawIcons(int num){
        
    }

    private void parseJSON(JSONArray jsonArray) throws JSONException {

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

        printData();

        gridAdapter = new GridAdapter(mainActivity,jsonArray.length(), "CARBON"); /*Call grid view when parsing is done*/
        gridView.setAdapter(gridAdapter);
    }

    private void setDefaults(){
        if(mainActivity.path != null) {
            fuelUsed.setText(mainActivity.path.gallonsUsed + "");
            avgSpeed.setText(mainActivity.path.averageSpeed + "");
            carbonUsed.setText(mainActivity.path.carbonUsed + " kilos CO2");
            treesKilled.setText(mainActivity.path.treesKilled + " trees killed");
        }
        else if(Profile.pathArray.size() > 0){
            fuelUsed.setText(Profile.pathArray.get(Profile.pathArray.size()-1).gallonsUsed + "");
            avgSpeed.setText(Profile.pathArray.get(Profile.pathArray.size()-1).averageSpeed + "");
            carbonUsed.setText(Profile.pathArray.get(Profile.pathArray.size()-1).carbonUsed + " kilos CO2");
            treesKilled.setText(Profile.pathArray.get(Profile.pathArray.size()-1).treesKilled + " trees killed");
        }else{
            Console.log(classID+"Error getting data from path");
            fuelUsed.setText(dayFuelNum+"");
            carbonUsed.setText(dayCarbonNum+" kilos CO2");
            treesKilled.setText(dayTreesNum+" trees killed");
        }
    }

    private void printData(){
        Console.log(classID+"Printing data now at "+currentTime);
        Console.log("Day "+dayStartRange+" to "+dayStopRange+" fuel carbon trees "+dayFuelNum+" "+dayCarbonNum+" "+dayTreesNum);
        Console.log("Week "+weekStartRange+" to "+weekStopRange+" fuel carbon trees "+weekFuelNum+" "+weekCarbonNum+" "+weekTreesNum);
        Console.log("Month fuel carbon trees "+monthFuelNum+" "+monthCarbonNum+" "+monthTreesNum);
    }

    /* DISCARDED APPROACH

    Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();

        //TODO: store running totals in Profile
        //TODO: replace with Profile.pathHistoryJSON - pathArrayJSON = Profile.pathHistoryJSON
//        String holderString = "[{\"initTimestamp\":\"1402365280\", \"finalMAF\":655.35,\"treesKilled\":-1},{\"initTimestamp\":\"1402365284\", \"finalMAF\":655.35,\"treesKilled\":-1,\"carbonUsed\":-61,\"initFuel\":0,\"gallonsUsed\":-7,\"initMAF\":406.65,\"averageSpeed\":55.5,\"finalTimestamp\":\"1402365290\",\"finalFuel\":0}, {\"initTimestamp\":\"1402365276\",\"initFuel\":0,\"initMAF\":406.65,\"finalFuel\":0,\"finalMAF\":655.35,\"gallonsUsed\":6}]";
        String holderString = "[{\"initTimestamp\":\"1402365280\", \"finalMAF\":655.35}, {\"initTimestamp\":\"1402365284\", \"finalMAF\":35.35}, {\"initTimestamp\":\"1402365261\", \"finalMAF\":91.35}]";
        try {
            pathArrayJSON = new JSONArray(holderString);
            Console.log(classID+" JSON array is "+pathArrayJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int i=0;i< pathArrayJSON.length();i++){
            try {
                JSONObject obj = (JSONObject) pathArrayJSON.get(i);
                Console.log(classID+" OBject "+i+" "+obj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Type listType =  new TypeToken<ArrayList<Path>>(){}.getType();
        pathArray=gson.fromJson(pathArrayJSON.toString(), listType);

//        Calculations.checkArray(pathArray);
//        Collections.sort(pathArray);
//        Console.log(classID+ "sorted! ");
//        Calculations.checkArray(pathArray);
        stopRange = System.currentTimeMillis();
    *
    * */

}
