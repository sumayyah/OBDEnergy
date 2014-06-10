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
import com.example.obdenergy.obdenergy.Utilities.Console;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by sumayyah on 5/31/14.
 */

public class GraphsFragment extends Fragment implements View.OnClickListener{

    private MainActivity mainActivity;
    private TextView fuelUsed;
    private Button today;
    private Button week;
    private Button month;

    private final String classID="GraphsFragment ";

    private final long millisInWeek = 604800000;
    private final long millisInDay = 86400000;

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

        fuelUsed = (TextView)(view.findViewById(R.id.fuelNumber));
        today = (Button)(view.findViewById(R.id.todayButton));
        week = (Button)(view.findViewById(R.id.weekButton));
        month = (Button)(view.findViewById(R.id.monthButton));

        today.setOnClickListener(this);
        week.setOnClickListener(this);
        month.setOnClickListener(this);

        fuelUsed.setText(mainActivity.path.gallonsUsed+"");

        currentTime = System.currentTimeMillis();
        dayStartRange = currentTime-millisInDay;
        dayStopRange = currentTime+millisInDay;
        weekStartRange = currentTime - millisInWeek;
        weekStopRange = currentTime + millisInWeek;

        String holderString = "[{\"initTimestamp\":\"1402365280\", \"finalMAF\":655.35,\"treesKilled\":7, \"gallonsUsed\":3, \"carbonUsed\":61},{\"initTimestamp\":\"1402365284\", \"finalMAF\":655.35,\"treesKilled\":1,\"carbonUsed\":5,\"initFuel\":0,\"gallonsUsed\":7,\"initMAF\":406.65,\"averageSpeed\":55.5,\"finalTimestamp\":\"1402365290\",\"finalFuel\":0}, {\"initTimestamp\":\"1402365276\",\"carbonUsed\":9, \"initFuel\":0,\"initMAF\":406.65,\"finalFuel\":0,\"treesKilled\":3,\"finalMAF\":655.35,\"gallonsUsed\":6}]";

        try {
            parseJSON(new JSONArray(holderString));

        } catch (JSONException e) {
            Console.log(classID+" failed to get JSON array");
            e.printStackTrace();
        }


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
                Console.log(classID+"clicked Today");
                drawIcons(0);
                break;
            case R.id.weekButton:
                Console.log(classID+"clicked Week");
                drawIcons(0);
                break;
            case R.id.monthButton:
                Console.log(classID+"clicked Month");
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

            Console.log(classID+" OBject "+i+" "+objTimestamp);

            /*Store data for month*/
            fuelNum = Double.parseDouble(obj.getString("gallonsUsed"));
            carbonNum = Double.parseDouble(obj.getString("carbonUsed"));
            treesNum = Double.parseDouble(obj.getString("treesKilled"));

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
