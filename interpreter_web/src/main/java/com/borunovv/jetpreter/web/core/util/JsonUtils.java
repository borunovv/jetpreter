package com.borunovv.jetpreter.web.core.util;

import com.google.gson.Gson;

/**
 * @author borunovv
 */
public  final class JsonUtils {

    private static ThreadLocal<Gson> gson = ThreadLocal.withInitial(Gson::new);

    @SuppressWarnings("unchecked")
    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.get().fromJson(json, clazz);
    }

    @SuppressWarnings("unchecked")
    public static String toJson(Object obj) {
        return gson.get().toJson(obj);
    }
}