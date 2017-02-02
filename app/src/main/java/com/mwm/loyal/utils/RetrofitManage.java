package com.mwm.loyal.utils;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.mwm.loyal.imp.ResListener.Str.Server_BaseUrl;
import static com.mwm.loyal.imp.ResListener.Str.Server_Method;
import static com.mwm.loyal.imp.ResListener.Str.action_account_locked;
import static com.mwm.loyal.imp.ResListener.Str.action_apkVerCheck;
import static com.mwm.loyal.imp.ResListener.Str.action_feedBack;
import static com.mwm.loyal.imp.ResListener.Str.action_login;
import static com.mwm.loyal.imp.ResListener.Str.action_register;
import static com.mwm.loyal.imp.ResListener.Str.action_scan;
import static com.mwm.loyal.imp.ResListener.Str.action_showIcon;
import static com.mwm.loyal.imp.ResListener.Str.action_ucrop_test;
import static com.mwm.loyal.imp.ResListener.Str.action_update;
import static com.mwm.loyal.imp.ResListener.Str.action_update_icon;

public class RetrofitManage {
    private static RetrofitManage mInstance;
    private static Retrofit.Builder retrofit;

    private RetrofitManage() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Server_BaseUrl)
                //增加返回值为String的支持
                .addConverterFactory(ScalarsConverterFactory.create())
                //增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(GsonConverterFactory.create())
                //增加返回值为Observable<T>的支持
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
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

    public static <T> void doEnqueueStr(Observable<T> observable, Subscriber<T> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public interface RequestServer {

        @FormUrlEncoded
        @POST(Server_Method + "doLoginTest")
        Call<String> doTestLogin(@Field("account") String account, @Field("password") String password);

        @FormUrlEncoded
        @POST(Server_Method + action_register)
        Call<String> doRegister(@Field("json_register") String json);

        @FormUrlEncoded
        @POST(Server_Method + action_login)
        Call<String> doLogin(@Field("json_login") String json);

        @FormUrlEncoded
        @Streaming
        @POST(Server_Method + action_showIcon)
        Call<ResponseBody> doShowIcon(@Field("account") String account);

        @FormUrlEncoded
        @POST(Server_Method + action_update)
        Call<String> doUpdateAccount(@Field("json_update") String json, @Field("update_state") String state, @Field("old_data") String old);

        @FormUrlEncoded
        @POST(Server_Method + action_account_locked)
        Call<String> doAccountLocked(@Field("account") String account);

        @Multipart
        @POST(Server_Method + action_update_icon)
        Call<String> doUpdateIcon(@Part("description") RequestBody description, @Part MultipartBody.Part iconFile);

        @FormUrlEncoded
        @POST(Server_Method + action_feedBack)
        Call<String> doFeedBack(@Field("json_feed") String json);

        @FormUrlEncoded
        @POST(Server_Method + action_ucrop_test)
        Call<String> doUCropTest(@Field("account") String account, @Field("password") String password);

        @FormUrlEncoded
        @POST(Server_Method + action_scan)
        Call<String> doScan(@Field("json_scan") String json, @Field("param") String param);

        @FormUrlEncoded
        @POST(Server_Method + action_apkVerCheck)
        Call<String> doApkVer(@Field("apkVer") String apkVer);

        @GET
        Call<ResponseBody> doDownLoadApk(@Url String url);
    }

    public interface ObservableServer {

        @FormUrlEncoded
        @POST(Server_Method + "doLoginTest")
        Observable<String> doTestLogin(@Field("account") String account, @Field("password") String password);

        @FormUrlEncoded
        @POST(Server_Method + action_register)
        Observable<String> doRegister(@Field("json_register") String json);

        @FormUrlEncoded
        @POST(Server_Method + action_login)
        Observable<String> doLogin(@Field("json_login") String json);

        @FormUrlEncoded
        @POST(Server_Method + action_showIcon)
        Observable<InputStream> doShowIcon(@Field("account") String account);

        @FormUrlEncoded
        @POST(Server_Method + action_update)
        Observable<String> doUpdateAccount(@Field("json_update") String json, @Field("update_state") String state, @Field("old_data") String old);

        @FormUrlEncoded
        @POST(Server_Method + action_account_locked)
        Observable<String> doAccountLocked(@Field("account") String account);

        @Multipart
        @POST(Server_Method + action_update_icon)
        Observable<String> doUpdateIcon(@Part("description") RequestBody description, @Part MultipartBody.Part iconFile);

        @FormUrlEncoded
        @POST(Server_Method + action_feedBack)
        Observable<String> doFeedBack(@Field("json_feed") String json);

        @FormUrlEncoded
        @POST(Server_Method + action_ucrop_test)
        Observable<String> doUCropTest(@Field("account") String account, @Field("password") String password);

        @FormUrlEncoded
        @POST(Server_Method + action_scan)
        Observable<String> doScanQuery(@Field("json_scan") String json);

        @FormUrlEncoded
        @POST(Server_Method + action_apkVerCheck)
        Observable<String> doApkVer(@Field("json_ver") String json);

        @GET
        Observable<ResponseBody> doDownLoadApk(@Url String url);
    }
}
