package com.mwm.loyal.handler;

import android.content.DialogInterface;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.activity.FeedBackActivity;
import com.mwm.loyal.base.BaseClickHandler;
import com.mwm.loyal.base.BaseAsyncTask;
import com.mwm.loyal.beans.FeedBackBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityFeedBackBinding;
import com.mwm.loyal.imp.Contact;
import com.mwm.loyal.utils.GsonUtil;
import com.mwm.loyal.utils.RetrofitManage;
import com.mwm.loyal.utils.StringUtil;
import com.mwm.loyal.utils.TimeUtil;
import com.mwm.loyal.utils.ToastUtil;

import java.io.IOException;

import retrofit2.Call;

public class FeedBackHandler extends BaseClickHandler implements Contact {
    private final ActivityFeedBackBinding binding;
    private FeedBackAsync mFeedAuth;

    public FeedBackHandler(FeedBackActivity activity, ActivityFeedBackBinding binding) {
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
                if (mFeedAuth != null)
                    return;
                mFeedAuth = new FeedBackAsync(backBean);
                mFeedAuth.execute();
                break;

        }
    }

    private class FeedBackAsync extends BaseAsyncTask<Void, Void, String> implements DialogInterface.OnCancelListener {
        private final FeedBackBean feedBackBean;

        FeedBackAsync(FeedBackBean backBean) {
            super(activity);
            feedBackBean = backBean;
            setDialogMessage("反馈中...稍等");
            setCancelable(true);
            setCanceledOnTouchOutside(false);
        }

        @Override
        protected String doInBackground(Void... voids) {
            Call<String> call = RetrofitManage.getInstance().getRequestServer().doFeedBack(feedBackBean.toString());
            try {
                //RequestBody body = new FormBody.Builder()
                //.add("json_feed", feedBackBean.toString()).build();
                //return OkHttpClientManager.getInstance().post_jsonDemo(StringUtil.getServiceUrl(Str.method_feedBack), body);
                return RetrofitManage.doExecuteStr(call);
            } catch (IOException e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            cancelDialog();
            mFeedAuth = null;
            if (StringUtil.showErrorToast(activity, result)) return;
            ResultBean bean = GsonUtil.getBeanFromJson(result, ResultBean.class);
            if (bean.getResultCode() == 1) {
                ToastUtil.showToast(activity, "感谢您的反馈");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.finish();
                    }
                }, 1000);
            } else {
                ToastUtil.showToast(activity, getStrWithNull(bean.getResultMsg()));
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mFeedAuth = null;
        }
    }
}
