package com.mwm.loyal.activity;

import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.databinding.ActivityFeedbackBinding;
import com.mwm.loyal.handler.FeedBackHandler;
import com.mwm.loyal.utils.ImageUtil;

import butterknife.BindView;

public class FeedBackActivity extends BaseSwipeActivity<ActivityFeedbackBinding> {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected int actLayoutRes() {
        return R.layout.activity_feedback;
    }

    @Override
    public void afterOnCreate() {
        toolbar.setTitle("问题反馈");
        setSupportActionBar(toolbar);
        binding.setClick(new FeedBackHandler(this, binding));
        binding.setDrawable(ImageUtil.getBackground(this));
    }

    @Override
    public int setEdgePosition() {
        return LEFT;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_history:
                startActivityByAct(ListFeedBackActivity.class);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
