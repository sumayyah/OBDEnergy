package com.example.obdenergy.obdenergy;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.obdenergy.obdenergy.Activities.Devices;
import com.example.obdenergy.obdenergy.Activities.FuelSurveyActivity;
import com.example.obdenergy.obdenergy.Activities.InitActivity;
import com.example.obdenergy.obdenergy.Activities.MetricActivity;
import com.example.obdenergy.obdenergy.Data.DisplayData;
import com.example.obdenergy.obdenergy.Data.Path;
import com.example.obdenergy.obdenergy.Data.Profile;
import com.example.obdenergy.obdenergy.Data.StorageDate;
import com.example.obdenergy.obdenergy.Utilities.BluetoothChatService;
import com.example.obdenergy.obdenergy.Utilities.Calculations;
import com.example.obdenergy.obdenergy.Utilities.Console;
import com.example.obdenergy.obdenergy.Utilities.Queue;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class MainActivity extends ActionBarActivity implements View.OnClickListener{

    private static final String classID = "MainActivity";

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

    private final String FUEL_REQUEST = "012F"; //Returns % of tank
    private final String MAF_REQUEST = "0110"; // Returns mass airflow in grams/sec
    private final String CHECK_PROTOCOL = "ATDP"; //Returns string of protocol type
    private final String SPEED_REQUEST = "010D"; //Returns km/h
    private final String INIT_REQUEST = "ATE0"; //Returns OK
    private final String CHANGE_PROTOCOL = "ATSP3"; //Changes protocol to ISO 9141-2
    private final String USER_DATA_FILE = "MyCarData";

    SharedPreferences userData;
    Path path = new Path();
    Calendar calendar = new GregorianCalendar();


    private TextView connectStatus;
    private Button startButton;
    private Button stopButton;
    private Button connectButton;

    private Boolean fuelDataGiven = true;
    private Boolean start = false;
    private Boolean stop = false;
    private static String command = "";

    private ProgressBar progressBar;

    private Thread fuelThread;
    private Queue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectStatus = (TextView)(findViewById(R.id.connectStatus));
        progressBar = (ProgressBar)(findViewById(R.id.progressSpinner));
        startButton = (Button)(findViewById(R.id.startButton));
        stopButton = (Button)(findViewById(R.id.stopButton));
        connectButton = (Button)(findViewById(R.id.connectButton));
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        connectButton.setOnClickListener(this);

        queue = Queue.getInstance(this);

        BluetoothAdapter =BluetoothAdapter.getDefaultAdapter();

        /*Check if device supports Bluetooth*/
        if(BluetoothAdapter==null) Console.log(classID+" Bluetooth is not supported in device.");

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

         /*Check if Bluetooth is enabled. If not, present that option to the user*/
        if (!BluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Console.log(classID+" Bluetooth is not enabled, request sent");
        }else {
            if(ChatService == null) setupChat();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){


        switch (requestCode){
            case REQUEST_CONNECT_DEVICE_SECURE:

                if (resultCode == Activity.RESULT_OK) {
                    connectStatus.setText("Connecting...");
                    progressBar.setVisibility(View.VISIBLE);
                    connectButton.setVisibility(View.GONE);
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:

                if (resultCode == Activity.RESULT_OK) {
                    connectStatus.setText("Connecting...");
                    progressBar.setVisibility(View.VISIBLE);
                    connectButton.setVisibility(View.GONE);
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

        ChatService = new BluetoothChatService(this, BTHandler);

        /*Initialize outgoing string buffer with null string*/
        WriteStringBuffer = new StringBuffer("");

    }

    private void onConnect(){
        /*Send initial messages*/
        sendMessage("ATE0");
        sendMessage("" + "\r");

    }

    private final Handler BTHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            Console.log(classID+" Handler recieved "+command+", calling dequeue");

            switch (msg.what){

                case MESSAGE_STATE_CHANGE:

                    switch (msg.arg1){
                        case BluetoothChatService.STATE_CONNECTED:
                            Console.log(classID + " Connected, calling onConnect");
                            connectStatus.setText("Connected to " + ConnectedDeviceName);
                            progressBar.setVisibility(View.GONE);
                            onConnect();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            connectStatus.setText("Connecting...");
                            progressBar.setVisibility(View.VISIBLE);
                            connectButton.setVisibility(View.GONE);
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                    }
                    break;
                case MESSAGE_WRITE:
                    break;
                case MESSAGE_READ:
                    readMessage(msg);
                    break;
                case MESSAGE_DEVICE_NAME:
                    ConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    break;
                case MESSAGE_TOAST:
                    Console.log(TOAST);
                    break;
            }
        }
    };

    private void readMessage(Message msg){

        byte[] readBuffer = (byte[]) msg.obj;
        String bufferString = new String(readBuffer, 0, msg.arg1);
        Console.log(classID+" Message is "+bufferString);

        if(command.equals(FUEL_REQUEST) && start){
            if(bufferString.equals("NO DATA") || bufferString.equals("ERROR")){
                fuelDataGiven = false;
                Console.log("Fuel data gets error return message");
                startInstantFuelReadings();
                sendMAFRequest();
                return;
            }
        }else if(command.equals(CHECK_PROTOCOL)){
            checkProtocol(bufferString);
        }else if(command.equals(MAF_REQUEST) && start){
            if(bufferString.equals("NO DATA") || bufferString.equals("ERROR")){ //If second line of defense - MAF - doesn't work, just get data from user for now
                createMetricActivity(0);
                return;
            }
        }else if(command.equals(CHANGE_PROTOCOL)){ //TODO: this may pop up when the buffer returns the random empties - TEST
            if(!bufferString.equals("OK")){
                String message = "Failed to change protocol to ISO 9141-2. Accuracy of data not guaranteed.";
                Console.showAlert(this, message);
            }
        }else if(command.equals(INIT_REQUEST)){
            if(bufferString.equals("OK")){
                Console.log(classID+" Init succeeded");
                return;
            }else Console.log("Init failed");
        }

        /*If we get 4 bytes of data returned*/
        if(bufferString!="" && bufferString.matches("\\s*[0-9A-Fa-f]{2} [0-9A-Fa-f]{2} [0-9A-Fa-f]{2} [0-9A-Fa-f]{2}\\s*\r?\n?")){

            bufferString.trim();
            String[] bytes = bufferString.split(" ");

            if((bytes[0]!=null) && (bytes[1]!=null) && (bytes[2]!=null) && (bytes[3]!=null)){
                int PID = Integer.parseInt(bytes[1], 16);
                String firstPart = bytes[2];
                String secondPart = bytes[3];
                String finalString = firstPart+secondPart;
                Console.log(classID+" No null pieces! They're "+firstPart+" and "+secondPart+" makes "+finalString+" PID "+PID+" From "+bytes[1]);

                switch(PID){
                    case 16: //MAF - airflow rate
                        Console.log(classID+"MAF Fuel data recieved "+finalString);
                        if(start && !stop){
                            path.setInitMAF(firstPart, secondPart);
                            Console.log(classID+" set as MAF initial fuel");
                        }else if(!start && stop){
                            path.setFinalMAF(firstPart, secondPart);
                            Console.log(classID+" set as MAF final fuel");
                            createMetricActivity(PID);
                        }else Console.log("Some other bool");
                        break;
                    default:
                        Console.log(classID+" switch case done got some other PID");
                        break;
                }
            } else Console.log("NUll pieces in first regex check :(");
        }
        /*If we get 3 bytes of data returned*/
        else if (!bufferString.equals("") && bufferString.matches("\\s*[0-9A-Fa-f]{2} [0-9A-Fa-f]{2} [0-9A-Fa-f]{2}\\s*\r?\n?")){

            bufferString.trim();
            String[] bytes = bufferString.split(" ");

            if(((bytes[0]!=null) && (bytes[1]!=null) && (bytes[2]!=null))){
                int PID = Integer.parseInt(bytes[1], 16);
                String secondPart = bytes[2];

                switch (PID){
                    case 47: //Fuel data

                        //TODO: check for 0 fuel here, or error data
                        Console.log(classID+" Fuel data recieved "+secondPart);
                        if(start && !stop){
                            path.setInitFuel(secondPart);
                            Console.log(classID+" set as initial fuel");
                        }else if(!start && stop){
                            path.setFinalFuel(secondPart);
                            Console.log(classID+" set as final fuel");
                            createMetricActivity(PID);
                        }else Console.log("Some other bool");
                        break;

                    case 13: //Speed data (KM/H)
                        Console.log(classID+" Speed data recieved"+secondPart);
                        path.addToSpeedArray(secondPart);
                        break;
                }

            }
        }
        else {
            Console.log("Buffer string doesn't match regex, it's "+bufferString);
        }

    }


    private void sendMAFRequest() {
        sendMessage(MAF_REQUEST + "\r");
    }

    private void startInstantFuelReadings() {
        final Handler fuelHandler = new Handler();

        fuelThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!stop) {
                    try {
                        Thread.sleep(2000);
                            fuelHandler.post(new Runnable() {

                                @Override
                                public void run() {

                                    sendMessage(SPEED_REQUEST+"\r");
                                }
                            });


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        fuelThread.start();
    }

    private void createMetricActivity(int PID) {
        String tankCapacity = Profile.getCapacity();
        Intent intent = null;
        String gallons = "";

        switch(PID){
            case 0: //No usable fuel data returned, go for the default
                intent = new Intent(this, FuelSurveyActivity.class);
                startActivity(intent);
                return;
            case 47: //Using fuel level data
                gallons = Calculations.getGallons(path.getInitFuel(), path.getFinalFuel(), tankCapacity);
                break;
            case 16: //Using MAF data
                gallons = Calculations.getGallons(path.getInitMAF(), path.getFinalMAF(), path.getInitTime(), path.getfinalTime());
                break;
            default:
                Console.log(classID+" Create metric activity wrong PID");
                break;
        }

        //TODO: get distance calculations from speedArray
        String miles = "10";

        DisplayData currentDisplayData = new DisplayData(gallons, miles, path.getfinalTime());
        intent = new Intent(this, MetricActivity.class);
        intent.putExtra("DATAPOINT", currentDisplayData);
        startActivity(intent);
    }

    private void checkProtocol(String bufferString) {
        //TODO: TEST checkProtocol
        if(!bufferString.equals("ISO 9141-2")){
            Console.log(classID+" Not ISO 9141, its' "+bufferString);
            sendMessage(CHANGE_PROTOCOL + "\r");//queue.add(CHANGE_PROTOCOL + "\r");
        }
    }

    private void createProfile(){
        String make = userData.getString("car_make", "");
        String model = userData.getString("car_model", "");
        String year = userData.getString("car_year", "");
        String tank = userData.getString("tank_capacity", "");
        String city = userData.getString("City", "");
        String highway = userData.getString("Highway", "");

        Profile.setMake(make);
        Profile.setModel(model);
        Profile.setYear(year);
        Profile.setCapacity(tank);
        Profile.setCitympg(city);
        Profile.setHighwaympg(highway);
    }

    private void collectData(){

        if(!fuelDataGiven){
            sendMAFRequest();
        }else sendMessage(FUEL_REQUEST+"\r");
    }

    public  void sendMessage(String message){


        /*Make sure we're connected*/
        if((ChatService.getState() != BluetoothChatService.STATE_CONNECTED)){
            Console.log(classID+" Not connected");
            String msg = "No Bluetooth device connected. Please connect some Bluetooth device and retry.";
            Console.showAlert(this, msg);
            return;
        }

        /*If there is actually a message*/
        if(message.length() > 0){

            byte[] toSend = message.getBytes();
            ChatService.write(toSend);
            WriteStringBuffer.setLength(0);
            command = message;
            //TODO: Logwriter

        }

    }


    private void startDataTransfer(){

        sendMessage(CHECK_PROTOCOL+"\r");

        /*Send request for initial fuel data*/
        sendMessage(FUEL_REQUEST+"\r");

    }

    private void connectDevice(Intent data, boolean secure){

        // Get the device MAC address and info
        String address = data.getExtras().getString(Devices.EXTRA_DEVICE_ADDRESS);
        String info = data.getExtras().getString(Devices.EXTRA_DEVICE_INFO);

        connectStatus.setText("Connected to: "+info+" "+address);

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
    public void onStop(){
        super.onStop();
        SharedPreferences.Editor editor = userData.edit();
        //TODO: store path array here
    }
    @Override
    public void onClick(View v) {
        Long time = System.currentTimeMillis()/1000;
        String timeString = time.toString();

        switch (v.getId()){
            case R.id.connectButton:
                Intent intent = new Intent(MainActivity.this, Devices.class);
                startActivityForResult(intent, REQUEST_CONNECT_DEVICE_SECURE);
                break;
            case R.id.startButton:
                    path.setInitTimestamp(timeString);
                    start = true;
                    startButton.setVisibility(View.GONE);
                    stopButton.setVisibility(View.VISIBLE);
                    startDataTransfer();
                break;
            case R.id.stopButton:
                start = false;
                stop = true;
                path.setStorageTime(calendar);
                StorageDate.printDate();
                path.setFinalTimestamp(timeString);
                collectData();
                Console.log(classID+" speed Array is "+path.printSpeeds());
                break;
        }
    }


}
