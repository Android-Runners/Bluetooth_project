package com.example.bluetooth_project.connectionStuff;

import android.util.Log;

import com.example.bluetooth_project.ALL.InputAndOutput;
import com.example.bluetooth_project.ALL.PublicStaticObjects;
import com.example.bluetooth_project.ObjectToSend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class Listener implements Runnable {

    @Override
    public void run() {

        while(true) {
            if(InputAndOutput.getInputStream() == null) {
                continue;
            }
            try {
                byte[] buffer = new byte[512000];
                InputAndOutput.getInputStream().read(buffer);

                JSONObject json = new JSONObject(new String(buffer));
                JSONArray bytes = json.getJSONArray("Bytes");

                if(bytes.length() == 3) {// Stop connection
                    if(InputAndOutput.getInputStream() != null) {
                        try {
                            InputAndOutput.getInputStream().close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        InputAndOutput.setInputStream(null);
                    }

                    if(InputAndOutput.getOutputStream() != null) {
                        try {
                            InputAndOutput.getOutputStream().close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        InputAndOutput.setOutputStream(null);
                    }

                    if(PublicStaticObjects.getSocket() != null) {
                        try {
                            PublicStaticObjects.getSocket().close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        PublicStaticObjects.setSocket(null);
                    }
                    if(PublicStaticObjects.getSendingActivity() != null) {
                        PublicStaticObjects.closeSendingActivity();
                    }
                } else {

                    JSONArray bytesOff = json.getJSONArray("BytesOff");
                    JSONArray bytesOn = json.getJSONArray("BytesOn");

                    byte[] recievedBytes = new byte[bytes.length()];
                    byte[] recievedBytesOn = new byte[bytesOn.length()];
                    byte[] recievedBytesOff = new byte[bytesOff.length()];
                    for (int i = 0; i < bytes.length(); ++i) {
                        recievedBytes[i] = (byte) bytes.getInt(i);
                    }
                    for (int i = 0; i < bytesOn.length(); ++i) {
                        recievedBytesOn[i] = (byte) bytesOn.getInt(i);
                    }
                    for (int i = 0; i < bytesOff.length(); ++i) {
                        recievedBytesOff[i] = (byte) bytesOff.getInt(i);
                    }

                    ObjectToSend objectRecieved = new ObjectToSend();
                    objectRecieved.setBytes(recievedBytes);
                    objectRecieved.setTimeOn(recievedBytesOn);
                    objectRecieved.setTimeOff(recievedBytesOff);

                    buffer = recievedBytes;

                    // Recieved all data
                    if (objectRecieved.getTimeOn() != null && objectRecieved.getTimeOff() != null
                            && buffer.length == 7 && PublicStaticObjects.getCheckBoxes() != null
                            && !PublicStaticObjects.getCheckBoxes().isEmpty()) {
                        PublicStaticObjects.setTime(new String(objectRecieved.getTimeOn()), new String(objectRecieved.getTimeOff()));
                        for (int i = 0; i < 7; i++) {
                            if (buffer[i] == 1) {
                                PublicStaticObjects.check(i, true);
                            } else {
                                PublicStaticObjects.check(i, false);
                            }
                        }
                    }

                }

            } catch (Exception e) {
                Log.e("!!!!", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
