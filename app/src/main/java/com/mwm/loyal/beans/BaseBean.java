package com.mwm.loyal.beans;

/**
 * 登录页面数据
 */
public class BaseBean {
    private String key;
    private String value;

    public String getKey() {
        return key == null ? "" : key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value == null ? "" : value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
