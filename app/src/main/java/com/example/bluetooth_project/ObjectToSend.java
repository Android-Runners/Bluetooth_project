package com.example.bluetooth_project;

public class ObjectToSend {

    private byte[] bytes;

    private byte[] timeOn;

    private byte[] timeOff;

    private byte[] interval;

    private byte[] proc;

    private byte[] fan;

    private byte[] nasos;

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

    public byte[] getInterval() {
        return interval;
    }

    public void setInterval(byte[] interval) {
        this.interval = interval;
    }

    public byte[] getProc() {
        return proc;
    }

    public void setProc(byte[] proc) {
        this.proc = proc;
    }

    public byte[] getFan() {
        return fan;
    }

    public void setFan(byte[] fan) {
        this.fan = fan;
    }

    public byte[] getNasos() {
        return nasos;
    }

    public void setNasos(byte[] nasos) {
        this.nasos = nasos;
    }
}
