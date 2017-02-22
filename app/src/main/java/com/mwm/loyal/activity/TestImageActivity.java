package com.mwm.loyal.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseActivity;
import com.mwm.loyal.databinding.ActivityTestImageBinding;
import com.mwm.loyal.utils.ResUtil;
import com.mwm.loyal.utils.TransManage;

import butterknife.ButterKnife;

public class TestImageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTestImageBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_test_image);
        TransManage.setTranslucentStatus(this);
        ButterKnife.bind(this);
        binding.setDrawable(ResUtil.getBackground(this));
    }

    public void onClick(View view) {
    }
}