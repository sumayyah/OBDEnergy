package com.example.obdenergy.obdenergy.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.obdenergy.obdenergy.Data.Path;
import com.example.obdenergy.obdenergy.MainActivity;
import com.example.obdenergy.obdenergy.R;
import com.example.obdenergy.obdenergy.Utilities.BluetoothChatService;
import com.example.obdenergy.obdenergy.Utilities.Console;
import com.example.obdenergy.obdenergy.Utilities.DataLogger;

import static android.view.View.VISIBLE;

/**
 * Created by sumayyah on 5/31/14.
 *
 * DriveFragment performs the essential communication to and from the OBD scantool.
 *
 * Connect functionalities allow the user to connect to a list of paired Bluetooth devices.
 *
 * Pressing the Start and Stop buttons creates Path objects. Pressing Start starts
 * communication with the scantool, and the fragment maintains asynchronous threads to run
 * continuous requests in the background. Pressing Stop Finishes creation of the path object
 * and alerts MainActivity that data collection is over, and to prompt calculations to begin.
 *
 * A listener interface allows it to pass data to MainActivity upon completion of a drive session.
 */

public class DriveFragment extends Fragment implements View.OnClickListener {

    private MainActivity mainActivity;
    private final String classID = "DriveFragment ";
    private String command;
    private String deviceInfo;

    public static boolean start;
    public static boolean stop;
    private boolean maf = true;
    private boolean speed = false;
    public static boolean startReady = false;

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

    private Button connectButton;
    private Button startButton;
    private Button stopButton;
    private TextView connectStatus;
    private static TextView timer;
    private ProgressBar progressBar;

    private RelativeLayout greenRing;
    private RelativeLayout redRing;
    private RelativeLayout greyRing;

    dataListener listener;

    public int counter;

    private long startTime = 0L;
    private long timeInProgress = 0L;
    private long timeSwapper = 0L;
    private long finalTime = 0L;

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

        Console.log(classID+"on CreateView");

        View view = inflater.inflate(R.layout.drive_fragment, container, false);

