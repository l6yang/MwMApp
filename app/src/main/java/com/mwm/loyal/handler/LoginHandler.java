package com.mwm.loyal.handler;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.View;

import com.loyal.base.util.GsonUtil;
import com.mwm.loyal.R;
import com.mwm.loyal.activity.ForgetActivity;
import com.mwm.loyal.activity.LoginActivity;
import com.mwm.loyal.activity.MainActivity;
import com.mwm.loyal.activity.RegisterActivity;
import com.mwm.loyal.base.BaseClickHandler;
import com.mwm.loyal.base.RxProgressSubscriber;
import com.mwm.loyal.beans.LoginBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityLoginBinding;
import com.mwm.loyal.impl.SubscribeListener;
import com.mwm.loyal.service.ImageService;
import com.mwm.loyal.utils.PreferencesUtil;
import com.mwm.loyal.utils.RxUtil;
import com.mwm.loyal.utils.ToastUtil;

public class LoginHandler extends BaseClickHandler<ActivityLoginBinding> implements SubscribeListener<ResultBean> {

    public LoginHandler(LoginActivity activity, ActivityLoginBinding binding) {
        super(activity, binding);
    }

    public void onClick(View view) {
        ToastUtil.hideInput(activity, view.getWindowToken());
        switch (view.getId()) {
            case R.id.pub_submit:
                login();
                break;
            case R.id.account_clear:
                binding.account.getText().clear();
                break;
            case R.id.password_clear:
                binding.password.getText().clear();
                break;
            case R.id.server_clear:
                binding.server.getText().clear();
                break;
            case R.id.mm_forget:
                startActivityByAct(ForgetActivity.class);
                break;
            case R.id.register:
                toRegister(R.id.pub_submit);
                break;
        }
    }

    private void login() {
        String account = binding.account.getText().toString().trim();
        String password = binding.password.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            showToast("用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            showToast("密码长度格式错误");
            return;
        }
        LoginBean loginBean = new LoginBean(account, password);
        //登录页面只有登录
        RxProgressSubscriber<ResultBean> subscriber = new RxProgressSubscriber<>(activity, this);
        subscriber.setMessage("登录中...").setTag(loginBean);
        RxUtil.rxExecuted(subscriber.doLogin(loginBean.toString()), subscriber);
    }

    private void toRegister(@IdRes int resId) {
        builder.putExtra("resId", resId);
        builder.putExtra("account", "");
        startActivityForResultByAct(RegisterActivity.class, Int.reqCode_register);
    }

    @Override
    public void onResult(int what, Object tag, ResultBean resultBean) {
        System.out.println("onResult::" + GsonUtil.bean2Json(resultBean));
        try {
            LoginBean loginBean = (LoginBean) tag;
            if (resultBean != null) {
                if (1 == resultBean.getResultCode()) {
                    PreferencesUtil.putLoginBean(activity, loginBean);
                    builder.putExtra("account", loginBean.account.get());
                    startActivityByAct(MainActivity.class);
                    Intent imageIntent = new Intent().setAction(IStr.actionDownload).putExtra("account", loginBean.account.get());
                    ImageService.startAction(activity, imageIntent);
                    finish();
                } else showDialog(resultBean.getResultMsg());
            } else
                showErrorDialog("解析失败");
        } catch (Exception e) {
            onError(what, tag, e);
        }
    }

    @Override
    public void onError(int what, Object tag, Throwable e) {
        showErrorDialog("登录失败", e);
    }
}