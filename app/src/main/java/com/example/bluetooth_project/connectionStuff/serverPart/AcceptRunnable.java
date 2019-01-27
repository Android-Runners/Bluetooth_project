package com.example.bluetooth_project.connectionStuff.serverPart;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.example.bluetooth_project.ALL.InputAndOutput;
import com.example.bluetooth_project.ALL.PublicStaticObjects;

import java.io.IOException;
import java.util.UUID;

public class AcceptRunnable implements Runnable {
    private final BluetoothServerSocket serverSocket;
    private final BluetoothAdapter bluetoothAdapter;
    private final UUID MY_UUID = UUID.fromString(PublicStaticObjects.getMyUuid());

    public AcceptRunnable() {
        this.bluetoothAdapter = PublicStaticObjects.getBluetoothAdapter();
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code.
            Log.e("in acceptRunnable: ", "" + (bluetoothAdapter == null));
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(bluetoothAdapter.getName(), MY_UUID);
            Log.e("in acceptRunnable: ", "Socket created");
        } catch (IOException e) {
            Log.e("in acceptRunnable: ", "Socket's listen() method failed", e);
            e.printStackTrace();
        }
        serverSocket = tmp;
    }

    @Override
    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                Log.e("in acceptRunnable: ", "Before");
                socket = serverSocket.accept();
                Log.e("in acceptRunnable: ", "After");
            } catch (IOException e) {
                Log.e("in acceptRunnable: ", "Socket's accept() method failed", e);
                e.printStackTrace();
                break;
            }

            if (socket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
                manageMyConnectedSocket(socket);
                try {
                    serverSocket.close();
                    PublicStaticObjects.setIsConnected(false);
                } catch(IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void manageMyConnectedSocket(BluetoothSocket socket) {
        try {
            InputAndOutput.setInputStream(socket.getInputStream());
            InputAndOutput.setOutputStream(socket.getOutputStream());
            PublicStaticObjects.setIsConnected(true);
            PublicStaticObjects.getMainActivity().runOnUiThread(
                    () -> PublicStaticObjects.getMainActivity().getArrayAdapter().clear());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancel() {
        try {
            serverSocket.close();
            PublicStaticObjects.setIsConnected(false);
        } catch (IOException e) {
            Log.e("kek", "Could not close the connect socket", e);
        }
    }
}
