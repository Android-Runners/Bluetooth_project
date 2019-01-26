package com.example.bluetooth_project.ALL;

import java.io.InputStream;
import java.io.OutputStream;

public class InputAndOutput {

    private static InputStream inputStream;
    private static OutputStream outputStream;

    public static InputStream getInputStream() {
        return inputStream;
    }

    public static void setInputStream(InputStream inputStream) {
        InputAndOutput.inputStream = inputStream;
    }

    public static OutputStream getOutputStream() {
        return outputStream;
    }

    public static void setOutputStream(OutputStream outputStream) {
        InputAndOutput.outputStream = outputStream;
    }

    private InputAndOutput() { }
}
