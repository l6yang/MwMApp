package com.mwm.loyal.utils;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.loyal.kit.OutUtil;
import com.loyal.kit.ResUtil;
import com.loyal.rx.RetrofitManage;
import com.loyal.rx.RxUtil;
import com.mwm.loyal.R;
import com.mwm.loyal.beans.WeatherBean;
import com.mwm.loyal.impl.IContactImpl;
import com.mwm.loyal.libs.rxjava.ObservableServer;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class WeatherUtil implements IContactImpl {
    public static void getCityWeather(String city, final Handler handler) {
        if (city.endsWith("市"))
            city = city.substring(0, city.length() - "市".length());
        String weatherUrl = "http://wthrcdn.etouch.cn/weather_mini?city=" + ResUtil.encode2Utf8(city);
        Observable<WeatherBean> observable = RetrofitManage.getInstance("http://192.168.0.1/").createServer(ObservableServer.class).getWeather(weatherUrl);
        Observer<WeatherBean> subscriber = new Observer<WeatherBean>() {

            @Override
            public void onError(Throwable e) {
                OutUtil.println("weather", "onError");
            }

            @Override
            public void onComplete() {
                OutUtil.println("weather", "onComplete");
            }

            @Override
            public void onSubscribe(Disposable d) {
                OutUtil.println("weather", "onSubscribe");
            }

            @Override
            public void onNext(WeatherBean weatherBean) {
                OutUtil.println("weather", "onNext");
                Message message = Message.obtain(handler, IntImpl.rx2Weather, weatherBean);
                message.sendToTarget();
            }
        };
        RxUtil.rxExecute(observable, subscriber);
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
