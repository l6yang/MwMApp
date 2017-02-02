package com.mwm.loyal.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class GsonUtil {
    public static <T> T getBeanFromJson(String json, Class<T> tClass) {
        Gson gson = new Gson();
        return gson.fromJson(json, tClass);
    }

    public static <T> List<T> getBeanListFromJson(String json, Class<T> tClass) {
        List<T> list = new ArrayList<>();
        try {
            if (TextUtils.isEmpty(json)) {
                list.clear();
                return new ArrayList<>();
            }
            JsonArray array = new JsonParser().parse(json).getAsJsonArray();
            for (JsonElement elem : array) {
                list.add((T) new Gson().fromJson(elem, tClass));
            }
        } catch (Exception e) {
            //
        }
        return list;
    }
}