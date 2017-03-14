package com.mwm.loyal.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.adapter.WeatherAdapter;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.beans.WeatherBean;
import com.mwm.loyal.databinding.ActivityWeatherBinding;
import com.mwm.loyal.utils.IntentUtil;
import com.mwm.loyal.utils.PreferencesUtil;
import com.mwm.loyal.utils.ResUtil;
import com.mwm.loyal.utils.StateBarUtil;
import com.mwm.loyal.utils.TimeUtil;
import com.mwm.loyal.utils.WeatherUtil;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherActivity extends BaseSwipeActivity implements View.OnClickListener {
    @BindView(R.id.pub_title)
    TextView pubTitle;
    @BindView(R.id.pub_back)
    ImageView pubBack;
    @BindView(R.id.pub_menu)
    ImageView pubMenu;
    @BindView(R.id.grid_weather)
    GridView gridWeather;
    private ActivityWeatherBinding binding;
    private HandlerClass mHandler;
    private List<WeatherBean.DataBean.ForecastBean> beanList = new ArrayList<>();
    private WeatherAdapter weatherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_weather);
        binding.setDrawable(ResUtil.getBackground(this));
        StateBarUtil.setTranslucentStatus(this);
        ButterKnife.bind(this);
        mHandler = new HandlerClass(this);
        initViews();
    }

    private void initViews() {
        String city = getIntent().getStringExtra("city");
        binding.setCity(city);
        binding.setDateTime(TimeUtil.getDateTime(Str.TIME_WEEK));
        pubMenu.setVisibility(View.GONE);
        pubTitle.setText(getString(R.string.MwMWeather));
        pubBack.setOnClickListener(this);
        gridWeather.setAdapter(weatherAdapter = new WeatherAdapter(this, beanList));
        try {
            if (city.endsWith("市"))
                city = city.substring(0, city.length() - "市".length());
            WeatherUtil.getCityWeather(city, mHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int setEdgePosition() {
        return LEFT;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pub_back:
                finish();
                break;
            case R.id.text_change_city:
                Intent intent = new Intent(this, CityActivity.class);
                IntentUtil.toStartActivityForResult(this, intent, Int.reqCode_Weather_city);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case Int.reqCode_Weather_city:
                String cityName = data.getStringExtra("cityName");
                if (TextUtils.isEmpty(cityName))
                    return;
                binding.setCity(cityName);
                PreferencesUtil.putString(getApplicationContext(), Str.KEY_CITY, cityName);
                try {
                    WeatherUtil.getCityWeather(cityName,mHandler);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
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
                case Int.rx2Weather:
                    WeatherBean weatherBean = (WeatherBean) msg.obj;
                    try {
                        String wendu = weatherBean.getData().getWendu();
                        activity.binding.setTemperCurrent(wendu + "°");
                        String weather = weatherBean.getData().getForecast().get(0).getType();
                        String high = weatherBean.getData().getForecast().get(0).getHigh();
                        String low = weatherBean.getData().getForecast().get(0).getLow();
                        activity.binding.setWeatherAndTemper(weather + "      " + low.replace("低温", "").trim() + "/" + high.replace("高温", "").trim());
                        activity.binding.setWeatherImg(ResUtil.getBackground(activity, R.mipmap.cloudy, false));
                        activity.binding.setGanmao(weatherBean.getData().getGanmao());
                        if (activity.weatherAdapter != null)
                            activity.weatherAdapter.refreshList(weatherBean.getData().getForecast());
                    } catch (Exception e) {
                        System.out.println("Handler::" + e.toString());
                    }
                    break;
            }
        }
    }
}