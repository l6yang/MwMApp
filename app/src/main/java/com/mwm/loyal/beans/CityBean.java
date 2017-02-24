package com.mwm.loyal.beans;

import java.io.Serializable;

public class CityBean implements Serializable {
    private String cityName;
    private String cityCode;
    private String cityLetter;

    public CityBean() {
    }

    public CityBean(String cityName, String cityCode, String cityLetter) {
        this.cityName = cityName;
        this.cityCode = cityCode;
        this.cityLetter = cityLetter;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityLetter() {
        return cityLetter;
    }

    public void setCityLetter(String cityLetter) {
        this.cityLetter = cityLetter;
    }
}
