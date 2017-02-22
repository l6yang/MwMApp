package com.mwm.loyal.handle;

import android.app.Activity;
import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.activity.AboutActivity;
import com.mwm.loyal.activity.AccountActivity;
import com.mwm.loyal.activity.SettingsActivity;
import com.mwm.loyal.imp.Contact;
import com.mwm.loyal.utils.IntentUtil;

public class SettingsHandler implements Contact {
    private final SettingsActivity settingsActivity;

    public SettingsHandler(SettingsActivity activity) {
        settingsActivity = activity;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings_about:
                IntentUtil.toStartActivity(settingsActivity, AboutActivity.class);
                break;
            case R.id.settings_switch:
                settingsActivity.setResult(Activity.RESULT_OK);
                settingsActivity.finish();
                break;
            case R.id.settings_security:
                IntentUtil.toStartActivityForResult(settingsActivity, AccountActivity.class, Int.reqCode_Settings_account);
                break;
        }
    }
}
