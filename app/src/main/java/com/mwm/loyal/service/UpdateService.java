package com.mwm.loyal.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.mwm.loyal.R;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.imp.Contact;
import com.mwm.loyal.utils.ApkUtil;
import com.mwm.loyal.utils.FileUtil;
import com.mwm.loyal.utils.IOUtil;
import com.mwm.loyal.utils.RetrofitManage;
import com.mwm.loyal.utils.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UpdateService extends IntentService implements Contact {
    private static final String NOTIFY_TAG = "Update";

    public UpdateService() {
        super("UpdateService");
    }

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
        RetrofitManage.ObservableServer server = RetrofitManage.getInstance().getObservableServer();
        server.doApkVer(ApkUtil.getApkVersion(this))
                .subscribeOn(Schedulers.newThread())
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
                        if (resultBean != null && resultBean.getResultCode() == 1) {
                            String url = StringUtil.replaceNull(resultBean.getExceptMsg());
                            //发送广播，showPopWindowForDownLoad
                            Intent intent = new Intent();
                            intent.setAction(Str.action_apkVerCheck);
                            intent.putExtra("apkUrl", url);
                            sendBroadcast(intent);
                        }
                    }
                });
    }

    private Notification.Builder initNotifyBuild() {
        Notification.Builder mBuilder = new Notification.Builder(UpdateService.this);
        mBuilder.setDefaults(Notification.FLAG_ONLY_ALERT_ONCE)
                .setSmallIcon(R.drawable.mwm)
                .setContentTitle("正在下载" + getString(R.string.app_name))
                .setContentText("已下载0%")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.mwm))
                .setAutoCancel(true);
        mBuilder.setProgress(0, 0, false);
        return mBuilder;
    }

    private void handleActionDownLoad(final String apkUrl) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = initNotifyBuild();
        if (!TextUtils.isEmpty(apkUrl) && apkUrl.endsWith(".apk")) {
            File file = new File(FileUtil.path_apk, FileUtil.apkFileName);
            InputStream in = null;
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                Call<ResponseBody> call = RetrofitManage.getInstance().getRequestServer().doDownLoadApk(apkUrl);
                ResponseBody responseBody = RetrofitManage.doExecute(call);
                in = responseBody.byteStream();
                long total = responseBody.contentLength();
                int len, current = 0;
                byte[] buff = new byte[1024];
                while ((len = in.read(buff)) != -1) {
                    out.write(buff, 0, len);
                    current += len;
                    //100f必须是Float类型的，不然下载大于58左右后会出现负数
                    int str1 = (int) ((current * 100f / total));
                    showNotify(manager, builder, str1);
                }
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtil.closeStream(in);
                IOUtil.closeStream(out);
            }
            doInstallApk(file.getPath());
        }
    }

    private void doInstallApk(String path) {
        if (!StringUtil.showErrorToast(UpdateService.this, path)) {
            if (!TextUtils.isEmpty(path)) {
                ApkUtil.install(UpdateService.this, new File(path));
            }
        }
    }

    private void showNotify(NotificationManager manager, Notification.Builder builder, int progress) {
        if (progress < 100) {
            builder.setProgress(100, progress, false)
                    .setContentText("已下载" + progress + "%");
            manager.notify(NOTIFY_TAG, 0, builder.build());
        } else {
            manager.cancel(NOTIFY_TAG, 0);
        }
    }
}