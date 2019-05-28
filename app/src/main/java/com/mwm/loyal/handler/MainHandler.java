package com.mwm.loyal.handler;

import android.text.TextUtils;
import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.activity.WeatherActivity;
import com.mwm.loyal.base.BaseActivity;
import com.mwm.loyal.base.BaseClickHandler;
import com.mwm.loyal.databinding.ActivityMainBinding;
import com.mwm.loyal.utils.PreferUtil;
import com.mwm.loyal.utils.UIHandler;

public class MainHandler extends BaseClickHandler<ActivityMainBinding> {
    public MainHandler(BaseActivity baseActivity, ActivityMainBinding binding) {
        super(baseActivity, binding);
    }

    public void onClick(View view) {
        hideKeyBoard(view);
        UIHandler.delay2Enable(view);
        switch (view.getId()) {
            case R.id.text_weather:
                if (binding.getWeather() != null && !TextUtils.isEmpty(binding.getWeather().trim())) {
                    intentBuilder.putExtra("city", PreferUtil.getString(activity.getApplicationContext(), StrImpl.KEY_CITY, StrImpl.defaultCity));
                    startActivityForResultByAct(WeatherActivity.class, IntImpl.reqCodeWeather);
                }
                break;
        }
    }
}
