package com.mwm.loyal.base;

import android.content.Context;

import com.loyal.base.rxjava.BaseRxProgressSubscriber;
import com.loyal.base.rxjava.RetrofitManage;
import com.loyal.base.rxjava.impl.SubscribeListener;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.beans.WeatherBean;
import com.mwm.loyal.impl.ObservableServer;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.Part;
import retrofit2.http.Url;

public class RxProgressSubscriber<T> extends BaseRxProgressSubscriber<T> implements ObservableServer {

    private ObservableServer server;

    public RxProgressSubscriber(Context context, SubscribeListener<T> listener) {
        this(context, "");
        setSubscribeListener(listener);
    }

    public RxProgressSubscriber(Context context, String ipAdd) {
        super(context, ipAdd);
    }

    public RxProgressSubscriber(Context context, String ipAdd, int what, boolean showDialog) {
        super(context, ipAdd, what, showDialog);
    }

    @Override
    public void createServer(RetrofitManage retrofitManage) {
        server = retrofitManage.createServer(ObservableServer.class);
    }

    @Override
    public String serverNameSpace() {
        return "mwm";
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