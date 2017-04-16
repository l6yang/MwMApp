package com.mwm.loyal.beans;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.mwm.loyal.utils.ApkUtil;

/**
 * 登录页面数据
 */
public class LoginBean extends BaseObservable {
    public ObservableField<String> account = new ObservableField<>();
    public ObservableField<String> password = new ObservableField<>();
    public ObservableField<String> nickname = new ObservableField<>();
    public ObservableField<String> icon = new ObservableField<>();
    public ObservableField<String> sign = new ObservableField<>();
    public ObservableField<String> device = new ObservableField<>();
    public ObservableInt locked = new ObservableInt();
    public ObservableField<String> serial = new ObservableField<>();
    public ObservableField<String> server = new ObservableField<>();
    public ObservableField<String> oldPassword = new ObservableField<>();
    public ObservableBoolean editable = new ObservableBoolean();

    /**
     * {@link com.mwm.loyal.handler.LoginHandler}
     * {@link com.mwm.loyal.handler.RegisterHandler}
     */
    public LoginBean(String account, String password) {
        this.account.set(account);
        this.password.set(password);
        this.device.set(ApkUtil.getDeviceID());
        this.serial.set(ApkUtil.getDeviceSerial());
    }

    public LoginBean(String account, String password, String oldPassword) {
        this.account.set(account);
        this.password.set(password);
        this.oldPassword.set(oldPassword);
        this.device.set(ApkUtil.getDeviceID());
        this.serial.set(ApkUtil.getDeviceSerial());
    }

    /**
     * {@link com.mwm.loyal.handler.RegisterHandler}
     */
    public LoginBean(String account, String password, boolean isServer, String param) {
        this.account.set(account);
        this.password.set(password);
        if (isServer)
            this.server.set(param);
        else
            this.nickname.set(param);
        this.device.set(ApkUtil.getDeviceID());
        this.serial.set(ApkUtil.getDeviceSerial());
    }

    public LoginBean() {
    }

    public LoginBean(String account, String password, String nickname, String icon, String sign, String device, int locked, String serial, String server, boolean editable) {
        this.account.set(account);
        this.password.set(password);
        this.nickname.set(nickname);
        this.icon.set(icon);
        this.sign.set(sign);
        this.device.set(device);
        this.locked.set(locked);
        this.serial.set(serial);
        this.server.set(server);
        this.editable.set(editable);
    }

    @Override
    public String toString() {
        return "{\"account\":" + (null == account.get() ? null : "\"" + account.get() + "\"") +
                ",\"password\":" + (null == password.get() ? null : "\"" + password.get() + "\"") +
                ",\"nickname\":" + (null == nickname.get() ? null : "\"" + nickname.get() + "\"") +
                ",\"icon\":" + (null == icon.get() ? null : "\"" + icon.get() + "\"") +
                ",\"sign\":" + (null == sign.get() ? null : "\"" + sign.get() + "\"") +
                ",\"device\":" + (null == device.get() ? null : "\"" + device.get() + "\"") +
                ",\"locked\":" + locked.get() +
                ",\"serial\":" + (null == serial.get() ? null : "\"" + serial.get() + "\"") +
                ",\"oldPassword\":" + (null == oldPassword.get() ? null : "\"" + oldPassword.get() + "\"") +
                "}";
    }
}