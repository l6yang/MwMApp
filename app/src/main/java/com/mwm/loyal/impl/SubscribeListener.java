package com.mwm.loyal.impl;

public interface SubscribeListener<T> {
    void onResult(int what, Object tag, T t);

    void onError(int what, Object tag, Throwable e);

}