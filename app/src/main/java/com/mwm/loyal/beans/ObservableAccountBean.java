package com.mwm.loyal.beans;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.annotation.NonNull;

import com.loyal.kit.DeviceUtil;
import com.mwm.loyal.impl.IContactImpl;
import com.mwm.loyal.impl.ServerImpl;

/**
 * 登录页面数据
 */
public class ObservableAccountBean extends BaseObservable implements IContactImpl {
    public ObservableField<String> account = new ObservableField<>();
    public ObservableField<String> password = new ObservableField<>();
    public ObservableField<String> nickname = new ObservableField<>();
    public ObservableField<String> icon = new ObservableField<>();
    public ObservableField<String> signature = new ObservableField<>();
    public ObservableField<String> device = new ObservableField<>();
    public ObservableField<String> locked = new ObservableField<>();
    public ObservableField<String> serial = new ObservableField<>();
    public ObservableField<String> server = new ObservableField<>();
    public ObservableBoolean editable = new ObservableBoolean();

    /**
     * {@link com.mwm.loyal.handler.LoginHandler}
     * {@link com.mwm.loyal.handler.RegisterHandler}
     */
    public ObservableAccountBean(String account, String password) {
        this.account.set(account);
        this.password.set(password);
    }


    /**
     * {@link com.mwm.loyal.handler.RegisterHandler}
     */
    public ObservableAccountBean(String account, String password, boolean isServer, String param) {
        this.account.set(account);
        this.password.set(password);
        if (isServer)
            this.server.set(param);
        else
            this.nickname.set(param);
    }

    public ObservableAccountBean() {
    }

    public ObservableAccountBean(String account, String password, String nickname, String icon, String signature, String device, String locked, String serial, String server, boolean editable) {
        this.account.set(account);
        this.password.set(password);
        this.nickname.set(nickname);
        this.icon.set(icon);
        this.signature.set(signature);
        this.device.set(device);
        this.locked.set(locked);
        this.serial.set(serial);
        this.server.set(server);
        this.editable.set(editable);
    }

    @NonNull
    @Override
    public String toString() {
        return "{\"account\":" + (null == account.get() ? null : "\"" + account.get() + "\"") +
                ",\"password\":" + (null == password.get() ? null : "\"" + password.get() + "\"") +
                ",\"nickname\":" + (null == nickname.get() ? null : "\"" + nickname.get() + "\"") +
                ",\"icon\":" + (null == icon.get() ? null : "\"" + icon.get() + "\"") +
                ",\"sign\":" + (null == signature.get() ? null : "\"" + signature.get() + "\"") +
                ",\"device\":" + ("\"" + ServerImpl.deviceId() + "\"") +
                ",\"locked\":" + (null == locked.get() ? null : "\"" + locked.get() + "\"") +
                ",\"serial\":" + ("\"" + DeviceUtil.deviceSerial() + "\"") +
                "}";
    }
}