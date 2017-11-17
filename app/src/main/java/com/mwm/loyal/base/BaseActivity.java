package com.mwm.loyal.base;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Spinner;

import com.mwm.loyal.R;
import com.mwm.loyal.impl.DialogClickListener;
import com.mwm.loyal.impl.UIInterface;
import com.mwm.loyal.service.UpdateService;
import com.mwm.loyal.utils.ConnectUtil;
import com.mwm.loyal.utils.StringUtil;
import com.mwm.loyal.utils.TimeUtil;
import com.mwm.loyal.utils.ToastUtil;
import com.mwm.loyal.widget.BaseDialog;

public abstract class BaseActivity<T extends ViewDataBinding> extends BasicActivity<T> implements UIInterface, DialogClickListener {
    private UpdateReceiver updateReceiver;
    protected ProgressDialog progressDialog;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initDialog();
    }

    private void initDialog() {
        progressDialog = new ProgressDialog(this);
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

    public void showProgressDialog() {
        showProgressDialog(null);
    }

    public void showProgressDialog(CharSequence message) {
        if (null != progressDialog) {
            progressDialog.setMessage(replaceNull(message));
            progressDialog.show();
        }
    }

    public void disMissDialog() {
        if (null != progressDialog) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateReceiver = new UpdateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Str.method_apkVerCheck);
        LocalBroadcastManager.getInstance(this).registerReceiver(updateReceiver, intentFilter);
    }

    @Override
    public void showToast(@NonNull String text) {
        ToastUtil.showToast(this, text);
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
        ToastUtil.showDialog(this, replaceNull(text), finish);
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
        String error = null == e ? "" : ConnectUtil.getError(e);
        showDialog(replaceNull(text) + (TextUtils.isEmpty(error) ? "" : "\n" + error), finish);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (updateReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(BaseDialog dialog, View view) {
        if (null != dialog && dialog.isShowing())
            dialog.dismiss();
        String apkUrl = null == dialog ? "" : (String) dialog.getTag();
        switch (view.getId()) {
            case R.id.dialog_btn_ok:
                if (!apkUrl.endsWith(".apk") || !apkUrl.endsWith(".APK")) {
                    showToast("apk路径地址错误");
                    return;
                }
                UpdateService.startActionUpdate(BaseActivity.this, Str.actionDownload, apkUrl);
                break;
            case R.id.dialog_btn_cancel:
                break;
        }
    }

    private class UpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent)
                return;
            String action = intent.getAction();
            String apkUrl = intent.getStringExtra("apkUrl");
            if (TextUtils.equals(Str.method_apkVerCheck, action)) {
                if (!TextUtils.isEmpty(apkUrl) && apkUrl.endsWith(".apk")) {
                    showUpdateDialog("检测到有新的版本，是否更新?", apkUrl);
                }
            }
        }
    }

    /**
     * 更新提示
     */
    public void showUpdateDialog(String content, final String apkUrl) {
        BaseDialog.Builder builder = new BaseDialog.Builder(this);
        builder.setContent(content).setTag(apkUrl)
                .setBtnText(new String[]{"下次再说", "立即更新"})
                .setOutsideCancel(false).setClickListener(this);
        builder.create().show();
    }
}
