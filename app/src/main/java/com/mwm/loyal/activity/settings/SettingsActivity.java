package com.mwm.loyal.activity.settings;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;

import com.mwm.loyal.R;
import com.mwm.loyal.adapter.SettingsLinkAdapter;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.beans.LinkBean;
import com.mwm.loyal.databinding.ActivitySettingsBinding;
import com.mwm.loyal.utils.ImageUtil;

import butterknife.BindView;

public class SettingsActivity extends BaseSwipeActivity<ActivitySettingsBinding> implements AdapterView.OnItemClickListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected int actLayoutRes() {
        return R.layout.activity_settings;
    }

    @Override
    public void afterOnCreate() {
        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);
        binding.setDrawable(ImageUtil.getBackground(this));
        binding.setAdapter(new SettingsLinkAdapter(this, "json/settings.json", LinkBean.class, true));
        binding.listView.setOnItemClickListener(this);
    }

    @Override
    public int setEdgePosition() {
        return LEFT;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        setResult(resultCode, data);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LinkBean linkBean = (LinkBean) parent.getAdapter().getItem(position);
        String className = linkBean.getLinkClass();
        String forResult = replaceNull(linkBean.getForResult());
        try {
            switch (forResult) {
                case "next":
                    hasIntentParams(true);
                    startActivityByAct(className);
                    break;
                case "nextForResult":
                    hasIntentParams(true);
                    startActivityForResultByAct(className, IntImpl.reqCodeSettings_Account);
                    break;
                case "finishSetResult":
                    setResult(RESULT_OK);
                    finish();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
