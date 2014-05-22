package com.example.obdenergy.obdenergy.Activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.obdenergy.obdenergy.R;
import com.example.obdenergy.obdenergy.Utilities.Console;

import java.util.Set;

/**
 * Created by sumayyah on 5/12/14.
 */
public class Devices extends Activity{

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    public static String EXTRA_DEVICE_INFO = "device_info";

    // Member fields
    private BluetoothAdapter BtAdapter;
    private ArrayAdapter<String> PairedDevicesArrayAdapter;
    private ArrayAdapter<String> NewDevicesArrayAdapter;

    private ProgressBar progressBar;

    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bt_device_menu);

        // If user presses back button
        setResult(Activity.RESULT_CANCELED);

        status = (TextView)(findViewById(R.id.status));
        progressBar = (ProgressBar)(findViewById(R.id.progressSpinner));

        // Initialize the button to perform device discovery
        Button scanButton = (Button) findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });

        //Arrays for paired and new devices
        PairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.bt_menu_cell);
        NewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.bt_menu_cell);

        // Find and set up the ListView for paired devices
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices_list);
        pairedListView.setAdapter(PairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(DeviceClickListener);

        // Find and set up the ListView for newly discovered devices
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices_list);
        newDevicesListView.setAdapter(NewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(DeviceClickListener);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(Receiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(Receiver, filter);

        // Get the local Bluetooth adapter
        BtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = BtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            findViewById(R.id.paired_devices_title).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                PairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = "No devices found";
            progressBar.setVisibility(View.GONE);
            PairedDevicesArrayAdapter.add(noDevices);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Close discovery
        if (BtAdapter != null) {
            BtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(Receiver);
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {

        status.setText("Scanning");
        progressBar.setVisibility(View.VISIBLE);

        // Turn on sub-title for new devices
        findViewById(R.id.new_devices_title).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (BtAdapter.isDiscovering()) {
            BtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        BtAdapter.startDiscovery();
    }

    // The on-click listener for all devices in the ListViews
    private AdapterView.OnItemClickListener DeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            BtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            //String address = "00:0D:18:A0:4E:35"; //FORCE OBD MAC Address
            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            intent.putExtra(EXTRA_DEVICE_INFO, info);
            Console.log("Put data in Devices bundle: " + info + " " + address);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    NewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                status.setText("Finished discovery");
                if (NewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = "No devices found";//getResources().getText(R.string.none_found).toString();
                    status.setText("No devices found");
                    progressBar.setVisibility(View.GONE);
                    NewDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };

}
