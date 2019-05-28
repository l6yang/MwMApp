package com.mwm.loyal.handler;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.loyal.kit.GsonUtil;
import com.loyal.rx.RxUtil;
import com.loyal.rx.impl.RxSubscriberListener;
import com.mwm.loyal.R;
import com.mwm.loyal.activity.RegisterActivity;
import com.mwm.loyal.base.BaseClickHandler;
import com.mwm.loyal.beans.AccountBean;
import com.mwm.loyal.beans.ObservableAccountBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityRegisterBinding;
import com.mwm.loyal.impl.OperaOnClickListener;
import com.mwm.loyal.libs.rxjava.RxProgressSubscriber;
import com.mwm.loyal.utils.OperateDialog;
import com.mwm.loyal.utils.ToastUtil;

import io.reactivex.Observable;

public class RegisterHandler extends BaseClickHandler<ActivityRegisterBinding> implements RxSubscriberListener<String>, OperaOnClickListener {
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
                clearText(binding.account);
                break;
            case R.id.password_clear:
                clearText(binding.password);
                break;
            case R.id.repeat_clear:
                clearText(binding.repeatMm);
                break;
            case R.id.nickname_clear:
                clearText(binding.nickname);
                break;
            case R.id.submitView:
                hideKeyBoard(binding.account);
                String account = getText(binding.account);
                String nickname = getText(binding.nickname);
                String password = getText(binding.password);
                String repeat = getText(binding.repeatMm);
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
        if (TextUtils.isEmpty(password)) {
            showToast("密码不能为空！");
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
        if (TextUtils.isEmpty(password)) {
            showToast("密码不能为空");
            return;
        }
        if (!TextUtils.equals(password, repeat)) {
            showToast("两次输入密码不一致");
            return;
        }
        ObservableAccountBean accountBean = new ObservableAccountBean();
        accountBean.account.set(account);
        accountBean.password.set(password);
        accountBean.nickname.set(TextUtils.isEmpty(nickname) ? "暂未填写" : nickname);
        accountBean.signature.set("此人很懒...");
        doAsyncTask(accountBean);
    }

    private void doResetPassWord(String account, String nickname, String password, String repeat) {
        if (TextUtils.isEmpty(nickname)) {
            showToast("旧密码不能为空");
            return;
        }
        if (TextUtils.isEmpty(nickname) || TextUtils.isEmpty(password)) {
            showToast("请输入密码");
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
        ObservableAccountBean accountBean = new ObservableAccountBean();
        accountBean.account.set(account);
        accountBean.password.set(password);
        accountBean.nickname.set(nickname);
        doAsyncTask(accountBean);
    }

    private void doAsyncTask(ObservableAccountBean observableAccountBean) {
        RxProgressSubscriber<String> subscriber = new RxProgressSubscriber<>(activity);
        subscriber.setWhat(resultReg).setDialogMessage("注册中...").showProgressDialog(true);
        subscriber.setTag(observableAccountBean).setSubscribeListener(this);
        String json = observableAccountBean.toString();
        json = encodeStr2Utf(json);
        Observable<String> observable = fromLogin ? subscriber.register(json) : subscriber.passwordUpdate(json);
        RxUtil.rxExecute(observable, subscriber);
    }

    @Override
    public void onResult(int what, Object tag, String result) {
        switch (what) {
            case resultReg: {
                ResultBean<AccountBean> resultBean = (ResultBean<AccountBean>) GsonUtil.json2BeanObject(result, ResultBean.class, AccountBean.class);
                if (null == resultBean) {
                    showDialog("解析" + (fromLogin ? "注册" : "修改") + "信息异常");
                    return;
                }
                String code = replaceNull(resultBean.getCode());
                String message = replaceNull(resultBean.getMessage());
                if (!TextUtils.equals("1", code)) {
                    showDialog(message);
                    return;
                }
                showToast(fromLogin ? "注册成功,请牢记用户名和密码" : "修改成功，请重新登录");
                final AccountBean accountBean = resultBean.getObj();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hasIntentParams(true);
                        intentBuilder.putExtra("beanJson", GsonUtil.bean2Json(accountBean));
                        activity.setResult(Activity.RESULT_OK, intentBuilder);
                        activity.finish();
                    }
                }, 1000);
            }
            break;
            case resultDes:
                ResultBean resultBean = GsonUtil.json2Bean(result, ResultBean.class);
                if (resultBean != null) {
                    if (TextUtils.equals("1", resultBean.getCode())) {
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
                    } else showDialog(resultBean.getMessage(), false);
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
        String account = getText(binding.account);
        String nickname = getText(binding.nickname);
        RxProgressSubscriber<String> subscriber = new RxProgressSubscriber<>(activity);
        subscriber.setWhat(resultDes).setDialogMessage("登录中...").showProgressDialog(true);
        subscriber.setSubscribeListener(this);
        RxUtil.rxExecute(subscriber.accountDestroy(new ObservableAccountBean(account, nickname).toString()), subscriber);
    }
}