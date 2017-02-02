package com.mwm.loyal.beans;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.mwm.loyal.utils.ApkUtil;

/**
 * 登录页面数据
 */
public class LoginBean extends BaseObservable {
    public ObservableField<String> account = new ObservableField<>();
    public ObservableField<String> password = new ObservableField<>();
    public ObservableField<String> nickname = new ObservableField<>();
    public ObservableField<String> icon = new ObservableField<>();
    public ObservableField<String> signature = new ObservableField<>();
    public ObservableField<String> device = new ObservableField<>();
    public ObservableField<String> lock = new ObservableField<>();
    public ObservableField<String> mac = new ObservableField<>();
    public ObservableField<String> server = new ObservableField<>();
    public ObservableBoolean editable = new ObservableBoolean();

    /**
     * {@link com.mwm.loyal.utils.PreferencesUtil}
     */
    public LoginBean(String account, String password, String server) {
        this.account.set(account);
        this.password.set(password);
        this.server.set(server);
    }

    /**
     * {@link com.mwm.loyal.handle.LoginHandler}
     * {@link com.mwm.loyal.handle.RegisterHandler}
     */
    public LoginBean(String account, String password, Context context) {
        this.account.set(account);
        this.password.set(password);
        this.device.set(ApkUtil.getDeviceID());
        this.mac.set(ApkUtil.getMAC(context));
    }

    /**
     * {@link com.mwm.loyal.handle.RegisterHandler}
     */
    public LoginBean(String account, String password, String nickname, Context context) {
        this.account.set(account);
        this.password.set(password);
        this.nickname.set(nickname);
        this.device.set(ApkUtil.getDeviceID());
        this.mac.set(ApkUtil.getMAC(context));
    }

    /**
     * {@link com.mwm.loyal.activity.AccountActivity}
     */
    public LoginBean(String account, Context context) {
        this.account.set(account);
        this.device.set(ApkUtil.getDeviceID());
        this.mac.set(ApkUtil.getMAC(context));
    }

    public LoginBean() {
    }

    @Override
    public String toString() {
        return "{\"account\":" + (account.get() == null ? null : "\"" + account.get() + "\"") +
                ",\"password\":" + (password.get() == null ? null : "\"" + password.get() + "\"") +
                ",\"nickname\":" + (nickname.get() == null ? null : "\"" + nickname.get() + "\"") +
                ",\"icon\":" + (icon.get() == null ? null : "\"" + icon.get() + "\"") +
                ",\"signature\":" + (signature.get() == null ? null : "\"" + signature.get() + "\"") +
                ",\"device\":" + (device.get() == null ? null : "\"" + device.get() + "\"") +
                ",\"lock\":" + (lock.get() == null ? "\"0\"" : "\"" + lock.get() + "\"") +
                ",\"mac\":" + (mac.get() == null ? null : "\"" + mac.get() + "\"") +
                ",\"server\":" + (server.get() == null ? null : "\"" + server.get() + "\"") +
                "}";
    }
}