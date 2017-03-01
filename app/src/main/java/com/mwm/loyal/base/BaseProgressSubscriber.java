package com.mwm.loyal.base;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mwm.loyal.imp.Progress;
import com.mwm.loyal.widget.DialogHandler;

import rx.Subscriber;

import static com.mwm.loyal.imp.Progress.ProgressCancelListener;

public class BaseProgressSubscriber<T> extends Subscriber<T> implements ProgressCancelListener {

    private DialogHandler dialogHandler;
    private boolean showDialog = true;
    private Progress.SubscribeListener<T> subscribeListener;

    public BaseProgressSubscriber(Context context) {
        dialogHandler = new DialogHandler(context, this);
        setShowDialog(false);
        setDialogMessage(null);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    public BaseProgressSubscriber(Context context, String message) {
        dialogHandler = new DialogHandler(context, this);
        setShowDialog(false);
        setDialogMessage(message);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }

    /**
     * @param listener 接收返回值
     */
    public void setSubscribeListener(Progress.SubscribeListener<T> listener) {
        subscribeListener = listener;
    }

    /**
     * @param context Context
     * @param message dialog提示
     * @param params  0:是否显示dialog，1:setCancelable，2:setCanceledOnTouchOutside
     */
    public BaseProgressSubscriber(Context context, String message, @NonNull boolean... params) {
        dialogHandler = new DialogHandler(context, this);
        setDialogMessage(message);
        showDialog = (params[0]);
        setCancelable(params.length >= 2 && params[1]);
        setCanceledOnTouchOutside(params.length >= 3 && params[2]);
    }

    public void setDialogMessage(CharSequence sequence) {
        if (dialogHandler != null) {
            dialogHandler.setDialogMessage(sequence);
        }
    }

    public void setCancelable(boolean flag) {
        if (dialogHandler != null)
            dialogHandler.setCancelable(flag);
    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        if (dialogHandler != null)
            dialogHandler.setCanceledOnTouchOutside(cancel);
    }

    private void showDialog() {
        if (dialogHandler != null) {
            dialogHandler.obtainMessage(DialogHandler.dialog_show).sendToTarget();
        }
    }

    private void dismissDialog() {
        if (dialogHandler != null) {
            dialogHandler.obtainMessage(DialogHandler.dialog_dismiss).sendToTarget();
            dialogHandler = null;
        }
    }

    @Override
    public void onStart() {
        if (showDialog)
            showDialog();
    }

    @Override
    public void onCompleted() {
        dismissDialog();
        if (subscribeListener != null)
            subscribeListener.onCompleted();
    }

    @Override
    public void onNext(T result) {
        if (subscribeListener != null)
            subscribeListener.onResult(result);
    }

    @Override
    public void onError(Throwable e) {
        if (subscribeListener != null)
            subscribeListener.onError(e);
        dismissDialog();
    }

    @Override
    public void onCancelProgress() {
        if (!isUnsubscribed()) {
            unsubscribe();
        }
    }
}