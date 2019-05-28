package com.mwm.loyal.activity;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.loyal.kit.OutUtil;
import com.loyal.rx.RxUtil;
import com.loyal.rx.impl.RxSubscriberListener;
import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.databinding.ActivityForgetBinding;
import com.mwm.loyal.libs.rxjava.RxProgressSubscriber;
import com.mwm.loyal.utils.ImageUtil;

import butterknife.BindView;

public class ForgetActivity extends BaseSwipeActivity<ActivityForgetBinding> implements View.OnClickListener, RxSubscriberListener<String> {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.forget_test)
    Button button;

    @Override
    protected int actLayoutRes() {
        return R.layout.activity_forget;
    }

    @Override
    public void afterOnCreate() {
        binding.setDrawable(ImageUtil.getBackground(this));
        initViews();
    }

    private void initViews() {
        toolbar.setTitle("重置密码");
        setSupportActionBar(toolbar);
        button.setOnClickListener(this);
    }

    @Override
    public int setEdgePosition() {
        return LEFT;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forget_test:
                RxProgressSubscriber<String> subscriber = new RxProgressSubscriber<>(this, "192.168.0.110");
                subscriber.setSubscribeListener(this);
                RxUtil.rxExecute(subscriber.loginByParams("loyal", "111111"), subscriber);
                break;
        }
    }

    @Override
    public void onResult(int what, Object tag, String s) {
        OutUtil.println("onResult--" + s);
    }

    @Override
    public void onError(int what, Object tag, Throwable e) {
        showErrorToast("", e);
        OutUtil.println("onError--" + e.toString());
    }
}
