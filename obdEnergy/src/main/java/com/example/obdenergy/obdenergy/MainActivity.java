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
import com.example.obdenergy.obdenergy.Data.DisplayData;
import com.example.obdenergy.obdenergy.Data.OBDData;
import com.example.obdenergy.obdenergy.Data.Profile;
import com.example.obdenergy.obdenergy.Utilities.BluetoothChatService;

import com.example.obdenergy.obdenergy.Utilities.Calculations;
import com.example.obdenergy.obdenergy.Utilities.Console;

public class MainActivity extends Activity implements View.OnClickListener{

    private final String classID = "MainActivity";

    // Name of the connected device
    private String ConnectedDeviceName = null;
    // String buffer for outgoing messages
    private static StringBuffer WriteStringBuffer;
    private static StringBuffer WriteStartStringBuffer;
    private static StringBuffer WriteStopStringBuffer;
    // Local Bluetooth adapter
    static BluetoothAdapter BluetoothAdapter = null;
    // Member object for the chat services
    private static BluetoothChatService StartChatService = null;
    private static BluetoothChatService StopChatService = null;
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


    private final String FUEL_REQUEST = "012F";
    private final String USER_DATA_FILE = "MyCarData";

    Profile userProfile;
    SharedPreferences userData;

    private TextView data;
    private Button startButton;
    private Button stopButton;
    private Button connectButton;

    private Boolean fuelDataGiven = true;
    private Boolean start = false;
    private Boolean stop = false;
    private String command = "";

