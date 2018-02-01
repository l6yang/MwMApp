package com.mwm.loyal.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseListAdapter;
import com.mwm.loyal.beans.WeatherBean;
import com.mwm.loyal.utils.WeatherUtil;

import java.util.List;

import butterknife.BindView;

public class WeatherAdapter extends BaseListAdapter<WeatherBean.DataBean.ForecastBean, WeatherAdapter.ViewHolder> {

    public WeatherAdapter(Context context, List<WeatherBean.DataBean.ForecastBean> arrList) {
        super(context, arrList);
    }

    @Override
    protected int adapterLayout() {
        return R.layout.item_grid_weather;
    }

    @Override
    public ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void onViewHolder(ViewHolder holder, int position) {
        try {
            WeatherBean.DataBean.ForecastBean forecastBean = getArrList().get(position);
            String when = replaceNull(forecastBean.getDate());
            int t = when.lastIndexOf("星期");
            String date = when.substring(0, t);
            String week = when.substring(t, when.length());
            holder.itemWeatherDate.setText(String.format("%s\n%s", date, week));
            String type = replaceNull(forecastBean.getType());
            holder.itemWeatherType.setText(type);
            int resId = WeatherUtil.getWeatherImg(type);
            holder.simWeatherType.setImageURI(Uri.parse("res://mipmap/" + resId));
            String high = replaceNull(forecastBean.getHigh());
            holder.itemWeatherHigh.setText(high.replace("高温", "").trim());
            String low = replaceNull(forecastBean.getLow());
            holder.itemWeatherLow.setText(low.replace("低温", "").trim());
            String wind = replaceNull(forecastBean.getFengli());
            String windPoint = replaceNull(forecastBean.getFengxiang());
            holder.itemWeatherWind.setText(String.format("%s\n%s", windPoint, wind));
        } catch (Exception e) {
            System.out.println("WeatherAdapter Err::" + e);
        }
    }

    class ViewHolder extends BaseListAdapter.ViewHolder {
        @BindView(R.id.item_weather_date)
        TextView itemWeatherDate;
        @BindView(R.id.item_weather_type)
        TextView itemWeatherType;
        @BindView(R.id.simple_weather_type)
        SimpleDraweeView simWeatherType;
        @BindView(R.id.item_weather_high)
        TextView itemWeatherHigh;
        @BindView(R.id.item_weather_low)
        TextView itemWeatherLow;
        @BindView(R.id.item_weather_wind)
        TextView itemWeatherWind;

        public ViewHolder(View view) {
            super(view);
        }
    }
}
