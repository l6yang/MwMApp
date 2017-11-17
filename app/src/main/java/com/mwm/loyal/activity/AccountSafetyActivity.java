package com.mwm.loyal.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.base.RxProgressSubscriber;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityAccountBinding;
import com.mwm.loyal.handler.AccountSafetyHandler;
import com.mwm.loyal.impl.SubscribeListener;
import com.mwm.loyal.utils.ResUtil;
import com.mwm.loyal.utils.RetrofitManage;

import butterknife.BindView;

public class AccountSafetyActivity extends BaseSwipeActivity<ActivityAccountBinding> implements View.OnClickListener, SubscribeListener<ResultBean> {
    @BindView(R.id.pub_back)
    ImageView pubBack;
    @BindView(R.id.pub_title)
    TextView pubTitle;
    @BindView(R.id.pub_menu)
    ImageView pubMenu;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_account;
    }

    @Override
    public void afterOnCreate() {
        binding.setClick(new AccountSafetyHandler(this, binding));
        binding.setDrawable(ResUtil.getBackground(this));
        initViews();
        checkLocked();
    }

    @Override
    public boolean isTransStatus() {
        return false;
    }

    private void initViews() {
        pubTitle.setText("账号与安全");
        pubMenu.setVisibility(View.GONE);
        pubBack.setOnClickListener(this);
        binding.accountDeviceLock.setOnClickListener(this);
    }

    @Override
    public int setEdgePosition() {
        return LEFT;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pub_back:
                finish();
                break;
        }
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
        RxProgressSubscriber<ResultBean> subscriber = new RxProgressSubscriber<>(this, this);
        RetrofitManage.rxExecuted(subscriber.doAccountLocked(account), subscriber);
    }

    @Override
    public void onResult(int what, Object tag, ResultBean resultBean) {
        if (resultBean != null) {
            if (1 == resultBean.getResultCode()) {
                toggleLocked(TextUtils.equals("1", replaceNull(resultBean.getResultMsg())));
            } else showDialog(resultBean.getResultMsg(), false);
        } else
            showErrorDialog("解析设备锁信息失败", false);
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
        showErrorDialog(e.toString(), false);
    }

}
