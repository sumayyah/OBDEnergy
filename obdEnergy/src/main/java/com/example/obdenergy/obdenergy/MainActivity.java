package com.example.obdenergy.obdenergy;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
//import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
    private String ConnectedDeviceName = null;
    // String buffer for outgoing messages
    private static StringBuffer WriteStringBuffer;
    // Local Bluetooth adapter
    static BluetoothAdapter BluetoothAdapter = null;
    // Member object for the chat services
    private static BluetoothChatService ChatService = null;

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

        BluetoothAdapter =BluetoothAdapter.getDefaultAdapter();

        /*Check if device supports Bluetooth*/
        if(BluetoothAdapter==null) Console.log(classID+" Bluetooth is not supported in device."); //TODO: Show alert
        else Console.log(classID+" Bluetooth is supported in device.");


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
    protected void onStart() {
        super.onStart();

        Console.log(classID+" onStart");

         /*Check if Bluetooth is enabled. If not, present that option to the user*/
        if (!BluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Console.log(classID+" Bluetooth is not enabled, request sent");
        }else {
            Console.log(classID+" Bluetooth is enabled");
            if(ChatService == null) setupChat();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        Console.log(classID+" Activity result");

        //TODO: add connectStatus textview

        switch (requestCode){
            case REQUEST_CONNECT_DEVICE_SECURE:

                if (resultCode == Activity.RESULT_OK) {
//                    connectStatus.setText("Connecting...");
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:

                if (resultCode == Activity.RESULT_OK) {
//                    connectStatus.setText("Connecting...");
                    connectDevice(data, false);
                }
                break;
            case REQUEST_CREATE_PROFILE:
                if(resultCode == Activity.RESULT_OK) createProfile();
                break;
            case REQUEST_ENABLE_BT:

                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Console.log(classID+" BT not enabled");
                    finish();
                }
        }
    }

    private void setupChat(){
        Console.log(classID+" Setting up chat");

        ChatService = new BluetoothChatService(this, BTHandler);

        /*Initialize outgoing string buffer with null string*/
        WriteStringBuffer = new StringBuffer("");

        /*Send initial messages*/
        sendMessage(""+"\r");
        sendMessage("ATE0");

    }

    private final Handler BTHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_STATE_CHANGE:

                    switch (msg.arg1){

                        case BluetoothChatService.STATE_CONNECTED:
                            Console.log(classID + " Connected, calling onConnect");
//                            connectStatus.setText("Connected to " + mConnectedDeviceName);
//                            onConnect(); //TODO: test and call only if setupChat doesn't work at this
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
//                            connectStatus.setText("Connecting...");
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                    }
                    break;
                case MESSAGE_WRITE:
                    Console.log(classID+" Write command given");
                    break;
                case MESSAGE_READ:
//                    readMessage(msg); //TODO: add read message
                    break;
                case MESSAGE_DEVICE_NAME:

//                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    break;
                case MESSAGE_TOAST:
                    Console.log(TOAST);
                    break;
            }
        }
    };

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

    private void sendMessage(String message){

        /*Make sure we're connected*/
        if(ChatService.getState() != BluetoothChatService.STATE_CONNECTED){
            Console.log(classID+" Not connected");
            return;
        }

        /*If there is actually a message*/
        if(message.length() > 0){

            byte[] toSend = message.getBytes();
            ChatService.write(toSend);
            Console.log(classID+" written "+message);
            //TODO: Logwriter

            /*Reset buffer*/
            WriteStringBuffer.setLength(0);
        }

    }


    private void startDataTransfer(){

        //TODO: Check protocol

        Long time = System.currentTimeMillis()/1000;
        String timeString = time.toString();
        //TODO:get initfuel + initdistance

        //TODO: if fuel data given -> fuelDataGiven = true, else false
    }

    private void connectDevice(Intent data, boolean secure){

        //TODO: different things for secure and insecure connections. Also what is extra info?

        // Get the device MAC address and info
        String address = data.getExtras().getString(Devices.EXTRA_DEVICE_ADDRESS);
        String info = data.getExtras().getString(Devices.EXTRA_DEVICE_INFO);

//        connectStatus.setText("Connected to: "+info+" "+address)

        // Get the BluetoothDevice object
        BluetoothDevice device = BluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        ChatService.connect(device, secure);
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
