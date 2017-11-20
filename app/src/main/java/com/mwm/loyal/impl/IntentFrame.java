package com.mwm.loyal.impl;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

public interface IntentFrame {

    void startActivity(@Nullable Class<?> tClass);

    void startActivityForResult(@Nullable Class<?> tClass, @IntRange(from = 2) int reqCode);

    void startService(@Nullable Class<?> tClass);

}