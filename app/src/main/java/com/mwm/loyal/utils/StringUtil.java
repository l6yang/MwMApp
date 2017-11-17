package com.mwm.loyal.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Spinner;

import com.mwm.loyal.beans.SpinBean;
import com.mwm.loyal.impl.Contact;

import java.net.URLDecoder;
import java.net.URLEncoder;
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

    public static void showErrorDialog(@NonNull Activity context, String error, boolean isFinish) {
        ToastUtil.showDialog(context, errorException(error), isFinish);
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
        } else if (TextUtils.equals("err", error)) {
            return true;
        } else if (error.startsWith("org.ksoap2.transport.HttpResponseException")) {
            return true;
        }
        return false;
    }

    private static String errorException(String error) {
        if (TextUtils.isEmpty(error)) {
            return "无数据";
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
        } else return error;
    }

    public static String replaceNull(CharSequence sequence) {
        return TextUtils.isEmpty(sequence) ? "" : sequence.toString().trim();
    }

    public static String replaceNull(Object object) {
        return null == object ? "" : replaceNull(object.toString());
    }

    public static boolean isEmpty(String str) {
        return ((str == null) || (str.trim().length() == 0));
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

    public static String encodeStr2Utf(String str) {
        if (TextUtils.isEmpty(str))
            return "";
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (Exception e) {
            return str;
        }
    }

    public static String decodeStr2Utf(String str) {
        if (TextUtils.isEmpty(str))
            return "";
        try {
            return URLDecoder.decode(str, "utf-8");
        } catch (Exception e) {
            return str;
        }
    }

    public static String getSpinSelectStr(Spinner spinner, String key) {
        if (null == spinner)
            return "";
        try {
            SpinBean spinBean = (SpinBean) spinner.getSelectedItem();
            switch (key) {
                case "dm":
                    return replaceNull(spinBean.getDm());
                case "glbm":
                    return replaceNull(spinBean.getGlbm());
                default:
                    return "";
            }
        } catch (Exception e) {
            return "";
        }
    }
}