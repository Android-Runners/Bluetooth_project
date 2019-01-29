package com.example.bluetooth_project.connectionStuff;

import com.example.bluetooth_project.ALL.InputAndOutput;
import com.example.bluetooth_project.ALL.PublicStaticObjects;

import java.io.IOException;

public class Listener implements Runnable {

    @Override
    public void run() {

        boolean isFirst = true;

        while(true) {
            if(InputAndOutput.getInputStream() == null) {
                continue;
            }
            else {
                if(isFirst) {
                    PublicStaticObjects.getMainActivity().runOnUiThread(() -> PublicStaticObjects.showToast("Не null"));
                    isFirst = false;
                }
            }
            try {
                // size must be 6 + 3, but who knows
                byte[] buffer = new byte[6 + 3];
                int size = InputAndOutput.getInputStream().read(buffer);
                if(size == 6 + 3 && buffer[0] == 2 && buffer[1] == 2 && buffer[2] == 8) {
                    byte[] buffer2 = new byte[6];
                    System.arraycopy(buffer, 3, buffer2, 0, 6);
                    PublicStaticObjects.getMainActivity().runOnUiThread(() ->
                            PublicStaticObjects.getMainActivity().getEditText().setText(new String(buffer2).toCharArray(), 0, 6));
                }
                else if(size == 3 && buffer[0] == 0 && buffer[1] == 1 && buffer[2] == 2) {
//                    isInitialized = false;
                    /*PublicStaticObjects.getMainActivity().runOnUiThread(() ->
                            PublicStaticObjects.showToast("Listener, disconnecting"));*/

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

                    //PublicStaticObjects.setBluetoothAdapter(BluetoothAdapter.getDefaultAdapter());
                    // PublicStaticObjects.setMyUuid(PublicStaticObjects.getMyUuid() + "1");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
