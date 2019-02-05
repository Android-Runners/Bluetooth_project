package com.example.bluetooth_project.ALL;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetooth_project.MainActivity;
import com.example.bluetooth_project.SendingActivity;

import java.util.List;

public class PublicStaticObjects {

    // functions:

    public static void showToast(String message) {
        Toast.makeText(mainActivity, message, Toast.LENGTH_LONG).show();
    }

    public static void setTitle(String s) {
        mainActivity.setTitle(s);
    }

    // variables:
    private static TextView txtTimeOn, txtTimeOff;
    private static List <CheckBox> checkBoxes;
    private static MainActivity mainActivity;
    private static SendingActivity sendingActivity;
    private static BluetoothAdapter bluetoothAdapter;
    private static String MY_UUID = "f890841e-0131-4d6e-b1e6-656bd3a1d25d";
    private static Boolean isConnected = false;
    private static BluetoothSocket socket;

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

    public static void stopBluetoothAdapter() {
     /*   bluetoothAdapter.disable();
        bluetoothAdapter.enable();*/
        bluetoothAdapter.cancelDiscovery();
        bluetoothAdapter = null;
    }

    public static BluetoothSocket getSocket() {
        return socket;
    }

    public static void setSocket(BluetoothSocket socket) {
        PublicStaticObjects.socket = socket;
    }

    public static void setMyUuid(String myUuid) {
        MY_UUID = myUuid;
    }

    public static List<CheckBox> getCheckBoxes() {
        return checkBoxes;
    }

    public static void check(int i, boolean b) {
        sendingActivity.runOnUiThread(() -> {
            if(b) {
                checkBoxes.get(i).setChecked(true);
            } else {
                checkBoxes.get(i).setChecked(false);
            }
        });
    }

    public static void setTime(String on, String off) {
        sendingActivity.runOnUiThread(() -> {
            txtTimeOn.setText(on);
            txtTimeOff.setText(off);
        });
    }

    public static void closeSendingActivity() {
        sendingActivity.runOnUiThread(() -> sendingActivity.finish());
    }

    public static void setCheckBoxes(List<CheckBox> checkBoxes) {
        PublicStaticObjects.checkBoxes = checkBoxes;
    }

    public static SendingActivity getSendingActivity() {
        return sendingActivity;
    }

    public static void setSendingActivity(SendingActivity sendingActivity) {
        PublicStaticObjects.sendingActivity = sendingActivity;
    }

    public static TextView getTxtTimeOn() {
        return txtTimeOn;
    }

    public static void setTxtTimeOn(TextView txtTimeOn) {
        PublicStaticObjects.txtTimeOn = txtTimeOn;
    }

    public static TextView getTxtTimeOff() {
        return txtTimeOff;
    }

    public static void setTxtTimeOff(TextView txtTimeOff) {
        PublicStaticObjects.txtTimeOff = txtTimeOff;
    }

    private PublicStaticObjects() {}
}
