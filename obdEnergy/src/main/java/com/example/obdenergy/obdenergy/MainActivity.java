package com.example.obdenergy.obdenergy;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.obdenergy.obdenergy.Activities.Devices;
import com.example.obdenergy.obdenergy.Activities.DriveFragment;
import com.example.obdenergy.obdenergy.Activities.FuelSurveyActivity;
import com.example.obdenergy.obdenergy.Activities.GraphsFragment;
import com.example.obdenergy.obdenergy.Activities.InfoActivity;
import com.example.obdenergy.obdenergy.Activities.InitActivity;
import com.example.obdenergy.obdenergy.Activities.MetricFragment;
import com.example.obdenergy.obdenergy.Activities.TabListener;
import com.example.obdenergy.obdenergy.Data.Path;
import com.example.obdenergy.obdenergy.Data.Profile;
import com.example.obdenergy.obdenergy.Utilities.BluetoothChatService;
import com.example.obdenergy.obdenergy.Utilities.Calculations;
import com.example.obdenergy.obdenergy.Utilities.Console;
import com.example.obdenergy.obdenergy.Utilities.DataLogger;
import com.example.obdenergy.obdenergy.Utilities.DynamoDBTask;
import com.example.obdenergy.obdenergy.Utilities.HttpTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by sumayyah on 5/10/14.
 *
 *
 * This is the central activity of the app, and manages the flow of data between fragments. It sets up DriveFragment,
 * receives data from it, performs calculations functions when DriveFragment finishes data collection,
 * and stores it in global variables then accessible from MetricsFragment and GraphsFragment.
 * It is also responsible for reading and writing data to and from storage - SharedPreferences, local memory, and
 * the cloud database.
 *
 * A listener interface allows it to receive data from DriveFragment upon completion of a drive session.
 */

public class MainActivity extends Activity implements DriveFragment.dataListener {

    private final String classID = "Main Activity ";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    private static final int REQUEST_CREATE_PROFILE = 4;

    // Names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    private static final int GET_DATA = 0;

    private final String USER_DATA_FILE = "MyCarData";

    DriveFragment driveFragment = new DriveFragment();
    MetricFragment metricFragment = new MetricFragment();
    GraphsFragment graphsFragment = new GraphsFragment();

    ActionBar.Tab Tab1;
    ActionBar.Tab Tab2;
    ActionBar.Tab Tab3;

    public Path path;
    public static SharedPreferences userData;
    public static JSONArray jsonPathArray;

    public ArrayList<Path> dbPathArray;
    public String queuedPathsFromMemory;

    Gson gson;
    public String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Console.log(classID+"on Create");
        Console.log(classID+"State is "+ BluetoothChatService.getState());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Set up the action bar.

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Tab1 = actionBar.newTab().setText("DRIVE");
        Tab2 = actionBar.newTab().setText("METRICS");
        Tab3 = actionBar.newTab().setText("GRAPHS");

        Tab1.setTabListener(new TabListener(driveFragment));
        Tab2.setTabListener(new TabListener(metricFragment));
        Tab3.setTabListener(new TabListener(graphsFragment));

        actionBar.addTab(Tab1);
        actionBar.addTab(Tab2);
        actionBar.addTab(Tab3);

        gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();
        dbPathArray = new ArrayList<Path>();

        /*If this is the first time running the app, get user data*/
        userData = getSharedPreferences(USER_DATA_FILE, 0);
        Boolean hasRun = userData.getBoolean("my_first_time", false);

