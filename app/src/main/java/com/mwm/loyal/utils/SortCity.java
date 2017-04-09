package com.mwm.loyal.utils;

import com.mwm.loyal.beans.CityBean;

import java.util.Comparator;

public class SortCity implements Comparator<CityBean> {

    @Override
    public int compare(CityBean o1, CityBean o2) {
        return o1.getCityLetter().toLowerCase().compareTo(o2.getCityLetter().toLowerCase());
    }
}
