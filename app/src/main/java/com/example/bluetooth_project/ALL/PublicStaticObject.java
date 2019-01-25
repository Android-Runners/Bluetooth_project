package com.example.bluetooth_project.ALL;

import android.bluetooth.BluetoothAdapter;

public class PublicStaticObject {
    private static BluetoothAdapter bluetoothAdapter;

    public static BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public static void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        PublicStaticObject.bluetoothAdapter = bluetoothAdapter;
    }

    private PublicStaticObject() {}

}
