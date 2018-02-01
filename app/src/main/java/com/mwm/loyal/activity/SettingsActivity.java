package com.mwm.loyal.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.databinding.ActivitySettingsBinding;
import com.mwm.loyal.handler.SettingsHandler;
import com.mwm.loyal.utils.ImageUtil;

import butterknife.BindView;

public class SettingsActivity extends BaseSwipeActivity<ActivitySettingsBinding> implements View.OnClickListener {
    @BindView(R.id.pub_back)
    ImageView pubBack;
    @BindView(R.id.pub_title)
    TextView pubTitle;
    @BindView(R.id.pub_menu)
    ImageView pubMenu;

    @Override
    protected int actLayoutRes() {
        return R.layout.activity_settings;
    }

    @Override
    public void afterOnCreate() {
        binding.setDrawable(ImageUtil.getBackground(this));
        binding.setClick(new SettingsHandler(this));
        initViews();
    }

    @Override
    public boolean isTransStatus() {
        return false;
    }

    private void initViews() {
        pubTitle.setText("设置");
        pubMenu.setVisibility(View.GONE);
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
