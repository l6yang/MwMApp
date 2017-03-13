package com.mwm.loyal.libs.manager;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class AppBean implements Serializable {
    private String name;
    private String apk;
    private String version;
    private String source;
    private String data;
    private Drawable icon;
    private Boolean system;

    public AppBean(String name, String apk, String version, String source, String data, Drawable icon, Boolean isSystem) {
        this.name = name;
        this.apk = apk;
        this.version = version;
        this.source = source;
        this.data = data;
        this.icon = icon;
        this.system = isSystem;
    }

    public AppBean(String string) {
        String[] split = string.split("##");
        if (split.length == 6) {
            this.name = split[0];
            this.apk = split[1];
            this.version = split[2];
            this.source = split[3];
            this.data = split[4];
            this.system = Boolean.getBoolean(split[5]);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApk() {
        return apk;
    }

    public void setApk(String apk) {
        this.apk = apk;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Boolean isSystem() {
        return system;
    }

    @Override
    public String toString() {
        return getName() + "##" + getApk() + "##" + getVersion() + "##" + getSource() + "##" + getData() + "##" + isSystem();
    }
}
