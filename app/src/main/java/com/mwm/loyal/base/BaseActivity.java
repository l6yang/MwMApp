package com.mwm.loyal.base;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.loyal.base.ui.activity.ABasicBindActivity;
import com.loyal.base.widget.BaseDialog;
import com.mwm.loyal.R;
import com.mwm.loyal.app.MwMApplication;
import com.mwm.loyal.impl.IContact;
import com.mwm.loyal.service.UpdateService;

import butterknife.ButterKnife;

public abstract class BaseActivity<T extends ViewDataBinding> extends ABasicBindActivity implements IContact, BaseDialog.DialogClickListener {
    private UpdateReceiver updateReceiver;
    protected ProgressDialog progressDialog;
    protected T binding;

    @Override
    public void setContentView() {
        binding = DataBindingUtil.setContentView(this, actLayoutRes());
    }

    @Override
    public void bindViews() {
        ButterKnife.bind(this);
    }

    protected void setCurrentTag(BaseActivity activity) {
        String tag = activity.getClass().getName();
        MwMApplication.getInstance().setActivityTag(tag);
    }

    protected String getCurrentTag() {
        return MwMApplication.getInstance().getActivityTag();
    }

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
        setCurrentTag(this);
        updateReceiver = new UpdateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IStr.method_apkVerCheck);
        LocalBroadcastManager.getInstance(this).registerReceiver(updateReceiver, intentFilter);
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
    public void onClick(BaseDialog dialog, View view, Object tag) {
        if (null != dialog && dialog.isShowing())
            dialog.dismiss();
        String apkUrl = null == dialog ? "" : (String) dialog.getTag();
        switch (view.getId()) {
            case R.id.dialog_btn_ok:
                if (!apkUrl.endsWith(".apk") || !apkUrl.endsWith(".APK")) {
                    showToast("apk路径地址错误");
                    return;
                }
                UpdateService.startActionUpdate(BaseActivity.this, IStr.actionDownload, apkUrl);
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
            if (TextUtils.equals(IStr.method_apkVerCheck, action)) {
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
