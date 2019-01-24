package com.example.bluetooth_project;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int REQUEST_ENABLE_BT = 1;
    private final static int ACTION_REQUEST_MULTIPLE_PERMISSION = 1;

    private Button buttonTurnOn;
    private ListView listView;
    private Button buttonDiscovery;

    private ArrayAdapter<String> arrayAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();

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

        // this hack is needed in Samsung J7
        checkPermission();

        // xml elements
        buttonTurnOn = findViewById(R.id.buttonTurnOn);
        buttonDiscovery = findViewById(R.id.buttonDiscovery);
        listView = findViewById(R.id.list);

        buttonTurnOn.setOnClickListener(this);
        buttonDiscovery.setOnClickListener(this);

        arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, new ArrayList<String>());

        listView.setAdapter(arrayAdapter);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }

    private void buttonDiscoveryAction() {
        if(bluetoothAdapter.isEnabled()) {
            arrayAdapter.clear();
            devices.clear();
            // if is already discovering discover again
            if(bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
            bluetoothAdapter.startDiscovery();
        }
        else {
            showToast("Вы должны включить Bluetooth");
        }
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

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                addElementToList(device.getName() + " " + device.getAddress());
                devices.add(device);
            }
        }
    };

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void addElementToList(String element) {
        arrayAdapter.add(element);
    }

    private void askToEnableBluetooth(BluetoothAdapter bluetoothAdapter) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, REQUEST_ENABLE_BT);
        if(!bluetoothAdapter.isEnabled()) {
            showToast("Вы должны включить Bluetooth");
        }
        else {
            showToast("Bluetooth уже включен");
        }
    }

    private void checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int pCheck;
            pCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            pCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            pCheck += this.checkSelfPermission("Manifest.permission.BLUETOOTH_ADMIN");
            pCheck += this.checkSelfPermission("Manifest.permission.BLUETOOTH");
            if (pCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH}, ACTION_REQUEST_MULTIPLE_PERMISSION);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonDiscovery:
                buttonDiscoveryAction();
                break;
            case R.id.buttonTurnOn:
                buttonTurnOnAction();
                break;
        }
    }
}
