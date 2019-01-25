package com.example.bluetooth_project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int REQUEST_ENABLE_BT = 1;
    private final static int ACTION_REQUEST_MULTIPLE_PERMISSION = 1;
    private final static int MAX_TIME_DISCOVER_SECONDS = 300;
    private static final int DISCOVERY_REQUEST = 228;

    private Button buttonTurnOn;
    private Button buttonDiscovery;
    private Button buttonDiscoverable;
    private ListView listView;

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

        // this hack is needed on Samsung J7
        checkPermission();

        // xml elements
        buttonTurnOn = findViewById(R.id.buttonTurnOn);
        buttonDiscovery = findViewById(R.id.buttonDiscovery);
        buttonDiscoverable = findViewById(R.id.buttonDiscoverable);
        listView = findViewById(R.id.list);


        buttonTurnOn.setOnClickListener(this);
        buttonDiscovery.setOnClickListener(this);
        buttonDiscoverable.setOnClickListener(this);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());

        listView.setAdapter(arrayAdapter);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showToast("itemSelect: position = " + i + ", id = " + l);
            }
        });

    }

    private Timer timer = new Timer();
    private TimerTask timerTaskDecreaseCounter = new TimerTask() {
        int secondsLeft = MAX_TIME_DISCOVER_SECONDS;
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            runOnUiThread(() ->
                    buttonDiscoverable.setText(getResources().getString(R.string.discoverable) +
                            " (" + --secondsLeft + ")"));
            if(secondsLeft == 0) {
                runOnUiThread(() -> {
                    buttonDiscoverable.setText(getResources().getString(R.string.discoverable));
                    timer.cancel();
                    timer.purge();
                });
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 228) {
            if(resultCode == 300) {
                timer.scheduleAtFixedRate(timerTaskDecreaseCounter, 0, 1000);
            } else {
                showToast("Разрешение не получено");
            }
        }
    }

    private void buttonDiscoverableAction() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, MAX_TIME_DISCOVER_SECONDS);
        startActivityForResult(discoverableIntent, DISCOVERY_REQUEST);
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
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // TODO
            }
        }
    };

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void addElementToList(String element) {
        if(arrayAdapter.getPosition(element) == -1) {
            arrayAdapter.add(element);
        }
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
            case R.id.buttonDiscoverable:
                buttonDiscoverableAction();
                break;
        }
    }

}
