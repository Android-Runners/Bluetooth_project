package com.example.bluetooth_project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.bluetooth_project.ALL.InputAndOutput;
import com.example.bluetooth_project.ALL.JsonConverter;
import com.example.bluetooth_project.ALL.PublicStaticObjects;
import com.example.bluetooth_project.connectionStuff.Listener;
import com.example.bluetooth_project.connectionStuff.clientPart.ConnectRunnable;
import com.example.bluetooth_project.connectionStuff.serverPart.AcceptRunnable;
import com.google.gson.Gson;

import java.io.IOException;
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
    private Button buttonSend;
    private Button buttonStop;
    private ListView listView;
    private EditText editText;
    private ScrollView scrollView;

    private ArrayAdapter<String> arrayAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();

    private AcceptRunnable acceptRunnable;
    private ConnectRunnable connectRunnable;
    private Thread threadAccept;
    private Thread threadConnect;
    private IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // filling PublicStaticObjects
        PublicStaticObjects.setBluetoothAdapter(bluetoothAdapter);
        PublicStaticObjects.setMainActivity(this);

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
        // buttonTurnOn = findViewById(R.id.buttonTurnOn);
        buttonDiscovery = findViewById(R.id.buttonDiscovery);
        buttonDiscoverable = findViewById(R.id.buttonDiscoverable);
        buttonSend = findViewById(R.id.buttonSend);
        buttonStop = findViewById(R.id.buttonStop);
        listView = findViewById(R.id.list);
        editText = findViewById(R.id.editText);
        scrollView = findViewById(R.id.scrollView);

        PublicStaticObjects.setEditText(editText);

        buttonDiscovery.setOnClickListener(this);
        buttonDiscoverable.setOnClickListener(this);
        buttonSend.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<>()) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                // making center align
                textView.setGravity(Gravity.CENTER);
                return textView;
            }
        };

        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver, filter);

        buttonStop.setEnabled(false);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> listViewAction(i) );

        editText.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                buttonSendAction();
            }
            return false;
        });

        if(bluetoothAdapter.isEnabled()) {
            runServer();
        }

        new Thread(new Listener()).start();

        buttonTurnOnAction();
    }

    private void runServer() {
        acceptRunnable = new AcceptRunnable();
        threadAccept = new Thread(acceptRunnable);
        threadAccept.start();
    }

    private void stopServer() {
        if(acceptRunnable != null) {
            acceptRunnable.cancel();
            threadAccept.interrupt();
            acceptRunnable = null;
        }
    }

    private Timer timer = new Timer();

    private TimerTask newTimerTaskDecreaseCounter() {
        return new TimerTask() {
            private int secondsLeft = MAX_TIME_DISCOVER_SECONDS;
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                runOnUiThread(() ->
                    buttonDiscoverable.setText(getResources().getString(R.string.discoverable) +
                                                " (" + --secondsLeft + ")"));
                if(secondsLeft <= 0 || !bluetoothAdapter.isEnabled()) {
                    runOnUiThread(() -> {
                        buttonDiscoverable.setText(getResources().getString(R.string.discoverable));
                        timer.cancel();
                        timer.purge();
                    });
                }
            }
        };
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
                buttonDiscovery.setText(R.string.start_discovery);
            }
            else if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                buttonDiscoverable.setText(getResources().getString(R.string.discoverable));
                try {
                    PublicStaticObjects.setIsConnected(false);
                    timer.cancel();
                    timer.purge();
                    stopServer();
                    runServer();
                } catch (Throwable e) {
                    Log.e("in MainActivity", "timer threw exception");
                }
                hideKeyboard();
                editText.setVisibility(View.INVISIBLE);
                editText.setText("");

                buttonStop.setEnabled(false);
                buttonStop.setText(R.string.stop);
                setButtonsEnabled(true);
            }
            else if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                editText.setVisibility(View.VISIBLE);
                editText.setText("");
                arrayAdapter.clear();
                devices.clear();

                buttonStop.setEnabled(true);
                setButtonsEnabled(false);
            }
            else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                    PublicStaticObjects.showToast(getResources().getString(R.string.warn_bt));
                    unregisterReceiver(receiver);
                    stopServer();

                    hideKeyboard();
                    editText.setVisibility(View.INVISIBLE);
                    editText.setText("");

                    buttonStop.setEnabled(false);
                    buttonStop.setText(R.string.stop);
                    setButtonsEnabled(true);
                }
                else if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
                    runServer();
                    registerReceiver(receiver, filter);
                }
            }
        }
    };

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setButtonsEnabled(Boolean b) {
        buttonDiscovery.setEnabled(b);
        buttonDiscoverable.setEnabled(b);
    //    buttonTurnOn.setEnabled(b);
    }

    private void addElementToList(String element) {
        if(arrayAdapter.getPosition(element) == -1) {
            arrayAdapter.add(element);
        }
    }

    private void askToEnableBluetooth() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, REQUEST_ENABLE_BT);
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

    // actions:

    private void buttonDiscoverableAction() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, MAX_TIME_DISCOVER_SECONDS);
        startActivityForResult(discoverableIntent, DISCOVERY_REQUEST);
    }

    private void buttonDiscoveryAction() {
        if(bluetoothAdapter.isEnabled()) {
            buttonDiscovery.setText(R.string.discovering);
            // if is already discovering discover again
            if(bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
            try {
                bluetoothAdapter.startDiscovery();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        else {
            PublicStaticObjects.showToast(getResources().getString(R.string.u_have_to_bt));
        }
    }

    private void buttonTurnOnAction() {
        // checking if bluetooth is enabled
        askToEnableBluetooth();
    }

    private void buttonSendAction() {
        if(InputAndOutput.getOutputStream() != null) {
            String toSend = editText.getText().toString();
            if(editText.getText().toString().length() != 6) {
                PublicStaticObjects.showToast(getResources().getString(R.string.wrong_sr));
            }
            else {
                try {
                    byte[] buffer = new byte[6 + 3];
                    buffer[0] = 2;
                    buffer[1] = 2;
                    buffer[2] = 8;
                    System.arraycopy(toSend.getBytes(), 0, buffer, 3, 6);
                    InputAndOutput.getOutputStream().flush();
                    InputAndOutput.getOutputStream().write(buffer);
                    InputAndOutput.getOutputStream().flush();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void buttonStopAction() {
        if(InputAndOutput.getOutputStream() != null) {
            buttonStop.setText(R.string.stop_process);
            try {
                byte[] buffer = new byte[3];
//                buffer[0] = 0;
                buffer[1] = 1;
                buffer[2] = 2;
                InputAndOutput.getOutputStream().write(buffer);
                InputAndOutput.getOutputStream().flush();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void listViewAction(int i) {
        connectRunnable = new ConnectRunnable(devices.get(i));
        threadConnect = new Thread(connectRunnable);
        threadConnect.start();
    }

    // override methods:

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        acceptRunnable.cancel();
        connectRunnable.cancel();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonDiscovery:
                buttonDiscoveryAction();
                break;
            case R.id.buttonDiscoverable:
                buttonDiscoverableAction();
                break;
            case R.id.buttonSend:
                buttonSendAction();
                break;
            case R.id.buttonStop:
                buttonStopAction();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 228) {
            if (resultCode == 300) {
                timer = new Timer();
                timer.scheduleAtFixedRate(newTimerTaskDecreaseCounter(), 0, 1000);
            } else {
                PublicStaticObjects.showToast(getResources().getString(R.string.deny_permission));
            }
        }
        if (requestCode == REQUEST_ENABLE_BT) {
            // resultCode == -1 - OK
            // resultCode ==  0 - NOT OK
            if (resultCode == -1) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> runServer());
                    } }, 2000);
            }
        }
    }

    // getters and setters:

    public EditText getEditText() {
        return editText;
    }

    public Button getButtonSend() {
        return buttonSend;
    }

    public ArrayList<BluetoothDevice> getDevices() {
        return devices;
    }

    public ArrayAdapter<String> getArrayAdapter() {
        return arrayAdapter;
    }
}
