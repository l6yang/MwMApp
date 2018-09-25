package com.mwm.loyal.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

import com.mwm.loyal.impl.OperaOnClickListener;

public class OperateDialog {

    private AlertDialog.Builder mBuilder;
    private OperaOnClickListener listener;

    OperateDialog(@NonNull Context context, OperaOnClickListener listener) {
        mBuilder = new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("")
                .setMessage("")
                .setPositiveButton("同意", mClickListener)
                .setNegativeButton("取消", mClickListener);
        this.listener = listener;
    }

    @NonNull
    public OperateDialog setTitle(@NonNull String title) {
        mBuilder.setTitle(title);
        return this;
    }

    @NonNull
    public OperateDialog setTitle(@StringRes int title) {
        mBuilder.setTitle(title);
        return this;
    }

    @NonNull
    public OperateDialog setMessage(@NonNull String message) {
        mBuilder.setMessage(message);
        return this;
    }

    @NonNull
    public OperateDialog setMessage(@StringRes int message) {
        mBuilder.setMessage(message);
        return this;
    }

    @NonNull
    public OperateDialog setNegativeButton(@NonNull String text, @Nullable DialogInterface.OnClickListener
            negativeListener) {
        mBuilder.setNegativeButton(text, negativeListener);
        return this;
    }

    @NonNull
    public OperateDialog setNegativeButton(@StringRes int text, @Nullable DialogInterface.OnClickListener
            negativeListener) {
        mBuilder.setNegativeButton(text, negativeListener);
        return this;
    }

    @NonNull
    public OperateDialog setPositiveButton(@NonNull String text) {
        mBuilder.setPositiveButton(text, mClickListener);
        return this;
    }

    @NonNull
    public OperateDialog setPositiveButton(@StringRes int text) {
        mBuilder.setPositiveButton(text, mClickListener);
        return this;
    }

    public void show() {
        mBuilder.show();
    }

    private DialogInterface.OnClickListener mClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_NEGATIVE:
                    listener.dialogCancel();
                    break;
                case DialogInterface.BUTTON_POSITIVE:
                    listener.goNext();
                    break;
            }
        }
    };
}
