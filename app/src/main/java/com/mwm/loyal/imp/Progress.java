package com.mwm.loyal.imp;

public class Progress {

    public interface ProgressCancelListener {
        void onCancelProgress();
    }

    public interface SubscribeListener<T> {
        void onResult(T t);

        void onError(Throwable e);

        void onCompleted();
    }
}
