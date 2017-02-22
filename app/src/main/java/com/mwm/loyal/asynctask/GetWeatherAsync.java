package com.mwm.loyal.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.Window;
import android.view.WindowManager;

import com.mwm.loyal.imp.Contact;

public class GetWeatherAsync extends AsyncTask<Void, Void, String> implements Contact, DialogInterface.OnCancelListener {
    private ProgressDialog progressDialog;

    GetWeatherAsync(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("正在定位中...");
        Window window = progressDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.alpha = 0.7f;// 透明度
            lp.dimAmount = 0.8f;// 黑暗度
            window.setAttributes(lp);
        }
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(this);
    }

    @Override
    protected String doInBackground(Void... params) {

        return null;
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }
}
