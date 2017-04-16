package com.mwm.loyal.utils;

import com.mwm.loyal.imp.Contact;
import com.mwm.loyal.imp.ObservableServer;
import com.mwm.loyal.imp.RequestServer;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RetrofitManage implements Contact {
    private static RetrofitManage mInstance;
    private static Retrofit.Builder retrofit;

    private Retrofit.Builder getBuild() {
        return new Retrofit.Builder()
                .baseUrl(Str.getBaseUrl())
                //增加返回值为String的支持
                .addConverterFactory(ScalarsConverterFactory.create())
                //增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(GsonConverterFactory.create())
                //增加返回值为Observable<T>的支持
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
    }

    private RetrofitManage() {
        retrofit = getBuild();
    }

    public static RetrofitManage getInstance() {
        if (mInstance == null) {
            synchronized (RetrofitManage.class) {
                if (mInstance == null) {
                    mInstance = new RetrofitManage();
                }
            }
        }
        return mInstance;
    }

    private Retrofit.Builder getRetrofit() {
        if (retrofit == null)
            RetrofitManage.getInstance();
        return retrofit;
    }

    public RequestServer getRequestServer() {
        return getRetrofit().build().create(RequestServer.class);
    }

    public ObservableServer getObservableServer() {
        return getRetrofit().build().create(ObservableServer.class);
    }

    /**
     * @param call 同步执行，返回自定义类
     */
    public static <T> T doExecute(Call<T> call) throws IOException {
        Response<T> response = call.execute();
        return response.body();
    }

    /**
     * @param call 同步执行，返回String
     */
    public static String doExecuteStr(Call<String> call) throws IOException {
        Response<String> response = call.execute();
        if (response.isSuccessful())
            return response.body();
        else throw new IOException("Unexpected code" + response);
    }

    public static <T> void rxExecuted(Observable<T> observable, Subscriber<T> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}
