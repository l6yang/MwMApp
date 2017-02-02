package com.mwm.loyal.handle;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.activity.RegisterActivity;
import com.mwm.loyal.base.BaseAsyncTask;
import com.mwm.loyal.beans.LoginBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityRegisterBinding;
import com.mwm.loyal.utils.GsonUtil;
import com.mwm.loyal.utils.RetrofitManage;
import com.mwm.loyal.utils.StringUtil;
import com.mwm.loyal.utils.ToastUtil;

import java.io.IOException;

import retrofit2.Call;

public class RegisterHandler {
    private final RegisterActivity registerActivity;
    private final ActivityRegisterBinding mBinding;
    private RegisterAsync mRegisterAuth;
    private final boolean fromLogin;

    public RegisterHandler(RegisterActivity activity, ActivityRegisterBinding binding, boolean fromLogin) {
        registerActivity = activity;
        mBinding = binding;
        this.fromLogin = fromLogin;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.account_clear:
                mBinding.account.getText().clear();
                break;
            case R.id.password_clear:
                mBinding.password.getText().clear();
                break;
            case R.id.repeat_clear:
                mBinding.repeatMm.getText().clear();
                break;
            case R.id.nickname_clear:
                mBinding.nickname.getText().clear();
                break;
            case R.id.pub_submit:
                String account = mBinding.account.getText().toString().trim();
                String nickname = mBinding.nickname.getText().toString().trim();
                String password = mBinding.password.getText().toString().trim();
                String repeat = mBinding.repeatMm.getText().toString().trim();
                if (fromLogin)
                    doRegister(account, nickname, password, repeat);
                else doResetPassWord(account, nickname, password, repeat);
                break;
        }
    }

    private void doRegister(String account, String nickname, String password, String repeat) {
        if (TextUtils.isEmpty(account)) {
            ToastUtil.showToast(registerActivity, "用户名不能为空");
            return;
        }
        if (!isValue(password)) {
            ToastUtil.showToast(registerActivity, "密码长度少于6位");
            return;
        }
        if (!TextUtils.equals(password, repeat)) {
            ToastUtil.showToast(registerActivity, "两次输入密码不一致");
            return;
        }
        doAsyncTask(nickname, new LoginBean(account, password, nickname, registerActivity));
    }

    private void doResetPassWord(String account, String nickname, String password, String repeat) {
        if (TextUtils.isEmpty(nickname)) {
            ToastUtil.showToast(registerActivity, "旧密码不能为空");
            return;
        }
        if (!isValue(nickname) || !isValue(password)) {
            ToastUtil.showToast(registerActivity, "密码长度不得少于6位");
            return;
        }
        if (TextUtils.equals(nickname, password)) {
            ToastUtil.showToast(registerActivity, "新密码和旧密码不能一致");
            return;
        }
        if (!TextUtils.equals(password, repeat)) {
            ToastUtil.showToast(registerActivity, "两次输入密码不一致");
            return;
        }
        doAsyncTask(nickname, new LoginBean(account, password, registerActivity));
    }

    private void doAsyncTask(String nickname, LoginBean loginBean) {
        if (mRegisterAuth != null)
            return;
        mRegisterAuth = new RegisterAsync(nickname, loginBean);
        mRegisterAuth.execute();
    }

    private boolean isValue(String string) {
        return string.length() >= 6;
    }

    private class RegisterAsync extends BaseAsyncTask<Void, Void, String> implements DialogInterface.OnCancelListener {
        private final LoginBean loginBean;
        private final String oldPassWord;

        RegisterAsync(String nickname, LoginBean bean) {
            super(registerActivity);
            loginBean = bean;
            oldPassWord = nickname;
            setDialogMessage("提交信息中，稍等...");
            setCancelable(true);
            setCanceledOnTouchOutside(false);
        }

        @Override
        protected String doInBackground(Void... voids) {
            RetrofitManage.RequestServer server = RetrofitManage.getInstance().getRequestServer();
            try {
                Call<String> call = fromLogin ? server.doRegister(loginBean.toString()) : server.doUpdateAccount(loginBean.toString(), "password", oldPassWord);
                /*RequestBody body = new FormBody.Builder()
                        .add(fromLogin ? "json_register" : "json_update", loginBean.toString())
                        .add(fromLogin ? "" : "update_state", fromLogin ? "" : "password")
                        .add(fromLogin ? "" : "old_password", fromLogin ? "" : oldPassWord)
                        .build();
                String method = fromLogin ? Str.action_register : Str.action_update;
                System.out.println(StringUtil.getServiceUrl(method));
                return OkHttpClientManager.getInstance().post_jsonDemo(StringUtil.getServiceUrl(method), body);*/
                return RetrofitManage.doExecuteStr(call);
            } catch (IOException e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            cancelDialog();
            mRegisterAuth = null;
            System.out.println(result);
            if (StringUtil.showErrorToast(registerActivity, result)) return;
            ResultBean bean = GsonUtil.getBeanFromJson(result, ResultBean.class);
            if (bean.getResultCode() == 1) {
                ToastUtil.showToast(registerActivity, fromLogin ? "注册成功,请牢记用户名和密码" : "修改成功，请重新登录");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!fromLogin) {
                            registerActivity.setResult(Activity.RESULT_OK);
                        }
                        registerActivity.finish();
                    }
                }, 1000);
            } else {
                ToastUtil.showToast(registerActivity, StringUtil.replaceNull(bean.getResultMsg()));
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mRegisterAuth = null;
        }
    }
}
