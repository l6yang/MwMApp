package com.mwm.loyal.handler;

import android.app.Activity;
import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.activity.AboutActivity;
import com.mwm.loyal.activity.AccountActivity;
import com.mwm.loyal.activity.SettingsActivity;
import com.mwm.loyal.base.BaseClickHandler;
import com.mwm.loyal.databinding.ActivitySettingsBinding;
import com.mwm.loyal.utils.IntentUtil;

public class SettingsHandler extends BaseClickHandler<ActivitySettingsBinding> {

    public SettingsHandler(SettingsActivity activity) {
        super(activity);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings_about:
                IntentUtil.toStartActivity(activity, AboutActivity.class);
                break;
            case R.id.settings_switch:
                activity.setResult(Activity.RESULT_OK);
                activity.finish();
                break;
            case R.id.settings_security:
                IntentUtil.toStartActivityForResult(activity, AccountActivity.class, Int.reqCode_Settings_account);
                break;
        }
    }
}
