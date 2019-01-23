package com.example.bluetooth_project;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;

    private Button buttonTurnOn;
    private ListView listView;
    private Button buttonDiscovery;

    private ArrayAdapter<String> arrayAdapter;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // bluetooth doesn't exist I guess
        if(bluetoothAdapter == null) {
            try {
                throw new Exception("bluetooth not found");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (!bluetoothAdapter.isEnabled())
            bluetoothAdapter.enable();
        leScanner  = bluetoothAdapter.getBluetoothLeScanner();*/


        buttonTurnOn = findViewById(R.id.buttonTurnOn);
        buttonDiscovery = findViewById(R.id.buttonDiscovery);
        listView = findViewById(R.id.list);

        buttonTurnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonTurnOnAction();
            }
        });
        buttonDiscovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 buttonDiscoveryAction();
            }
        });

        arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, new ArrayList<String>());

        listView.setAdapter(arrayAdapter);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

    //    addElementToList("kek");
    }

    private void buttonDiscoveryAction() {
        //Toast.makeText(getApplicationContext(), "77", Toast.LENGTH_LONG).show();
        bluetoothAdapter.startDiscovery();
        //Toast.makeText(getApplicationContext(), "79", Toast.LENGTH_LONG).show();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        //    Toast.makeText(getApplicationContext(), "85", Toast.LENGTH_LONG).show();
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
    //            Toast.makeText(getApplicationContext(), device.getAddress(), Toast.LENGTH_LONG).show();
                addElementToList(device.getName() + " " + device.getAddress());
            }
//            Toast.makeText(getApplicationContext(), "91", Toast.LENGTH_LONG).show();
        }
    };

    private void addElementToList(String element) {
        arrayAdapter.add(element);
    }

    private void buttonTurnOnAction() {

        // checking if bluetooth is enabled
        if(!bluetoothAdapter.isEnabled()) {
            askToEnableBluetooth(bluetoothAdapter);
        }

        // while it's turning on we should do nothing
        synchronized (this) {
            while(bluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_ON);
        }
    }

    private void askToEnableBluetooth(BluetoothAdapter bluetoothAdapter) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent,  REQUEST_ENABLE_BT);
        if(!bluetoothAdapter.isEnabled()) {
            // TODO: user should enable it
            Log.e("keklelkek", "Bluetooth wasn't enabled");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }
}
