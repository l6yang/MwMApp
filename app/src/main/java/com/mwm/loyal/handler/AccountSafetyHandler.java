package com.mwm.loyal.handler;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.activity.AccountSafetyActivity;
import com.mwm.loyal.activity.RegisterActivity;
import com.mwm.loyal.base.BaseClickHandler;
import com.mwm.loyal.base.BaseProgressSubscriber;
import com.mwm.loyal.beans.LoginBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityAccountBinding;
import com.mwm.loyal.imp.SubscribeListener;
import com.mwm.loyal.utils.ApkUtil;
import com.mwm.loyal.utils.IntentUtil;
import com.mwm.loyal.utils.RetrofitManage;

public class AccountSafetyHandler extends BaseClickHandler<ActivityAccountBinding> implements SubscribeListener<ResultBean> {

    public AccountSafetyHandler(AccountSafetyActivity activity, ActivityAccountBinding binding) {
        super(activity, binding);
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
                loginBean.locked.set(TextUtils.equals(replaceNull(des), "off") ? 1 : 0);
                loginBean.device.set(ApkUtil.getDeviceID());
                loginBean.serial.set(ApkUtil.getDeviceSerial());
                BaseProgressSubscriber<ResultBean> subscriber = new BaseProgressSubscriber<>(activity, this);
                RetrofitManage.rxExecuted(subscriber.doUpdateAccount(loginBean.toString()), subscriber);
                break;
            case R.id.account_destroy:
                Intent intent = new Intent(activity, RegisterActivity.class);
                intent.putExtra("extra", "destroy");
                IntentUtil.toStartActivityForResult(activity, intent, Int.reqCode_destroy);
                break;
        }
    }

    @Override
    public void onResult(int what, ResultBean resultBean) {
        if (null != resultBean) {
            if (1 == resultBean.getResultCode()) {
                String des = binding.accountDeviceLock.getContentDescription().toString();
                if (TextUtils.equals(des, "off")) {
                    showToast("该账号已与此设备绑定");
                    binding.accountDeviceLock.setContentDescription(getString(R.string.on));
                    binding.accountDeviceLock.setImageResource(R.mipmap.switch_on);
                } else {
                    showToast("已取消与此设备绑定");
                    binding.accountDeviceLock.setImageResource(R.mipmap.switch_off);
                    binding.accountDeviceLock.setContentDescription(getString(R.string.off));
                }
            } else showDialog(resultBean.getResultMsg(), false);
        } else showErrorDialog("解析设备锁信息失败", false);
    }

    @Override
    public void onError(int what, Throwable e) {
        System.out.println("onError::" + what + "::" + e.toString());
        showErrorDialog(e.toString(), false);
    }

    @Override
    public void onCompleted(int what) {
    }
}
