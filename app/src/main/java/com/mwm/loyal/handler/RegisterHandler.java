package com.mwm.loyal.handler;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.activity.RegisterActivity;
import com.mwm.loyal.base.BaseClickHandler;
import com.mwm.loyal.beans.LoginBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityRegisterBinding;
import com.mwm.loyal.utils.RetrofitManage;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RegisterHandler extends BaseClickHandler<ActivityRegisterBinding> {
    private final boolean fromLogin;

    public RegisterHandler(RegisterActivity activity, ActivityRegisterBinding binding, boolean fromLogin) {
        super(activity,binding);
        this.fromLogin = fromLogin;
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
                String account = binding.account.getText().toString().trim();
                String nickname = binding.nickname.getText().toString().trim();
                String password = binding.password.getText().toString().trim();
                String repeat = binding.repeatMm.getText().toString().trim();
                if (fromLogin) {
                    doRegister(account, nickname, password, repeat);
                } else doResetPassWord(account, nickname, password, repeat);
                break;
        }
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
        if (TextUtils.isEmpty(nickname)) {
            showToast("请填写您的昵称");
            return;
        }
        if (!TextUtils.equals(password, repeat)) {
            showToast("两次输入密码不一致");
            return;
        }
        doAsyncTask(nickname, new LoginBean(account, password, false, nickname));
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
        doAsyncTask(nickname, new LoginBean(account, password));
    }

    private void doAsyncTask(String oldPassWord, final LoginBean loginBean) {
        if (progressDialog != null)
            progressDialog.show();
        RetrofitManage.ObservableServer server = RetrofitManage.getInstance().getObservableServer();
        Observable<ResultBean> observable = fromLogin ? server.doRegister(loginBean.toString()) : server.doUpdateAccount(loginBean.toString(), "password", oldPassWord);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResultBean>() {
                    @Override
                    public void onCompleted() {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        showErrorDialog(e.toString(), false);
                    }

                    @Override
                    public void onNext(ResultBean resultBean) {
                        if (resultBean != null) {
                            if (resultBean.getResultCode() == 1) {
                                showToast(fromLogin ? "注册成功,请牢记用户名和密码" : "修改成功，请重新登录");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent();
                                        intent.putExtra("account", loginBean.account.get());
                                        if (!fromLogin) {
                                            activity.setResult(Activity.RESULT_OK, intent);
                                        }
                                        activity.finish();
                                    }
                                }, 1000);
                            } else showDialog(resultBean.getResultMsg(), false);
                        } else
                            showErrorDialog("解析异常", false);
                    }
                });
    }

    private boolean isValue(String string) {
        return string.length() >= 6;
    }
}