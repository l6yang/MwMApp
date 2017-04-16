package com.mwm.loyal.imp;

public interface SubscribeListener<T> {
    void onResult(int what, T t);

    void onError(int what, Throwable e);

    void onCompleted(int what);
}