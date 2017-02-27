package com.mwm.loyal.beans;

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
     * {@link com.mwm.loyal.handle.LoginHandler}
     * {@link com.mwm.loyal.handle.RegisterHandler}
     */
    public LoginBean(String account, String password) {
        this.account.set(account);
        this.password.set(password);
        this.device.set(ApkUtil.getDeviceID());
        this.mac.set(ApkUtil.getMacAddressFromIp());
    }

    /**
     * {@link com.mwm.loyal.handle.RegisterHandler}
     */
    public LoginBean(String account, String password, boolean isServer, String param) {
        this.account.set(account);
        this.password.set(password);
        if (isServer)
            this.server.set(param);
        else
            this.nickname.set(param);
        this.device.set(ApkUtil.getDeviceID());
        this.mac.set(ApkUtil.getMacAddressFromIp());
    }

    public LoginBean() {
    }

    public LoginBean(String account, String password, String nickname, String icon, String signature, String device, String lock, String mac, String server, boolean editable) {
        this.account.set(account);
        this.password.set(password);
        this.nickname.set(nickname);
        this.icon.set(icon);
        this.signature.set(signature);
        this.device.set(device);
        this.lock.set(lock);
        this.mac.set(mac);
        this.server.set(server);
        this.editable.set(editable);
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