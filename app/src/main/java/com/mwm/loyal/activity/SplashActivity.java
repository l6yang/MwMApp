package com.mwm.loyal.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.loyal.basex.impl.CommandViewClickListener;
import com.loyal.basex.impl.AndPerMission;
import com.loyal.rx.impl.MultiplePermissionsListener;
import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseActivity;
import com.mwm.loyal.databinding.ActivitySplashBinding;
import com.mwm.loyal.utils.FileUtil;
import com.mwm.loyal.utils.ImageUtil;

import butterknife.BindView;

public class SplashActivity extends BaseActivity<ActivitySplashBinding> implements MultiplePermissionsListener {
    @BindView(R.id.pub_id)
    TextView mContentView;
    private final SplashRunnable runnable = new SplashRunnable(3);
    private StringBuilder stringBuilder = new StringBuilder();

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    protected int actLayoutRes() {
        return R.layout.activity_splash;
    }

    @Override
    public void afterOnCreate() {
        binding.setDrawable(ImageUtil.getBackground(this));
        multiplePermissions(this, AndPerMission.READ_PHONE_STATE,
                AndPerMission.WRITE_EXTERNAL_STORAGE, AndPerMission.READ_EXTERNAL_STORAGE);
    }

    public void onClick(View view) {
        mContentView.removeCallbacks(runnable);
        startActivityByAct(LoginActivity.class);
        finish();
    }

    @Override
    public void onMultiplePermissions(@NonNull String permissionName, boolean successful, boolean shouldShow) {
        permissionName = replaceNull(permissionName);
        if (!successful) {
            switch (permissionName) {
                case AndPerMission.READ_PHONE_STATE:
                    stringBuilder.append(" 设备状态权限，");
                    break;
                case AndPerMission.WRITE_EXTERNAL_STORAGE:
                    stringBuilder.append(" 存储权限，");
                    break;
            }
        }
        if (!TextUtils.isEmpty(stringBuilder)) {
            showPermissionNextDialog("请开启" + stringBuilder.toString(), new CommandViewClickListener() {
                @Override
                public void onViewClick(DialogInterface dialog, View view, Object tag) {
                    if (null != dialog)
                        dialog.dismiss();
                    startActivity(new Intent(Settings.ACTION_SETTINGS)); //直接进入手机中设置界面
                    finish();
                }
            }, false);
            return;
        }
        switch (permissionName) {
            case AndPerMission.READ_PHONE_STATE:
                break;
            case AndPerMission.WRITE_EXTERNAL_STORAGE:
                FileUtil.createFiles();
                mContentView.setVisibility(View.VISIBLE);
                mContentView.setText(String.format(getString(R.string.skip), 3));
                mContentView.postDelayed(runnable, 1000);
                break;
        }
    }

    private final class SplashRunnable implements Runnable {
        private int mWhat;

        SplashRunnable(int what) {
            this.mWhat = what;
        }

        @Override
        public void run() {
            if (mWhat == 1) {
                onClick(mContentView);
                return;
            }
            mContentView.setText(String.format(getString(R.string.skip), --mWhat));
            mContentView.postDelayed(this, 1000);
        }
    }

    @Override
    protected void onDestroy() {
        mContentView.removeCallbacks(runnable);
        super.onDestroy();
    }
}
