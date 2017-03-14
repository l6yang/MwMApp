package com.mwm.loyal.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.mwm.loyal.base.BaseProgressSubscriber;
import com.mwm.loyal.imp.Progress;

import rx.Observable;

public class RxUtil<T> implements Progress.SubscribeListener<T> {
    public static final int rxOnResult = -25;
    public static final int rxOnError = -26;
    public static final int rxOnCompleted = -27;
    private Handler handler;

    public RxUtil(Context context, Observable<T> observable, Handler handler) {
        BaseProgressSubscriber<T> subscriber = new BaseProgressSubscriber<>(context, "处理中...", true, false, true);
        RetrofitManage.doEnqueueStr(observable, subscriber);
        subscriber.setSubscribeListener(this);
        this.handler = handler;
    }

    @Override
    public void onResult(T t) {
        Message onResult = Message.obtain(handler, rxOnResult, t);
        onResult.sendToTarget();
    }

    @Override
    public void onError(Throwable e) {
        Message onResult = Message.obtain(handler, rxOnError, e);
        onResult.sendToTarget();
    }

    @Override
    public void onCompleted() {
        Message onResult = Message.obtain(handler, rxOnCompleted);
        onResult.sendToTarget();
    }
}
