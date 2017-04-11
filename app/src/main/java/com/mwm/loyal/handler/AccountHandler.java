package com.mwm.loyal.handler;

import android.text.TextUtils;
import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.activity.AccountActivity;
import com.mwm.loyal.activity.RegisterActivity;
import com.mwm.loyal.base.BaseClickHandler;
import com.mwm.loyal.base.BaseProgressSubscriber;
import com.mwm.loyal.beans.LoginBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityAccountBinding;
import com.mwm.loyal.imp.Progress;
import com.mwm.loyal.utils.ApkUtil;
import com.mwm.loyal.utils.IntentUtil;
import com.mwm.loyal.utils.RetrofitManage;

import rx.Observable;

public class AccountHandler extends BaseClickHandler<ActivityAccountBinding> implements Progress.SubscribeListener<ResultBean> {

    public AccountHandler(AccountActivity activity, ActivityAccountBinding binding) {
        super(activity,binding);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.account_mm_reset:
                IntentUtil.toStartActivityForResult(activity, RegisterActivity.class, Int.reqCode_UpdateMM);
                break;
            case R.id.account_device_lock:
                String des = binding.accountDeviceLock.getContentDescription().toString();
                System.out.println(des);
                LoginBean loginBean = new LoginBean();
                loginBean.account.set(activity.getIntent().getStringExtra("account"));
                loginBean.lock.set(TextUtils.equals(getStrWithNull(des), "off") ? "1" : "0");
                loginBean.device.set(ApkUtil.getDeviceID());
                loginBean.mac.set(ApkUtil.getMacAddressFromIp());
                Observable<ResultBean> observable = RetrofitManage.getInstance().getObservableServer().doUpdateAccount(loginBean.toString(), "lock", "");
                BaseProgressSubscriber<ResultBean> subscriber = new BaseProgressSubscriber<>(activity, "doing...", true, false, true);
                subscriber.setSubscribeListener(this);
                RetrofitManage.doEnqueueStr(observable, subscriber);
                break;
        }
    }

    @Override
    public void onResult(ResultBean resultBean) {
        if (TextUtils.equals(getStrWithNull(resultBean.getResultMsg()), "lock")) {
            showToast("该账号已与此设备绑定");
            binding.accountDeviceLock.setContentDescription(getString(R.string.on));
            binding.accountDeviceLock.setImageResource(R.mipmap.switch_on);
        } else if (TextUtils.equals(getStrWithNull(resultBean.getResultMsg()), "unlock")) {
            showToast("已取消与此设备绑定");
            binding.accountDeviceLock.setImageResource(R.mipmap.switch_off);
            binding.accountDeviceLock.setContentDescription(getString(R.string.off));
        }
    }

    @Override
    public void onError(Throwable e) {
        showErrorDialog(e.toString(),false);
    }

    @Override
    public void onCompleted() {
    }
}
