package com.mwm.loyal.handler;

import android.app.Activity;
import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.activity.AboutActivity;
import com.mwm.loyal.activity.AccountSafetyActivity;
import com.mwm.loyal.activity.SettingsActivity;
import com.mwm.loyal.base.BaseClickHandler;
import com.mwm.loyal.databinding.ActivitySettingsBinding;

public class SettingsHandler extends BaseClickHandler<ActivitySettingsBinding> {

    public SettingsHandler(SettingsActivity activity) {
        super(activity);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings_about:
                startActivityByAct(AboutActivity.class);
                break;
            case R.id.settings_switch:
                activity.setResult(Activity.RESULT_OK);
                activity.finish();
                break;
            case R.id.settings_security:
                startActivityForResultByAct(AccountSafetyActivity.class, Int.reqCode_Settings_account);
                break;
        }
    }
}
