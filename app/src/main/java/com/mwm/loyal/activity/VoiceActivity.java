package com.mwm.loyal.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseActivity;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.databinding.ActivityVoiceBinding;
import com.mwm.loyal.utils.FileUtil;
import com.mwm.loyal.utils.ResUtil;
import com.mwm.loyal.utils.StateBarUtil;
import com.mwm.loyal.utils.ToastUtil;
import com.mwm.loyal.utils.VoiceUtil;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mwm.loyal.utils.VoiceUtil.ENGLISH_SPEECH_FEMALE_MODEL_NAME;
import static com.mwm.loyal.utils.VoiceUtil.ENGLISH_TEXT_MODEL_NAME;

public class VoiceActivity extends BaseSwipeActivity implements SpeechSynthesizerListener, View.OnClickListener {
    @BindView(R.id.pub_title)
    TextView pubTitle;
    @BindView(R.id.pub_back)
    ImageView pubBack;
    @BindView(R.id.pub_menu)
    ImageView pubMenu;
    private HandlerClass mHandler;
    private SpeechSynthesizer mSpeechSynthesizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityVoiceBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_voice);
        ButterKnife.bind(this);
        StateBarUtil.setTranslucentStatus(this);
        binding.setDrawable(ResUtil.getBackground(this));
        mHandler = new HandlerClass(this);
        initView();
        initPresenter();
    }

    @Override
    public int setEdgePosition() {
        return LEFT;
    }

    private void initView() {
        FileUtil.createFiles();
        pubBack.setOnClickListener(this);
        pubMenu.setVisibility(View.GONE);
        pubTitle.setText(R.string.action_voice);
        VoiceUtil voiceUtil = new VoiceUtil(this);
        mSpeechSynthesizer = voiceUtil.getSpeechSynthesizer();
    }

    private void initPresenter() {
        mSpeechSynthesizer.setSpeechSynthesizerListener(this);
        AuthInfo authInfo = mSpeechSynthesizer.auth(TtsMode.MIX);

        if (authInfo.isSuccess()) {
            toPrint("auth success");
        } else {
            String errorMsg = authInfo.getTtsError().getDetailMessage();
            toPrint("auth failed errorMsg=" + errorMsg);
        }
        // 初始化tts
        mSpeechSynthesizer.initTts(TtsMode.MIX);
        // 加载离线英文资源（提供离线英文合成功能）
        int result =
                mSpeechSynthesizer.loadEnglishModel(FileUtil.path_voice + ENGLISH_TEXT_MODEL_NAME, FileUtil.path_voice
                        + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        toPrint("loadEnglishModel result=" + result);

        //打印引擎信息和model基本信息
        printEngineInfo();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pub_back:
                finish();
                System.gc();
                break;
            case R.id.button:
                speak();
                break;
        }
    }

    private void speak() {
        String text = getString(R.string.album_permission_storage_failed_hint);
        //需要合成的文本text的长度不能超过1024个GBK字节。
        int result = mSpeechSynthesizer.speak(text);
        if (result < 0) {
            toPrint("error,please look up error code in doc or URL:http://yuyin.baidu.com/docs/tts/122 ");
        }
    }

    @Override
    public void onSynthesizeStart(String utteranceId) {
        toPrint("onSynthesizeStart utteranceId=" + utteranceId);
    }

    /**
     * 合成数据和进度的回调接口，分多次回调
     *
     * @param utteranceId
     * @param data        合成的音频数据。该音频数据是采样率为16K，2字节精度，单声道的pcm数据。
     * @param progress    文本按字符划分的进度，比如:你好啊 进度是0-3
     */
    @Override
    public void onSynthesizeDataArrived(String utteranceId, byte[] data, int progress) {
        // toPrint("onSynthesizeDataArrived");
        mHandler.sendMessage(mHandler.obtainMessage(mHandler.UI_CHANGE_SYNTHES_TEXT_SELECTION, progress, 0));
    }

    /**
     * 合成正常结束，每句合成正常结束都会回调，如果过程中出错，则回调onError，不再回调此接口
     *
     * @param utteranceId
     */
    @Override
    public void onSynthesizeFinish(String utteranceId) {
        toPrint("onSynthesizeFinish utteranceId=" + utteranceId);
    }

    /**
     * 播放开始，每句播放开始都会回调
     *
     * @param utteranceId
     */
    @Override
    public void onSpeechStart(String utteranceId) {
        toPrint("onSpeechStart utteranceId=" + utteranceId);
    }

    /**
     * 播放进度回调接口，分多次回调
     *
     * @param utteranceId
     * @param progress    文本按字符划分的进度，比如:你好啊 进度是0-3
     */
    @Override
    public void onSpeechProgressChanged(String utteranceId, int progress) {
        // toPrint("onSpeechProgressChanged");
        mHandler.sendMessage(mHandler.obtainMessage(mHandler.UI_CHANGE_INPUT_TEXT_SELECTION, progress, 0));
    }

    /**
     * 播放正常结束，每句播放正常结束都会回调，如果过程中出错，则回调onError,不再回调此接口
     *
     * @param utteranceId
     */
    @Override
    public void onSpeechFinish(String utteranceId) {
        toPrint("onSpeechFinish utteranceId=" + utteranceId);
    }

    /**
     * 当合成或者播放过程中出错时回调此接口
     *
     * @param utteranceId
     * @param error       包含错误码和错误信息
     */
    @Override
    public void onError(String utteranceId, SpeechError error) {
        toPrint("onError error=" + "(" + error.code + ")" + error.description + "--utteranceId=" + utteranceId);
    }

    private static class HandlerClass extends Handler {
        private final WeakReference<BaseActivity> weakReference;
        private final int PRINT = 0;
        private final int UI_CHANGE_INPUT_TEXT_SELECTION = 1;
        private final int UI_CHANGE_SYNTHES_TEXT_SELECTION = 2;

        HandlerClass(BaseActivity baseActivity) {
            weakReference = new WeakReference<>(baseActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            VoiceActivity activity = (VoiceActivity) weakReference.get();
            switch (msg.what) {
                case PRINT:
                    activity.print(msg);
                    break;
                case UI_CHANGE_INPUT_TEXT_SELECTION:
                    /*if (msg.arg1 <= mInput.getText().length()) {
                        mInput.setSelection(0, msg.arg1);
                    }*/
                    break;
                case UI_CHANGE_SYNTHES_TEXT_SELECTION:
                    /*SpannableString colorfulText = new SpannableString(mInput.getText().toString());
                    if (msg.arg1 <= colorfulText.toString().length()) {
                        colorfulText.setSpan(new ForegroundColorSpan(Color.GRAY), 0, msg.arg1, Spannable
                                .SPAN_EXCLUSIVE_EXCLUSIVE);
                        mInput.setText(colorfulText);
                    }*/
                    break;
            }
        }
    }

    private void print(Message msg) {
        String message = (String) msg.obj;
        if (message != null) {
            Log.w("TAG", message);
            ToastUtil.showToast(this, message);
            scrollLog(message);
        }
    }

    private void scrollLog(String message) {
       /* Spannable colorMessage = new SpannableString(message + "\n");
        colorMessage.setSpan(new ForegroundColorSpan(0xff0000ff), 0, message.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mShowText.append(colorMessage);
        Layout layout = mShowText.getLayout();
        if (layout != null) {
            int scrollAmount = layout.getLineTop(mShowText.getLineCount()) - mShowText.getHeight();
            if (scrollAmount > 0) {
                mShowText.scrollTo(0, scrollAmount + mShowText.getCompoundPaddingBottom());
            } else {
                mShowText.scrollTo(0, 0);
            }
        }*/
    }

    /**
     * 打印引擎so库版本号及基本信息和model文件的基本信息
     */
    private void printEngineInfo() {
      /*  toPrint("EngineVersioin=" + SynthesizerTool.getEngineVersion());
        toPrint("EngineInfo=" + SynthesizerTool.getEngineInfo());
        String textModelInfo = SynthesizerTool.getModelInfo(FileUtil.path_voice + TEXT_MODEL_NAME);
        toPrint("textModelInfo=" + textModelInfo);
        String speechModelInfo = SynthesizerTool.getModelInfo(FileUtil.path_voice + SPEECH_FEMALE_MODEL_NAME);
        toPrint("speechModelInfo=" + speechModelInfo);*/
    }

    private void toPrint(String str) {
        Message msg = Message.obtain();
        msg.obj = str;
        mHandler.sendMessage(msg);
    }

    @Override
    protected void onDestroy() {
        mSpeechSynthesizer.release();
        super.onDestroy();
    }
}
