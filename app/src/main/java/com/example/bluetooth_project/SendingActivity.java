package com.example.bluetooth_project;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;

import com.example.bluetooth_project.ALL.InputAndOutput;
import com.example.bluetooth_project.ALL.PublicStaticObjects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SendingActivity extends AppCompatActivity {

    List<CheckBox> checkBoxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PublicStaticObjects.setSendingActivity(this);

        checkBoxes = new ArrayList<CheckBox>();
        checkBoxes.add(findViewById(R.id.rdbtPn));
        checkBoxes.add(findViewById(R.id.rdbtVt));
        checkBoxes.add(findViewById(R.id.rdbtSr));
        checkBoxes.add(findViewById(R.id.rdbtCht));
        checkBoxes.add(findViewById(R.id.rdbtPt));
        checkBoxes.add(findViewById(R.id.rdbtSb));
        checkBoxes.add(findViewById(R.id.rdbtVs));

        PublicStaticObjects.setCheckBoxes(checkBoxes);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(InputAndOutput.getOutputStream() != null) {
                    try {
                        byte[] toSend = new byte[7];
                        for (int i = 0; i < 7; i++) {
                            toSend[i] = 0;
                            if (checkBoxes.get(i).isChecked()) {
                                toSend[i] = 1;
                            }
                        }
                        InputAndOutput.getOutputStream().flush();
                        InputAndOutput.getOutputStream().write(toSend);
                        InputAndOutput.getOutputStream().flush();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void buttonStopAction() {
        if(InputAndOutput.getOutputStream() != null) {
            try {
                byte[] buffer = new byte[3];
                buffer[1] = 1;
                buffer[2] = 2;
                InputAndOutput.getOutputStream().write(buffer);
                InputAndOutput.getOutputStream().flush();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        buttonStopAction();
        PublicStaticObjects.setSendingActivity(null);
    }

}
