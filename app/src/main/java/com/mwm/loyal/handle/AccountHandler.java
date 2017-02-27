package com.mwm.loyal.handle;

import android.text.TextUtils;
import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.activity.AccountActivity;
import com.mwm.loyal.activity.RegisterActivity;
import com.mwm.loyal.base.BaseProgressSubscriber;
import com.mwm.loyal.beans.LoginBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityAccountBinding;
import com.mwm.loyal.imp.Progress;
import com.mwm.loyal.imp.Contact;
import com.mwm.loyal.utils.ApkUtil;
import com.mwm.loyal.utils.GsonUtil;
import com.mwm.loyal.utils.IntentUtil;
import com.mwm.loyal.utils.RetrofitManage;
import com.mwm.loyal.utils.StringUtil;
import com.mwm.loyal.utils.ToastUtil;

import rx.Observable;

public class AccountHandler implements Progress.SubscribeListener<String> {
    private final AccountActivity accountActivity;
    private final ActivityAccountBinding binding;

    public AccountHandler(AccountActivity activity, ActivityAccountBinding binding) {
        accountActivity = activity;
        this.binding = binding;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.account_mm_reset:
                IntentUtil.toStartActivityForResult(accountActivity, RegisterActivity.class, Contact.Int.reqCode_Account_UpdateMM);
                break;
            case R.id.account_device_lock:
                String des = binding.accountDeviceLock.getContentDescription().toString();
                System.out.println(des);
                LoginBean loginBean = new LoginBean();
                loginBean.account.set(accountActivity.getIntent().getStringExtra("account"));
                loginBean.lock.set(TextUtils.equals(StringUtil.replaceNull(des), "off") ? "1" : "0");
                loginBean.device.set(ApkUtil.getDeviceID());
                loginBean.mac.set(ApkUtil.getMacAddressFromIp());
                Observable<String> observable = RetrofitManage.getInstance().getObservableServer().doUpdateAccount(loginBean.toString(), "lock", "");
                BaseProgressSubscriber<String> subscriber = new BaseProgressSubscriber<>(accountActivity, "doing...", true, false, true);
                subscriber.setSubscribeListener(this);
                RetrofitManage.doEnqueueStr(observable, subscriber);
                break;
        }
    }

    @Override
    public void onResult(String result) {
        ResultBean bean = GsonUtil.getBeanFromJson(result, ResultBean.class);
        if (TextUtils.equals(StringUtil.replaceNull(bean.getResultMsg()), "lock")) {
            ToastUtil.showToast(accountActivity, "该账号已与此设备绑定");
            binding.accountDeviceLock.setContentDescription(accountActivity.getString(R.string.on));
            binding.accountDeviceLock.setImageResource(R.mipmap.switch_on);
        } else if (TextUtils.equals(StringUtil.replaceNull(bean.getResultMsg()), "unlock")) {
            ToastUtil.showToast(accountActivity, "已取消与此设备绑定");
            binding.accountDeviceLock.setImageResource(R.mipmap.switch_off);
            binding.accountDeviceLock.setContentDescription(accountActivity.getString(R.string.off));
        }
    }

    @Override
    public void onError(Throwable e) {
        System.out.println(e.toString());
    }

    @Override
    public void onCompleted() {

    }
}
