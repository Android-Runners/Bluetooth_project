package com.example.bluetooth_project.ALL;

import android.bluetooth.BluetoothAdapter;
import android.widget.Toast;

import com.example.bluetooth_project.MainActivity;

public class PublicStaticObjects {

    // functions:

    public static void showToast(String message) {
        Toast.makeText(mainActivity, message, Toast.LENGTH_LONG).show();
    }

    // variables:
    private static MainActivity mainActivity;
    private static BluetoothAdapter bluetoothAdapter;
    private static String MY_UUID = "f890841e-0131-4d6e-b1e6-656bd3a1d25d";

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

    private PublicStaticObjects() {}
}
