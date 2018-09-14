package com.mwm.loyal.utils;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.schedulers.Schedulers;

public class RxUtil extends com.loyal.base.rxjava.RxUtil{

    public static <T> void rxExecutedByIO(Observable<T> observable, Observer<T> observer) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(observer);
    }
}
