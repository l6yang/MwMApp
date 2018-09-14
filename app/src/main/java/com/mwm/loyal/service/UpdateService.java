package com.mwm.loyal.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.mwm.loyal.R;
import com.mwm.loyal.impl.IContact;
import com.mwm.loyal.utils.ApkUtil;
import com.mwm.loyal.utils.StringUtil;

import java.io.File;
import java.text.DecimalFormat;

public class UpdateService extends IntentService implements IContact {
    private static final String NOTIFY_TAG = "Update";

    public UpdateService() {
        super("UpdateService");
    }

    private NotificationCompat.Builder builder;
    private NotificationManager manager;
    private int downloadCount = 0;

    //还需传入apkUrl
    public static void startActionUpdate(Context context, String action, String apkUrl) {
        Intent intent = new Intent(context, UpdateService.class);
        intent.setAction(action);
        intent.putExtra("apkUrl", apkUrl);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (StrImpl.actionUpdate.equals(action)) {
                handleActionUpdate();
            } else if (StrImpl.actionDownload.equals(action)) {
                String apkUrl = intent.getStringExtra("apkUrl");
                handleActionDownLoad(apkUrl);
            } else stopSelf();
        }
    }

    private void handleActionUpdate() {
    }

    private void handleActionDownLoad(final String apkUrl) {
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.mwm)
                .setContentTitle("正在下载" + getString(R.string.app_name))
                .setContentText("已下载0%")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.mwm))
                .setAutoCancel(true).setOngoing(true);
        manager.notify(NOTIFY_TAG, 0, builder.build());
    }

    private void downloadCompleted(boolean error, String path) {
    }

    private void doInstallApk(String path) {
        if (!StringUtil.showErrorToast(UpdateService.this, path)) {
            if (!TextUtils.isEmpty(path)) {
                ApkUtil.install(UpdateService.this, new File(path));
            }
        }
    }


    /**
     * 界面和通知栏进度同步
     */

    private String getDataSize(long var0) {
        DecimalFormat var2 = new DecimalFormat("###.00");
        return var0 < 1024L ? var0 + "bytes" : (var0 < 1048576L ? var2.format((double) ((float) var0 / 1024.0F))
                + "KB" : (var0 < 1073741824L ? var2.format((double) ((float) var0 / 1024.0F / 1024.0F))
                + "MB" : (var0 < 0L ? var2.format((double) ((float) var0 / 1024.0F / 1024.0F / 1024.0F))
                + "GB" : "error")));
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        manager.cancel(NOTIFY_TAG, 0);
    }
}