package com.example.bluetooth_project.connectionStuff;

import android.view.View;

import com.example.bluetooth_project.ALL.InputAndOutput;
import com.example.bluetooth_project.ALL.PublicStaticObjects;

import java.io.IOException;

public class Listener implements Runnable {

    @Override
    public void run() {

        while(true) {
            if(InputAndOutput.getInputStream() == null) {
                continue;
            }
//            if(PublicStaticObjects.getIsConnected()) {
                if(PublicStaticObjects.getMainActivity().getEditText().getVisibility() == View.INVISIBLE) {
                    PublicStaticObjects.getMainActivity().runOnUiThread(() -> {
                        PublicStaticObjects.getMainActivity().getEditText().setVisibility(View.VISIBLE);
                        PublicStaticObjects.getMainActivity().getButtonSend().setVisibility(View.VISIBLE);
                    });
                }
                try {
                    // size must be 6 + 3, but who knows
                    byte[] buffer = new byte[6 + 3];
                    int size = InputAndOutput.getInputStream().read(buffer);
                    if(buffer[0] == 2 && buffer[1] == 2 && buffer[2] == 8 && size == 6 + 3) {
                        byte[] buffer2 = new byte[6];
                        System.arraycopy(buffer, 3, buffer2, 0, 6);
                        PublicStaticObjects.getMainActivity().runOnUiThread(() ->
                                PublicStaticObjects.getMainActivity().getEditText().setText(new String(buffer2).toCharArray(), 0, 6));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
//            }
//            else {
                // TODO: getIsConnectedBecomes false
//                if(PublicStaticObjects.getMainActivity().getEditText().getVisibility() == View.VISIBLE) {
//                    PublicStaticObjects.getMainActivity().runOnUiThread(() -> {
//                        PublicStaticObjects.getMainActivity().getEditText().setVisibility(View.INVISIBLE);
//                        PublicStaticObjects.getMainActivity().getButtonSend().setVisibility(View.INVISIBLE);
//                    });
//                }
//            }
        }
    }
}
