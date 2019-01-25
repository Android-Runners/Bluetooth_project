package com.example.bluetooth_project.connectionStuff;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.example.bluetooth_project.ALL.PublicStaticObject;

import java.io.IOException;
import java.util.UUID;

public class ConnectRunnable implements Runnable {

    private final BluetoothSocket socket;
    private final BluetoothDevice device;
    private final BluetoothAdapter bluetoothAdapter;
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fc");

    public ConnectRunnable(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        this.device = device;
        this.bluetoothAdapter = PublicStaticObject.getBluetoothAdapter();

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e("lol", "Socket's create() method failed", e);
        }
        socket = tmp;
    }

    @Override
    public void run() {

        bluetoothAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            socket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                socket.close();
            } catch (IOException closeException) {
                Log.e("lol", "Could not close the client socket", closeException);
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.

        //manageMyConnectedSocket(mmSocket);

    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            Log.e("lol", "Could not close the client socket", e);
        }
    }
}
