package com.mwm.loyal.impl;

public interface IContactImpl {

    class StrImpl {
        public static final String appId = "9319579";
        public static final String appKey = "QAZQ5Gh8H6nAG2GOuPM0THDZ";
        public static final String secretKey = "c4f7e878acdb1314a1b5fe3ae502d645";
        public static final String KEY_ACCOUNT = "account";
        public static final String KEY_CITY = "city";
        public static final String KEY_WEATHER = "weather";
        public static final String KEY_PASSWORD = "password";
        public static final String KEY_SERVER = "server";
        public static final String actionUpdate = "com.mwm.loyal.activity.action.update";
        public static final String actionDownload = "com.mwm.loyal.activity.action.downLoad";
        public static final String KAY_ENCRYPT_DECODE = "com.mwm.forLoyal";

        public static final String defaultCity = "西安";
        public static final String defaultWeather = "0";
        public static final String share = "share";

    }

    class IntImpl {
        public static final int reqCodeNoRequest = 100;
        public static final int reqCodeSetting = 102;
        public static final int reqCodeZXing = 103;
        public static final int reqCodeUCrop = 104;
        public static final int reqCodePreview = 105;
        public static final int reqCodeIcon = 106;
        public static final int reqCodeWeather = 107;
        public static final int reqCodeCity = 108;
        public static final int reqCodeSettings_Account = 109;
        public static final int reqCodeUpdateMM = 110;
        public static final int reqCodeRegister = 111;
        public static final int reqCodeDestroy = 112;
        public static final int permissionMemory = 600;
        public static final int permissionCamera = 601;
        public static final int permissionReadPhone = 602;
        public static final int permissionLocation = 603;
        public static final int rx2Weather = 706;
        //异步
        public static final int async2Null = -102;
        public static final int delayed2Activity = -103;
    }
}
