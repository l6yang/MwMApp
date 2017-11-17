package com.mwm.loyal.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Spinner;

import com.mwm.loyal.impl.IntentFrame;
import com.mwm.loyal.impl.UIInterface;
import com.mwm.loyal.utils.IntentUtil;
import com.mwm.loyal.utils.StringUtil;
import com.mwm.loyal.utils.TimeUtil;
import com.mwm.loyal.utils.ToastUtil;

public abstract class BaseClickHandler<V extends ViewDataBinding> implements IntentFrame, UIInterface {
    protected ProgressDialog progressDialog;
    protected BaseActivity activity;
    protected V binding;
    protected IntentUtil builder;

    public BaseClickHandler(BaseActivity baseActivity) {
        this(baseActivity, null);
    }

    public BaseClickHandler(BaseActivity baseActivity, V binding) {
        this.activity = baseActivity;
        this.binding = binding;
        initDialog(baseActivity);
        hasIntentParams(false);
    }

    public final String getString(@StringRes int resId) {
        return activity.getString(resId);
    }

    @Override
    public void showToast(@NonNull String text) {
        ToastUtil.showToast(activity, text);
    }

    @Override
    public void showToast(@StringRes int resId) {
        showToast(getString(resId));
    }

    @Override
    public void showDialog(@NonNull String text) {
        showDialog(text, false);
    }

    @Override
    public void showDialog(@NonNull String text, boolean finish) {
        ToastUtil.showDialog(activity, replaceNull(text), finish);
    }

    @Override
    public String replaceNull(CharSequence sequence) {
        return StringUtil.replaceNull(sequence);
    }

    @Override
    public String subEndTime(@NonNull String t) {
        return TimeUtil.subEndTime(t);
    }

    @Override
    public String encodeStr2Utf(@NonNull String string) {
        return StringUtil.encodeStr2Utf(string);
    }

    @Override
    public String decodeStr2Utf(@NonNull String string) {
        return StringUtil.decodeStr2Utf(string);
    }

    @Override
    public String getSpinSelectStr(Spinner spinner, @NonNull String key) {
        return StringUtil.getSpinSelectStr(spinner, key);
    }

    @Override
    public void showErrorDialog(@NonNull String text) {
        showErrorDialog(text, false);
    }

    @Override
    public void showErrorDialog(@NonNull String text, boolean finish) {
        showErrorDialog(text, null, finish);
    }

    @Override
    public void showErrorDialog(@NonNull String text, Throwable e) {
        showErrorDialog(text, e, false);
    }

    @Override
    public void showErrorDialog(@NonNull String text, Throwable e, boolean finish) {
        ToastUtil.showDialog(activity, replaceNull(text) + (null == e ? "" : e.getMessage()), finish);
    }

    protected void hasIntentParams(boolean hasParam) {
        builder = null;
        if (hasParam)
            builder = new IntentUtil(activity, activity.getIntent());
        else builder = new IntentUtil(activity);
    }

    @Override
    public void startActivity(@Nullable Class<?> tClass) {
        builder.startActivity(tClass);
    }

    @Override
    public void startActivityForResult(@Nullable Class<?> tClass, @IntRange(from = 2) int reqCode) {
        builder.startActivityForResult(tClass, reqCode);
    }

    @Override
    public void startService(@Nullable Class<?> tClass) {
        builder.startService(tClass);
    }

    public final void finish() {
        activity.finish();
    }

    public final void setResult(int resultCode) {
        activity.setResult(resultCode);
    }

    public final void setResult(int resultCode, Intent intent) {
        activity.setResult(resultCode, intent);
    }

    protected final String replaceNull(Object object) {
        return StringUtil.replaceNull(object);
    }

    private void initDialog(BaseActivity baseActivity) {
        progressDialog = new ProgressDialog(baseActivity);
        progressDialog.setMessage("处理中...");
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
}
