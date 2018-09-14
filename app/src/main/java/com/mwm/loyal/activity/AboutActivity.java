package com.mwm.loyal.activity;

import android.view.View;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.databinding.ActivityAboutBinding;
import com.mwm.loyal.handler.AboutHandler;
import com.mwm.loyal.utils.ApkUtil;
import com.mwm.loyal.utils.ImageUtil;

import butterknife.BindView;

public class AboutActivity extends BaseSwipeActivity<ActivityAboutBinding> implements View.OnClickListener {
    @BindView(R.id.pub_title)
    TextView pubTitle;
    @BindView(R.id.pub_back)
    View pubBack;

    @Override
    protected int actLayoutRes() {
        return R.layout.activity_about;
    }

    @Override
    public void afterOnCreate() {
        binding.setVersion(ApkUtil.getApkVersion(this));
        binding.setDrawable(ImageUtil.getBackground(this));
        binding.setClick(new AboutHandler(this));
        initViews();
    }

    private void initViews() {
        pubTitle.setText("关于我们");
        pubBack.setOnClickListener(this);
    }

    @Override
    public int setEdgePosition() {
        return LEFT;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pub_back:
                finish();
                break;
        }
    }
}
