package com.mwm.loyal.handle;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.mwm.loyal.R;
import com.mwm.loyal.activity.ForgetActivity;
import com.mwm.loyal.activity.LoginActivity;
import com.mwm.loyal.activity.MainActivity;
import com.mwm.loyal.activity.RegisterActivity;
import com.mwm.loyal.beans.LoginBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityLoginBinding;
import com.mwm.loyal.utils.FileUtil;
import com.mwm.loyal.utils.GsonUtil;
import com.mwm.loyal.utils.ImageUtil;
import com.mwm.loyal.utils.PreferencesUtil;
import com.mwm.loyal.utils.RetrofitManage;
import com.mwm.loyal.utils.StringUtil;
import com.mwm.loyal.utils.ToastUtil;

import java.io.File;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class LoginHandler {
    private final LoginActivity loginActivity;
    private final ActivityLoginBinding mBinding;
    private LoginAsync mLoginAuth;

    public LoginHandler(LoginActivity activity, ActivityLoginBinding binding) {
        loginActivity = activity;
        mBinding = binding;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pub_submit:
                String account = mBinding.account.getText().toString().trim();
                String password = mBinding.password.getText().toString().trim();
                if (TextUtils.isEmpty(account)) {
                    ToastUtil.showToast(loginActivity, "用户名不能为空");
                    return;
                }
                if (TextUtils.isEmpty(password) || password.length() < 6) {
                    ToastUtil.showToast(loginActivity, "密码长度格式错误");
                    return;
                }
                LoginBean bean = new LoginBean(account, password, loginActivity);
                if (mLoginAuth != null)
                    return;
                mLoginAuth = new LoginAsync(bean);
                mLoginAuth.execute();
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

    private void toRegister(int resId) {
        Intent intent = new Intent(loginActivity, RegisterActivity.class);
        intent.putExtra("resId", resId);
        intent.putExtra("account", "");
        loginActivity.startActivity(intent);
    }

    private DownIconAsync mDownAuth;

    private class LoginAsync extends AsyncTask<Void, Void, String> implements DialogInterface.OnCancelListener {
        private final LoginBean loginBean;
        private final ProgressDialog mDialog;

        LoginAsync(LoginBean bean) {
            loginBean = bean;
            mDialog = new ProgressDialog(loginActivity);
            mDialog.setMessage("处理中...");
            Window window = mDialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.alpha = 0.7f;// 透明度
                lp.dimAmount = 0.8f;// 黑暗度
                window.setAttributes(lp);
            }
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setOnCancelListener(this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            Call<String> call = RetrofitManage.getInstance().getRequestServer().doLogin(loginBean.toString());
            try {
                return RetrofitManage.doExecuteStr(call);
            } catch (IOException e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mLoginAuth = null;
            if (StringUtil.showErrorToast(loginActivity, result)) {
                mDialog.dismiss();
                return;
            }
            ResultBean bean = GsonUtil.getBeanFromJson(result, ResultBean.class);
            if (bean.getResultCode() == 1) {
                PreferencesUtil.putLoginBean(loginActivity, loginBean);
                if (mDownAuth != null)
                    return;
                mDownAuth = new DownIconAsync(mDialog, loginBean, bean);
                mDownAuth.execute();
            } else {
                mDialog.dismiss();
                System.out.println(bean.getResultMsg());
                ToastUtil.showToast(loginActivity, StringUtil.replaceNull(bean.getResultMsg()));
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mLoginAuth = null;
        }

        @Override
        public void onCancel(DialogInterface dialogInterface) {
            LoginAsync.this.cancel(true);
        }
    }

    private class DownIconAsync extends AsyncTask<Void, Void, String> implements DialogInterface.OnCancelListener {
        private final LoginBean loginBean;
        private final ProgressDialog mDialog;
        private ResultBean resultBean;

        DownIconAsync(ProgressDialog mDialog, LoginBean bean, ResultBean resultBean) {
            loginBean = bean;
            this.resultBean = resultBean;
            this.mDialog = mDialog;
        }

        @Override
        protected String doInBackground(Void... voids) {
            File iconFile = new File(FileUtil.path_icon, "icon_" + loginBean.account.get() + ".jpg");
            FileUtil.deleteFile(iconFile);
            try {
                Call<ResponseBody> call = RetrofitManage.getInstance().getRequestServer().doShowIcon(loginBean.account.get());
                ResponseBody body = RetrofitManage.doExecute(call);
                return ImageUtil.saveToFile(iconFile, body.byteStream());
            } catch (IOException e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mDialog.cancel();
            mDownAuth = null;
            if (StringUtil.showErrorToast(loginActivity, result)) return;
            PreferencesUtil.putLoginBean(loginActivity, loginBean);
            Intent intent = new Intent(loginActivity, MainActivity.class);
            intent.putExtra("account", loginBean.account.get());
            intent.putExtra("nickname", resultBean.getResultMsg());
            intent.putExtra("signature", resultBean.getExceptMsg());
            loginActivity.startActivity(intent);
            loginActivity.finish();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mDownAuth = null;
        }

        @Override
        public void onCancel(DialogInterface dialogInterface) {
            DownIconAsync.this.cancel(true);
        }
    }
}
