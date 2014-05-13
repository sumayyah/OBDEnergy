package com.example.obdenergy.obdenergy;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
//import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.obdenergy.obdenergy.Activities.Devices;
import com.example.obdenergy.obdenergy.Activities.FuelSurveyActivity;
import com.example.obdenergy.obdenergy.Activities.InitActivity;
import com.example.obdenergy.obdenergy.Activities.MetricActivity;
import com.example.obdenergy.obdenergy.Utilities.BluetoothChatService;

import com.example.obdenergy.obdenergy.Utilities.Console;

public class MainActivity extends Activity implements View.OnClickListener{

    private final String classID = "MainActivity";

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // String buffer for outgoing messages
    private static StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    static BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private static BluetoothChatService mChatService = null;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    private static final int REQUEST_CREATE_PROFILE = 4;


    // Key names received from the BluetoothChatService Handler
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

    Profile userProfile;
    SharedPreferences userData;

    private TextView data;
    private Button startButton;
    private Button stopButton;
    private Button connectButton;

    private Boolean fuelDataGiven = false;
    private String initFuel = "";
    private String initDistance = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Console.log(classID+" on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = (TextView)(findViewById(R.id.data));
        startButton = (Button)(findViewById(R.id.startButton));
        stopButton = (Button)(findViewById(R.id.stopButton));
        connectButton = (Button)(findViewById(R.id.connectButton));
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        connectButton.setOnClickListener(this);

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

        Console.log(classID+" Activity result");

        //TODO: request secure connects and enable bluetooth

        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CREATE_PROFILE){
            createProfile();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //TODO: check bTooth enabled
        //TODO: once enabled, setup Chat
    }

    private void setupChat(){
        Console.log(classID+" setting up chat");

        //TODO: set up chat service handler, output buffer, adn initial message
    }

    private void createProfile(){
        Console.log(classID+" creating user profile object from stored data");
        String make = userData.getString("car_make", "");
        String model = userData.getString("car_model", "");
        String year = userData.getString("car_year", "");
        String tank = userData.getString("tank_capacity", "");
        String city = userData.getString("city_mpg", "");
        String highway = userData.getString("highway_mpg", "");

        userProfile = new Profile(make, model, year, tank, city, highway);

        data.setText("Car data "+make+" "+model+" "+year);
    }

    private void collectData(){

        Console.log(classID+" collecting Data");

        Long time = System.currentTimeMillis()/1000;
        String timeString = time.toString();

        String tankCapacity = userData.getString("tank_capacity", "");
        Intent intent = null;

        if(fuelDataGiven){

            //TODO: get fuel level + distance

            //TODO: String gallons = Calculations.getGallons(initfuel, finalfuel, tankcapacity)
            //TODO: Calculate miles = distance - initdistance
           //TODO: create Data currentData = new Data(gallons, miles, timeString)

            String gallons = "10", miles = "300";

            Data currentData = new Data(gallons, miles, timeString);

            intent = new Intent(this, MetricActivity.class);
            intent.putExtra("DATAPOINT", currentData);
            startActivity(intent);
        }
        else{
            intent = new Intent(this, FuelSurveyActivity.class);
            startActivity(intent);
        }

    }



    private void startDataTransfer(){

        //TODO: Check protocol

        Long time = System.currentTimeMillis()/1000;
        String timeString = time.toString();
        //TODO:get initfuel + initdistance

        //TODO: if fuel data given -> fuelDataGiven = true, else false
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Console.log(classID+" button clicked");
        switch (v.getId()){
            case R.id.connectButton:
                Console.log(classID+" connect");
                Intent intent = new Intent(MainActivity.this, Devices.class);
                startActivityForResult(intent, REQUEST_CONNECT_DEVICE_SECURE);
                break;
            case R.id.startButton:
                Console.log(classID+" start");
                startDataTransfer();
                break;
            case R.id.stopButton:
                Console.log(classID+" stop");
                collectData();
                break;
        }
    }
}
