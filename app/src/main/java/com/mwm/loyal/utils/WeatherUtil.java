package com.mwm.loyal.utils;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.loyal.base.rxjava.RetrofitManage;
import com.mwm.loyal.R;
import com.mwm.loyal.beans.WeatherBean;
import com.mwm.loyal.impl.IContact;
import com.mwm.loyal.impl.ObservableServer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class WeatherUtil implements IContact {
    public static void getCityWeather(String city, final Handler handler) throws UnsupportedEncodingException {
        if (city.endsWith("市"))
            city = city.substring(0, city.length() - "市".length());
        String weatherUrl = "http://wthrcdn.etouch.cn/weather_mini?city=" + URLEncoder.encode(city, "utf-8");
        Observable<WeatherBean> observable = RetrofitManage.getInstance(weatherUrl+"/").createServer(ObservableServer.class).getWeather(weatherUrl);
        Observer<WeatherBean> subscriber = new Observer<WeatherBean>() {

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(WeatherBean weatherBean) {
                Message message = Message.obtain(handler, IntImpl.rx2Weather, weatherBean);
                message.sendToTarget();
            }
        };
        RxUtil.rxExecuted(observable, subscriber);
    }

    public static int getWeatherImg(String type) {
        int resId = R.mipmap.ic_weather_unknow;
        if (TextUtils.isEmpty(type)) return resId;
        if (TextUtils.equals("多云", type)) {
            resId = R.mipmap.ic_weather_cloudy;
        } else if (TextUtils.equals("晴", type)) {
            resId = R.mipmap.ic_weather_sun;
        } else if (TextUtils.equals("阴", type)) {
            resId = R.mipmap.ic_weather_shade;
        } else if (type.endsWith("雪")) {
            resId = R.mipmap.ic_weather_snow;
        } else if (type.endsWith("雨")) {
            resId = R.mipmap.ic_weather_rainy;
        }
        return resId;
    }
}
