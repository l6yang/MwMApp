package com.mwm.loyal.beans;

public class AccountBean {

    /**
     * locked : 0
     * account : loyal
     * nickname : loyal
     * password : 111111
     * signature : y
     */

    private String locked;
    private String account;
    private String nickname;
    private String password;
    private String signature;
    private String serial;
    private String device;
    private byte[] bytes;

    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getDevice() {
        return device;
    }

    public String getSerial() {
        return serial;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
