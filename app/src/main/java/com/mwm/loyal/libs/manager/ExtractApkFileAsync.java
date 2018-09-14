package com.mwm.loyal.libs.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

import com.mwm.loyal.R;
import com.mwm.loyal.impl.IContact;
import com.mwm.loyal.utils.ToastUtil;

public class ExtractApkFileAsync extends AsyncTask<Void, String, Boolean> {
    private Context context;
    private ProgressDialog progressDialog;
    private AppBean appBean;
    private Handler mHandler;

    ExtractApkFileAsync(Context context, AppBean appBean, Handler mHandler) {
        this.context = context;
        this.appBean = appBean;
        this.mHandler = mHandler;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("正在提取中...");
        Window window = progressDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.alpha = 0.7f;// 透明度
            lp.dimAmount = 0.8f;// 黑暗度
            window.setAttributes(lp);
        }
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (null != progressDialog)
            progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return UtilsApp.copyFile(appBean);
    }

    @Override
    protected void onPostExecute(Boolean status) {
        super.onPostExecute(status);
        progressDialog.dismiss();
        Message message = Message.obtain(mHandler, IContact.IntImpl.async2Null);
        message.sendToTarget();
        if (status) {
            ToastUtil.showToast(context, String.format(context.getString(R.string.dialog_saved_description), appBean.getName(), UtilsApp.getAPKFilename(appBean)));
        } else {
            ToastUtil.showToast(context, context.getString(R.string.dialog_extract_fail) + "\n" + context.getString(R.string.dialog_extract_fail_description));
        }
    }
}