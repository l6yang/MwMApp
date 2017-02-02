package com.mwm.loyal.beans;

import android.databinding.ObservableField;

public class ContactBean {
    private ObservableField<String> account = new ObservableField<>();
    private ObservableField<String> contact = new ObservableField<>();
    private ObservableField<String> time = new ObservableField<>();

    public ContactBean(String account, String contact, String time) {
        this.account.set(account);
        this.contact.set(contact);
        this.time.set(time);
    }

    public ContactBean() {
    }

    @Override
    public String toString() {
        return "{\"account\":" + (account == null ? null : "\"" + account.get() + "\"") +
                ",\"contact\":" + (contact == null ? null : "\"" + contact.get() + "\"") +
                ",\"time\":" + (time == null ? null : "\"" + time.get() + "\"") +
                "}";
    }
}
