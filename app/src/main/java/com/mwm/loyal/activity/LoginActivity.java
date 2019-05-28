package com.mwm.loyal.activity;

import android.content.Intent;
import android.view.KeyEvent;

import com.loyal.kit.GsonUtil;
import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseActivity;
import com.mwm.loyal.beans.AccountBean;
import com.mwm.loyal.beans.ObservableAccountBean;
import com.mwm.loyal.databinding.ActivityLoginBinding;
import com.mwm.loyal.handler.LoginHandler;
import com.mwm.loyal.utils.ImageUtil;
import com.mwm.loyal.utils.PreferUtil;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {

    @Override
    protected int actLayoutRes() {
        return R.layout.activity_login;
    }

    @Override
    public void afterOnCreate() {
        ObservableAccountBean bean = PreferUtil.getLoginBean(this);
        binding.setObservableAccountBean(bean);
        binding.setClick(new LoginHandler(this, binding));
        binding.setDrawable(ImageUtil.getBackground(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case IntImpl.reqCodeRegister:
                String beanJson = null == data ? "" : replaceNull(data.getStringExtra("beanJson"));
                AccountBean accountBean = GsonUtil.json2Bean(beanJson, AccountBean.class);
                String account = null == accountBean ? "" : replaceNull(accountBean.getAccount());
                binding.account.setText(account);
                binding.password.setText("");
                break;
        }
    }

    private long lastTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - lastTime <= 2000) {
                finish();
            } else {
                showToast("再次点击返回键退出");
                lastTime = System.currentTimeMillis();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}