package com.mwm.loyal.libs.swipback.app;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.libs.swipback.utils.SwipeBackLayout;
import com.mwm.loyal.libs.swipback.utils.SwipeBackUtil;

public class SwipeBackActivityHelper<V> {
    private Activity mActivity;

    private SwipeBackLayout mSwipeBackLayout;

    public SwipeBackActivityHelper(Activity activity) {
        mActivity = activity;
    }

    public void onActivityCreate() {
        mActivity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mActivity.getWindow().getDecorView().setBackground(null);
        mSwipeBackLayout = (SwipeBackLayout) LayoutInflater.from(mActivity).inflate(
                R.layout.swipeback_layout, null);
        mSwipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener() {
            @Override
            public void onScrollStateChange(int state, float scrollPercent) {
            }

            @Override
            public void onEdgeTouch(int edgeFlag) {
                SwipeBackUtil.convertActivityToTranslucent(mActivity);
            }

            @Override
            public void onScrollOverThreshold() {

            }
        });
    }

    public void onPostCreate() {
        mSwipeBackLayout.attachToActivity(mActivity);
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackLayout;
    }
}