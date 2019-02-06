package com.example.bluetooth_project;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.bluetooth_project.ALL.InputAndOutput;
import com.example.bluetooth_project.ALL.PublicStaticObjects;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SendingActivity extends AppCompatActivity {

    List<CheckBox> checkBoxes;
    LinearLayout timeOn, timeOff;
    TextView txtTimeOn, txtTimeOff, txtDateTime;
    EditText editInterval, editProc, editFan, editNasos;

    ObjectToSend myObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PublicStaticObjects.setSendingActivity(this);

        editInterval = findViewById(R.id.editInterval);
        editProc = findViewById(R.id.editProc);
        editFan = findViewById(R.id.editFan);
        editNasos = findViewById(R.id.editNasos);

        checkBoxes = new ArrayList<CheckBox>();
        checkBoxes.add(findViewById(R.id.rdbtPn));
        checkBoxes.add(findViewById(R.id.rdbtVt));
        checkBoxes.add(findViewById(R.id.rdbtSr));
        checkBoxes.add(findViewById(R.id.rdbtCht));
        checkBoxes.add(findViewById(R.id.rdbtPt));
        checkBoxes.add(findViewById(R.id.rdbtSb));
        checkBoxes.add(findViewById(R.id.rdbtVs));


        timeOn = findViewById(R.id.timeOn);
        timeOff = findViewById(R.id.timeOff);
        txtTimeOn = findViewById(R.id.textTimeOn);
        txtTimeOff = findViewById(R.id.textTimeOff);
        txtDateTime = findViewById(R.id.txtDateTime);

        PublicStaticObjects.setCheckBoxes(checkBoxes);
        PublicStaticObjects.setTxtTimeOn(txtTimeOn);
        PublicStaticObjects.setTxtTimeOff(txtTimeOff);
        PublicStaticObjects.setEditFan(editFan);
        PublicStaticObjects.setEditInterval(editInterval);
        PublicStaticObjects.setEditNasos(editNasos);
        PublicStaticObjects.setEditProc(editProc);
        PublicStaticObjects.setTxtDateTime(txtDateTime);

        myObject = new ObjectToSend();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(InputAndOutput.getOutputStream() != null) {
                    try {
                        if(editInterval.getText().toString().isEmpty()) {
                            editInterval.setText(R.string.begin_interval);
                        }
                        if(editFan.getText().toString().isEmpty()) {
                            editFan.setText(R.string.begin_ventilator);
                        }
                        if(editNasos.getText().toString().isEmpty()) {
                            editNasos.setText(R.string.begin_nasos);
                        }
                        if(editProc.getText().toString().isEmpty()) {
                            editProc.setText(R.string.begin_procrab);
                        }

                        byte[] toSend = new byte[7];
                        for (int i = 0; i < 7; i++) {
                            toSend[i] = 0;
                            if (checkBoxes.get(i).isChecked()) {
                                toSend[i] = 1;
                            }
                        }
                        myObject.setBytes(toSend);
                        myObject.setTimeOn((txtTimeOn.getText() + "").getBytes());
                        myObject.setTimeOff((txtTimeOff.getText() + "").getBytes());
                        myObject.setInterval((editInterval.getText() + "").getBytes());
                        myObject.setNasos((editNasos.getText() + "").getBytes());
                        myObject.setProc((editProc.getText() + "").getBytes());
                        myObject.setFan((editFan.getText() + "").getBytes());
                        sendData(myObject);

                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        FloatingActionButton fabDate = findViewById(R.id.fabDate);
        fabDate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View view) {
                //   DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
                //   String date = df.format(Calendar.getInstance().getTime());
                DateFormat df = new SimpleDateFormat("h:mm a");
                String time = df.format(Calendar.getInstance().getTime());
                df = new SimpleDateFormat("EEEE");
                String day = df.format(Calendar.getInstance().getTime());
                df = new SimpleDateFormat("yyyy.MM.dd");
                String date = df.format(Calendar.getInstance().getTime());
                try {
                    JSONObject jsSend = new JSONObject();
                    JSONArray dateTime = new JSONArray(time.getBytes());
                    JSONArray dateDay = new JSONArray(day.getBytes());
                    JSONArray dateDate = new JSONArray(date.getBytes());
                    jsSend.put("time", dateTime);
                    jsSend.put("day", dateDay);
                    jsSend.put("date", dateDate);
                    InputAndOutput.getOutputStream().flush();
                    InputAndOutput.getOutputStream().write(jsSend.toString().getBytes());
                    InputAndOutput.getOutputStream().flush();
                    PublicStaticObjects.setTimeDate(
                            "отправлено",
                            new String(time.getBytes()),
                            new String(day.getBytes()),
                            new String(date.getBytes()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        timeOn.setOnClickListener(view -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(SendingActivity.this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onTimeSet(TimePicker timePicker,
                                              int selectedHour, int selectedMinute) {

                            txtTimeOn.setText(timeCheck(true, selectedHour, selectedMinute));

                        }
                    }, hour, minute, true);// Yes 24 hour time

            mTimePicker.setTitle("Время включения");
            mTimePicker.show();
        });

        timeOff.setOnClickListener(view -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(SendingActivity.this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onTimeSet(TimePicker timePicker,
                                              int selectedHour, int selectedMinute) {

                            txtTimeOff.setText(timeCheck(false, selectedHour, selectedMinute));

                        }
                    }, hour, minute, true);// Yes 24 hour time

            mTimePicker.setTitle("Время выключения");
            mTimePicker.show();
        });

    }

    private void sendData(ObjectToSend myObject) {
        try {
            JSONObject jsSend = new JSONObject();
            JSONArray jsBytes = new JSONArray(myObject.getBytes());
            JSONArray jsBytesOn = new JSONArray(myObject.getTimeOn());
            JSONArray jsBytesOff = new JSONArray(myObject.getTimeOff());
            JSONArray jsInterval = new JSONArray(myObject.getInterval());
            JSONArray jsProc = new JSONArray(myObject.getProc());
            JSONArray jsFan = new JSONArray(myObject.getFan());
            JSONArray jsNasos = new JSONArray(myObject.getNasos());

            jsSend.put("Bytes", jsBytes);
            jsSend.put("BytesOn", jsBytesOn);
            jsSend.put("BytesOff", jsBytesOff);
            jsSend.put("Interval", jsInterval);
            jsSend.put("Proc", jsProc);
            jsSend.put("Fan", jsFan);
            jsSend.put("Nasos", jsNasos);
            InputAndOutput.getOutputStream().flush();
            InputAndOutput.getOutputStream().write(jsSend.toString().getBytes());
            InputAndOutput.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String timeCheck(boolean b, int selectedHour, int selectedMinute) {

        String s;
        if(b) {
            s = "Время вкл\n";
        } else {
            s = "Время выкл\n";
        }

        String hour = selectedHour + "";

        if(hour.length() == 1) {
            s += "0" + selectedHour;
        } else {
            s += "" + selectedHour;
        }

        s += ":";

        hour = selectedMinute + "";

        if(hour.length() == 1) {
            s += "0" + selectedMinute;
        } else {
            s += "" + selectedMinute;
        }

        return s;
    }

    private void buttonStopAction() {
        PublicStaticObjects.getMainActivity().runOnUiThread(() -> {
            if(InputAndOutput.getOutputStream() != null) {
                try {
                    byte[] buffer = new byte[3];
                    buffer[1] = 1;
                    buffer[2] = 2;
                    JSONObject toSendStop = new JSONObject();
                    JSONArray stopBuffer = new JSONArray(buffer);
                    toSendStop.put("STOP", stopBuffer);
                    InputAndOutput.getOutputStream().write(toSendStop.toString().getBytes());
                    InputAndOutput.getOutputStream().flush();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        buttonStopAction();
        PublicStaticObjects.showToast("Ожидайте рассоединения...");
        PublicStaticObjects.setIsConnected(false);
        PublicStaticObjects.setSendingActivity(null);
    }

}
