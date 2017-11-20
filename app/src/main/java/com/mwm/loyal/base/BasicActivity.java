package com.mwm.loyal.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.mwm.loyal.app.MwMApplication;
import com.mwm.loyal.impl.IntentFrame;
import com.mwm.loyal.utils.IntentUtil;
import com.mwm.loyal.utils.StateBarUtil;

import butterknife.ButterKnife;

public abstract class BasicActivity<T extends ViewDataBinding> extends AppCompatActivity implements IntentFrame {
    protected abstract
    @LayoutRes
    int getLayoutRes();

    protected T binding;

    public abstract void afterOnCreate();

    public abstract boolean isTransStatus();

    protected IntentUtil builder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, getLayoutRes());
        StateBarUtil.setTranslucentStatus(this, isTransStatus());//沉浸式状态栏
        ButterKnife.bind(this);
        afterOnCreate();
        hasIntentParams(false);
    }

    public void hasIntentParams(boolean hasParam) {
        builder = null;
        if (hasParam)
            builder = new IntentUtil(this, getIntent());
        else builder = new IntentUtil(this);
    }

    @Override
    public void startActivity(@Nullable Class<?> tClass) {
        builder.startActivity(tClass);
    }

    @Override
    public void startActivityForResult(@Nullable Class<?> tClass, @IntRange(from = 2) int reqCode) {
        builder.startActivityForResult(tClass, reqCode);
    }

    @Override
    public void startService(@Nullable Class<?> tClass) {
        builder.startService(tClass);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCurrentTag(this);
    }

    protected void setCurrentTag(BasicActivity activity) {
        String tag = activity.getClass().getName();
        MwMApplication.getInstance().setActivityTag(tag);
    }

    protected String getCurrentTag() {
        return MwMApplication.getInstance().getActivityTag();
    }
}
