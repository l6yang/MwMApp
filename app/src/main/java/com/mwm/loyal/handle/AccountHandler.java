package com.mwm.loyal.handle;

import android.text.TextUtils;
import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.activity.AccountActivity;
import com.mwm.loyal.activity.RegisterActivity;
import com.mwm.loyal.base.BaseActivityHandler;
import com.mwm.loyal.base.BaseProgressSubscriber;
import com.mwm.loyal.beans.LoginBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityAccountBinding;
import com.mwm.loyal.imp.Contact;
import com.mwm.loyal.imp.Progress;
import com.mwm.loyal.utils.ApkUtil;
import com.mwm.loyal.utils.IntentUtil;
import com.mwm.loyal.utils.RetrofitManage;
import com.mwm.loyal.utils.StringUtil;

import rx.Observable;

public class AccountHandler extends BaseActivityHandler implements Progress.SubscribeListener<ResultBean>, Contact {
    private final ActivityAccountBinding binding;

    public AccountHandler(AccountActivity activity, ActivityAccountBinding binding) {
        super(activity);
        this.binding = binding;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.account_mm_reset:
                IntentUtil.toStartActivityForResult(getActivity(), RegisterActivity.class, Int.reqCode_UpdateMM);
                break;
            case R.id.account_device_lock:
                String des = binding.accountDeviceLock.getContentDescription().toString();
                System.out.println(des);
                LoginBean loginBean = new LoginBean();
                loginBean.account.set(getActivity().getIntent().getStringExtra("account"));
                loginBean.lock.set(TextUtils.equals(StringUtil.replaceNull(des), "off") ? "1" : "0");
                loginBean.device.set(ApkUtil.getDeviceID());
                loginBean.mac.set(ApkUtil.getMacAddressFromIp());
                Observable<ResultBean> observable = RetrofitManage.getInstance().getObservableServer().doUpdateAccount(loginBean.toString(), "lock", "");
                BaseProgressSubscriber<ResultBean> subscriber = new BaseProgressSubscriber<>(getActivity(), "doing...", true, false, true);
                subscriber.setSubscribeListener(this);
                RetrofitManage.doEnqueueStr(observable, subscriber);
                break;
        }
    }

    @Override
    public void onResult(ResultBean resultBean) {
        if (TextUtils.equals(StringUtil.replaceNull(resultBean.getResultMsg()), "lock")) {
            showToast("该账号已与此设备绑定");
            binding.accountDeviceLock.setContentDescription(getString(R.string.on));
            binding.accountDeviceLock.setImageResource(R.mipmap.switch_on);
        } else if (TextUtils.equals(StringUtil.replaceNull(resultBean.getResultMsg()), "unlock")) {
            showToast("已取消与此设备绑定");
            binding.accountDeviceLock.setImageResource(R.mipmap.switch_off);
            binding.accountDeviceLock.setContentDescription(getString(R.string.off));
        }
    }

    @Override
    public void onError(Throwable e) {
        showErrorDialog(e.toString());
    }

    @Override
    public void onCompleted() {

    }
}
