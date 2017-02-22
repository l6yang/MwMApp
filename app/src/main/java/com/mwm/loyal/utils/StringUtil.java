package com.mwm.loyal.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.mwm.loyal.imp.Contact;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil implements Contact {

    public static boolean showErrorToast(Context context, String error) {
        if (showErrorToast(error)) {
            ToastUtil.showToast(context, errorException(error));
            return true;
        }
        return false;
    }

    public static boolean showErrorDialog(Activity context, String error, boolean isFinish) {
        if (showErrorToast(error)) {
            ToastUtil.showDialog(context, errorException(error), isFinish);
            return true;
        }
        return false;
    }

    private static boolean showErrorToast(String error) {
         if (error.startsWith("java.io.IOException: Unexpected codeResponse")) {
            return true;
        } else if (error.startsWith("java.net.SocketTimeoutException")) {
            return true;
        } else if (error.startsWith("java.net.SocketException")) {
            return true;
        } else if (error.startsWith("java.net.ConnectException")) {
            return true;
        } else if (error.startsWith("java.io.EOFException")) {
            return true;
        } else if (error.startsWith("java.net.UnknownHostException")) {
            return true;
        } else if (error.startsWith("org.ksoap2.transport.HttpResponseException")) {
            return true;
        } else if (error.startsWith("SoapFault - faultcode")) {
            return true;
        } else if (TextUtils.equals(error, "err")) {
            return true;
        } else if (error.startsWith("org.ksoap2.transport.HttpResponseException")) {
            return true;
        }
        return false;
    }

    private static String errorException(String error) {
        if (TextUtils.isEmpty(error)) {
            return "获取数据失败";
        } else if (error.startsWith("java.io.IOException: Unexpected codeResponse")) {
            return "服务器响应异常";
        } else if (error.startsWith("java.net.SocketTimeoutException")) {
            return "连接服务器超时";
        } else if (error.startsWith("java.net.SocketException")) {
            return "网络服务异常";
        } else if (error.startsWith("java.net.ConnectException")) {
            return "连接服务器失败";
        } else if (error.startsWith("java.net.UnknownHostException")) {
            return "网络未连接或者当前网络地址未被识别";
        } else if (error.startsWith("org.ksoap2.transport.HttpResponseException")) {
            return "与服务器通信失败";
        } else return "";
    }

    public static String replaceNull(Object object) {
        return object == null || TextUtils.equals(object.toString(), "null") ? "" : object.toString();
    }

    public static boolean isEmpty(String str) {
        return ((str == null) || (str.trim().length() == 0));
    }

    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    public static boolean isMobileNo(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean ipValue(String address) {
        if (isEmpty(address))
            return false;
        String ipPattern = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        Pattern pattern = Pattern.compile(ipPattern);
        Matcher matcher = pattern.matcher(address);
        return matcher.matches();
    }
}
