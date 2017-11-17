package com.mwm.loyal.libs.swipback.app;

import android.databinding.ViewDataBinding;
import android.os.Bundle;

import com.mwm.loyal.base.BaseActivity;
import com.mwm.loyal.libs.swipback.utils.SwipeBackLayout;
import com.mwm.loyal.libs.swipback.utils.SwipeBackUtil;

public abstract class SwipeBackActivity<T extends ViewDataBinding> extends BaseActivity<T> implements SwipeBackActivityBase {
    private SwipeBackActivityHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        SwipeBackUtil.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }
}