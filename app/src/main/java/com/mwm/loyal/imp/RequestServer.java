package com.mwm.loyal.imp;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

import static com.mwm.loyal.imp.Contact.Str.*;

public interface RequestServer {

    @FormUrlEncoded
    @POST(action + method_register)
    Call<String> doRegister(@Field("json_register") String json);

    @FormUrlEncoded
    @POST(action + method_login)
    Call<String> doLogin(@Field("json_login") String json);

    @FormUrlEncoded
    @Streaming
    @POST(action + method_showIcon)
    Call<ResponseBody> doShowIcon(@Field("account") String account);

    @FormUrlEncoded
    @POST(action + method_update)
    Call<String> doUpdateAccount(@Field("json_update") String json, @Field("update_state") String state, @Field("old_data") String old);

    @FormUrlEncoded
    @POST(action + method_account_locked)
    Call<String> doAccountLocked(@Field("account") String account);

    @Multipart
    @POST(action + method_update_icon)
    Call<String> doUpdateIcon(@Part("description") RequestBody description, @Part MultipartBody.Part iconFile);

    @FormUrlEncoded
    @POST(action + method_feedBack)
    Call<String> doFeedBack(@Field("json_feed") String json);

    @FormUrlEncoded
    @POST(action + method_ucrop_test)
    Call<String> doUCropTest(@Field("account") String account, @Field("password") String password);

    @FormUrlEncoded
    @POST(action + method_scan)
    Call<String> doScan(@Field("json_scan") String json, @Field("param") String param);

    @FormUrlEncoded
    @POST(action + method_apkVerCheck)
    Call<String> doApkVer(@Field("apkVer") String apkVer);

    @GET
    Call<ResponseBody> doDownLoadApk(@Url String url);

    @GET
    Call<String> getWeather(@Url String url);
}