package com.mwm.loyal.impl;

import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.beans.WeatherBean;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

import static com.mwm.loyal.impl.Contact.Str.*;

public interface ObservableServer {

    @FormUrlEncoded
    @POST(action + "doLoginTest")
    Observable<String> doTestLogin(@Field("account") String account, @Field("password") String password);

    @FormUrlEncoded
    @POST(action + method_register)
    Observable<ResultBean> doRegister(@Field("json_register") String json);

    @FormUrlEncoded
    @POST(action + method_queryAccount)
    Observable<ResultBean> doQueryAccount(@Field("account") String account);

    @FormUrlEncoded
    @POST(action + method_login)
    Observable<ResultBean> doLogin(@Field("json_login") String json);

    @FormUrlEncoded
    @POST(action + method_destroyAccount)
    Observable<ResultBean> destroyAccount(@Field("json_destroy") String json);

    @FormUrlEncoded
    @Streaming
    @POST(action + method_showIcon)
    Observable<ResponseBody> doShowIcon(@Field("account") String account);

    @FormUrlEncoded
    @POST(action + method_update)
    Observable<ResultBean> doUpdateAccount(@Field("json_update") String json);

    @FormUrlEncoded
    @POST(action + method_account_locked)
    Observable<ResultBean> doAccountLocked(@Field("account") String account);

    @Multipart
    @POST(action + method_update_icon)
    Observable<String> doUpdateIcon(@Part("description") RequestBody description, @Part MultipartBody.Part iconFile);

    @FormUrlEncoded
    @POST(action + method_feedBack)
    Observable<ResultBean> doFeedBack(@Field("json_feed") String json);

    @FormUrlEncoded
    @POST(action + method_getSelfFeed)
    Observable<ResultBean> getSelfFeed(@Field("account") String account);

    @FormUrlEncoded
    @POST(action + method_deleteSelfFeed)
    Observable<ResultBean> deleteSelfFeed(@Field("json_delete") String json);

    @FormUrlEncoded
    @POST(action + method_ucrop_test)
    Observable<String> doUCropTest(@Field("account") String account, @Field("password") String password);

    @FormUrlEncoded
    @POST(action + method_scan)
    Observable<ResultBean> doScan(@Field("json_scan") String json, @Field("param") String param);

    @FormUrlEncoded
    @POST(action + method_apkVerCheck)
    Observable<ResultBean> doApkVer(@Field("apkVer") String apkVer);

    @Streaming
    @GET
    Observable<ResponseBody> doDownLoadApk(@Url String url);

    @Streaming
    @GET
    Observable<ResponseBody> downloadImage(@Url String url);

    @GET
    Observable<WeatherBean> getWeather(@Url String url);
}