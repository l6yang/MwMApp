package com.mwm.loyal.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.mwm.loyal.base.RxProgressSubscriber;
import com.mwm.loyal.impl.Contact;
import com.mwm.loyal.impl.SubscribeListener;
import com.mwm.loyal.utils.FileUtil;
import com.mwm.loyal.utils.ImageUtil;
import com.mwm.loyal.utils.RxUtil;

import java.io.File;

import okhttp3.ResponseBody;

/**
 * 缓存照片
 */
public class ImageService extends IntentService implements Contact, SubscribeListener<ResponseBody> {

    public ImageService() {
        super("ImageService");
    }

    public static void startAction(Context context, Intent intent) {
        intent.setClass(context, ImageService.class);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            System.out.println("onHandleIntent::" + action);
            if (Str.actionDownload.equals(action)) {
                handleAction(intent);
            } else {
                stopSelf();
            }
        }
    }

    private void handleAction(Intent intent) {
        System.out.println("");
        if (null == intent)
            return;
        String account = intent.getStringExtra("account");
        RxProgressSubscriber<ResponseBody> subscriber = new RxProgressSubscriber<>(this, this);
        subscriber.setShowDialog(false).setTag(account);
        String url = Str.getServerUrl(Str.method_showIcon) + "&account=" + account;
        RxUtil.rxExecutedByIO(subscriber.downloadImage(url), subscriber);
    }

    @Override
    public void onResult(int what, Object tag, ResponseBody responseBody) {
        try {
            File iconFile = new File(FileUtil.path_icon, "icon_" + tag + ".jpg");
            FileUtil.deleteFile(iconFile);
            String save = ImageUtil.saveToFile(iconFile, responseBody.byteStream());
            System.out.println(TextUtils.isEmpty(save) ? "下载失败" : "保存成功");
            onError(what, tag, null);
        } catch (Exception e) {
            System.out.println("imageService::Error--" + e);
            onError(what, tag, e);
        }
    }

    @Override
    public void onError(int what, Object tag, Throwable e) {
        startAction(this, new Intent());
    }
}
