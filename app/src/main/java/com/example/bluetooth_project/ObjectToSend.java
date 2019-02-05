package com.example.bluetooth_project;

public class ObjectToSend {

    private byte[] bytes;

    private byte[] timeOn;

    private byte[] timeOff;

    public ObjectToSend() {}

    public ObjectToSend(byte[] bytesToSend) {
        this.bytes = bytesToSend;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytesToSend) {
        this.bytes = bytesToSend;
    }

    public byte[] getTimeOn() {
        return timeOn;
    }

    public void setTimeOn(byte[] timeOn) {
        this.timeOn = timeOn;
    }

    public byte[] getTimeOff() {
        return timeOff;
    }

    public void setTimeOff(byte[] timeOff) {
        this.timeOff = timeOff;
    }

}
