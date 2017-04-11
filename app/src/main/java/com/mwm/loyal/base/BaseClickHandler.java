package com.mwm.loyal.base;

import android.app.ProgressDialog;
import android.databinding.ViewDataBinding;
import android.support.annotation.StringRes;
import android.view.Window;
import android.view.WindowManager;

import com.mwm.loyal.imp.Contact;
import com.mwm.loyal.utils.StringUtil;
import com.mwm.loyal.utils.ToastUtil;

public abstract class BaseClickHandler<V extends ViewDataBinding> implements Contact {
    protected ProgressDialog progressDialog;
    protected BaseActivity activity;
    protected V binding;

    public BaseClickHandler(BaseActivity baseActivity) {
        this(baseActivity, null);
    }

    public BaseClickHandler(BaseActivity baseActivity, V binding) {
        this.activity = baseActivity;
        this.binding = binding;
        initDialog(baseActivity);
    }

    public final String getString(@StringRes int resId) {
        return activity.getResources().getString(resId);
    }

    public final void showToast(@StringRes int resId) {
        ToastUtil.showToast(activity, resId);
    }

    public final void showToast(CharSequence sequence) {
        ToastUtil.showToast(activity, getStrWithNull(sequence));
    }

    public final void showToast(String text) {
        ToastUtil.showToast(activity, getStrWithNull(text));
    }

    public final void showErrorDialog(String text, boolean finish) {
        StringUtil.showErrorDialog(activity, getStrWithNull(text), finish);
    }

    public final void showDialog(String text, boolean finish) {
        ToastUtil.showDialog(activity, getStrWithNull(text), finish);
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
