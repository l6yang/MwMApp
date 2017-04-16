package com.mwm.loyal.handler;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.activity.ForgetActivity;
import com.mwm.loyal.activity.LoginActivity;
import com.mwm.loyal.activity.MainActivity;
import com.mwm.loyal.activity.RegisterActivity;
import com.mwm.loyal.base.BaseClickHandler;
import com.mwm.loyal.base.BaseProgressSubscriber;
import com.mwm.loyal.beans.LoginBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityLoginBinding;
import com.mwm.loyal.imp.SubscribeListener;
import com.mwm.loyal.utils.FileUtil;
import com.mwm.loyal.utils.ImageUtil;
import com.mwm.loyal.utils.PreferencesUtil;
import com.mwm.loyal.utils.RetrofitManage;
import com.mwm.loyal.utils.ToastUtil;

import java.io.File;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LoginHandler extends BaseClickHandler<ActivityLoginBinding> implements SubscribeListener<ResultBean> {

    public LoginHandler(LoginActivity activity, ActivityLoginBinding binding) {
        super(activity, binding);
        progressDialog.setMessage("处理中...");
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pub_submit:
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
                //System.out.println("mac==" + DeviceHelper.getInstance(activity).getMacAddress());
                login2MainActivity(new LoginBean(account, password));
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
                Intent intent = new Intent(activity, ForgetActivity.class);
                activity.startActivity(intent);
                break;
            case R.id.register:
                toRegister(R.id.pub_submit);
                break;
        }
        ToastUtil.hideInput(activity, binding.account.getWindowToken());
    }

    private void login2MainActivity(final LoginBean loginBean) {
        showDialog();
        BaseProgressSubscriber<ResponseBody> bodySubscriber = new BaseProgressSubscriber<>(activity, -1);
        Observable<ResponseBody> observable = bodySubscriber.doShowIcon(loginBean.account.get()).subscribeOn(Schedulers.io());
        RetrofitManage.rxExecuted(observable, bodySubscriber);
        final BaseProgressSubscriber<ResultBean> loginSubscriber = new BaseProgressSubscriber<>(activity, 0, this);
        Observable<ResultBean> loginObservable = observable.flatMap(new Func1<ResponseBody, Observable<ResultBean>>() {
            @Override
            public Observable<ResultBean> call(ResponseBody body) {
                File iconFile = new File(FileUtil.path_icon, "icon_" + loginBean.account.get() + ".jpg");
                FileUtil.deleteFile(iconFile);
                String save = ImageUtil.saveToFile(iconFile, body.byteStream());
                if (!TextUtils.isEmpty(save))
                    return loginSubscriber.doLogin(loginBean.toString());
                else return null;
            }
        });
        RetrofitManage.rxExecuted(loginObservable, loginSubscriber);

        /*observable.subscribeOn(Schedulers.io()).flatMap(new Func1<ResponseBody, Observable<ResultBean>>() {
        @Override public Observable<ResultBean> call(ResponseBody body) {
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
        @Override public void onCompleted() {
        if (progressDialog != null)
        progressDialog.dismiss();
        }

        @Override public void onError(Throwable e) {
        if (progressDialog != null)
        progressDialog.dismiss();
        showErrorDialog(e.toString(), false);
        }

        @Override public void onNext(ResultBean resultBean) {
        if (resultBean != null) {
        if (resultBean.getResultCode() == 1) {
        PreferencesUtil.putLoginBean(activity, loginBean);
        Intent intent = new Intent(activity, MainActivity.class);
        intent.putExtra("account", loginBean.account.get());
        intent.putExtra("nickname", resultBean.getResultMsg());
        intent.putExtra("sign", resultBean.getExceptMsg());
        activity.startActivity(intent);
        activity.finish();
        } else showDialog(resultBean.getResultMsg(), false);
        } else
        showErrorDialog("解析失败", false);
        }
        });*/
    }

    private void toRegister(int resId) {
        Intent intent = new Intent(activity, RegisterActivity.class);
        intent.putExtra("resId", resId);
        intent.putExtra("account", "");
        activity.startActivityForResult(intent, Int.reqCode_register);
    }

    @Override
    public void onResult(int what, ResultBean resultBean) {
        disMissDialog();
        String account = binding.account.getText().toString().trim();
        String password = binding.password.getText().toString().trim();
        LoginBean loginBean = new LoginBean(account, password);
        if (resultBean != null) {
            if (resultBean.getResultCode() == 1) {
                PreferencesUtil.putLoginBean(activity, loginBean);
                Intent intent = new Intent(activity, MainActivity.class);
                intent.putExtra("account", loginBean.account.get());
                activity.startActivity(intent);
                activity.finish();
            } else showDialog(resultBean.getResultMsg(), false);
        } else
            showErrorDialog("解析失败", false);
    }

    @Override
    public void onError(int what, Throwable e) {
        disMissDialog();
        showErrorDialog(e.toString(), false);
    }

    @Override
    public void onCompleted(int what) {

    }
}