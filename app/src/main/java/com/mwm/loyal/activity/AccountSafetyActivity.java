package com.mwm.loyal.activity;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.loyal.kit.GsonUtil;
import com.loyal.kit.OutUtil;
import com.loyal.rx.RxUtil;
import com.loyal.rx.impl.RxSubscriberListener;
import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityAccountBinding;
import com.mwm.loyal.handler.AccountSafetyHandler;
import com.mwm.loyal.libs.rxjava.RxProgressSubscriber;
import com.mwm.loyal.utils.ImageUtil;

import butterknife.BindView;

public class AccountSafetyActivity extends BaseSwipeActivity<ActivityAccountBinding> implements RxSubscriberListener<String> {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected int actLayoutRes() {
        return R.layout.activity_account;
    }

    @Override
    public void afterOnCreate() {
        toolbar.setTitle("账号与安全");
        setSupportActionBar(toolbar);
        binding.setClick(new AccountSafetyHandler(this, binding));
        binding.setDrawable(ImageUtil.getBackground(this));
        checkLocked();
    }

    @Override
    public int setEdgePosition() {
        return LEFT;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        setResult(resultCode, data);
        finish();
    }

    private void checkLocked() {
        String account = getIntent().getStringExtra("account");
        RxProgressSubscriber<String> subscriber = new RxProgressSubscriber<>(this);
        subscriber.setDialogMessage("加载中...").showProgressDialog(true);
        subscriber.setSubscribeListener(this);
        RxUtil.rxExecute(subscriber.accountLock(account), subscriber);
    }

    @Override
    public void onResult(int what, Object tag, String result) {
        OutUtil.println(result);
        ResultBean resultBean = GsonUtil.json2Bean(result, ResultBean.class);
        if (resultBean != null) {
            if (TextUtils.equals("1", resultBean.getCode())) {
                toggleLocked(TextUtils.equals("1", replaceNull(resultBean.getMessage())));
            } else showToast(resultBean.getMessage());
        } else
            showToast("解析设备锁信息失败");
    }

    private void toggleLocked(boolean locked) {
        if (locked) {
            binding.accountDeviceLock.setContentDescription(getString(R.string.on));
            binding.accountDeviceLock.setImageResource(R.mipmap.switch_on);
        } else {
            binding.accountDeviceLock.setImageResource(R.mipmap.switch_off);
            binding.accountDeviceLock.setContentDescription(getString(R.string.off));
        }
    }

    @Override
    public void onError(int what, Object tag, Throwable e) {
        showErrorDialog("失败", e);
    }

}
