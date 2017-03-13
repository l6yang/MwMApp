package com.mwm.loyal.handle;

import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.activity.AboutActivity;
import com.mwm.loyal.activity.FeedBackActivity;
import com.mwm.loyal.base.BaseActivityHandler;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.utils.ApkUtil;
import com.mwm.loyal.utils.IntentUtil;
import com.mwm.loyal.utils.RetrofitManage;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AboutHandler extends BaseActivityHandler {

    public AboutHandler(AboutActivity activity) {
        super(activity);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.about_feedBack:
                IntentUtil.toStartActivity(getActivity(), FeedBackActivity.class);
                break;
            case R.id.about_version:
                progressDialog.setMessage("正在检查中");
                progressDialog.show();
                doVerApk();
                break;
        }
    }

    private void doVerApk() {
        RetrofitManage.ObservableServer server = RetrofitManage.getInstance().getObservableServer();
        server.doApkVer(ApkUtil.getApkVersion(getActivity()))
                .subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResultBean>() {
                    @Override
                    public void onCompleted() {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        showErrorDialog(e.toString(), false);
                    }

                    @Override
                    public void onNext(ResultBean resultBean) {
                        if (resultBean != null) {
                            if (resultBean.getResultCode() == 1) {
                                getActivity().showUpdateDialog(resultBean.getResultMsg(), resultBean.getExceptMsg());
                            } else showDialog(resultBean.getResultMsg(), false);
                        } else {
                            showErrorDialog("解析失败", false);
                        }
                    }
                });
    }
}
