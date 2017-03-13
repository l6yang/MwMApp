package com.mwm.loyal.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.mwm.loyal.R;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.imp.Contact;
import com.mwm.loyal.libs.network.DownLoadAPI;
import com.mwm.loyal.libs.network.DownLoadBean;
import com.mwm.loyal.libs.network.imp.DownLoadListener;
import com.mwm.loyal.utils.ApkUtil;
import com.mwm.loyal.utils.FileUtil;
import com.mwm.loyal.utils.RetrofitManage;
import com.mwm.loyal.utils.StringUtil;

import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UpdateService extends IntentService implements Contact {
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
            if (Str.ACTION_UPDATE.equals(action)) {
                handleActionUpdate();
            } else if (Str.ACTION_DOWN.equals(action)) {
                String apkUrl = intent.getStringExtra("apkUrl");
                handleActionDownLoad(apkUrl);
            } else stopSelf();
        }
    }

    private void handleActionUpdate() {
        Observable<ResultBean> observable = RetrofitManage.getInstance().getObservableServer().doApkVer(ApkUtil.getApkVersion(this));
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResultBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(ResultBean resultBean) {
                        if (resultBean != null) {
                            if (resultBean.getResultCode() == 1) {
                                String url = StringUtil.replaceNull(resultBean.getExceptMsg());
                                //发送广播，showPopWindowForDownLoad
                                Intent intent = new Intent();
                                intent.setAction(Str.action_apkVerCheck);
                                intent.putExtra("apkUrl", url);
                                sendBroadcast(intent);
                            }
                        }
                    }
                });
    }

    private void handleActionDownLoad(final String apkUrl) {
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.mwm)
                .setContentTitle("正在下载" + getString(R.string.app_name))
                .setContentText("已下载0%")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.mwm))
                .setAutoCancel(true);
        manager.notify(NOTIFY_TAG, 0, builder.build());
        DownLoadListener listener = new DownLoadListener() {
            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                //不频繁发送通知，防止通知栏下拉卡顿
                int progress = (int) (bytesRead * 100f / contentLength);
                if ((downloadCount == 0) || progress > downloadCount) {
                    DownLoadBean download = new DownLoadBean();
                    download.setTotalFileSize(contentLength);
                    download.setCurrentFileSize(bytesRead);
                    download.setProgress(progress);
                    showNotify(download);
                }
            }
        };
        if (!TextUtils.isEmpty(apkUrl) && apkUrl.endsWith(".apk")) {
            final File file = new File(FileUtil.path_apk, FileUtil.apkFileName);
            Observable<ResponseBody> observable = DownLoadAPI.getInstance(listener).getDownService().downLoad(apkUrl);
            DownLoadAPI.saveFile(observable, file, new Subscriber<InputStream>() {
                @Override
                public void onCompleted() {
                    downloadCompleted(file.getPath());
                }

                @Override
                public void onError(Throwable e) {
                    downloadCompleted(e.toString());
                }

                @Override
                public void onNext(InputStream inputStream) {

                }
            });
        }
    }

    private void downloadCompleted(String path) {
        DownLoadBean download = new DownLoadBean();
        download.setProgress(100);
        //sendIntent(download);
        manager.cancel(NOTIFY_TAG, 0);
        builder.setProgress(0, 0, false);
        manager.notify(NOTIFY_TAG, 0, builder.build());
        doInstallApk(path);
    }

    private void doInstallApk(String path) {
        if (!StringUtil.showErrorToast(UpdateService.this, path)) {
            if (!TextUtils.isEmpty(path)) {
                ApkUtil.install(UpdateService.this, new File(path));
            }
        }
    }

    private void showNotify(DownLoadBean loadBean) {
        //sendIntent(loadBean);
        int progress = loadBean.getProgress();
        builder.setProgress(100, progress, false).
                setContentText(getDataSize(loadBean.getCurrentFileSize())
                        + "/" +
                        getDataSize(loadBean.getTotalFileSize()));
        manager.notify(NOTIFY_TAG, 0, builder.build());
    }

    /**
     * 界面和通知栏进度同步
     */
    private void sendIntent(DownLoadBean download) {
        Intent intent = new Intent("MESSAGE_PROGRESS");
        intent.putExtra("downLoad", download);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

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