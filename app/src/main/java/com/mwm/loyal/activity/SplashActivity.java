package com.mwm.loyal.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseActivity;
import com.mwm.loyal.databinding.ActivitySplashBinding;
import com.mwm.loyal.utils.ImageUtil;

import butterknife.BindView;

public class SplashActivity extends BaseActivity<ActivitySplashBinding> {
    @BindView(R.id.pub_id)
    TextView mContentView;
    private final SplashRunnable runnable = new SplashRunnable(3);

    @Override
    protected int actLayoutRes() {
        return R.layout.activity_splash;
    }

    @Override
    public void afterOnCreate() {
        binding.setDrawable(ImageUtil.getBackground(this));
    }

    @Override
    public boolean isTransStatus() {
        return false;
    }

    public void onClick(View view) {
        mContentView.removeCallbacks(runnable);
        startActivityByAct(LoginActivity.class);
        finish();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mContentView.setVisibility(View.VISIBLE);
        mContentView.setText(String.format(getString(R.string.skip), 3));
        mContentView.postDelayed(runnable, 1000);
    }

    private final class SplashRunnable implements Runnable {
        private int mWhat = 3;

        SplashRunnable(int what) {
            this.mWhat = what;
        }

        @Override
        public void run() {
            if (mWhat == 0) {
                onClick(mContentView);
                return;
            }
            mContentView.setText(String.format(getString(R.string.skip), --mWhat));
            mContentView.postDelayed(this, 1000);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode != KeyEvent.KEYCODE_BACK && super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContentView.removeCallbacks(runnable);
    }
}
