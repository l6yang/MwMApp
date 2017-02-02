package com.mwm.loyal.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

import com.mwm.loyal.imp.Progress.ProgressCancelListener;

public class DialogHandler extends Handler implements DialogInterface.OnCancelListener {
    public static final int dialog_show = -5;
    public static final int dialog_dismiss = -6;
    private ProgressDialog progressDialog;
    private Context context;
    private ProgressCancelListener cancelListener;

    public DialogHandler(Context context, ProgressCancelListener mProgressCancelListener) {
        this.context = context;
        initDialog();
        this.cancelListener = mProgressCancelListener;
    }

    private void initDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setOnCancelListener(this);
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        }
    }

    public void setDialogTitle(CharSequence title) {
        if (progressDialog != null)
            progressDialog.setTitle(title);
    }

    public void setDialogTitle(int resId) {
        if (progressDialog != null)
            progressDialog.setTitle(resId);
    }

    public void setCancelable(boolean flag) {
        if (progressDialog != null)
            progressDialog.setCancelable(flag);
    }

    public void setDialogMessage(CharSequence message) {
        if (progressDialog != null)
            progressDialog.setMessage(message);
    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        if (progressDialog != null)
            progressDialog.setCanceledOnTouchOutside(cancel);
    }

    private void dismissDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case dialog_show:
                initDialog();
                break;
            case dialog_dismiss:
                dismissDialog();
                break;
        }
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        if (cancelListener != null)
            cancelListener.onCancelProgress();
    }
}
