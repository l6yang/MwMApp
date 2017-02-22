package com.mwm.loyal.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.databinding.ActivityFeedBackBinding;
import com.mwm.loyal.handle.FeedBackHandler;
import com.mwm.loyal.utils.ResUtil;
import com.mwm.loyal.utils.TransManage;

public class FeedBackActivity extends BaseSwipeActivity implements View.OnClickListener {
    @BindView(R.id.pub_back)
    ImageView pubBack;
    @BindView(R.id.pub_title)
    TextView pubTitle;
    @BindView(R.id.pub_menu)
    ImageView pubFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityFeedBackBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_feed_back);
        TransManage.setTranslucentStatus(this);
        ButterKnife.bind(this);
        binding.setClick(new FeedBackHandler(this, binding));
        binding.setDrawable(ResUtil.getBackground(this));
        initViews();
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
