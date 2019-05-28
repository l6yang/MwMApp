package com.mwm.loyal.impl;

import android.os.Build;

import com.mwm.loyal.utils.CipherUtil;

public class ServerImpl implements IContactImpl {
    private static final String http = "http://";
    public static final String https = "https://";

    public static final String BASEURL = "baseUrl";
    public static final String IPADD_DEFAULT = "192.168.20.96";//192.168.0.110
    public static final String PORT_DEFAULT = "8080";
    public static final String NAMESPACE = "mwm";
    public static final String ACTION = "action.do?method=";

    public static String getServerUrl(String method) {
        return String.format("%s%s%s", getBaseUrl(), ACTION, method);
    }

    public static String getQrCodeContent(String account) {
        return String.format("%s%sscan&key=%s", getBaseUrl(), ACTION, CipherUtil.encode(account));
    }

    public static String showAvatar(String account) {
        return String.format("%s&account=%s", getServerUrl("showAvatar"), account);
    }

    public static String getBaseUrl() {
        return String.format("%s%s:%s/%s/", http, IPADD_DEFAULT, PORT_DEFAULT, NAMESPACE);
    }

    public static String deviceId() {
        return String.format("%s(%s)", Build.MANUFACTURER, Build.MODEL);
    }
}
