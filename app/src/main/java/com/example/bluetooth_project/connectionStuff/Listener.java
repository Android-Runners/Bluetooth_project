package com.example.bluetooth_project.connectionStuff;

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
        }
    }
}
