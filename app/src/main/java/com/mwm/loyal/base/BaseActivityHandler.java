package com.mwm.loyal.base;

import android.app.ProgressDialog;
import android.databinding.ViewDataBinding;
import android.support.annotation.StringRes;
import android.view.Window;
import android.view.WindowManager;

import com.mwm.loyal.imp.Contact;
import com.mwm.loyal.utils.StringUtil;
import com.mwm.loyal.utils.ToastUtil;

public class BaseActivityHandler<T> implements Contact {
    protected ProgressDialog progressDialog;
    private BaseActivity baseActivity;
    private ViewDataBinding binding;

    public BaseActivityHandler(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
        initDialog(baseActivity);
    }

    public BaseActivityHandler(BaseActivity baseActivity, ViewDataBinding binding) {
        this.baseActivity = baseActivity;
        this.binding = binding;
        initDialog(baseActivity);
    }

    public BaseActivity getActivity() {
        return baseActivity;
    }

    public ViewDataBinding getBinding() {
        return binding;
    }

    public final String getString(@StringRes int resId) {
        return baseActivity.getResources().getString(resId);
    }

    public final void showToast(@StringRes int resId) {
        ToastUtil.showToast(baseActivity, resId);
    }

    public final void showToast(CharSequence sequence) {
        ToastUtil.showToast(baseActivity, getStrWithNull(sequence));
    }

    public final void showToast(String text) {
        ToastUtil.showToast(baseActivity, getStrWithNull(text));
    }

    public final void showErrorDialog(String text, boolean finish) {
        StringUtil.showErrorDialog(baseActivity, getStrWithNull(text), finish);
    }

    public final void showDialog(String text, boolean finish) {
        ToastUtil.showDialog(baseActivity, getStrWithNull(text), finish);
    }

    protected final String getStrWithNull(Object object) {
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
