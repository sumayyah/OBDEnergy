package com.example.obdenergy.obdenergy;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.example.obdenergy.obdenergy.Activities.DriveFragment;
import com.example.obdenergy.obdenergy.Activities.GraphsFragment;
import com.example.obdenergy.obdenergy.Activities.InitActivity;
import com.example.obdenergy.obdenergy.Activities.MetricFragment;
import com.example.obdenergy.obdenergy.Activities.TabListener;
import com.example.obdenergy.obdenergy.Data.Path;
import com.example.obdenergy.obdenergy.Data.Profile;
import com.example.obdenergy.obdenergy.Data.Test;
import com.example.obdenergy.obdenergy.Utilities.Calculations;
import com.example.obdenergy.obdenergy.Utilities.Console;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;

public  class MainActivity extends Activity implements DriveFragment.dataListener {
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    ArrayList<Fragment> fragmentArray = new ArrayList<Fragment>();
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

    public Test test;
    public Test test2;
    public ArrayList<Test> testArray;

    public Path path;
    public static SharedPreferences userData;
    public static JSONArray jsonPathArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
//        final ActionBar actionBar = getSupportActionBar();

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

        test = new Test();
        test2 = new Test();
        testArray = new ArrayList<Test>();

        test.array.add("a");
        test.array.add("b");
        test.array.add("c");
        test.array.add("d");
        test.array.add("e");
        test.array.add("f");
        test.array.add("g");
        test2.array.add("h");
        test2.array.add("i");
        test2.array.add("j");
        test2.array.add("k");
        test2.array.add("l");

        testArray.add(test);
        testArray.add(test2);


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

                if (resultCode == Activity.RESULT_OK) { //TODO: create one function in drivefragment
                    driveFragment.setConnectStatus("Connecting...");
                    driveFragment.setProgressBar(true);
                    driveFragment.connectDevice(data, true);
                    Console.log(classID+"Got connect requet, sent off to drive");
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:

                if (resultCode == Activity.RESULT_OK) {
                    driveFragment.setConnectStatus("Connecting...");
                    driveFragment.setProgressBar(true);
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
//                    Console.log(classID+" BT not enabled");
                    finish();
                }
        }
    }

    @Override
    public void DriveFragmentDataComm(int PID) {
        Console.log(classID+" received PID "+ PID);

        driveFragment.confirmData(PID);

//        String tankCapacity = Profile.getCapacity();
        String tankCapacity = "14";

        String gallons = "0.0";
        String street = "";
        Double miles = path.getMiles();

        switch(PID){
            case 0:
                Console.log(classID+" with no data");
                if(path.isHighway())
                {
                    street = "Highway";
                    gallons = Calculations.getGallons(miles, street);
                }
                else gallons = Calculations.getGallons(miles, "City");

                return;
            case 47: //Using fuel level data
                Console.log(classID+"With Fuel");
                gallons = Calculations.getGallons(path.getInitFuel(), path.getFinalFuel(), tankCapacity);
                if(gallons.equals("0.0")) {
                    DriveFragmentDataComm(0); //In case of errors or bad data, get backup algorithm
                    return;
                }
                break;
            case 16: //Using MAF data
                Console.log(classID+" with MAF");
                gallons = Calculations.getGallons(path.getInitMAF(), path.getFinalMAF(), path.getInitTime(), path.getfinalTime());
                if(gallons.equals("0.0")) {
                    DriveFragmentDataComm(0); //In case of errors or bad data, get backup algorithm
                    return;
                }
                break;
            case 4:
                //TODO: get fuelSurveyActivity
                break;
            default:
                Console.log(classID+" Create metric activity wrong PID");
                break;
        }

        String carbonUsed = Calculations.getCarbon(Double.parseDouble(gallons));
        String treesKilled = Calculations.getTrees(Double.parseDouble(gallons));
        metricFragment.MetricFragmentDataComm(gallons, carbonUsed, treesKilled);

        path.gallonsUsed = Double.parseDouble(gallons);
        path.carbonUsed = Double.parseDouble(carbonUsed);
        path.treesKilled = Double.parseDouble(treesKilled);

        path.calculateAvgSpeed();
        Profile.addToPathArray(path);
        Console.log(classID+"Added path to Profile array");
        Profile.checkArray();
    }

    public void printMessage(String data){
        Console.log(classID+"printing "+data);
    }

    private void createProfile(){
        String make = userData.getString("car_make", "");
        String model = userData.getString("car_model", "");
        String year = userData.getString("car_year", "");
        String tank = userData.getString("tank_capacity", "");
        String city = userData.getString("City", "");
        String highway = userData.getString("Highway", "");
        String pathStringArray = userData.getString("Paths", "");

        Console.log(classID+"Path string is "+pathStringArray);
        try {
            jsonPathArray = new JSONArray(pathStringArray);
            Console.log(classID+"Path JSON array is "+jsonPathArray);
        } catch (JSONException e) {
            e.printStackTrace();
            Console.log(classID+" Failed to convert string to JSON");
        }

        Profile.setMake(make);
        Profile.setModel(model);
        Profile.setYear(year);
        Profile.setCapacity(tank);
        Profile.setCitympg(city);
        Profile.setHighwaympg(highway);

        Console.log(classID+"Created Profile, checking contents");
        Console.log(Profile.checkContents());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Console.log(classID + "stopped");

        /*Create GSON builder that can write static variables (Path needs static vars and methods)*/
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();
        Type listType = new TypeToken<ArrayList<Path>>(){}.getType();
        String jsonArray = gson.toJson(Profile.pathArray);

        userData.edit().putString("Paths", jsonArray.toString()).commit();

        Console.log(classID+"Put array "+jsonArray+"in set, commited to SharedPrefs");
    }
}
