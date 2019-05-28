package com.mwm.loyal.handler;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.loyal.kit.GsonUtil;
import com.loyal.kit.TimeUtil;
import com.loyal.rx.RxUtil;
import com.loyal.rx.impl.RxSubscriberListener;
import com.mwm.loyal.R;
import com.mwm.loyal.activity.FeedBackActivity;
import com.mwm.loyal.base.BaseClickHandler;
import com.mwm.loyal.beans.FeedBackBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityFeedbackBinding;
import com.mwm.loyal.libs.rxjava.RxProgressSubscriber;

public class FeedBackHandler extends BaseClickHandler implements RxSubscriberListener<String> {
    private final ActivityFeedbackBinding binding;

    public FeedBackHandler(FeedBackActivity activity, ActivityFeedbackBinding binding) {
        super(activity);
        this.binding = binding;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submitView:
                String content = binding.editFeedBack.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    showToast("请填写反馈内容");
                    return;
                }
                String account = activity.getIntent().getStringExtra("account");
                FeedBackBean backBean = new FeedBackBean(account, content, TimeUtil.getDateTime());
                RxProgressSubscriber<String> subscriber = new RxProgressSubscriber<>(activity);
                subscriber.setDialogMessage("").showProgressDialog(true).setSubscribeListener(this);
                RxUtil.rxExecute(subscriber.feedback(backBean.toString()), subscriber);
                break;
        }
        hideKeyBoard(binding.editFeedBack);
    }

    @Override
    public void onResult(int what, Object tag, String result) {
        ResultBean resultBean = GsonUtil.json2Bean(result, ResultBean.class);
        if (null != resultBean) {
            if (TextUtils.equals("1", resultBean.getCode())) {
                showToast("感谢您的反馈");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.finish();
                    }
                }, 1000);
            } else showDialog(resultBean.getMessage(), false);
        } else showDialog("解析反馈数据失败", false);
    }

    @Override
    public void onError(int what, Object tag, Throwable e) {
        showErrorDialog("", e);
    }
}
