package com.mwm.loyal.libs.rxjava;

import com.mwm.loyal.beans.WeatherBean;

import io.reactivex.Observable;
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

import static com.mwm.loyal.impl.ServerImpl.ACTION;

public interface ObservableServer {
    @FormUrlEncoded
    @POST(ACTION + "register")
    Observable<String> register(@Field("beanJson") String json);

    @FormUrlEncoded
    @POST(ACTION + "loginByParams")
    Observable<String> loginByParams(@Field("account") String account, @Field("password") String password);

    @FormUrlEncoded
    @POST(ACTION + "loginByJson")
    Observable<String> loginByJson(@Field("beanJson") String json);

    @FormUrlEncoded
    @POST(ACTION + "queryAccount")
    Observable<String> queryAccount(@Field("account") String account);

    @FormUrlEncoded
    @POST(ACTION + "accountDestroy")
    Observable<String> accountDestroy(@Field("beanJson") String json);

    @FormUrlEncoded
    @Streaming
    @POST(ACTION + "showAvatar")
    Observable<ResponseBody> showAvatar(@Field("account") String account);

    @FormUrlEncoded
    @POST(ACTION + "accountUpdate")
    Observable<String> accountUpdate(@Field("beanJson") String json);

    @FormUrlEncoded
    @POST(ACTION + "passwordUpdate")
    Observable<String> passwordUpdate(@Field("beanJson") String json);

    @FormUrlEncoded
    @POST(ACTION + "accountLock")
    Observable<String> accountLock(@Field("account") String account);

    @Multipart
    @POST(ACTION + "updateAvatar")
    Observable<String> updateAvatar(@Part("description") RequestBody description, @Part MultipartBody.Part iconFile);

    @FormUrlEncoded
    @POST(ACTION + "feedback")
    Observable<String> feedback(@Field("beanJson") String json);

    @FormUrlEncoded
    @POST(ACTION + "getFeedback")
    Observable<String> getFeedback(@Field("account") String account);

    @FormUrlEncoded
    @POST(ACTION + "deleteFeedback")
    Observable<String> deleteFeedback(@Field("beanJson") String json);

    @FormUrlEncoded
    @POST(ACTION + "scan")
    Observable<String> scan(@Field("beanJson") String json, @Field("param") String param);

    @FormUrlEncoded
    @POST(ACTION + "checkUpdate")
    Observable<String> checkUpdate(@Field("version") String version);

    @Streaming
    @GET
    Observable<ResponseBody> downloadImage(@Url String url);

    @GET
    Observable<WeatherBean> getWeather(@Url String url);
}