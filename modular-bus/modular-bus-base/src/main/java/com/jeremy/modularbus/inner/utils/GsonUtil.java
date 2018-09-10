package com.jeremy.modularbus.inner.utils;


import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;


public final class GsonUtil {
    private Gson mGson;

    private GsonUtil() {
        mGson = new Gson();
    }

    private static class SingletonHolder {
        private static final GsonUtil INSTANCE = new GsonUtil();
    }

    public static GsonUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Gson getGson() {
        return mGson;
    }

    public static String toJson(Object src) {
        return getInstance().getGson().toJson(src);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return getInstance().getGson().fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return getInstance().getGson().fromJson(json, typeOfT);
    }

    public static <T> T fromJson(JsonElement element, Class<T> tClass) {
        return getInstance().getGson().fromJson(element, tClass);
    }

}
