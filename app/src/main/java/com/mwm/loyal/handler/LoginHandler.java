package com.mwm.loyal.handler;

import android.text.TextUtils;
import android.view.View;

import com.loyal.kit.GsonUtil;
import com.loyal.kit.OutUtil;
import com.loyal.rx.RxUtil;
import com.loyal.rx.impl.RxSubscriberListener;
import com.mwm.loyal.R;
import com.mwm.loyal.activity.ForgetActivity;
import com.mwm.loyal.activity.LoginActivity;
import com.mwm.loyal.activity.MainActivity;
import com.mwm.loyal.activity.RegisterActivity;
import com.mwm.loyal.activity.settings.ServerActivity;
import com.mwm.loyal.base.BaseClickHandler;
import com.mwm.loyal.beans.AccountBean;
import com.mwm.loyal.beans.ObservableAccountBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityLoginBinding;
import com.mwm.loyal.impl.TextChangedListener;
import com.mwm.loyal.libs.rxjava.RxProgressSubscriber;
import com.mwm.loyal.utils.PreferUtil;
import com.mwm.loyal.utils.UIHandler;

public class LoginHandler extends BaseClickHandler<ActivityLoginBinding> implements RxSubscriberListener<String> {

    public LoginHandler(LoginActivity activity, ActivityLoginBinding binding) {
        super(activity, binding);
        initViews();
    }

    private void initViews() {
        binding.account.addTextChangedListener(new TextChangedListener(binding.account, binding.accountClear));
        binding.password.addTextChangedListener(new TextChangedListener(binding.password, binding.passwordClear));
    }

    public void onClick(View view) {
        hideKeyBoard(view);
        UIHandler.delay2Enable(view);
        switch (view.getId()) {
            case R.id.submitView:
                login();
                break;
            case R.id.account_clear:
                clearText(binding.account);
                break;
            case R.id.password_clear:
                clearText(binding.password);
                break;
            case R.id.serverView:
                startActivityByAct(ServerActivity.class);
                break;
            case R.id.forgetView:
                startActivityByAct(ForgetActivity.class);
                break;
            case R.id.registerView:
                toRegister();
                break;
        }
    }

    private void login() {
        String account = getText(binding.account);
        String password = getText(binding.password);
        if (TextUtils.isEmpty(account)) {
            showToast("请输入用户名！");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showToast("请输入密码！");
            return;
        }
        ObservableAccountBean observableAccountBean = new ObservableAccountBean(account, password);
        //登录页面只有登录
        RxProgressSubscriber<String> subscriber = new RxProgressSubscriber<>(activity);
        subscriber.setDialogMessage("登录中...").showProgressDialog(true).setTag(observableAccountBean);
        subscriber.setSubscribeListener(this);
        RxUtil.rxExecute(subscriber.loginByJson(observableAccountBean.toString()), subscriber);
    }

    private void toRegister() {
        intentBuilder.putExtra("resId", R.id.submitView);
        intentBuilder.putExtra("account", "");
        startActivityForResultByAct(RegisterActivity.class, IntImpl.reqCodeRegister);
    }

    @Override
    public void onResult(int what, Object tag, String result) {
        try {
            OutUtil.println("login", result);
            ResultBean<AccountBean> resultBean = (ResultBean<AccountBean>) GsonUtil.json2BeanObject(result, ResultBean.class, AccountBean.class);
            ObservableAccountBean observableAccountBean = (ObservableAccountBean) tag;
            String code = null == resultBean ? "" : replaceNull(resultBean.getCode());
            String message = null == resultBean ? "解析失败" : replaceNull(resultBean.getMessage());
            AccountBean accountBean = null == resultBean ? null : resultBean.getObj();
            if (TextUtils.equals("1", code)) {
                String locked = null == accountBean ? "" : replaceNull(accountBean.getLocked());
                if (TextUtils.equals("1", locked)) {
                    showToast("当前设备已被别的设备锁定");
                    return;
                }
                String account = null == accountBean ? "" : replaceNull(accountBean.getAccount());
                PreferUtil.putLoginBean(activity, observableAccountBean);
                intentBuilder.putExtra("account", account);
                startActivityByAct(MainActivity.class);
                intentBuilder.setAction(StrImpl.actionDownload);
                //ImageService.startAction(activity, intentBuilder);
                finish();
            } else
                showDialog(message);
        } catch (Exception e) {
            onError(what, tag, e);
        }
    }

    @Override
    public void onError(int what, Object tag, Throwable e) {
        showErrorDialog("登录失败", e);
    }
}