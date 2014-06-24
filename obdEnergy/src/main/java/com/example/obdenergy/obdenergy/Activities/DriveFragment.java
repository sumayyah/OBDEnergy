package com.example.obdenergy.obdenergy.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.obdenergy.obdenergy.Data.Path;
import com.example.obdenergy.obdenergy.MainActivity;
import com.example.obdenergy.obdenergy.R;
import com.example.obdenergy.obdenergy.Utilities.BluetoothChatService;
import com.example.obdenergy.obdenergy.Utilities.Console;

/**
 * Created by sumayyah on 5/31/14.
 */

public class DriveFragment extends Fragment implements View.OnClickListener {

    private MainActivity mainActivity;
    private final String classID = "DriveFragment ";
    private String command;

    private boolean start;
    private boolean stop;
    private boolean maf = true;
    private boolean speed = false;
    private boolean fuelDataGiven = true;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    private static final int REQUEST_CREATE_PROFILE = 4;

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
    private final String AUTO_PROTOCOL = "ATSP0"; //Lets logger find closest protocol automatically

    private final String USER_DATA_FILE = "MyCarData";


    private Button connectButton;
    private Button startButton;
    private Button stopButton;
    private TextView connectStatus;
    private TextView timer;
    private ProgressBar progressBar;

    dataListener listener;

    public int counter;

    private long startTime = 0L;
    private long timeInProgress = 0L;
    private long timeSwapper = 0L;
    private long finalTime = 0L;

    private Thread fuelThread;
    private Thread speedThread;
    private final Handler timeHandler = new Handler();
    private final Handler speedHandler = new Handler();

    // Name of the connected device
    private String ConnectedDeviceName = null;
    // String buffer for outgoing messages
    private static StringBuffer WriteStringBuffer;
    // Local Bluetooth adapter
    static BluetoothAdapter BluetoothAdapter = null;
    // Member object for the chat services
    private static BluetoothChatService ChatService = null;
    public static final String DEVICE_NAME = "device_name";

    /*
    * Lifecycle
    *
    * Operations that establish variables and initiate functions as the fragment is created by the activity
    * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.drive_fragment, container, false);
        connectButton = (Button) (view.findViewById(R.id.connectButton));
        startButton = (Button) (view.findViewById(R.id.startButton));
        stopButton = (Button) (view.findViewById(R.id.stopButton));
        connectButton.setOnClickListener(this);
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);


        connectStatus = (TextView) (view.findViewById(R.id.connectStatus));
        timer = (TextView) (view.findViewById(R.id.timer));
        progressBar = (ProgressBar) (view.findViewById(R.id.progressSpinner));

        BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        /*Check if device supports Bluetooth*/
        if(BluetoothAdapter==null) {
            Console.showAlert(getActivity(), "Bluetooth not supported in device");
            Console.log(classID + " Bluetooth is not supported in device.");
            getActivity().finish();
        }


        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.mainActivity = (MainActivity) activity;

