package com.mwm.loyal.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.loyal.kit.DeviceUtil;
import com.loyal.rx.RxUtil;
import com.loyal.rx.impl.RxSubscriberListener;
import com.mwm.loyal.impl.IContactImpl;
import com.mwm.loyal.libs.rxjava.RxProgressSubscriber;


public class CheckUpdateService extends IntentService implements IContactImpl, RxSubscriberListener<String> {

    public CheckUpdateService() {
        super("CheckUpdateService");
    }

    //还需传入apkUrl
    public static void startAction(Context context) {
        Intent intent = new Intent(context, CheckUpdateService.class);
        intent.setAction(StrImpl.actionUpdate);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (StrImpl.actionUpdate.equals(action)) {
                handleActionUpdate();
            } else stopSelf();
        }
    }

    private void handleActionUpdate() {
        RxProgressSubscriber<String> subscriber = new RxProgressSubscriber<>(this);
        subscriber.showProgressDialog(false);
        subscriber.setSubscribeListener(this);
        RxUtil.rxExecute(subscriber.checkUpdate(DeviceUtil.apkVersion(this)), subscriber);
    }

    @Override
    public void onResult(int what, Object tag, String result) {
        try {
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            onError(what, tag, e);
        }
    }

    @Override
    public void onError(int what, Object tag, Throwable e) {
        System.out.println(e);
    }
}