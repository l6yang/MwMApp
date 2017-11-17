package com.mwm.loyal.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import com.mwm.loyal.impl.IntentFrame;

public class IntentUtil extends Intent implements IntentFrame {
    private Activity activity;

    /**
     * 下一个Activity收不到上一个Activity中Intent携带的参数
     *
     * @see #IntentUtil(Activity)
     */
    public IntentUtil(Activity activity) {
        super();
        this.activity = activity;
    }

    /**
     * @param intent default=null
     */
    public IntentUtil(Activity activity, Intent intent) {
        super(intent);
        this.activity = activity;
    }

    @Override
    public void startActivity(@Nullable Class<?> tClass) {
        if (null == activity)
            throw new NullPointerException("the Context must not null");
        else if (null == tClass)
            throw new ActivityNotFoundException("No Activity found to handle Intent {  }");
        else {
            setClass(activity, tClass);
            activity.startActivity(this);
        }
    }

    @Override
    public void startActivityForResult(@Nullable Class<?> tClass, @IntRange(from = 2) int reqCode) {
        if (null == activity)
            throw new NullPointerException("the Context must not null");
        else if (null == tClass)
            throw new NullPointerException("the StartActivity must not null");
        else {
            setClass(activity, tClass);
            activity.startActivityForResult(this, reqCode);
        }
    }

    @Override
    public void startService(@Nullable Class<?> tClass) {
        if (null == activity)
            throw new NullPointerException("the Context must not null");
        else if (null == tClass)
            throw new NullPointerException("the StartActivity must not null");
        else {
            setClass(activity, tClass);
            activity.startService(this);
        }
    }
}
