package com.mwm.loyal.handler;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.loyal.base.rxjava.impl.SubscribeListener;
import com.loyal.base.util.TimeUtil;
import com.mwm.loyal.R;
import com.mwm.loyal.activity.FeedBackActivity;
import com.mwm.loyal.base.BaseClickHandler;
import com.mwm.loyal.base.RxProgressSubscriber;
import com.mwm.loyal.beans.FeedBackBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityFeedbackBinding;
import com.mwm.loyal.utils.RxUtil;
import com.mwm.loyal.utils.ToastUtil;

public class FeedBackHandler extends BaseClickHandler implements SubscribeListener<ResultBean> {
    private final ActivityFeedbackBinding binding;

    public FeedBackHandler(FeedBackActivity activity, ActivityFeedbackBinding binding) {
        super(activity);
        this.binding = binding;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pub_submit:
                String content = binding.editFeedBack.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showToast(activity, "请填写反馈内容");
                    return;
                }
                String account = activity.getIntent().getStringExtra("account");
                FeedBackBean backBean = new FeedBackBean(account, content, TimeUtil.getDateTime());
                RxProgressSubscriber<ResultBean> subscriber = new RxProgressSubscriber<>(activity, this);
                RxUtil.rxExecuted(subscriber.doFeedBack(backBean.toString()), subscriber);
                break;
        }
        ToastUtil.hideInput(activity, binding.editFeedBack.getWindowToken());
    }

    @Override
    public void onResult(int what, Object tag, ResultBean resultBean) {
        if (null != resultBean) {
            if (1 == resultBean.getResultCode()) {
                ToastUtil.showToast(activity, "感谢您的反馈");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.finish();
                    }
                }, 1000);
            } else showDialog(resultBean.getResultMsg(), false);
        } else showDialog("解析反馈数据失败", false);
    }

    @Override
    public void onError(int what, Object tag, Throwable e) {
        showErrorDialog(e+"");
    }
}
