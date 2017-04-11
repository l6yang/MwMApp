package com.mwm.loyal.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.databinding.ActivityFeedBackBinding;
import com.mwm.loyal.handler.FeedBackHandler;
import com.mwm.loyal.utils.ResUtil;

import butterknife.BindView;

public class FeedBackActivity extends BaseSwipeActivity<ActivityFeedBackBinding> implements View.OnClickListener {
    @BindView(R.id.pub_back)
    ImageView pubBack;
    @BindView(R.id.pub_title)
    TextView pubTitle;
    @BindView(R.id.pub_menu)
    ImageView pubFilter;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_feed_back;
    }

    @Override
    public void afterOnCreate() {
        binding.setClick(new FeedBackHandler(this, binding));
        binding.setDrawable(ResUtil.getBackground(this));
        initViews();
    }

    @Override
    public boolean isTransStatus() {
        return false;
    }

    private void initViews() {
        pubFilter.setVisibility(View.GONE);
        pubBack.setOnClickListener(this);
        pubTitle.setText("问题反馈");
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
