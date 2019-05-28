package com.mwm.loyal.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.text.TextUtils;

import com.loyal.kit.OutUtil;
import com.loyal.rx.RxUtil;
import com.loyal.rx.impl.RxSubscriberListener;
import com.mwm.loyal.impl.ServerImpl;
import com.mwm.loyal.libs.rxjava.RxProgressSubscriber;
import com.mwm.loyal.utils.FileUtil;
import com.mwm.loyal.utils.ImageUtil;

import java.io.File;

import okhttp3.ResponseBody;

public class ImgDownloadService extends IntentService implements RxSubscriberListener<ResponseBody> {
    private static final String ACTION = "com.mwm.loyal.service.action.ImgDownload";

    public ImgDownloadService() {
        super("ImgDownloadService");
    }

    public static void startAction(Context context, String account) {
        Intent intent = new Intent(context, ImgDownloadService.class);
        intent.putExtra("account",account);
        intent.setAction(ACTION);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION.equals(action)) {
                final String account = intent.getStringExtra("account");
                handleAction(account);
            }
        }
    }

    private void handleAction(String account) {
        RxProgressSubscriber<ResponseBody> subscriber = new RxProgressSubscriber<>(this);
        subscriber.showProgressDialog(false).setTag(account);
        subscriber.setSubscribeListener(this);
        String url = ServerImpl.showAvatar(account);
        OutUtil.println("imgUrl", url);
        RxUtil.rxExecuteAndCompute(subscriber.downloadImage(url), subscriber);
    }

    @Override
    public void onResult(int what, Object tag, ResponseBody body) {
        File iconFile = new File(FileUtil.path_icon, "icon_" + tag + ".jpg");
        String save = ImageUtil.saveToFile(iconFile, body.byteStream());
        OutUtil.println(TextUtils.isEmpty(save) ? "下载失败" : "保存成功");
    }

    @Override
    public void onError(int what, Object tag, Throwable e) {
        OutUtil.println("onError-" + e);
    }
}
