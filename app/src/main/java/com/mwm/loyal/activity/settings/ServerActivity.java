package com.mwm.loyal.activity.settings;

import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.databinding.ActivityServerBinding;
import com.mwm.loyal.utils.ImageUtil;

import butterknife.BindView;

public class ServerActivity extends BaseSwipeActivity<ActivityServerBinding> implements View.OnClickListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public int setEdgePosition() {
        return LEFT;
    }

    @Override
    protected int actLayoutRes() {
        return R.layout.activity_server;
    }

    @Override
    public void afterOnCreate() {
        toolbar.setTitle("接入地址");
        setSupportActionBar(toolbar);
        binding.setDrawable(ImageUtil.getBackground(this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
