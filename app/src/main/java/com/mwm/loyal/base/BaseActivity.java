package com.mwm.loyal.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.loyal.basex.ui.activity.ABasicFullScreenActivity;
import com.mwm.loyal.impl.IContactImpl;

import butterknife.ButterKnife;

public abstract class BaseActivity<T extends ViewDataBinding> extends ABasicFullScreenActivity implements IContactImpl {
    protected ProgressDialog progressDialog;
    protected T binding;

    @Override
    public boolean isFullScreen() {
        return false;
    }

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
}
