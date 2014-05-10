package com.example.obdenergy.obdenergy;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.obdenergy.obdenergy.Activities.FuelSurveyActivity;
import com.example.obdenergy.obdenergy.Activities.InitActivity;
import com.example.obdenergy.obdenergy.Utilities.BluetoothChatService;
import com.example.obdenergy.obdenergy.Data;
import com.example.obdenergy.obdenergy.Profile;
import com.example.obdenergy.obdenergy.R;
import com.example.obdenergy.obdenergy.Utilities.Console;

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
    private static final int REQUEST_CODE = 1;


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

    private final String PREFS_NAME = "MyCarData";

    Profile userProfile;
    SharedPreferences userData;

    private TextView data;
    private Button startButton;
    private Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = (TextView)(findViewById(R.id.data));
        startButton = (Button)(findViewById(R.id.startButton));
        stopButton = (Button)(findViewById(R.id.stopButton));
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);

        /*If this is the first time running the app, get user data*/
        userData = getSharedPreferences(PREFS_NAME, 0);
        Boolean hasRun = userData.getBoolean("my_first_time", false);

        if(!hasRun){
            Console.log(classID + "FIRST TIME!!");
            userData.edit().putBoolean("my_first_time", true).commit();
            Intent intent = new Intent(this, InitActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        }
        else {
            Console.log(classID+" Not the first time! "+hasRun);
            createProfile();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            createProfile();
        }
    }

    private void createProfile(){
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
        Long time = System.currentTimeMillis()/1000;
        String timeString = time.toString();

        String gallons = "10";
        String miles = "300";

        Data currentData = new Data(gallons, miles, timeString);

        //TODO: check if fuel PID works, or not - then send to screens. For now open asking act anyway

        Intent intent = new Intent(this, FuelSurveyActivity.class);
        intent.putExtra("DATAPOINT", currentData);

        Console.log("Collected data "+currentData.getGallons()+" "+currentData.getGallons());
        startActivity(intent);

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
        switch (v.getId()){
            case R.id.startButton:
                break;
            case R.id.stopButton:
                Console.log("Stop button clicked! ");
                collectData();
                break;
        }
    }
}
