package com.mwm.loyal.activity;

import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseActivity;
import com.mwm.loyal.databinding.ActivityTestImageBinding;
import com.mwm.loyal.utils.ResUtil;

public class TestImageActivity extends BaseActivity<ActivityTestImageBinding> {

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_test_image;
    }

    @Override
    public void afterOnCreate() {
        binding.setDrawable(ResUtil.getBackground(this));
    }

    public void onClick(View view) {
    }
}