        if(!hasRun){
            userData.edit().putBoolean("my_first_time", true).commit();
            Intent intent = new Intent(this, InitActivity.class);
            startActivityForResult(intent, REQUEST_CREATE_PROFILE);
        }
        else {createProfile();}

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode, resultCode, data);
        Console.log(classID+"onActivityResult: req "+requestCode+" res "+resultCode);


        if(data.getExtras() != null){
            Intent x = data;
            String address = x.getExtras().getString(Devices.EXTRA_DEVICE_ADDRESS);
            String info = x.getExtras().getString(Devices.EXTRA_DEVICE_INFO);
        } else {};

        if(resultCode == 0 || resultCode == RESULT_CANCELED ){
            Console.log(classID+"result cancelled");
            return;
        }

        switch (requestCode){
            case REQUEST_CONNECT_DEVICE_SECURE:
                if (resultCode == Activity.RESULT_OK) {
                    driveFragment.setConnectValidators("Connecting...", true);
                    driveFragment.connectDevice(data, true);
                } else return;
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:

                if (resultCode == Activity.RESULT_OK) {
                    driveFragment.setConnectValidators("Connecting...", true);
                    driveFragment.connectDevice(data, false);
                } else return;
                break;
            case REQUEST_CREATE_PROFILE:
                if(resultCode == Activity.RESULT_OK) createProfile();
                else return;
                break;
            case REQUEST_ENABLE_BT:

                if (resultCode == Activity.RESULT_OK) {
                    //Bluetooth is now enabled, so set up a chat session
                    driveFragment.setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
//                    Console.log(classID+" BT not enabled");
                    finish();
                }
        }
    }

    @Override
    public void DriveFragmentDataComm(int PID) {
        Console.log(classID+" recieved PID "+ PID);

        driveFragment.confirmData(PID);

        String tankCapacity = Profile.getCapacity();

        double gallons = 0.0;
        String street = "";
        path.milesTravelled = Calculations.getMiles(path.speedArray, path.timeArray);
        Double miles = path.milesTravelled;

        /*Calculate gallons based on type of data collected*/
        switch(PID){
            case 0:
                if(path.isHighway())
                {
                    gallons = Calculations.getGallons(miles, "Highway");
                    Console.log(classID+"case 0 is highway");
                }
                else {
                    Console.log(classID+"case 0 is city");
                    gallons = Calculations.getGallons(miles, "City");
                }
                Console.log(classID+"gallons with no data "+gallons);
                if(gallons == 0.0 || gallons == Double.NEGATIVE_INFINITY || gallons == Double.POSITIVE_INFINITY || gallons == Double.NaN ) {
                    Console.log(classID+"Datacomm gallons is "+gallons);
                    DriveFragmentDataComm(4);
                    return;
                }
                break;
            case 47: //Using fuel level data - this should not be used, since MAF is primary calculator
                Console.log(classID + "With Fuel");
                gallons = Calculations.getGallons(path.getInitFuel(), path.getFinalFuel(), tankCapacity);
                if(gallons == 0.0 || gallons == Double.NEGATIVE_INFINITY || gallons == Double.POSITIVE_INFINITY || gallons == Double.NaN) {
                    Console.log(classID+"Datacomm gallons is "+gallons);
                    DriveFragmentDataComm(0); //In case of errors or bad data, get backup algorithm
                    return;
                }
                break;

            case 16: //Using MAF data
                Console.log(classID + " with MAF");
                gallons = Calculations.getGallons(path.MAFarray, 5.0); /*Based on 5 second intervals*/
                if(gallons == 0.0 || gallons == Double.NEGATIVE_INFINITY || gallons == Double.POSITIVE_INFINITY || gallons == Double.NaN) {
                    Console.log(classID+"Datacomm gallons is "+gallons);
                    DriveFragmentDataComm(47); //In case of errors or bad data, get backup algorithm
                    return;
                }
                break;
            case 4:/*This should never get called*/
                Intent intent = new Intent(this, FuelSurveyActivity.class);
                startActivity(intent);
                break;
            default:
                Console.log(classID+" Create metric activity wrong PID");
                break;
        }

        Double carbonUsed = Calculations.getCarbon(gallons);
        Double treesKilled = Calculations.getTrees(gallons);

        path.gallonsUsed = gallons;
        path.carbonUsed = carbonUsed;
        path.treesKilled = treesKilled;

        path.averageSpeed = Calculations.getAvgSpeed(path.speedArray);
        if(Profile.checkPath(path)){
            Console.log(classID+"Path is ok, adding to array and DB");
            Profile.addToPathArray(path);
            dbPathArray.add(path);
            graphsFragment.GraphsFragmentDataComm(path);

            //If we have wifi, send this path along with any others queued, into database
            if(isNetworkAvailable() && DBDataExists()){
                Console.log(classID+"path done, wifi available, adding to db");
                concatenateAndSendDBData(queuedPathsFromMemory, dbPathArray);
            } //If we don't have wifi, save the path in the queue
            else {
                Console.log(classID+"path done, no wifi, keeping in array "+dbPathArray.size());
            }

        } else Console.log(classID+"Path didn't check out");
        Profile.printPathArray();
        metricFragment.MetricFragmentDataComm(String.valueOf(gallons), String.valueOf(carbonUsed), String.valueOf(treesKilled));
    }

    @Override
    public void pathData() {

    }

    public void printMessage(String data){
        Console.log(classID+"printing "+data);
    }

    private void createProfile(){

        username = userData.getString("name", "");
        queuedPathsFromMemory = userData.getString("pathQueue", "");

        /*If there is a queue and we have wifi, write to database*/
        if(DBDataExists() && isNetworkAvailable()){
            Console.log(classID+"We have wifi right from the start, path queue is "+queuedPathsFromMemory);
            sendToAWSDatabase(System.currentTimeMillis(), username, queuedPathsFromMemory);

            /*Reset variables*/
            userData.edit().putString("pathQueue", "").commit();
            queuedPathsFromMemory = "";
        }

        String make = userData.getString("car_make", "");
        String model = userData.getString("car_model", "");
        String year = userData.getString("car_year", "");
        String tank = userData.getString("tank_capacity", "");
        String city = userData.getString("City", "");
        String highway = userData.getString("Highway", "");
        String pathStringArray = userData.getString("Paths", "");

        Profile.setMake(make);
        Profile.setModel(model);
        Profile.setYear(year);
        Profile.setCapacity(tank);
        Profile.setCitympg(city);
        Profile.setHighwaympg(highway);

        try {
            /*If input is null, create an empty JSON array*/
            if(pathStringArray.matches("")) Profile.pathHistoryJSON = new JSONArray();
            else Profile.pathHistoryJSON = new JSONArray(pathStringArray);

            Console.log(classID+"Historical JSON array is "+Profile.pathHistoryJSON);
            graphsFragment.GraphsFragmentDataComm();
        } catch (JSONException e) {
            e.printStackTrace();
            Console.log(classID + " Failed to convert string to JSON");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();


        /*If the app is still running, then don't update*/
        if(DriveFragment.start && DriveFragment.startReady && !DriveFragment.stop ) {
            return;
        }

        String finalPaths = "";

        Profile.printPathArray();
        String jsonArrayString = gson.toJson(Profile.pathArray);
        Console.log(classID+"Collected path "+jsonArrayString);

        JSONArray finalJSONArray = null;

        try {
             jsonPathArray = new JSONArray(jsonArrayString);
        } catch (JSONException e) {

            e.printStackTrace();
        }


        /*Write data to permanent storage in device*/
        for(Path p: Profile.pathArray){
            DataLogger.writeData("PATH: \n"+p.returnData());
        }

        Console.log(classID+"Profile path history, json path "+Profile.pathHistoryJSON+" "+jsonPathArray);


        /*Check what exactly should be pushed into SharedPreferences, to avoid null data*/
        if(Profile.pathHistoryJSON== null && Profile.pathArray.size() ==0){
            Console.log(classID+"No data to push to Shared prefs");
            return;
        }
        else if(Profile.pathHistoryJSON== null || Profile.pathHistoryJSON.length()==0){
            finalJSONArray = jsonPathArray;
            Console.log(classID+"history is null");
        }
        else if(Profile.pathArray.size() ==0){
            finalJSONArray = Profile.pathHistoryJSON;
            Console.log(classID+"Paths are null");
        } else finalJSONArray = Calculations.concatenateJSON(Profile.pathHistoryJSON, jsonPathArray);

        userData.edit().putString("Paths", finalJSONArray.toString()).commit();
//        userData.edit().putString("Paths", "").commit(); /*For testing null strings purposes*/


        /*Update the database, if there is data and if there is wifi.
        If no wifi, concatenate all data and write to SharedPreferences*/

        if(isNetworkAvailable() && DBDataExists()) {
           concatenateAndSendDBData(queuedPathsFromMemory, dbPathArray);
        } else {
            Console.log(classID+" no wifi or no data -> no database update");

            if(dbPathArray.size()> 0) { finalPaths = queuedPathsFromMemory+gson.toJson(dbPathArray);}
            else { finalPaths = queuedPathsFromMemory;}

            //TODO: this can be put in "if" above, and the "else" left blank, and finalPaths removed from function scope
            userData.edit().putString("pathQueue", finalPaths).commit();
        }

        Console.log(classID + "Put array " + finalJSONArray + "in set, committed to SharedPrefs");
    }

    public void sendToAWSDatabase(Long timestamp, String username, String json) {

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");

        Date resultdate = new Date(timestamp);
        String date = (sdf.format(resultdate));

        Console.log(classID+"Calling send to database");
        String[] params = {date, username, json};
        DynamoDBTask dbTask = new DynamoDBTask();
        dbTask.execute(params);
    }

    public boolean DBDataExists(){
        if (queuedPathsFromMemory.matches("") && dbPathArray.size() == 0){
            Console.log(classID+"DB data doesn't exist");
            return false;
        }else{
            Console.log(classID+"DB data exists");
            return true;
        }
    }


    private void concatenateAndSendDBData(String queueFromMemory, ArrayList<Path> currentPathsArray){
        Console.log(classID+"Concatenate DB data");
        String currentPathsJSONstring = gson.toJson(currentPathsArray);

        sendToAWSDatabase(System.currentTimeMillis(), username, queueFromMemory + currentPathsJSONstring);

        queuedPathsFromMemory = "";
        dbPathArray.clear();
        Console.log(classID+" Pushed to DB: "+queueFromMemory+currentPathsJSONstring);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        Console.log(classID + "Network ping: " + (activeNetworkInfo != null && activeNetworkInfo.isConnected()));
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.editCarInfo: /*This will overwrite info in SharedPrefs*/
                Intent intent = new Intent(this, InitActivity.class);
                startActivityForResult(intent, REQUEST_CREATE_PROFILE);
                return true;
            case R.id.info:
                Intent intentInfo = new Intent(this, InfoActivity.class);
                startActivity(intentInfo);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void sendToDatabase(String json){
        String url = "http://192.168.1.8:8888/";
        String[] params = {url, json};

        HttpTask task = new HttpTask();
        task.execute(params);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    //TODO: Check if resultCode errors are caused by back press
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Console.log(classID+"onBackPressed");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }
}
