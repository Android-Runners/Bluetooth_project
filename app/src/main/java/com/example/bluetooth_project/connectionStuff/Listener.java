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

                if (!json.isNull("STOP")) {
                    if (InputAndOutput.getInputStream() != null) {
                        try {
                            InputAndOutput.getInputStream().close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        InputAndOutput.setInputStream(null);
                    }

                    if (InputAndOutput.getOutputStream() != null) {
                        try {
                            InputAndOutput.getOutputStream().close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        InputAndOutput.setOutputStream(null);
                    }

                    if (PublicStaticObjects.getSocket() != null) {
                        try {
                            PublicStaticObjects.getSocket().close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        PublicStaticObjects.setSocket(null);
                    }
                    if (PublicStaticObjects.getSendingActivity() != null) {
                        PublicStaticObjects.closeSendingActivity();
                    }
                } else {
                    if(!json.isNull("time")) {
                        JSONArray time = json.getJSONArray("time");
                        JSONArray day = json.getJSONArray("day");
                        JSONArray date = json.getJSONArray("date");
                        PublicStaticObjects.setTimeDate(
                                "получено",
                                new String(toByteArray(time)),
                                new String(toByteArray(day)),
                                new String(toByteArray(date)));
                    } else{

                        JSONArray bytes = json.getJSONArray("Bytes");
                        JSONArray bytesOn = json.getJSONArray("BytesOn");
                        JSONArray bytesOff = json.getJSONArray("BytesOff");
                        JSONArray interval = json.getJSONArray("Interval");
                        JSONArray proc = json.getJSONArray("Proc");
                        JSONArray fan = json.getJSONArray("Fan");
                        JSONArray nasos = json.getJSONArray("Nasos");

                        ObjectToSend objectRecieved = new ObjectToSend();
                        objectRecieved.setBytes(toByteArray(bytes));
                        objectRecieved.setTimeOn(toByteArray(bytesOn));
                        objectRecieved.setTimeOff(toByteArray(bytesOff));
                        objectRecieved.setInterval(toByteArray(interval));
                        objectRecieved.setProc(toByteArray(proc));
                        objectRecieved.setFan(toByteArray(fan));
                        objectRecieved.setNasos(toByteArray(nasos));

                        buffer = objectRecieved.getBytes();

                        // Recieved all data
                        if (objectRecieved.getTimeOn() != null && objectRecieved.getTimeOff() != null
                                && buffer.length == 7 && PublicStaticObjects.getCheckBoxes() != null
                                && !PublicStaticObjects.getCheckBoxes().isEmpty()) {
                            PublicStaticObjects.setTime(
                                    new String(objectRecieved.getTimeOn()),
                                    new String(objectRecieved.getTimeOff()));
                            for (int i = 0; i < 7; i++) {
                                if (buffer[i] == 1) {
                                    PublicStaticObjects.check(i, true);
                                } else {
                                    PublicStaticObjects.check(i, false);
                                }
                            }
                            PublicStaticObjects.setParam(
                                    new String(objectRecieved.getInterval()),
                                    new String(objectRecieved.getProc()),
                                    new String(objectRecieved.getFan()),
                                    new String(objectRecieved.getNasos()));
                        }
                    }

                }

            } catch (Exception e) {
                Log.e("!!!!", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private byte[] toByteArray(JSONArray jsonArray) {
        byte[] ans = new byte[jsonArray.length()];
        try {
            for (int i = 0; i < ans.length; ++i) {
                ans[i] = (byte) jsonArray.getInt(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ans;
    }
}
