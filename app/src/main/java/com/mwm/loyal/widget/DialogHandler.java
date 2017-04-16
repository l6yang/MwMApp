package com.mwm.loyal.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;

import com.mwm.loyal.imp.ProgressCancelListener;

public class DialogHandler extends Handler implements DialogInterface.OnCancelListener {
    private ProgressDialog progressDialog;
    private Context context;
    private ProgressCancelListener listener;

    private DialogHandler() {
    }

    public DialogHandler(Context context, ProgressCancelListener cancelListener) {
        this.context = context;
        initDialog();
        this.listener = cancelListener;
    }

    private void initDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setOnCancelListener(this);
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

    public void setDialogMessage(CharSequence message) {
        if (progressDialog != null)
            progressDialog.setMessage(message);
    }

    private void setTitle(int resId) {
        if (progressDialog != null)
            progressDialog.setTitle(resId);
    }

    private void setTitle(CharSequence title) {
        if (progressDialog != null)
            progressDialog.setTitle(title);
    }

    private void setCancelable(boolean flag) {
        if (progressDialog != null)
            progressDialog.setCancelable(flag);
    }

    private void setMessage(CharSequence message) {
        if (progressDialog != null)
            progressDialog.setMessage(message);
    }

    private void setCanceledOnTouchOutside(boolean cancel) {
        if (progressDialog != null)
            progressDialog.setCanceledOnTouchOutside(cancel);
    }

    private void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void showDialog() {
        if (null != progressDialog && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        if (listener != null)
            listener.onCancelProgress();
    }

    public static final class Builder {
        private Context mContext;
        private ProgressCancelListener listener;
        private DialogHandler handler;

        public Builder(Context context, ProgressCancelListener cancelListener) {
            this.mContext = context;
            this.listener = cancelListener;
            handler = new DialogHandler(mContext, listener);
        }

        public Builder setMessage(CharSequence sequence) {
            handler.setMessage(sequence);
            return this;
        }

        public Builder setTitle(CharSequence title) {
            handler.setTitle(title);
            return this;
        }

        public Builder setTitle(int resId) {
            handler.setTitle(resId);
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            handler.setCancelable(cancelable);
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean outsideCancel) {
            handler.setCanceledOnTouchOutside(outsideCancel);
            return this;
        }

        public DialogHandler getHandler() {
            return handler;
        }

        public void show() {
            handler.showDialog();
        }

        public void dismiss() {
            handler.dismissDialog();
        }
    }
}
