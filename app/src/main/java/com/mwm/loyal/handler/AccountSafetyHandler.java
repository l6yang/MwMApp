package com.mwm.loyal.handler;

import android.text.TextUtils;
import android.view.View;

import com.loyal.kit.DeviceUtil;
import com.loyal.kit.GsonUtil;
import com.loyal.rx.RxUtil;
import com.loyal.rx.impl.RxSubscriberListener;
import com.mwm.loyal.R;
import com.mwm.loyal.activity.AccountSafetyActivity;
import com.mwm.loyal.activity.RegisterActivity;
import com.mwm.loyal.base.BaseClickHandler;
import com.mwm.loyal.beans.ObservableAccountBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityAccountBinding;
import com.mwm.loyal.impl.ServerImpl;
import com.mwm.loyal.libs.rxjava.RxProgressSubscriber;

public class AccountSafetyHandler extends BaseClickHandler<ActivityAccountBinding> implements RxSubscriberListener<String> {

    public AccountSafetyHandler(AccountSafetyActivity activity, ActivityAccountBinding binding) {
        super(activity, binding);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.account_mm_reset:
                hasIntentParams(true);
                startActivityForResultByAct(RegisterActivity.class, IntImpl.reqCodeUpdateMM);
                break;
            case R.id.account_device_lock:
                String des = binding.accountDeviceLock.getContentDescription().toString();
                ObservableAccountBean observableAccountBean = new ObservableAccountBean();
                observableAccountBean.account.set(activity.getIntent().getStringExtra("account"));
                observableAccountBean.locked.set(TextUtils.equals("off", replaceNull(des)) ? "1" : "0");
                observableAccountBean.device.set(ServerImpl.deviceId());
                observableAccountBean.serial.set(DeviceUtil.deviceSerial());
                RxProgressSubscriber<String> subscriber = new RxProgressSubscriber<>(activity);
                subscriber.setDialogMessage("").showProgressDialog(true);
                subscriber.setSubscribeListener(this);
                RxUtil.rxExecute(subscriber.accountUpdate(observableAccountBean.toString()), subscriber);
                break;
            case R.id.account_destroy:
                intentBuilder.putExtra("extra", "destroy");
                startActivityForResultByAct(RegisterActivity.class, IntImpl.reqCodeDestroy);
                break;
        }
    }

    @Override
    public void onResult(int what, Object tag, String result) {
        ResultBean resultBean = GsonUtil.json2Bean(result, ResultBean.class);
        if (null != resultBean) {
            if (TextUtils.equals("1", resultBean.getCode())) {
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
            } else showToast(resultBean.getMessage());
        } else showToast("解析设备锁信息失败");
    }

    @Override
    public void onError(int what, Object tag, Throwable e) {
        showErrorDialog("",e);
    }
}