        if (activity instanceof dataListener)
            listener = (dataListener) activity;
        else {
        }
    }

    @Override
    public void onStart() {
        super.onStart();

         /*Check if Bluetooth is enabled. If not, present that option to the user*/
        if (!BluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//            Console.log(classID+" Bluetooth is not enabled, request sent");
        } else {
            if (ChatService == null) setupChat();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();

        if (counter > 0 && start && !stop) {
            Console.log(classID + "onResume");
            startButton.setVisibility(View.GONE);
            stopButton.setVisibility(View.VISIBLE);
        }

    }

    public void confirmData(int data) {}

    /*
    * Connect
    *
    * Functions that handle operations upon connect
    *
    * */
    public void setConnectStatus(String status) {
        connectStatus.setText(status);
        connectButton.setVisibility(View.GONE);
    }

    public void setProgressBar(boolean on) {
        if (on) progressBar.setVisibility(View.VISIBLE);
        else progressBar.setVisibility(View.GONE);
    }

    public void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address and info
        String address = data.getExtras().getString(Devices.EXTRA_DEVICE_ADDRESS);
        String info = data.getExtras().getString(Devices.EXTRA_DEVICE_INFO);
        connectStatus.setText("Connected to: " + info);

        // Get the BluetoothDevice object
        BluetoothDevice device = BluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        ChatService.connect(device, secure);
    }

    public void setupChat() {
        ChatService = new BluetoothChatService(this, BTHandler);

        /*Initialize outgoing string buffer with null string*/
        WriteStringBuffer = new StringBuffer("");

    }

    private void onConnect(){
        /*Send initial messages*/
        sendMessage("ATE0");
        sendMessage("" + "\r");

    }

    /*
    * Threads
    *
    * These run asynchronously in the background
    * */
    private Runnable timerThread = new Runnable() {
        @Override
        public void run() {

            timeInProgress = SystemClock.uptimeMillis() - startTime;
            finalTime = timeSwapper + timeInProgress;

            counter++;

            int secs = (int) (finalTime / 1000);
            int hours = secs / 3600;
            int mins = hours % 60;
            secs = secs % 3600;

            timer.setText("" + String.format("%02d", hours) + ":"
                    + String.format("%02d", mins) + ":"
                    + String.format("%02d", secs));
            timeHandler.postDelayed(this, 0);

        }
    };

    private void startInstantReadings() {

        /*This staggers speed and MAF readings because OBD can't handle both at once*/
        speedThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!stop) {
                    try {
                        Thread.sleep(2500);
                        if(maf && !speed){
                            Console.log(classID+" MAF's turn");

                            speedHandler.post(new Runnable() {

                                @Override
                                public void run() {
                                    sendMAFRequest();
                                }
                            });

                            maf = false;
                            speed = true;
                        }else if (speed && !maf){
                            Console.log(classID+" Speed's turn");

                            speedHandler.post(new Runnable() {

                                @Override
                                public void run() {
                                sendMessage(SPEED_REQUEST + "\r");
                                    Long time = System.currentTimeMillis() / 1000;
                                    String timeString = time.toString();
                                    mainActivity.path.addToTimeArray(timeString);
                                }
                            });
                            speed = false;
                            maf = true;
                        }else Console.log(classID+" Something went wrong in speedThread");

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        speedThread.start();
    }



    @Override
    public void onClick(View v) {

        Long time = System.currentTimeMillis()/1000; /*Time in seconds*/
        String timeString = time.toString();

        switch (v.getId()) {
            case R.id.connectButton:
                Intent intent = new Intent(getActivity(), Devices.class);
                getActivity().startActivityForResult(intent, REQUEST_CONNECT_DEVICE_SECURE);
                break;
            case R.id.startButton:
                mainActivity.path.setInitTimestamp(timeString);

                startButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.VISIBLE);

                startTime = SystemClock.uptimeMillis();
                timeHandler.postDelayed(timerThread, 0);

                start = true;
                stop = false;

                onStartPressed();

                break;
            case R.id.stopButton:
                mainActivity.path.setFinalTimestamp(timeString);

                timeSwapper += timeInProgress;
                timeHandler.removeCallbacks(timerThread);

                /*Reset the timer*/
                timer.setText("00:00:00");
                startTime = 0L;
                timeInProgress = 0L;
                timeSwapper = 0L;
                finalTime = 0L;
                Console.log(classID + "Counter is " + counter);

                stop = true;
                start = false;

                startButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.GONE);

                onStopPressed();

                break;
        }
    }

    public interface dataListener {
        public void DriveFragmentDataComm(int PID);
    }

    public void sendMessage(String message) {


        /*Make sure we're connected*/
        if ((ChatService.getState() != BluetoothChatService.STATE_CONNECTED)) {
            Console.log(classID + " Not connected");
            String msg = "No Bluetooth device connected. Please connect some Bluetooth device and retry.";
            Console.showAlert(mainActivity, msg);
            return;
        }

        /*If there is actually a message*/
        if (message.length() > 0) {

            byte[] toSend = message.getBytes();
            ChatService.write(toSend);
            WriteStringBuffer.setLength(0);
            command = message;
            Console.log(classID+" Sent message: "+message);
        }

    }

    private void onStartPressed() {

        mainActivity.path = new Path();
        Console.log(classID+"created new path");
//        sendMessage(CHECK_PROTOCOL + "\r");

        /*Send request for initial fuel data*/
//        sendMessage(FUEL_REQUEST + "\r");
        fuelDataGiven = false;

        startInstantReadings();

    }

    private void onStopPressed() {

        timeHandler.removeCallbacks(timerThread);
        speedHandler.removeCallbacks(speedThread);

        if(fuelDataGiven) sendMessage(FUEL_REQUEST+"\r");

        mainActivity.path.printData();

    }

    private void sendMAFRequest() {
//        Console.log(classID+"sending MAF request");
        sendMessage(MAF_REQUEST + "\r");
    }

    private final Handler BTHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){

                case MESSAGE_STATE_CHANGE:

                    switch (msg.arg1){
                        case BluetoothChatService.STATE_CONNECTED:
                            Console.log(classID+"State connected");
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
//                    Console.log(TOAST);
                    break;
            }
        }
    };

    private void readMessage(Message msg){

        byte[] readBuffer = (byte[]) msg.obj;
        String bufferString = new String(readBuffer, 0, msg.arg1);
        Console.log(classID+"Command: "+command+" Message is "+bufferString);

        String fourByteNormal="\\s*[0-9A-Fa-f]{2} [0-9A-Fa-f]{2} [0-9A-Fa-f]{2} [0-9A-Fa-f]{2}\\s*\\r*\\n?";
        String fourByteAbnormal="\\s*[0-9A-Fa-f] [0-9A-Fa-f]{2} [0-9A-Fa-f]{2} [0-9A-Fa-f]{2}\\s*\\r*\\n? (\\s*[0-9A-Fa-f]{2} [0-9A-Fa-f]{2} [0-9A-Fa-f]{2} [0-9A-Fa-f]{2}\\s*\\r*\\n?)* \\s*\\r*\\n?";
        String threeByteNormal="\\s*[0-9A-Fa-f]{2} [0-9A-Fa-f]{2} [0-9A-Fa-f]{2}\\s*\\S*\\r?\\n?";
        String threeByteAbnormal="\\s*[0-9A-Fa-f] [0-9A-Fa-f]{2} [0-9A-Fa-f]{2}\\s*\\S*\\r?\\n? (\\s*[0-9A-Fa-f]{2} [0-9A-Fa-f]{2} [0-9A-Fa-f]{2}\\s*\\S*\\r?\\n?)* ";

//        DataLogger.writeData("Command: "+command+" Message: "+bufferString+"\n");

        /*If data returned is absent or in an unacceptable format*/

        if(command.equals(FUEL_REQUEST) && start){
            if(bufferString.equals("NO DATA") || bufferString.equals("ERROR")){
                fuelDataGiven = false;
                Console.log(classID+"Start tried fuel and failed");
                startInstantReadings();
                sendMAFRequest();
                return;
            }
        }else if(command.equals(CHECK_PROTOCOL)){
//            checkProtocol(bufferString);
        }else if(command.equals(MAF_REQUEST) && start){
            if(bufferString.equals("NO DATA") || bufferString.equals("ERROR")){ //If second line of defense - MAF - doesn't work, just get data from user for now
                Console.log(classID+"Start tried MAF and failed");
                listener.DriveFragmentDataComm(0);
                return;
            }
        }else if(command.equals(CHANGE_PROTOCOL)){ //TODO: this may pop up when the buffer returns the random empties - TEST
            if(!bufferString.equals("OK")){
                String message = "Failed to change protocol to ISO 9141-2. Accuracy of data not guaranteed.";
                Console.showAlert(mainActivity, message);
            }
        }else if(command.equals(INIT_REQUEST)){
            if(bufferString.equals("OK")){
                Console.log(classID+" Init succeeded");
                return;
            }else Console.log("Init failed");
        }

        /*If we get 4 bytes of data returned*/ //TODO: test with different baud rates and or timeouts
        if(bufferString!="" && (bufferString.matches(fourByteNormal) || bufferString.matches(fourByteAbnormal))){

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
                        Console.log(classID + "MAF Fuel data recieved " + finalString);
                        if(start && !stop){
                            mainActivity.path.addToMAFarray(firstPart, secondPart);
                        }else if(!start && stop){
                            listener.DriveFragmentDataComm(PID);
                        }else Console.log("Some other bool");
                        break;
                    default:
                        Console.log(classID+" switch case done got some other PID");
                        break;
                }
            } else Console.log("NUll pieces in first regex check :(");
        }
        /*If we get 3 bytes of data returned*/

        else if (!bufferString.equals("")&&(bufferString.matches(threeByteNormal) || bufferString.matches(threeByteAbnormal))){

            bufferString.trim();
            String[] bytes = bufferString.split(" ");

            if(((bytes[0]!=null) && (bytes[1]!=null) && (bytes[2]!=null))){
                int PID = Integer.parseInt(bytes[1], 16);
                String secondPart = bytes[2];

                switch (PID){
                    case 47: //Fuel data

                        Console.log(classID+" Fuel data recieved "+secondPart);
                        if(start && !stop){
                            mainActivity.path.setInitFuel(secondPart);
                            Console.log(classID+" set as initial fuel");
                        }else if(!start && stop){
                            mainActivity.path.setFinalFuel(secondPart);
                            Console.log(classID+" set as final fuel");
                            listener.DriveFragmentDataComm(PID);
                        }else Console.log("Some other bool");
                        break;

                    case 13: //Speed data (KM/H)
                        Console.log(classID+" Speed data recieved"+secondPart);
                        mainActivity.path.addToSpeedArray(secondPart);
                        break;
                }

            }
        }
        else {
            Console.log("Buffer string doesn't match regex, it's "+bufferString);

            if(stop) {
                Console.log(classID+" No data calculated at all");
//                listener.DriveFragmentDataComm(4);
            }

        }

    }
}
