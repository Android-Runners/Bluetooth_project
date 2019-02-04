package com.example.bluetooth_project.ALL;

import com.google.gson.Gson;

public class JsonConverter {

    /** gson instance to convert into/from json */
    private static Gson gson = new Gson();

    /** converts object to Json
     * @param object object, that will be converted
     * */
    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    /**
     * @param json String, that contains Json obj
     * @param classOfT Class of returned type
     * */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    /** private constructor so no one can create an instance */
    private JsonConverter() { }
}
