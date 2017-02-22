package com.mwm.loyal.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseProgressSubscriber;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.databinding.ActivityForgetBinding;
import com.mwm.loyal.imp.Progress;
import com.mwm.loyal.utils.ResUtil;
import com.mwm.loyal.utils.RetrofitManage;
import com.mwm.loyal.utils.StringUtil;
import com.mwm.loyal.utils.TransManage;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class ForgetActivity extends BaseSwipeActivity implements View.OnClickListener, Progress.SubscribeListener<String> {
    @BindView(R.id.pub_back)
    ImageView pubBack;
    @BindView(R.id.pub_title)
    TextView pubTitle;
    @BindView(R.id.pub_menu)
    ImageView pubFilter;
    @BindView(R.id.forget_test)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityForgetBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_forget);
        ButterKnife.bind(this);
        TransManage.setTranslucentStatus(this);
        binding.setDrawable(ResUtil.getBackground(this));
        initViews();
    }

    private void initViews() {
        pubTitle.setText("重置密码");
        pubBack.setOnClickListener(this);
        pubFilter.setVisibility(View.GONE);
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
                RetrofitManage.ObservableServer server = RetrofitManage.getInstance().getObservableServer();
                Observable<String> call = server.doTestLogin("loyal", "111111");
                BaseProgressSubscriber<String> subscriber = new BaseProgressSubscriber<>(this, "doing...", true, false, true);
                subscriber.setSubscribeListener(this);
                RetrofitManage.doEnqueueStr(call, subscriber);
                break;
        }
    }

    @Override
    public void onResult(String s) {
        System.out.println("onResult--" + s);
    }

    @Override
    public void onError(Throwable e) {
        StringUtil.showErrorToast(this, e.toString());
        System.out.println("onError--" + e.toString());
    }

    @Override
    public void onCompleted() {
    }
}
