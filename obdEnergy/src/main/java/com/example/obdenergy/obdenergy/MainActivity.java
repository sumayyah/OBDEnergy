package com.example.obdenergy.obdenergy;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends ActionBarActivity implements View.OnClickListener{

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

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";


    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    private final String PREFS_NAME = "MyCarData";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*If this is the first time running the app, get user data*/
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Boolean hasRun = settings.getBoolean("my_first_time", false);
        Console.log("Run "+hasRun);

        if(!hasRun){
            Console.log(classID + "FIRST TIME!!");
            settings.edit().putBoolean("my_first_time", true).commit();
            Intent intent = new Intent(this, InitActivity.class);
            startActivity(intent);
        }
        else {Console.log(classID+" Not the first time! "+hasRun);}

        //TODO: upload sharedPrefs data to Profile class
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

    }
}
