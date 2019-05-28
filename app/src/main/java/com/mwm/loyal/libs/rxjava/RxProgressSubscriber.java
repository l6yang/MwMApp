package com.mwm.loyal.libs.rxjava;

import android.content.Context;

import com.loyal.rx.BaseRxServerSubscriber;
import com.loyal.rx.RetrofitManage;
import com.mwm.loyal.beans.WeatherBean;
import com.mwm.loyal.impl.ServerImpl;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class RxProgressSubscriber<T> extends BaseRxServerSubscriber<T> implements ObservableServer {

    private ObservableServer server;

    public RxProgressSubscriber(Context context) {
        this(context, ServerImpl.IPADD_DEFAULT);
    }

    public RxProgressSubscriber(Context context, String ipAdd) {
        super(context, ipAdd);
    }

    @Override
    public String defaultPort() {
        return ServerImpl.PORT_DEFAULT;
    }

    @Override
    public void createServer(RetrofitManage retrofitManage) {
        server = retrofitManage.createServer(ObservableServer.class);
    }

    @Override
    public String serverNameSpace() {
        return ServerImpl.NAMESPACE;
    }

    @Override
    public Observable<String> loginByParams(String account, String password) {
        return server.loginByParams(account, password);
    }

    @Override
    public Observable<String> register(String json) {
        return server.register(json);
    }

    @Override
    public Observable<String> queryAccount(String account) {
        return server.queryAccount(account);
    }

    @Override
    public Observable<String> loginByJson(String json) {
        return server.loginByJson(json);
    }

    @Override
    public Observable<String> accountDestroy(String json) {
        return server.accountDestroy(json);
    }

    @Override
    public Observable<ResponseBody> showAvatar(String account) {
        return server.showAvatar(account);
    }

    @Override
    public Observable<String> accountUpdate(String json) {
        return server.accountUpdate(json);
    }

    @Override
    public Observable<String> passwordUpdate(String json) {
        return server.passwordUpdate(json);
    }

    @Override
    public Observable<String> accountLock(String account) {
        return server.accountLock(account);
    }

    @Override
    public Observable<String> updateAvatar(RequestBody description, MultipartBody.Part iconFile) {
        return server.updateAvatar(description, iconFile);
    }

    @Override
    public Observable<String> feedback(String json) {
        return server.feedback(json);
    }

    @Override
    public Observable<String> deleteFeedback(String json) {
        return server.deleteFeedback(json);
    }

    @Override
    public Observable<String> getFeedback(String account) {
        return server.getFeedback(account);
    }

    @Override
    public Observable<String> scan(String json, String param) {
        return server.scan(json, param);
    }

    @Override
    public Observable<String> checkUpdate(String version) {
        return server.checkUpdate(version);
    }

    @Override
    public Observable<WeatherBean> getWeather(String url) {
        return server.getWeather(url);
    }

    @Override
    public Observable<ResponseBody> downloadImage(String url) {
        return server.downloadImage(url);
    }
}