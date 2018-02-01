package com.mwm.loyal.handler;

import android.text.TextUtils;
import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.activity.AccountSafetyActivity;
import com.mwm.loyal.activity.RegisterActivity;
import com.mwm.loyal.base.BaseClickHandler;
import com.mwm.loyal.base.RxProgressSubscriber;
import com.mwm.loyal.beans.LoginBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityAccountBinding;
import com.mwm.loyal.impl.SubscribeListener;
import com.mwm.loyal.utils.ApkUtil;
import com.mwm.loyal.utils.RxUtil;

public class AccountSafetyHandler extends BaseClickHandler<ActivityAccountBinding> implements SubscribeListener<ResultBean> {

    public AccountSafetyHandler(AccountSafetyActivity activity, ActivityAccountBinding binding) {
        super(activity, binding);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.account_mm_reset:
                startActivityForResultByAct(RegisterActivity.class, Int.reqCode_UpdateMM);
                break;
            case R.id.account_device_lock:
                String des = binding.accountDeviceLock.getContentDescription().toString();
                LoginBean loginBean = new LoginBean();
                loginBean.account.set(activity.getIntent().getStringExtra("account"));
                loginBean.locked.set(TextUtils.equals("off", replaceNull(des)) ? 1 : 0);
                loginBean.device.set(ApkUtil.getDeviceID());
                loginBean.serial.set(ApkUtil.getDeviceSerial());
                RxProgressSubscriber<ResultBean> subscriber = new RxProgressSubscriber<>(activity, this);
                RxUtil.rxExecuted(subscriber.doUpdateAccount(loginBean.toString()), subscriber);
                break;
            case R.id.account_destroy:
                builder.putExtra("extra", "destroy");
                startActivityForResultByAct(RegisterActivity.class, Int.reqCode_destroy);
                break;
        }
    }

    @Override
    public void onResult(int what, Object tag, ResultBean resultBean) {
        if (null != resultBean) {
            if (1 == resultBean.getResultCode()) {
                String des = binding.accountDeviceLock.getContentDescription().toString();
                if (TextUtils.equals("off", des)) {
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
    public void onError(int what, Object tag, Throwable e) {
        showErrorDialog(e.toString(), false);
    }
}
