package com.mwm.loyal.base;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.loyal.base.impl.CommandViewClickListener;
import com.loyal.base.ui.activity.ABasicBindActivity;
import com.loyal.base.widget.CommandDialog;
import com.mwm.loyal.R;
import com.mwm.loyal.impl.IContact;
import com.mwm.loyal.service.UpdateService;

import butterknife.ButterKnife;

public abstract class BaseActivity<T extends ViewDataBinding> extends ABasicBindActivity implements IContact, CommandViewClickListener {
    protected ProgressDialog progressDialog;
    protected T binding;

    @Override
    public void setViewByLayoutRes() {
        binding = DataBindingUtil.setContentView(this, actLayoutRes());
    }

    @Override
    public void bindViews() {
        ButterKnife.bind(this);
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
    public void onViewClick(CommandDialog dialog, View view, Object tag) {
        if (null != dialog && dialog.isShowing())
            dialog.dismiss();
        String apkUrl = null == dialog ? "" : (String) dialog.getTag();
        switch (view.getId()) {
            case R.id.dialog_btn_ok:
                if (!apkUrl.endsWith(".apk") || !apkUrl.endsWith(".APK")) {
                    showToast("apk路径地址错误");
                    return;
                }
                UpdateService.startActionUpdate(BaseActivity.this, StrImpl.actionDownload, apkUrl);
                break;
            case R.id.dialog_btn_cancel:
                break;
        }
    }

    /**
     * 更新提示
     */
    public void showUpdateDialog(String content, final String apkUrl) {
        CommandDialog.Builder builder = new CommandDialog.Builder(this);
        builder.setContent(content).setTag(apkUrl)
                .setBtnText(new String[]{"下次再说", "立即更新"})
                .setOutsideCancel(false).setClickListener(this);
        builder.create().show();
    }
}
