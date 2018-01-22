package com.mwm.loyal.impl;

import android.text.TextUtils;

public interface Contact {

    class Str {
        public static final String appId = "9319579";
        public static final String appKey = "QAZQ5Gh8H6nAG2GOuPM0THDZ";
        public static final String secretKey = "c4f7e878acdb1314a1b5fe3ae502d645";
        public static final String TIME_ALL = "yyyy-MM-dd HH:mm:ss";
        public static final String TIME_WEEK = "yyyy-MM-dd EEEE";
        public static final String TIME_YEAR_MONTH_DAY = "yyyy-MM-dd";
        public static final String HOURS_MIN = "HH:mm";
        public static final String MONTH_DAY_HOUR_MIN = "MM-dd HH:mm";
        public static final String YEAR_MONTH = "yyyy-MM";
        public static final String KEY_IP = "ip";
        public static final String KEY_ACCOUNT = "account";
        public static final String KEY_CITY = "city";
        public static final String KEY_WEATHER = "weather";
        public static final String KEY_PASSWORD = "password";
        public static final String KEY_SERVER = "server";
        public static final String service_action_loc = "com.mwm.loyal.service.action.loc";
        public static final String actionUpdate = "com.mwm.loyal.activity.action.update";
        public static final String actionDownload = "com.mwm.loyal.activity.action.downLoad";
        public static final String method_register = "doRegister";
        public static final String method_login = "doLogin";
        public static final String method_queryAccount = "doQueryAccount";
        public static final String method_showIcon = "doShowIconByIO";
        public static final String method_update = "doUpdateAccount";
        public static final String method_account_locked = "doAccountLocked";
        public static final String method_update_icon = "doUpdateIcon";
        public static final String method_feedBack = "doFeedBack";
        public static final String method_getSelfFeed = "doGetSelfFeed";
        public static final String method_deleteSelfFeed = "deleteSelfFeed";
        public static final String method_ucrop_test = "doUCropTest";
        public static final String method_scan = "doScan";
        public static final String method_apkVerCheck = "doCheckApkVer";
        public static final String method_destroyAccount = "destroyAccount";
        public static final String KAY_ENCRYPT_DECODE = "com.mwm.forLoyal";
        public static final String ipAdd = "192.168.1.15";
        public static final String port = ":8080";
        private static final String http = "http://";
        public static final String https = "https://";
        public static final String action = "action.do?method=";
        public static final String defaultCity = "西安";
        public static final String defaultWeather = "0";
        public static final String share = "share";

        public static String getServerUrl(String method) {
            return getBaseUrl() + action + method;
        }

        public static String getBaseUrl() {
            return http + ipAdd + port + "/mwm/";
        }

        public static String replaceNull(CharSequence sequence) {
            return TextUtils.isEmpty(sequence) ? "" : sequence.toString().trim();
        }
    }

    class Int {
        public static final int reqCode_Main_noRequest = 100;
        public static final int reqCode_Main_Setting = 102;
        public static final int reqCode_Main_Zing = 103;
        public static final int reqCode_Main_UCrop = 104;
        public static final int reqCode_Main_Preview = 105;
        public static final int reqCode_Main_icon = 106;
        public static final int reqCode_Main_weather = 107;
        public static final int reqCode_Weather_city = 108;
        public static final int reqCode_Settings_account = 109;
        public static final int reqCode_UpdateMM = 110;
        public static final int reqCode_register = 111;
        public static final int reqCode_destroy = 112;
        public static final int permissionMemory = 600;
        public static final int permissionCamera = 601;
        public static final int permissionReadPhone = 602;
        public static final int permissionLocation = 603;
        public static final int rx2Weather = 706;
        //异步
        public static final int async2Null = -102;
        public static final int delayed2Activity = -103;
    }

    enum TYPE {
        NONE, LEFT, RIGHT
    }
}
