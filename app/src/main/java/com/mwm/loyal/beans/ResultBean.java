package com.mwm.loyal.beans;

import com.loyal.kit.PatternBean;

public class ResultBean<T> extends PatternBean<T> {
    private String  code;
    private String message;
    private T obj;

    public ResultBean(String code, String message, T obj) {
        this.code = code;
        this.message = message;
        this.obj = obj;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }
}