        /*Prevent phone from sleeping when collecting data*/
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        connectButton = (Button) (view.findViewById(R.id.connectButton));
        startButton = (Button) (view.findViewById(R.id.startButton));
        stopButton = (Button) (view.findViewById(R.id.stopButton));
        connectButton.setOnClickListener(this);
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);

        connectStatus = (TextView) (view.findViewById(R.id.connectStatus));
        timer = (TextView) (view.findViewById(R.id.timer));
        progressBar = (ProgressBar) (view.findViewById(R.id.progressSpinner));

        greenRing = (RelativeLayout)(view.findViewById(R.id.ringLayoutGreen));
        redRing = (RelativeLayout)(view.findViewById(R.id.ringLayoutRed));
        greyRing = (RelativeLayout)(view.findViewById(R.id.ringLayoutGrey));

        /*Prevents app from going to sleep and disruptin communication*/
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        timer.setVisibility(View.GONE);

        BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        /*Check if device supports Bluetooth*/
        if(BluetoothAdapter==null) {
            Console.showAlert(getActivity(), "Bluetooth not supported in device");
            getActivity().finish();
        }


        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        Console.log(classID+"on Attach");

        super.onAttach(activity);

        this.mainActivity = (MainActivity) activity;

        if (activity instanceof dataListener)
            listener = (dataListener) activity;
        else {
        }
    }

    @Override
    public void onStart() {
        Console.log(classID+"on Start");

        super.onStart();

         /*Check if Bluetooth is enabled. If not, present that option to the user*/
        if (!BluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            if (ChatService == null) setupChat();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        Console.log(classID+"On pause");
    }

    @Override
    public void onResume() {

        if(ChatService.getState() == BluetoothChatService.STATE_CONNECTED ){
            connectStatus.setText("Connected to: "+deviceInfo);
            if(start && !stop && startReady)setUI(2);
            else if(!start && !stop && startReady) setUI(1);
            else if(!start && stop && startReady){setUI(1);}
            else if(!startReady) onConnect();
            else Console.log(classID+"Some wrong state data");
        }

        super.onResume();

    }

    public void confirmData(int data) {}

    /*
    * Connect
    *
    * Functions that handle operations upon connect
    *
    * */

    public void setConnectValidators(String status, boolean on){
        connectStatus.setText(status);
        connectButton.setVisibility(View.GONE);

        if (on) progressBar.setVisibility(VISIBLE);
        else progressBar.setVisibility(View.GONE);

    }

    public void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address and info
        String address = data.getExtras().getString(Devices.EXTRA_DEVICE_ADDRESS);
        String info = data.getExtras().getString(Devices.EXTRA_DEVICE_INFO);
        Console.log(classID+"Recieved connect info from main");
        connectStatus.setText("Connected to: " + info);
        deviceInfo = info;

        // Get the BluetoothDevice object
        BluetoothDevice device = BluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        ChatService.connect(device, secure);
    }

    public void setupChat() {
        ChatService = new BluetoothChatService(this, BTHandler);

        /*Initialize outgoing string buffer with null string*/
        WriteStringBuffer = new StringBuffer("");
        Console.log(classID+"Set up chat");

    }

    private void onConnect(){
        /*Send initial messages*/
        sendMessage("ATE0");
        sendMessage("" + "\r");

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setUI(1);
                startReady = true;

            }
        }, 2500);

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
                            speedHandler.post(new Runnable() {

                                @Override
                                public void run() {
                                    sendMessage(MAF_REQUEST + "\r");;
                                }
                            });

                            maf = false;
                            speed = true;
                        }else if (speed && !maf){

                            speedHandler.post(new Runnable() {

                                @Override
                                public void run() {
                                sendMessage(SPEED_REQUEST + "\r");

                                }
                            });
                            speed = false;
                            maf = true;
                        }else; //Some error occurred

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
                Console.log(classID+"Start pressed");
                if(!startReady) break;

                mainActivity.path.setInitTimestamp(timeString);

                setUI(2);

                startTime = SystemClock.uptimeMillis();
                timeHandler.postDelayed(timerThread, 0);

                start = true;
                stop = false;

                onStartPressed();

                break;
            case R.id.stopButton:
                Console.log(classID+"Stop pressed");
                mainActivity.path.setFinalTimestamp(timeString);

                timeSwapper += timeInProgress;
                timeHandler.removeCallbacks(timerThread);
                speedHandler.removeCallbacks(speedThread);

                /*Reset the timer*/
                timer.setText("00:00:00");
                startTime = 0L;
                timeInProgress = 0L;
                timeSwapper = 0L;
                finalTime = 0L;

                stop = true;
                start = false;

                setUI(1);

                onStopPressed();

                break;
        }
    }

    public interface dataListener {
        public void DriveFragmentDataComm(int PID);
        public void pathData();
    }

    public void sendMessage(String message) {

        /*Make sure we're connected*/
        if ((ChatService.getState() != BluetoothChatService.STATE_CONNECTED)) {
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
        }

    }

    private void onStartPressed() {

        mainActivity.path = new Path();
        mainActivity.path.username = mainActivity.username;
        startInstantReadings();

    }

    private void onStopPressed() {

        timeHandler.removeCallbacks(timerThread);
        speedHandler.removeCallbacks(speedThread);

    }



    private final Handler BTHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){

                case MESSAGE_STATE_CHANGE:

                    switch (msg.arg1){
                        case BluetoothChatService.STATE_CONNECTED:
                            connectStatus.setText("Connected to " + ConnectedDeviceName);
                            progressBar.setVisibility(View.GONE);
                            onConnect();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            connectStatus.setText("Connecting...");
                            progressBar.setVisibility(VISIBLE);
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
                    break;
            }
        }
    };

    public void setUI(int caseNum){
        switch (caseNum){

            /*No state, in waiting*/
            case 0:
                startButton.setTextColor(Color.parseColor("#AAAAAA"));
                greyRing.setVisibility(View.VISIBLE);
                greenRing.setVisibility(View.GONE);
                timer.setVisibility(View.GONE);
                break;
            /*Start is ready to press*/
            case 1:
                startButton.setVisibility(View.VISIBLE);
                startButton.setTextColor(Color.parseColor("#A4C739"));
                stopButton.setVisibility(View.GONE);
                greyRing.setVisibility(View.GONE);
                redRing.setVisibility(View.GONE);
                greenRing.setVisibility(VISIBLE);
                timer.setVisibility(VISIBLE);
                break;

            /*Stop is ready to press*/
            case 2:

                startButton.setVisibility(View.GONE);
                stopButton.setVisibility(VISIBLE);

                greenRing.setVisibility(View.GONE);
                greyRing.setVisibility(View.GONE);
                redRing.setVisibility(VISIBLE);
                timer.setVisibility(VISIBLE);
                break;
            default:
                break;
        }
    }

    private void readMessage(Message msg){

        byte[] readBuffer = (byte[]) msg.obj;
        String bufferString = new String(readBuffer, 0, msg.arg1);
        Console.log(classID+"Command: "+command+" Response is "+bufferString);

        String fourByteNormal="\\s*[0-9A-Fa-f]{2} [0-9A-Fa-f]{2} [0-9A-Fa-f]{2} [0-9A-Fa-f]{2}\\s*\\r*\\n?";
        String fourByteAbnormal="\\s*[0-9A-Fa-f] [0-9A-Fa-f]{2} [0-9A-Fa-f]{2} [0-9A-Fa-f]{2}\\s*\\r*\\n? (\\s*[0-9A-Fa-f]{2} [0-9A-Fa-f]{2} [0-9A-Fa-f]{2} [0-9A-Fa-f]{2}\\s*\\r*\\n?)* \\s*\\r*\\n?";
        String threeByteNormal="\\s*[0-9A-Fa-f]{2} [0-9A-Fa-f]{2} [0-9A-Fa-f]{2}\\s*\\S*\\r?\\n?";
        String threeByteAbnormal="\\s*[0-9A-Fa-f] [0-9A-Fa-f]{2} [0-9A-Fa-f]{2}\\s*\\S*\\r?\\n? (\\s*[0-9A-Fa-f]{2} [0-9A-Fa-f]{2} [0-9A-Fa-f]{2}\\s*\\S*\\r?\\n?)* ";


        /*If we get 4 bytes of data returned*/
        if(bufferString!="" && (bufferString.matches(fourByteNormal) || bufferString.matches(fourByteAbnormal))){

            bufferString.trim();
            String[] bytes = bufferString.split(" ");

            if((bytes[0]!=null) && (bytes[1]!=null) && (bytes[2]!=null) && (bytes[3]!=null)){
                int PID = Integer.parseInt(bytes[1], 16);
                String firstPart = bytes[2];
                String secondPart = bytes[3];
                String finalString = firstPart+secondPart;

                switch(PID){
                    case 16: //MAF - airflow rate
                        Console.log(classID + "MAF Fuel data received " + finalString);
                        if(start && !stop){
                            mainActivity.path.addToMAFarray(firstPart, secondPart);
                        }else if(!start && stop){
                            listener.DriveFragmentDataComm(PID);
                        }else {}
                        break;
                    default:
                        break;
                }
            } else;
        }
        /*If we get 3 bytes of data returned*/

        else if (!bufferString.equals("")&&(bufferString.matches(threeByteNormal) || bufferString.matches(threeByteAbnormal))){

            bufferString.trim();
            String[] bytes = bufferString.split("\\s+"); //Removes any number of spaces


            if(((bytes[0]!=null) && (bytes[1]!=null) && (bytes[2]!=null))){
                int PID = Integer.parseInt(bytes[1], 16);
                String secondPart = bytes[2];

                switch (PID){
                    case 47: //Fuel data

                        Console.log(classID+"Engine Fuel data recieved "+secondPart);
                        if(start && !stop){
                            mainActivity.path.setInitFuel(secondPart);

                        }else if(!start && stop){
                            mainActivity.path.setFinalFuel(secondPart);
                            Console.log(classID + " set as final fuel, calling Main Activity");
                            timeHandler.removeCallbacks(timerThread);
                            speedHandler.removeCallbacks(speedThread);
                        }else Console.log("Some other bool");
                        break;

                    case 13: //Speed data (KM/H)
                        Console.log(classID+" Speed data recieved"+secondPart);
                        mainActivity.path.addToSpeedArray(secondPart);

                        /*Activate with MAF settings*/
                        if(!start && stop) listener.DriveFragmentDataComm(16);

                        /*Time array and speed array should be the same size*/
                        Long time = System.currentTimeMillis() / 1000;
                        String timeString = time.toString();
                        mainActivity.path.addToTimeArray(timeString);
                        break;
                }
            }
        }
        else {

            if(stop) {

                Console.log(classID+" No data calculated at all");
                listener.DriveFragmentDataComm(16);
            }

        }

    }
}
