package com.example.bluetooth_project.ALL;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.widget.Toast;

import com.example.bluetooth_project.MainActivity;

public class PublicStaticObjects {

    // functions:

    public static void showToast(String message) {
        Toast.makeText(mainActivity, message, Toast.LENGTH_LONG).show();
    }

    public static void setTitle(String s) {
        mainActivity.setTitle(s);
    }

    // variables:
    private static BluetoothSocket socket;
    private static MainActivity mainActivity;
    private static BluetoothAdapter bluetoothAdapter;
    private static String MY_UUID = "f890841e-0131-4d6e-b1e6-656bd3a1d25d";
    private static Boolean isConnected = false;

    // getters and setters:
    public static String getMyUuid() {
        return MY_UUID;
    }

    public static BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public static void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        PublicStaticObjects.bluetoothAdapter = bluetoothAdapter;
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public static void setMainActivity(MainActivity mainActivity) {
        PublicStaticObjects.mainActivity = mainActivity;
    }

    public static Boolean getIsConnected() {
        return isConnected;
    }

    public static void setIsConnected(Boolean isConnected) {
        PublicStaticObjects.isConnected = isConnected;
    }

    public static BluetoothSocket getSocket() {
        return socket;
    }

    public static void setSocket(BluetoothSocket socket) {
        PublicStaticObjects.socket = socket;
    }

    private PublicStaticObjects() {}
}
