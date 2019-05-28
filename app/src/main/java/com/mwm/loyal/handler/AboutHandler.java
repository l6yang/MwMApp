package com.mwm.loyal.handler;

import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.activity.AboutActivity;
import com.mwm.loyal.activity.FeedBackActivity;
import com.mwm.loyal.base.BaseClickHandler;
import com.mwm.loyal.databinding.ActivityAboutBinding;

public class AboutHandler extends BaseClickHandler<ActivityAboutBinding> {

    public AboutHandler(AboutActivity activity) {
        super(activity);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.about_feedBack:
                startActivityByAct(FeedBackActivity.class);
                break;
            case R.id.about_version:
                progressDialog.setMessage("正在检查中");
                progressDialog.show();
                //doVerApk();
                break;
        }
    }

    /*private void doVerApk() {
        ObservableServer server = RetrofitManage.getInstance().getObservableServer();
        server.checkUpdate(DeviceUtil.apkVersion(activity))
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
                                activity.showUpdateDialog(resultBean.getResultMsg(), resultBean.getExceptMsg());
                            } else showDialog(resultBean.getResultMsg(), false);
                        } else {
                            showErrorDialog("解析失败");
                        }
                    }
                });
    }*/
}
