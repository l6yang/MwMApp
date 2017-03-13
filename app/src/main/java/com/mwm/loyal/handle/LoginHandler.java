package com.mwm.loyal.handle;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.activity.ForgetActivity;
import com.mwm.loyal.activity.LoginActivity;
import com.mwm.loyal.activity.MainActivity;
import com.mwm.loyal.activity.RegisterActivity;
import com.mwm.loyal.base.BaseActivityHandler;
import com.mwm.loyal.beans.LoginBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityLoginBinding;
import com.mwm.loyal.utils.FileUtil;
import com.mwm.loyal.utils.ImageUtil;
import com.mwm.loyal.utils.PreferencesUtil;
import com.mwm.loyal.utils.RetrofitManage;

import java.io.File;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LoginHandler extends BaseActivityHandler {
    private final LoginActivity loginActivity;
    private final ActivityLoginBinding mBinding;

    public LoginHandler(LoginActivity activity, ActivityLoginBinding binding) {
        super(activity);
        loginActivity = activity;
        mBinding = binding;
        progressDialog.setMessage("处理中...");
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pub_submit:
                String account = mBinding.account.getText().toString().trim();
                String password = mBinding.password.getText().toString().trim();
                if (TextUtils.isEmpty(account)) {
                    showToast("用户名不能为空");
                    return;
                }
                if (TextUtils.isEmpty(password) || password.length() < 6) {
                    showToast("密码长度格式错误");
                    return;
                }
                login2MainActivity(new LoginBean(account, password));
                break;
            case R.id.account_clear:
                mBinding.account.getText().clear();
                break;
            case R.id.password_clear:
                mBinding.password.getText().clear();
                break;
            case R.id.server_clear:
                mBinding.server.getText().clear();
                break;
            case R.id.mm_forget:
                Intent intent = new Intent(loginActivity, ForgetActivity.class);
                loginActivity.startActivity(intent);
                break;
            case R.id.register:
                toRegister(R.id.pub_submit);
                break;
        }
    }

    private void login2MainActivity(final LoginBean loginBean) {
        if (progressDialog != null)
            progressDialog.show();
        final RetrofitManage.ObservableServer server = RetrofitManage.getInstance().getObservableServer();
        Observable<ResponseBody> observable = server.doShowIcon(loginBean.account.get());
        observable.subscribeOn(Schedulers.io()).flatMap(new Func1<ResponseBody, Observable<ResultBean>>() {
            @Override
            public Observable<ResultBean> call(ResponseBody body) {
                File iconFile = new File(FileUtil.path_icon, "icon_" + loginBean.account.get() + ".jpg");
                FileUtil.deleteFile(iconFile);
                String save = ImageUtil.saveToFile(iconFile, body.byteStream());
                if (!TextUtils.isEmpty(save))
                    return server.doLogin(loginBean.toString());
                else return null;
            }
        }).subscribeOn(Schedulers.newThread())//请求在新的线程中执行请求
                .observeOn(AndroidSchedulers.mainThread())//在主线程中执行
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
                                PreferencesUtil.putLoginBean(loginActivity, loginBean);
                                Intent intent = new Intent(loginActivity, MainActivity.class);
                                intent.putExtra("account", loginBean.account.get());
                                intent.putExtra("nickname", resultBean.getResultMsg());
                                intent.putExtra("signature", resultBean.getExceptMsg());
                                loginActivity.startActivity(intent);
                                loginActivity.finish();
                            } else showDialog(resultBean.getResultMsg(), false);
                        } else
                            showErrorDialog("解析失败", false);
                    }
                });
    }

    private void toRegister(int resId) {
        Intent intent = new Intent(loginActivity, RegisterActivity.class);
        intent.putExtra("resId", resId);
        intent.putExtra("account", "");
        loginActivity.startActivityForResult(intent, Int.reqCode_register);
    }
}