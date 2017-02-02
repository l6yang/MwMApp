package com.mwm.loyal.app;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

import cn.sharesdk.framework.ShareSDK;

public class MwMApplication extends Application {
    private static MwMApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        Fresco.initialize(this);
        ShareSDK.initSDK(this);
    }

    public static MwMApplication getApplication() {
        return application;
    }
}