package com.mwm.loyal.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Spinner;

import com.loyal.base.impl.IUIInterface;
import com.loyal.base.impl.IntentFrame;
import com.loyal.base.util.ConnectUtil;
import com.loyal.base.util.IntentUtil;
import com.loyal.base.util.ObjectUtil;
import com.loyal.base.util.TimeUtil;
import com.loyal.base.util.ToastUtil;
import com.mwm.loyal.impl.IContact;

public abstract class BaseClickHandler<V extends ViewDataBinding> implements IntentFrame.ActivityFrame, IUIInterface,IContact {
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

    protected void hasIntentParams(boolean hasParam) {
        builder = null;
        if (hasParam)
            builder = new IntentUtil(activity, activity.getIntent());
        else builder = new IntentUtil(activity);
    }

    public final String getString(@StringRes int resId) {
        return activity.getString(resId);
    }

    @Override
    public void startActivityByAct(@Nullable Class<?> tClass) {
        builder.startActivityByAct(tClass);
    }

    @Override
    public void startActivityForResultByAct(@Nullable Class<?> tClass, @IntRange(from = 2) int reqCode) {
        builder.startActivityForResultByAct(tClass, reqCode);
    }

    @Override
    public void startServiceByAct(@Nullable Class<?> tClass) {
        builder.startServiceByAct(tClass);
    }

    @Override
    public void showToast(@NonNull CharSequence sequence) {
        ToastUtil.showToast(activity, sequence);
    }

    @Override
    public void showToast(@StringRes int resId) {
        showToast(getString(resId));
    }

    @Override
    public void showErrorToast(int resId, Throwable e) {
        showErrorToast(getString(resId), e);
    }

    @Override
    public void showErrorToast(@NonNull CharSequence sequence, Throwable e) {
        String error = null == e ? "" : ConnectUtil.getError(e);
        showToast(replaceNull(sequence) + (TextUtils.isEmpty(error) ? "" : "\n" + error));
    }

    @Override
    public void showDialog(@NonNull CharSequence sequence) {
        showDialog(sequence, false);
    }

    @Override
    public void showDialog(@NonNull CharSequence sequence, boolean finish) {
        ToastUtil.showDialog(activity, replaceNull(sequence), finish);
    }

    @Override
    public String replaceNull(CharSequence sequence) {
        return Str.replaceNull(sequence);
    }

    @Override
    public String subEndTime(@NonNull CharSequence timeSequence) {
        return TimeUtil.subEndTime(timeSequence);
    }

    @Override
    public String encodeStr2Utf(@NonNull String string) {
        return Str.encodeStr2Utf(string);
    }

    @Override
    public String decodeStr2Utf(@NonNull String string) {
        return Str.decodeStr2Utf(string);
    }

    @Override
    public String getSpinSelectStr(Spinner spinner, @NonNull String methodName) {
        return (String) ObjectUtil.getMethodValue(spinner.getSelectedItem(), methodName);
    }

    @Override
    public void showErrorDialog(@NonNull CharSequence sequence) {
        showErrorDialog(sequence, false);
    }

    @Override
    public void showErrorDialog(@NonNull CharSequence sequence, boolean finish) {
        showErrorDialog(sequence, null, finish);
    }

    @Override
    public void showErrorDialog(@NonNull CharSequence sequence, Throwable e) {
        showErrorDialog(sequence, e, false);
    }

    @Override
    public void showErrorDialog(@NonNull CharSequence sequence, Throwable e, boolean finish) {
        String error = null == e ? "" : ConnectUtil.getError(e);
        showDialog(replaceNull(sequence) + (TextUtils.isEmpty(error) ? "" : "\n" + error), finish);
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
