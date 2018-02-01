package com.mwm.loyal.handler;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.activity.RegisterActivity;
import com.mwm.loyal.base.BaseClickHandler;
import com.mwm.loyal.base.RxProgressSubscriber;
import com.mwm.loyal.beans.LoginBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityRegisterBinding;
import com.mwm.loyal.impl.OperaOnClickListener;
import com.mwm.loyal.impl.SubscribeListener;
import com.mwm.loyal.utils.OperateDialog;
import com.mwm.loyal.utils.RxUtil;
import com.mwm.loyal.utils.ToastUtil;

import rx.Observable;

public class RegisterHandler extends BaseClickHandler<ActivityRegisterBinding> implements SubscribeListener<ResultBean>, OperaOnClickListener {
    private final boolean fromLogin;
    private final String extra;
    private final int resultReg = 2;
    private final int resultDes = 4;

    public RegisterHandler(RegisterActivity activity, ActivityRegisterBinding binding) {
        super(activity, binding);
        this.fromLogin = TextUtils.isEmpty(activity.getIntent().getStringExtra("account"));
        this.extra = activity.getIntent().getStringExtra("extra");
        progressDialog.setMessage("提交信息中...");
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.account_clear:
                binding.account.getText().clear();
                break;
            case R.id.password_clear:
                binding.password.getText().clear();
                break;
            case R.id.repeat_clear:
                binding.repeatMm.getText().clear();
                break;
            case R.id.nickname_clear:
                binding.nickname.getText().clear();
                break;
            case R.id.pub_submit:
                ToastUtil.hideInput(activity, binding.account.getWindowToken());
                String account = binding.account.getText().toString().trim();
                String nickname = binding.nickname.getText().toString().trim();
                String password = binding.password.getText().toString().trim();
                String repeat = binding.repeatMm.getText().toString().trim();
                if (fromLogin) {
                    doRegister(account, nickname, password, repeat);
                } else {
                    if (TextUtils.isEmpty(extra))
                        doResetPassWord(account, nickname, password, repeat);
                    else destroyAccount(account, nickname);
                }
                break;
        }
    }

    private void destroyAccount(final String account, final String password) {
        if (TextUtils.isEmpty(account)) {
            showToast("用户名不能为空");
            return;
        }
        if (!isValue(password)) {
            showToast("密码长度少于6位");
            return;
        }
        OperateDialog operateDialog = ToastUtil.operateDialog(activity, this);
        operateDialog.setTitle(R.string.dialog_Title).setMessage(String.format(getString(R.string.destroy_message), account)).show();
    }

    private void doRegister(String account, String nickname, String password, String repeat) {
        if (TextUtils.isEmpty(account)) {
            showToast("用户名不能为空");
            return;
        }
        if (!isValue(password)) {
            showToast("密码长度少于6位");
            return;
        }
        if (!TextUtils.equals(password, repeat)) {
            showToast("两次输入密码不一致");
            return;
        }
        doAsyncTask(new LoginBean(account, password, false, TextUtils.isEmpty(nickname) ? account : nickname));
    }

    private void doResetPassWord(String account, String nickname, String password, String repeat) {
        if (TextUtils.isEmpty(nickname)) {
            showToast("旧密码不能为空");
            return;
        }
        if (!isValue(nickname) || !isValue(password)) {
            showToast("密码长度不得少于6位");
            return;
        }
        if (TextUtils.equals(nickname, password)) {
            showToast("新密码和旧密码不能一致");
            return;
        }
        if (!TextUtils.equals(password, repeat)) {
            showToast("两次输入密码不一致");
            return;
        }
        doAsyncTask(new LoginBean(account, password, nickname));
    }

    private void doAsyncTask(LoginBean loginBean) {
        RxProgressSubscriber<ResultBean> subscriber = new RxProgressSubscriber<>(activity, this);
        subscriber.setWhat(resultReg);
        Observable<ResultBean> observable = fromLogin ? subscriber.doRegister(loginBean.toString()) : subscriber.doUpdateAccount(loginBean.toString());
        RxUtil.rxExecuted(observable, subscriber);
    }

    private boolean isValue(String string) {
        return string.length() >= 6;
    }

    @Override
    public void onResult(int what, Object tag, ResultBean resultBean) {
        final String account = binding.account.getText().toString().trim();
        final String password = binding.password.getText().toString().trim();
        switch (what) {
            case resultReg:
                if (resultBean != null) {
                    if (resultBean.getResultCode() == 1) {
                        showToast(fromLogin ? "注册成功,请牢记用户名和密码" : "修改成功，请重新登录");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent();
                                intent.putExtra("account", account);
                                intent.putExtra("password", fromLogin ? password : "");
                                activity.setResult(Activity.RESULT_OK, intent);
                                activity.finish();
                            }
                        }, 1000);
                    } else showDialog(resultBean.getResultMsg(), false);
                } else
                    showErrorDialog("解析" + (fromLogin ? "注册" : "修改") + "信息异常", false);
                break;
            case resultDes:
                if (resultBean != null) {
                    if (resultBean.getResultCode() == 1) {
                        showToast("账号已注销");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent();
                                intent.putExtra("account", "");
                                activity.setResult(Activity.RESULT_OK, intent);
                                activity.finish();
                            }
                        }, 1000);
                    } else showDialog(resultBean.getResultMsg(), false);
                } else
                    showErrorDialog("解析注销信息异常", false);
                break;
        }
    }

    @Override
    public void onError(int what, Object tag, Throwable e) {
        showErrorDialog(e.toString(), false);
    }

    @Override
    public void dialogCancel() {
    }

    @Override
    public void goNext() {
        String account = binding.account.getText().toString().trim();
        String nickname = binding.nickname.getText().toString().trim();
        RxProgressSubscriber<ResultBean> subscriber = new RxProgressSubscriber<>(activity, this);
        subscriber.setWhat(resultDes);
        RxUtil.rxExecuted(subscriber.destroyAccount(new LoginBean(account, nickname).toString()), subscriber);
    }
}