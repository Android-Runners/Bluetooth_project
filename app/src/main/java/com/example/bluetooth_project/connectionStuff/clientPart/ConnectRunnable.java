package com.example.bluetooth_project.connectionStuff.clientPart;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.example.bluetooth_project.ALL.InputAndOutput;
import com.example.bluetooth_project.ALL.PublicStaticObjects;

import java.io.IOException;
import java.util.UUID;

public class ConnectRunnable implements Runnable {

    private final BluetoothSocket socket;
    private final BluetoothDevice device;
    private final BluetoothAdapter bluetoothAdapter;
    private final UUID MY_UUID = UUID.fromString(PublicStaticObjects.getMyUuid());

    public ConnectRunnable(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        this.device = device;
        this.bluetoothAdapter = PublicStaticObjects.getBluetoothAdapter();

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e("in connectRunnable: ", "Socket's create() method failed", e);
        }
        socket = tmp;
    }

    @Override
    public void run() {

        bluetoothAdapter.cancelDiscovery();

        if(connectDevices()) {
            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            manageMyConnectedSocket(socket);
        }
    }

    private void manageMyConnectedSocket(BluetoothSocket socket) {
        try {
            InputAndOutput.setInputStream(socket.getInputStream());
            InputAndOutput.setOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean connectDevices() {
        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            Log.e("in connectRunnable: ", "Before");
            socket.connect();
            PublicStaticObjects.setIsConnected(true);
            Log.e("in connectRunnable: ", "After");
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                socket.close();
                PublicStaticObjects.setIsConnected(false);
            } catch (IOException closeException) {
                Log.e("in connectRunnable: ", "Could not close the client socket", closeException);
            }
            return false;
        }
        return true;
    }

    public void cancel() {
        try {
            socket.close();
            PublicStaticObjects.setIsConnected(false);
        } catch (IOException e) {
            Log.e("lol", "Could not close the client socket", e);
        }
    }
}
