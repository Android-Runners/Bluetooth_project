package com.example.bluetooth_project.connectionStuff;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.example.bluetooth_project.ALL.PublicStaticObject;

import java.io.IOException;
import java.util.UUID;

public class AcceptRunnable implements Runnable {
    private final BluetoothServerSocket serverSocket;
    private final BluetoothAdapter bluetoothAdapter;
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fa");

    public AcceptRunnable() {
        this.bluetoothAdapter = PublicStaticObject.getBluetoothAdapter();
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code.
            Log.e("KEK", "" + (bluetoothAdapter == null));
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(bluetoothAdapter.getName(), MY_UUID);
            Log.e("kek", "Socket created");

        } catch (IOException e) {
            Log.e("kek", "Socket's listen() method failed", e);
        }
        serverSocket = tmp;
    }

    @Override
    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                Log.e("kek", "Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
            //    manageMyConnectedSocket(socket);
                try {
                    serverSocket.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public void cancel() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            Log.e("kek", "Could not close the connect socket", e);
        }
    }
}
