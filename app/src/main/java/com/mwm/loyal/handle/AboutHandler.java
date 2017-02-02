package com.mwm.loyal.handle;

import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.activity.AboutActivity;
import com.mwm.loyal.activity.FeedBackActivity;
import com.mwm.loyal.asynctask.VerApkAsync;
import com.mwm.loyal.utils.IntentUtil;
import com.mwm.loyal.utils.ToastUtil;

public class AboutHandler {
    private final AboutActivity aboutActivity;

    public AboutHandler(AboutActivity activity) {
        aboutActivity = activity;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.about_feedBack:
                IntentUtil.toStartActivity(aboutActivity, FeedBackActivity.class);
                break;
            case R.id.about_version:
                doVerApk();
                ToastUtil.showToast(aboutActivity, "当前已是最新版本");
                break;
        }
    }

    private VerApkAsync mVerAuth;

    private void doVerApk() {
        if (mVerAuth != null)
            return;
        mVerAuth = new VerApkAsync(aboutActivity);
        mVerAuth.execute();
    }
}
