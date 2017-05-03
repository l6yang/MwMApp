package com.mwm.loyal.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.beans.TestBean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ikidou.reflect.TypeBuilder;

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

     /* public static <T> ResultBean<T> fromJsonObject(Reader reader, Class<T> clazz) {
        Type type = new GsonParameterized(ResultBean.class, new Class[]{clazz});
        return new Gson().fromJson(reader, type);
    }*/

    public static <T> TestBean<List<T>> fromJsonArray(String json, Class<T> clazz) {
        Type type = TypeBuilder
                .newInstance(ResultBean.class)
                .beginSubType(List.class)
                .addTypeParam(clazz)
                .endSubType()
                .build();
        return new Gson().fromJson(json, type);
    }

    public static <T> TestBean<T> fromJsonObject(String json, Class<T> clazz) {
        Type type = TypeBuilder
                .newInstance(ResultBean.class)
                .addTypeParam(clazz)
                .build();
        return new Gson().fromJson(json, type);
    }

    public static String bean2Json(Object object) {
        if (null == object)
            return "{}";
        return new Gson().toJson(object);
    }
}