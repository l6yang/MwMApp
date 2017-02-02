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
import com.mwm.loyal.databinding.ActivityAboutBinding;
import com.mwm.loyal.handle.AboutHandler;
import com.mwm.loyal.utils.ApkUtil;
import com.mwm.loyal.utils.ResUtil;
import com.mwm.loyal.utils.TransManage;

public class AboutActivity extends BaseSwipeActivity implements View.OnClickListener {
    @BindView(R.id.pub_title)
    TextView pubTitle;
    @BindView(R.id.pub_back)
    ImageView pubBack;
    @BindView(R.id.pub_menu)
    ImageView pubFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAboutBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_about);
        TransManage.compat(this);
        ButterKnife.bind(this);
        binding.setVersion(ApkUtil.getApkVersion(this));
        binding.setDrawable(ResUtil.getBackground(this));
        binding.setClick(new AboutHandler(this));
        initViews();
    }

    private void initViews() {
        pubFilter.setVisibility(View.GONE);
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
