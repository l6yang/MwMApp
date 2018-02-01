package com.mwm.loyal.activity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.base.RxProgressSubscriber;
import com.mwm.loyal.databinding.ActivityForgetBinding;
import com.mwm.loyal.impl.ObservableServer;
import com.mwm.loyal.impl.SubscribeListener;
import com.mwm.loyal.utils.ImageUtil;
import com.mwm.loyal.utils.RetrofitManage;
import com.mwm.loyal.utils.RxUtil;

import butterknife.BindView;
import rx.Observable;

public class ForgetActivity extends BaseSwipeActivity<ActivityForgetBinding> implements View.OnClickListener, SubscribeListener<String> {
    @BindView(R.id.pub_back)
    ImageView pubBack;
    @BindView(R.id.pub_title)
    TextView pubTitle;
    @BindView(R.id.pub_menu)
    ImageView pubMenu;
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

    @Override
    public boolean isTransStatus() {
        return false;
    }

    private void initViews() {
        pubTitle.setText("重置密码");
        pubBack.setOnClickListener(this);
        pubMenu.setVisibility(View.GONE);
        button.setOnClickListener(this);
    }

    @Override
    public int setEdgePosition() {
        return LEFT;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pub_back:
                System.gc();
                finish();
                break;
            case R.id.forget_test:
                ObservableServer server = RetrofitManage.getInstance().getObservableServer();
                Observable<String> call = server.doTestLogin("loyal", "111111");
                RxProgressSubscriber<String> subscriber = new RxProgressSubscriber<>(this, this);
                RxUtil.rxExecuted(call, subscriber);
                break;
        }
    }

    @Override
    public void onResult(int what, Object tag, String s) {
        System.out.println("onResult--" + s);
    }

    @Override
    public void onError(int what, Object tag, Throwable e) {
        showErrorToast("", e);
        System.out.println("onError--" + e.toString());
    }
}
