package com.mwm.loyal.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseProgressSubscriber;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityAccountBinding;
import com.mwm.loyal.handle.AccountHandler;
import com.mwm.loyal.imp.Progress;
import com.mwm.loyal.utils.GsonUtil;
import com.mwm.loyal.utils.ResUtil;
import com.mwm.loyal.utils.RetrofitManage;
import com.mwm.loyal.utils.StateBarUtil;
import com.mwm.loyal.utils.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class AccountActivity extends BaseSwipeActivity implements View.OnClickListener, Progress.SubscribeListener<String> {
    @BindView(R.id.pub_back)
    ImageView pubBack;
    @BindView(R.id.pub_title)
    TextView pubTitle;
    @BindView(R.id.pub_menu)
    ImageView pubFilter;
    private ActivityAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_account);
        ButterKnife.bind(this);
        StateBarUtil.setTranslucentStatus(this);
        binding.setClick(new AccountHandler(this, binding));
        binding.setDrawable(ResUtil.getBackground(this));
        initViews();
        checkLocked();
    }

    private void initViews() {
        pubTitle.setText("账号与安全");
        pubFilter.setVisibility(View.GONE);
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
        Observable<String> observable = RetrofitManage.getInstance().getObservableServer().doAccountLocked(account);
        BaseProgressSubscriber<String> subscriber = new BaseProgressSubscriber<>(this, "doing...", true, false, true);
        subscriber.setSubscribeListener(this);
        RetrofitManage.doEnqueueStr(observable, subscriber);
    }

    @Override
    public void onResult(String result) {
        ResultBean bean = GsonUtil.getBeanFromJson(result, ResultBean.class);
        System.out.println(result);
        toggleLocked(TextUtils.equals(StringUtil.replaceNull(bean.getResultMsg()), "1"));
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
    public void onError(Throwable e) {
        System.out.println(TAG + e.toString());
    }

    @Override
    public void onCompleted() {

    }

    private final String TAG = "Account";
}
