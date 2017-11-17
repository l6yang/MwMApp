package com.mwm.loyal.base;

import android.content.Context;
import android.support.annotation.IntRange;
import android.text.TextUtils;

import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.beans.WeatherBean;
import com.mwm.loyal.impl.ObservableServer;
import com.mwm.loyal.impl.ProgressCancelListener;
import com.mwm.loyal.impl.SubscribeListener;
import com.mwm.loyal.utils.RetrofitManage;
import com.mwm.loyal.widget.DialogHandler;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.Part;
import retrofit2.http.Url;
import rx.Observable;
import rx.Subscriber;

public class RxProgressSubscriber<T> extends Subscriber<T> implements ProgressCancelListener, ObservableServer {

    private DialogHandler.Builder builder;
    private SubscribeListener<T> subscribeListener;
    private int mWhat;
    private ObservableServer server = RetrofitManage.getInstance().getObservableServer();
    private Object tag;
    private boolean isShowDialog = true;//是否显示dialog

    public RxProgressSubscriber(Context context) {
        this(context, null);
    }

    public RxProgressSubscriber(Context context, SubscribeListener<T> listener) {
        this(context, 2, listener);
    }

    /**
     * @param what default=2
     */
    public RxProgressSubscriber(Context context, @IntRange(from = 2) int what, SubscribeListener<T> listener) {
        setSubscribeListener(listener);
        setWhat(what);
        initDialog(context);
    }

    public RxProgressSubscriber setWhat(@IntRange(from = 2) int what) {
        this.mWhat = what;
        return this;
    }

    public RxProgressSubscriber setSubscribeListener(SubscribeListener<T> listener) {
        this.subscribeListener = listener;
        return this;
    }

    public RxProgressSubscriber setShowDialog(boolean showDialog) {
        isShowDialog = showDialog;
        return this;
    }

    public RxProgressSubscriber setTag(Object tag) {
        this.tag = tag;
        return this;
    }

    private void initDialog(Context context) {
        builder = new DialogHandler.Builder(context, this);
        setMessage(null);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    public RxProgressSubscriber setMessage(CharSequence sequence) {
        if (builder != null) {
            builder.setMessage(sequence);
        }
        return this;
    }

    public RxProgressSubscriber setCancelable(boolean flag) {
        if (builder != null)
            builder.setCancelable(flag);
        return this;
    }

    public RxProgressSubscriber setCanceledOnTouchOutside(boolean cancel) {
        if (builder != null)
            builder.setCanceledOnTouchOutside(cancel);
        return this;
    }

    private void showDialog() {
        if (isShowDialog && builder != null)
            builder.show();
    }

    private void dismissDialog() {
        if (builder != null) {
            builder.dismiss();
            builder = null;
        }
    }

    public Object getTag() {
        return tag;
    }

    @Override
    public void onStart() {
        showDialog();
    }

    @Override
    public void onCompleted() {
        dismissDialog();
    }

    @Override
    public void onNext(T result) {
        if (subscribeListener != null)
            subscribeListener.onResult(mWhat, tag, result);
    }

    @Override
    public void onError(Throwable e) {
        dismissDialog();
        if (TextUtils.equals("已取消操作", null == e ? "" : e.getMessage())) {
            if (subscribeListener != null)
                subscribeListener.onError(mWhat, tag, e);
            subscribeListener = null;
        } else {
            if (subscribeListener != null)
                subscribeListener.onError(mWhat, tag, e);
        }
    }

    @Override
    public void onCancelProgress() {
        onError(new Exception("已取消操作"));
        onCompleted();
        if (!isUnsubscribed()) {
            unsubscribe();
        }
    }

    @Override
    public Observable<String> doTestLogin(@Field("account") String account, @Field("password") String password) {
        return server.doTestLogin(account, password);
    }

    @Override
    public Observable<ResultBean> doRegister(@Field("json_register") String json) {
        return server.doRegister(json);
    }

    @Override
    public Observable<ResultBean> doQueryAccount(@Field("account") String account) {
        return server.doQueryAccount(account);
    }

    @Override
    public Observable<ResultBean> doLogin(@Field("json_login") String json) {
        return server.doLogin(json);
    }

    @Override
    public Observable<ResultBean> destroyAccount(@Field("json_destroy") String json) {
        return server.destroyAccount(json);
    }

    @Override
    public Observable<ResponseBody> doShowIcon(@Field("account") String account) {
        return server.doShowIcon(account);
    }

    @Override
    public Observable<ResultBean> doUpdateAccount(@Field("json_update") String json) {
        return server.doUpdateAccount(json);
    }

    @Override
    public Observable<ResultBean> doAccountLocked(@Field("account") String account) {
        return server.doAccountLocked(account);
    }

    @Override
    public Observable<String> doUpdateIcon(@Part("description") RequestBody description, @Part MultipartBody.Part iconFile) {
        return server.doUpdateIcon(description, iconFile);
    }

    @Override
    public Observable<ResultBean> doFeedBack(@Field("json_feed") String json) {
        return server.doFeedBack(json);
    }

    @Override
    public Observable<ResultBean> deleteSelfFeed(@Field("json_delete") String json) {
        return server.deleteSelfFeed(json);
    }

    @Override
    public Observable<ResultBean> getSelfFeed(@Field("account") String account) {
        return server.getSelfFeed(account);
    }

    @Override
    public Observable<String> doUCropTest(@Field("account") String account, @Field("password") String password) {
        return server.doUCropTest(account, password);
    }

    @Override
    public Observable<ResultBean> doScan(@Field("json_scan") String json, @Field("param") String param) {
        return server.doScan(json, param);
    }

    @Override
    public Observable<ResultBean> doApkVer(@Field("apkVer") String apkVer) {
        return server.doApkVer(apkVer);
    }

    @Override
    public Observable<ResponseBody> doDownLoadApk(@Url String url) {
        return server.doDownLoadApk(url);
    }

    @Override
    public Observable<WeatherBean> getWeather(@Url String url) {
        return server.getWeather(url);
    }

    @Override
    public Observable<ResponseBody> downloadImage(String url) {
        return server.downloadImage(url);
    }
}