package com.mwm.loyal.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.mwm.loyal.utils.PingUtil;

public class PingService extends Service {
    public PingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PingUtil.isIpReachable("");
        return super.onStartCommand(intent, flags, startId);
    }
}
