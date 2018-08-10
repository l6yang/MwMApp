package com.mwm.loyal.app;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class MwMApp extends Application {
    private static MwMApp application;
    private String activityTag;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        Fresco.initialize(this);
    }

    public static synchronized MwMApp getInstance() {
        return application;
    }

    public void setActivityTag(String activityTag) {
        this.activityTag = activityTag;
    }

    public String getActivityTag() {
        return activityTag;
    }
}