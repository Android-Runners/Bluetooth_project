package com.example.bluetooth_project.connectionStuff;

import com.example.bluetooth_project.ALL.PublicStaticObjects;

public class ListenerConnection implements Runnable {
    @Override
    public void run() {
        while(true) {
            if(PublicStaticObjects.getSocket() == null) {
                continue;
            }
            if(!PublicStaticObjects.getSocket().isConnected()) {
                PublicStaticObjects.getMainActivity().runOnUiThread(() ->
                        PublicStaticObjects.setTitle("Not connected"));
            } else {
                PublicStaticObjects.getMainActivity().runOnUiThread(() ->
                        PublicStaticObjects.setTitle("Is connected"));
            }
        }
    }
}
