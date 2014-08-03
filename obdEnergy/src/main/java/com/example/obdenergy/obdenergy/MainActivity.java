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

import com.example.obdenergy.obdenergy.Activities.DriveFragment;
import com.example.obdenergy.obdenergy.Activities.FuelSurveyActivity;
import com.example.obdenergy.obdenergy.Activities.GraphsFragment;
import com.example.obdenergy.obdenergy.Activities.InfoActivity;
import com.example.obdenergy.obdenergy.Activities.InitActivity;
import com.example.obdenergy.obdenergy.Activities.MetricFragment;
import com.example.obdenergy.obdenergy.Activities.TabListener;
import com.example.obdenergy.obdenergy.Data.Path;
import com.example.obdenergy.obdenergy.Data.Profile;
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

        /*Create GSON builder that can write static variables (Path needs static vars and methods)*/
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


        switch (requestCode){
            case REQUEST_CONNECT_DEVICE_SECURE:
                if (resultCode == Activity.RESULT_OK) {
                    driveFragment.setConnectValidators("Connecting...", true);
                    driveFragment.connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:

                if (resultCode == Activity.RESULT_OK) {
                    driveFragment.setConnectValidators("Connecting...", true);
                    driveFragment.connectDevice(data, false);
                }
                break;
            case REQUEST_CREATE_PROFILE:
                if(resultCode == Activity.RESULT_OK) createProfile();
                break;
            case REQUEST_ENABLE_BT:

                if (resultCode == Activity.RESULT_OK) {
                    //Bluetooth is now enabled, so set up a chat session
                    driveFragment.setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    finish();
                }
        }
    }

    @Override
    public void DriveFragmentDataComm(int PID) {

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
                }
                else {
                    gallons = Calculations.getGallons(miles, "City");
                }
                if(gallons == 0.0 ) DriveFragmentDataComm(4);

                return;
            case 47: //Using fuel level data
                DataLogger.writeConsoleData(classID+"Calculations based on fuel");

                gallons = Calculations.getGallons(path.getInitFuel(), path.getFinalFuel(), tankCapacity);
                if(gallons == 0.0 || gallons == Double.NEGATIVE_INFINITY || gallons == Double.POSITIVE_INFINITY || gallons == Double.NaN) {
                    DriveFragmentDataComm(0); //In case of errors or bad data, get backup algorithm
                    return;
                }
                break;

            case 16: //Using MAF data
                DataLogger.writeConsoleData(classID+" Calculations based on MAF");
                gallons = Calculations.getGallons(path.MAFarray, 5.0); /*Based on 5 second intervals*/
                if(gallons == 0.0 || gallons == Double.NEGATIVE_INFINITY || gallons == Double.POSITIVE_INFINITY || gallons == Double.NaN) {
                    DriveFragmentDataComm(47); //In case of errors or bad data, get backup algorithm
                    return;
                }
                break;
            case 4:/*This should never get called*/
                Intent intent = new Intent(this, FuelSurveyActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

        String carbonUsed = Calculations.getCarbon(gallons);
        Double treesKilled = Calculations.getTrees(gallons);

        path.gallonsUsed = gallons;
        path.carbonUsed = Double.parseDouble(carbonUsed);
        path.treesKilled = treesKilled;

        path.averageSpeed = Calculations.getAvgSpeed(path.speedArray);
        if(Profile.checkPath(path)){
            Profile.addToPathArray(path);
            graphsFragment.GraphsFragmentDataComm(path);

            //If we have wifi, send this path along with any others queued, into database
            if(isNetworkAvailable() && DBDataExists()){
                Console.log(classID+"path done, wifi available, adding to db");
                dbPathArray.add(path);
                concatenateAndSendDBData(queuedPathsFromMemory, dbPathArray);
            } //If we don't have wifi, save the path in the queue
            else {
                dbPathArray.add(path);
                Console.log(classID+"path done, no wifi, pushing to array "+dbPathArray.size());
            }

        }
        Profile.printPathArray();
        metricFragment.MetricFragmentDataComm(String.valueOf(gallons), carbonUsed, String.valueOf(treesKilled));
    }

    @Override
    public void pathData() {

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
            Profile.pathHistoryJSON = new JSONArray(pathStringArray);
            Console.log(classID+"Historical JSON array is "+Profile.pathHistoryJSON);
            graphsFragment.GraphsFragmentDataComm();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();


        Profile.printPathArray();
        String jsonArrayString = gson.toJson(Profile.pathArray);

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

        /*Check what exactly should be pushed into SharedPreferences, to avoid null data*/
        if(Profile.pathHistoryJSON== null && Profile.pathArray.size() ==0){
            return;
        }
        else if(Profile.pathHistoryJSON== null || Profile.pathHistoryJSON.length()==0){
            finalJSONArray = jsonPathArray;
        }
        else if(Profile.pathArray.size() ==0){
            finalJSONArray = Profile.pathHistoryJSON;
        } else finalJSONArray = Calculations.concatenateJSON(Profile.pathHistoryJSON, jsonPathArray);

        userData.edit().putString("Paths", finalJSONArray.toString()).commit();

        DataLogger.writeConsoleData(classID+"Writing to local storage: "+finalJSONArray);

        /*Update the database, if there is data and if there is wifi.
        If no wifi, concatenate all data and write to SharedPreferences*/

        if(isNetworkAvailable() && DBDataExists()) {
           concatenateAndSendDBData(queuedPathsFromMemory, dbPathArray);
        } else {

            DataLogger.writeConsoleData(classID+" no wifi or no data -> no database update");
            String finalPaths = queuedPathsFromMemory+gson.toJson(dbPathArray);
            userData.edit().putString("pathQueue", finalPaths).commit();
        }
    }

    public void sendToAWSDatabase(Long timestamp, String username, String json) {

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");

        Date resultdate = new Date(timestamp);
        String date = (sdf.format(resultdate));

        String[] params = {date, username, json};
        DynamoDBTask dbTask = new DynamoDBTask();
        dbTask.execute(params);
    }

    public boolean DBDataExists(){
        if (queuedPathsFromMemory.matches("") && dbPathArray.size() == 0){
            return false;
        }else{
            Console.log(classID+"DB data exists");
            return true;
        }
    }


    private void concatenateAndSendDBData(String queueFromMemory, ArrayList<Path> currentPathsArray){
        String currentPathsJSONstring = gson.toJson(currentPathsArray);

        sendToAWSDatabase(System.currentTimeMillis(), username, queueFromMemory+currentPathsJSONstring);

        queuedPathsFromMemory = "";
        dbPathArray.clear();
        DataLogger.writeConsoleData(classID+" Pushed to DB: "+queueFromMemory+currentPathsJSONstring);

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        Console.log(classID+"Network ping: "+(activeNetworkInfo != null && activeNetworkInfo.isConnected()));
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

}
