package com.mwm.loyal.utils;

import com.mwm.loyal.impl.Contact;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class ConnectUtil implements Contact {

    public static String getError(Throwable throwable) {
        if (null == throwable)
            return "未知错误";
        if (throwable instanceof ConnectException)
            return "连接服务器失败";
        else if (throwable instanceof SocketTimeoutException)
            return "连接超时";
        else if (throwable instanceof UnknownHostException)
            return "网络未连接或者当前网络地址未被识别";
        else if (throwable instanceof SocketException)
            return "网络服务出现问题";
        else return throwable.getMessage();
    }
}
