package com.mwm.loyal.base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.imp.Contact;
import com.mwm.loyal.service.UpdateService;
import com.mwm.loyal.utils.StringUtil;
import com.mwm.loyal.utils.ToastUtil;

public abstract class BaseActivity extends AppCompatActivity implements Contact {
    private UpdateReceiver updateReceiver;
    protected ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDialog();
    }

    private void initDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("");
        Window window = progressDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.alpha = 0.7f;// 透明度
            lp.dimAmount = 0.8f;// 黑暗度
            window.setAttributes(lp);
        }
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateReceiver = new UpdateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Str.action_apkVerCheck);
        registerReceiver(updateReceiver, intentFilter);
    }

    public void showToast(String text) {
        ToastUtil.showToast(this, text);
    }

    public void showErrorDialog(String text, boolean finish) {
        StringUtil.showErrorDialog(this, replaceNull(text), finish);
    }

    public void showDialog(String text, boolean finish) {
        ToastUtil.showDialog(this, replaceNull(text), finish);
    }
    public String replaceNull(Object t) {
        return StringUtil.replaceNull(t);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (updateReceiver != null) {
            unregisterReceiver(updateReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class UpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String apkUrl = intent.getStringExtra("apkUrl");
            if (TextUtils.equals(action, Str.action_apkVerCheck)) {
                if (!TextUtils.isEmpty(apkUrl) && apkUrl.endsWith(".apk")) {
                    showUpdateDialog("检测到有新的版本，是否更新?", apkUrl);
                }
            }
        }
    }

    protected final String getStrWithNull(Object object) {
        return StringUtil.replaceNull(object);
    }

    /**
     * 更新提示
     */
    public void showUpdateDialog(String content, final String apkUrl) {
        final AlertDialog myDialog = new AlertDialog.Builder(this).create();
        if (myDialog.isShowing())
            myDialog.dismiss();
        myDialog.show();
        myDialog.setCanceledOnTouchOutside(true);
        myDialog.setCancelable(false);
        if (myDialog.getWindow() != null)
            myDialog.getWindow().setContentView(R.layout.dialog_permission);
        TextView mContent = (TextView) myDialog.getWindow().findViewById(R.id.dialog_tv_content);
        mContent.setText(getStrWithNull(content));
        Button btn_ok = (Button) myDialog.getWindow().findViewById(R.id.dialog_btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myDialog.isShowing())
                    myDialog.dismiss();
                //update
                UpdateService.startActionUpdate(BaseActivity.this, Str.ACTION_DOWN, apkUrl);
            }
        });
        btn_ok.setText("立即更新");
        btn_ok.setTextSize(16);
        Button btn_cancel = (Button) myDialog.getWindow().findViewById(R.id.dialog_btn_cancel);
        btn_cancel.setText("下次再说");
        btn_cancel.setTextSize(16);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myDialog.isShowing())
                    myDialog.dismiss();
            }
        });
    }
}
