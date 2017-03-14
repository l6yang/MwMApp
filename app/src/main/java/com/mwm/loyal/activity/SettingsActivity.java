package com.mwm.loyal.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.databinding.ActivitySettingsBinding;
import com.mwm.loyal.handle.SettingsHandler;
import com.mwm.loyal.utils.ResUtil;
import com.mwm.loyal.utils.StateBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends BaseSwipeActivity implements View.OnClickListener {
    @BindView(R.id.pub_back)
    ImageView pubBack;
    @BindView(R.id.pub_title)
    TextView pubTitle;
    @BindView(R.id.pub_menu)
    ImageView pubFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySettingsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        ButterKnife.bind(this);
        StateBarUtil.setTranslucentStatus(this);
        binding.setDrawable(ResUtil.getBackground(this));
        binding.setClick(new SettingsHandler(this));
        initViews();
    }

    private void initViews() {
        pubTitle.setText("设置");
        pubFilter.setVisibility(View.GONE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        setResult(resultCode, data);
        finish();
    }
}
