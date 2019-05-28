package com.mwm.loyal.activity;

import android.support.v7.widget.Toolbar;
import android.view.View;

import com.loyal.kit.DeviceUtil;
import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.databinding.ActivityAboutBinding;
import com.mwm.loyal.handler.AboutHandler;
import com.mwm.loyal.utils.ImageUtil;

import butterknife.BindView;

public class AboutActivity extends BaseSwipeActivity<ActivityAboutBinding> implements View.OnClickListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected int actLayoutRes() {
        return R.layout.activity_about;
    }

    @Override
    public void afterOnCreate() {
        toolbar.setTitle("关于我们");
        setSupportActionBar(toolbar);
        binding.setVersion(DeviceUtil.apkVersion(this));
        binding.setDrawable(ImageUtil.getBackground(this));
        binding.setClick(new AboutHandler(this));
    }

    @Override
    public int setEdgePosition() {
        return LEFT;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }
}
