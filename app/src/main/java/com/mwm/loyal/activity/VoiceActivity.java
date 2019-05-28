package com.mwm.loyal.activity;

import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.databinding.ActivityVoiceBinding;
import com.mwm.loyal.utils.BdttsUtil;
import com.mwm.loyal.utils.FileUtil;
import com.mwm.loyal.utils.ImageUtil;

import butterknife.BindView;

public class VoiceActivity extends BaseSwipeActivity<ActivityVoiceBinding> implements View.OnClickListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private BdttsUtil bdttsUtil = new BdttsUtil();

    @Override
    protected int actLayoutRes() {
        return R.layout.activity_voice;
    }

    @Override
    public int setEdgePosition() {
        return LEFT;
    }

    @Override
    public void afterOnCreate() {
        toolbar.setTitle(R.string.action_voice);
        setSupportActionBar(toolbar);
        binding.setDrawable(ImageUtil.getBackground(this));
        FileUtil.createFiles();
        bdttsUtil.initTTs(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                speak();
                break;
        }
    }

    private void speak() {
        String text = getString(R.string.album_permission_storage_failed_hint);
        //需要合成的文本text的长度不能超过1024个GBK字节。
        bdttsUtil.stop();
        bdttsUtil.speak(text);
    }
}
