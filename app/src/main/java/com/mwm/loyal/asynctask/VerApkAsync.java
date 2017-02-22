package com.mwm.loyal.asynctask;

import android.content.Context;

import com.mwm.loyal.base.BaseAsyncTask;
import com.mwm.loyal.imp.Contact;
import com.mwm.loyal.utils.ApkUtil;
import com.mwm.loyal.utils.RetrofitManage;

import java.io.IOException;

import retrofit2.Call;

public final class VerApkAsync extends BaseAsyncTask<Void, Void, String> implements Contact {
    private final String apkVersion;

    public VerApkAsync(Context context) {
        super(context);
        apkVersion = ApkUtil.getApkVersion(context);
        setDialogMessage("更新中...");
        setCanceledOnTouchOutside(false);
        setCancelable(true);
    }

    @Override
    protected String doInBackground(Void... voids) {
        Call<String> call = RetrofitManage.getInstance().getRequestServer().doApkVer(apkVersion);
        try {
            /*RequestBody body = new FormBody.Builder()
                    .add("json_ver", apkVersion)
                    .build();*/
            //return OkHttpClientManager.getInstance().post_jsonDemo(StringUtil.getServiceUrl(Str.action_apkVer), body);
            return RetrofitManage.doExecuteStr(call);
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        cancelDialog();
        System.out.println(result);
        // Observable.from(result)
    }

}