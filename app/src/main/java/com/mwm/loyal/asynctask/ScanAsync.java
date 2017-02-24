package com.mwm.loyal.asynctask;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

import com.mwm.loyal.activity.MainActivity;
import com.mwm.loyal.beans.ContactBean;
import com.mwm.loyal.imp.Contact;
import com.mwm.loyal.utils.RetrofitManage;

import java.io.IOException;

import retrofit2.Call;

public class ScanAsync extends AsyncTask<Void, Void, String> implements DialogInterface.OnCancelListener, Contact {
    private final ContactBean contactBean;
    private ProgressDialog mDialog;
    private final MainActivity mainActivity;
    private Handler handler;

    public ScanAsync(MainActivity activity, ContactBean bean, Handler handler) {
        mainActivity = activity;
        this.contactBean = bean;
        this.handler = handler;
        mDialog = new ProgressDialog(activity);
        mDialog.setMessage("处理中...");
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.alpha = 0.7f;// 透明度
            lp.dimAmount = 0.8f;// 黑暗度
            window.setAttributes(lp);
        }
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setOnCancelListener(this);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog.show();
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            Call<String> call = RetrofitManage.getInstance().getRequestServer().doScan(contactBean.toString(), mainActivity.getPackageName());
            return RetrofitManage.doExecuteStr(call);
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        mDialog.dismiss();
        Message msg2Null = Message.obtain(handler, Int.async2Null);
        msg2Null.sendToTarget();
        System.out.println(result);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Message msg2Null = Message.obtain(handler, Int.async2Null);
        msg2Null.sendToTarget();
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        ScanAsync.this.cancel(true);
    }
}
