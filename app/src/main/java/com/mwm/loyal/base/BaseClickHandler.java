package com.mwm.loyal.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;

import com.loyal.base.impl.IUICommandImpl;
import com.loyal.base.impl.IntentFrame;
import com.loyal.kit.ConnectUtil;
import com.loyal.kit.IntentBuilder;
import com.loyal.kit.ObjectUtil;
import com.loyal.kit.TimeUtil;
import com.loyal.kit.ToastUtil;
import com.mwm.loyal.impl.IContactImpl;

public abstract class BaseClickHandler<V extends ViewDataBinding> implements IntentFrame.ActFrame, IUICommandImpl, IContactImpl {
    protected ProgressDialog progressDialog;
    protected BaseActivity activity;
    protected V binding;
    protected IntentBuilder intentBuilder;
    private ToastUtil toastUtil;

    public BaseClickHandler(BaseActivity baseActivity) {
        this(baseActivity, null);
    }

    public BaseClickHandler(BaseActivity baseActivity, V binding) {
        this.activity = baseActivity;
        this.binding = binding;
        toastUtil = new ToastUtil(baseActivity);
        initDialog(baseActivity);
        hasIntentParams(false);
    }

    protected void hasIntentParams(boolean hasParam) {
        intentBuilder = null;
        if (hasParam)
            intentBuilder = new IntentBuilder(activity, activity.getIntent());
        else intentBuilder = new IntentBuilder(activity);
    }

    public final String getString(@StringRes int resId) {
        return activity.getString(resId);
    }

    @Override
    public void hideKeyBoard(@NonNull View view) {
        InputMethodManager im = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (im != null)
            im.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void startServiceByAct(@Nullable String className) {
        intentBuilder.startServiceByAct(className);
    }

    @Override
    public void startActivityForResultByAct(@Nullable String className, int reqCode) {
        intentBuilder.startActivityForResultByAct(className, reqCode);
    }

    @Override
    public void startActivityByAct(@Nullable String className) {
        intentBuilder.startActivityByAct(className);
    }

    @Override
    public void startActivityByAct(@Nullable Class<?> tClass) {
        intentBuilder.startActivityByAct(tClass);
    }

    @Override
    public void startActivityForResultByAct(@Nullable Class<?> tClass, @IntRange(from = 2) int reqCode) {
        intentBuilder.startActivityForResultByAct(tClass, reqCode);
    }

    @Override
    public void startServiceByAct(@Nullable Class<?> tClass) {
        intentBuilder.startServiceByAct(tClass);
    }

    @Override
    public void showToast(@NonNull CharSequence sequence) {
        toastUtil.show(sequence);
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
        activity.showDialog(sequence, finish);
    }

    @Override
    public String replaceNull(CharSequence sequence) {
        return BaseStr.replaceNull(sequence);
    }

    @Override
    public String subEndTime(@NonNull CharSequence timeSequence) {
        return TimeUtil.subEndTime(timeSequence);
    }

    @Override
    public String encodeStr2Utf(@NonNull String string) {
        return BaseStr.encodeStr2Utf(string);
    }

    @Override
    public String decodeStr2Utf(@NonNull String string) {
        return BaseStr.decodeStr2Utf(string);
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
        if (null != toastUtil) {
            toastUtil.cancel();
            toastUtil = null;
        }
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

    protected void clearText(EditText editText) {
        editText.setText("");
    }

    protected String getText(AppCompatEditText editText) {
        Editable editable = editText.getText();
        return null == editable ? "" : editable.toString().trim();
    }
}