    private OBDData obdData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Console.log(classID+" on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = (TextView)(findViewById(R.id.displayData));
        startButton = (Button)(findViewById(R.id.startButton));
        stopButton = (Button)(findViewById(R.id.stopButton));
        connectButton = (Button)(findViewById(R.id.connectButton));
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        connectButton.setOnClickListener(this);

        BluetoothAdapter =BluetoothAdapter.getDefaultAdapter();
        obdData = new OBDData();

        /*Check if device supports Bluetooth*/
        if(BluetoothAdapter==null) Console.log(classID+" Bluetooth is not supported in device."); //TODO: Show alert
        else Console.log(classID+" Bluetooth is supported in device.");


        /*If this is the first time running the app, get user data*/
        userData = getSharedPreferences(USER_DATA_FILE, 0);
        Boolean hasRun = userData.getBoolean("my_first_time", false);

        if(!hasRun){
            userData.edit().putBoolean("my_first_time", false).commit(); //TODO: check if this should be true or false
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

        /*Initialize outgoing string buffer with null string*/ //TODO: check if we need all three of these
        WriteStringBuffer = new StringBuffer("");


        /*Send initial messages*/
        sendMessage(""+"\r"); //TODO: check if these shold be sent before start button press
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
                    if(start == true && stop == false) {
//                        readStartMessage(msg);
                    }else if(start == false && stop == true){
//                        readStopMessage(msg);
                    }else {
                        Console.log(classID+" Wrong bools, some button error");
                    }
                    readMessage(msg);
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

    private void readMessage(Message msg){

        Console.log("Reading message! Command is "+command);
        byte[] readBuffer = (byte[]) msg.obj;
        String bufferString = new String(readBuffer, 0, msg.arg1);

        /*If we get 4 bytes of data returned*/
        if(bufferString!="" && bufferString.matches("\\s*[0-9A-Fa-f]{2} [0-9A-Fa-f]{2} [0-9A-Fa-f]{2} [0-9A-Fa-f]{2}\\s*\r?\n?")){
            Console.log("Buffer String matches 8 digit pattern: "+bufferString);

            bufferString.trim();
            String[] bytes = bufferString.split(" ");

            if((bytes[0]!=null) && (bytes[1]!=null) && (bytes[2]!=null) && (bytes[3]!=null)){
                String firstPart = bytes[2];
                String secondPart = bytes[3];
                String finalString = firstPart+secondPart;
                Console.log("No null pieces! They're "+firstPart+" and "+secondPart+" makes "+finalString);
//                hexToInt(finalString);

            } else Console.log("NUll pieces in first regex check :(");
        }
        /*If we get 3 bytes of data returned*/
        else if (bufferString!="" && bufferString.matches("\\s*[0-9A-Fa-f]{2} [0-9A-Fa-f]{2} [0-9A-Fa-f]{2}\\s*\r\n?")){
            Console.log("Buffer String matches 4 digit pattern: "+bufferString);

            bufferString.trim();
            String[] bytes = bufferString.split(" ");

            if(((bytes[0]!=null) && (bytes[1]!=null) && (bytes[2]!=null)) && !bytes[2].equals("12") && !bytes[2].equals("80") && !bufferString.equals("NO DATA")){
                String secondPart = bytes[2];
                Console.log("No null pieces! It's "+secondPart+" fuel is "+ Calculations.getFuel(secondPart));

//                hexToInt(secondPart);

//                response.append("\n"+"Command: "+command+"\n"+" Response: "+bufferString);

            } else {
                Console.log("Buffer string is mistaken, its "+bufferString);
                if(command.equals(FUEL_REQUEST)){
                    Console.log("FUel returns erroneous data, set to false");
                    fuelDataGiven = false;
                }
            }
        }
        else {
            Console.log("Buffer string doesn't match regex, it's "+bufferString);
//            response.append("\n"+"Command: "+command+"\n"+" Response: "+bufferString);
        }

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

        Console.log(classID+" collecting DisplayData");

        Long time = System.currentTimeMillis()/1000;
        String timeString = time.toString();

        String tankCapacity = userData.getString("tank_capacity", "");
        Intent intent = null;

        if(fuelDataGiven){

            //TODO: get fuel level + distance

            //TODO: String gallons = Calculations.getGallons(initfuel, finalfuel, tankcapacity)
            //TODO: Calculate miles = distance - initdistance
           //TODO: create DisplayData currentDisplayData = new DisplayData(gallons, miles, timeString)

            String gallons = "10", miles = "300";

            DisplayData currentDisplayData = new DisplayData(gallons, miles, timeString);

            intent = new Intent(this, MetricActivity.class);
            intent.putExtra("DATAPOINT", currentDisplayData);
            startActivity(intent);
        }
        else{
            intent = new Intent(this, FuelSurveyActivity.class);
            startActivity(intent);
        }

    }

    private void sendMessage(String message){

        /*Make sure we're connected*/
        if((ChatService.getState() != BluetoothChatService.STATE_CONNECTED)){
            Console.log(classID+" Not connected");
            return;
        }

        /*If there is actually a message*/
        if(message.length() > 0){

            byte[] toSend = message.getBytes();
            ChatService.write(toSend);
            WriteStringBuffer.setLength(0);
            command = message;
            Console.log(classID+" written "+message+" as command "+command);
            //TODO: Logwriter

        }

    }


    private void startDataTransfer(){

        //TODO: Check protocol


        //TODO:get initfuel + initdistance with sendmessage
        /*Send request for initial fuel data*/
        sendMessage(FUEL_REQUEST+"\r");
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
        Long time = System.currentTimeMillis()/1000;
        String timeString = time.toString();

        switch (v.getId()){
            case R.id.connectButton:
                Console.log(classID+" connect");
                Intent intent = new Intent(MainActivity.this, Devices.class);
                startActivityForResult(intent, REQUEST_CONNECT_DEVICE_SECURE);
                break;
            case R.id.startButton:
                Console.log(classID+" start");
                obdData.setInitTimestamp(timeString);
                start = true;
                startDataTransfer();
                break;
            case R.id.stopButton:
                Console.log(classID+" stop");
                start = false;
                stop = true;
                obdData.setFinalTimestamp(timeString);
                collectData();
                break;
        }
    }

    //
//    private void setupStartChat(){
//
//        Console.log(classID+" Set up startChat");
//
//        StartChatService = new BluetoothChatService(this, BTStartHandler);
//
//        /*Initialize outgoing string buffer with null string*/
//        WriteStartStringBuffer = new StringBuffer("");
//
//        /*Send initial messages*/
//        sendMessage(""+"\r");
//        sendMessage("ATE0");
//
//    }
//    private void setupStopChat(){
//
//        Console.log(classID+" Set up stopChat");
//
//        StopChatService = new BluetoothChatService(this, BTStopHandler);
//
//        /*Initialize outgoing string buffer with null string*/
//        WriteStartStringBuffer = new StringBuffer("");
//    }
}
