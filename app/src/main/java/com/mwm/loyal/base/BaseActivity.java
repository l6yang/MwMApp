package com.mwm.loyal.base;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.imp.ResListener;
import com.mwm.loyal.service.UpdateService;

public abstract class BaseActivity extends AppCompatActivity implements ResListener {
    private UpdateReceiver updateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    showUpdateDialog(context, "检测到有新的版本，是否更新?", apkUrl);
                }
            }
        }
    }

    /**
     * 更新提示
     */
    private void showUpdateDialog(final Context context, String content, final String apkUrl) {
        final AlertDialog myDialog = new AlertDialog.Builder(context).create();
        if (myDialog.isShowing())
            myDialog.dismiss();
        myDialog.show();
        myDialog.setCanceledOnTouchOutside(true);
        myDialog.setCancelable(false);
        if (myDialog.getWindow() != null)
            myDialog.getWindow().setContentView(R.layout.dialog_permission);
        TextView mContent = (TextView) myDialog.getWindow().findViewById(R.id.dialog_tv_content);
        mContent.setText(content);
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
