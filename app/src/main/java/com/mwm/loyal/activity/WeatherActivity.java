package com.mwm.loyal.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;

import com.loyal.kit.OutUtil;
import com.loyal.kit.TimeUtil;
import com.mwm.loyal.R;
import com.mwm.loyal.adapter.WeatherAdapter;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.beans.WeatherBean;
import com.mwm.loyal.databinding.ActivityWeatherBinding;
import com.mwm.loyal.utils.ImageUtil;
import com.mwm.loyal.utils.PreferUtil;
import com.mwm.loyal.utils.WeatherUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class WeatherActivity extends BaseSwipeActivity<ActivityWeatherBinding> implements View.OnClickListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.grid_weather)
    GridView gridWeather;
    private HandlerClass mHandler;
    private List<WeatherBean.DataBean.ForecastBean> beanList = new ArrayList<>();
    private WeatherAdapter weatherAdapter;

    @Override
    protected int actLayoutRes() {
        return R.layout.activity_weather;
    }

    @Override
    public void afterOnCreate() {
        toolbar.setTitle(getString(R.string.MwMWeather));
        setSupportActionBar(toolbar);
        binding.setDrawable(ImageUtil.getBackground(this));
        mHandler = new HandlerClass(this);
        initViews();
    }

    private void initViews() {
        String city = getIntent().getStringExtra("city");
        binding.setCity(city);
        binding.setDateTime(TimeUtil.getDateTime(TimeUtil.TIME_WEEK));

        gridWeather.setAdapter(weatherAdapter = new WeatherAdapter(this, beanList));
        if (city.endsWith("市"))
            city = city.substring(0, city.length() - "市".length());
        WeatherUtil.getCityWeather(city, mHandler);
    }

    @Override
    public int setEdgePosition() {
        return LEFT;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_change_city:
                Intent intent = new Intent(this, CityActivity.class);
                startActivityForResult(intent, IntImpl.reqCodeCity);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case IntImpl.reqCodeCity:
                String cityName = data.getStringExtra("cityName");
                if (TextUtils.isEmpty(cityName))
                    return;
                binding.setCity(cityName);
                PreferUtil.putString(getApplicationContext(), StrImpl.KEY_CITY, cityName);
                WeatherUtil.getCityWeather(cityName, mHandler);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, new Intent());
        finish();
        super.onBackPressed();
    }

    private static class HandlerClass extends Handler {
        private final WeakReference<WeatherActivity> weakReference;

        HandlerClass(WeatherActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            WeatherActivity activity = weakReference.get();
            switch (msg.what) {
                case IntImpl.rx2Weather:
                    WeatherBean weatherBean = (WeatherBean) msg.obj;
                    try {
                        String wendu = weatherBean.getData().getWendu();
                        activity.binding.setTemperCurrent(wendu + "°");
                        String weather = weatherBean.getData().getForecast().get(0).getType();
                        String high = weatherBean.getData().getForecast().get(0).getHigh();
                        String low = weatherBean.getData().getForecast().get(0).getLow();
                        activity.binding.setWeatherAndTemper(weather + "      " + low.replace("低温", "").trim() + "/" + high.replace("高温", "").trim());
                        activity.binding.setWeatherImg(ImageUtil.getBackground(activity, R.mipmap.ic_weather_cloudy, false));
                        activity.binding.setGanmao(weatherBean.getData().getGanmao());
                        if (activity.weatherAdapter != null)
                            activity.weatherAdapter.notifyList(weatherBean.getData().getForecast());
                    } catch (Exception e) {
                        OutUtil.println("Handler::" + e.toString());
                    }
                    break;
            }
        }
    }
}