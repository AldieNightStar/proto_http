package haxidenti.httpproto.server;

import haxidenti.httpproto.Coder;

import java.lang.reflect.Method;

public class GsonCoder implements Coder {

    Object gson;

    Method toJson;
    Method fromJson;

    public GsonCoder() {
        try {
            gson = Class.forName("com.google.gson.Gson").getConstructor().newInstance();
            toJson = gson.getClass().getMethod("toJson", Object.class);
            fromJson = gson.getClass().getMethod("fromJson", String.class, Class.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toJson(Object o) {
        try {
            return (String) toJson.invoke(gson, o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T toObject(String json, Class<T> type) {
        try {
            return (T) fromJson.invoke(gson, json, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
