package com.mwm.loyal.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.databinding.ActivityAboutBinding;
import com.mwm.loyal.handle.AboutHandler;
import com.mwm.loyal.utils.ApkUtil;
import com.mwm.loyal.utils.ResUtil;

import butterknife.BindView;

public class AboutActivity extends BaseSwipeActivity<ActivityAboutBinding> implements View.OnClickListener {
    @BindView(R.id.pub_title)
    TextView pubTitle;
    @BindView(R.id.pub_back)
    ImageView pubBack;
    @BindView(R.id.pub_menu)
    ImageView pubMenu;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_about;
    }

    @Override
    public void afterOnCreate() {
        binding.setVersion(ApkUtil.getApkVersion(this));
        binding.setDrawable(ResUtil.getBackground(this));
        binding.setClick(new AboutHandler(this));
        initViews();
    }

    @Override
    public boolean isTransStatus() {
        return false;
    }

    private void initViews() {
        pubMenu.setVisibility(View.GONE);
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
