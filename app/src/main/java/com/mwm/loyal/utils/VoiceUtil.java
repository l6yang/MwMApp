package com.mwm.loyal.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.baidu.tts.client.SpeechSynthesizer;
import com.loyal.base.util.IOUtil;
import com.mwm.loyal.impl.IContact;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class VoiceUtil {
    public static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
    public static final String SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";
    public static final String TEXT_MODEL_NAME = "bd_etts_text.dat";
    public static final String ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat";
    public static final String ENGLISH_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male_en.dat";
    public static final String ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat";
    private SpeechSynthesizer mSpeechSynthesizer;
    private CopyAsync mCopyAuth;

    public VoiceUtil(Context context) {
        copyAssetsFile(context);
        initSpeechSynthesizer(context);
    }

    private void initSpeechSynthesizer(Context context) {
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(context);
        // 文本模型文件路径 (离线引擎使用)
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, FileUtil.path_voice
                + TEXT_MODEL_NAME);
        // 声学模型文件路径 (离线引擎使用)
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, FileUtil.path_voice
                + SPEECH_FEMALE_MODEL_NAME);
        // 请替换为语音开发者平台上注册应用得到的App ID (离线授权)
        mSpeechSynthesizer.setAppId(IContact.StrImpl.appId/*这里只是为了让Demo运行使用的APPID,请替换成自己的id。*/);
        // 请替换为语音开发者平台注册应用得到的apikey和secretkey (在线授权)
        mSpeechSynthesizer.setApiKey(IContact.StrImpl.appKey,
                IContact.StrImpl.secretKey/*这里只是为了让Demo正常运行使用APIKey,请替换成自己的APIKey*/);
        // 发音人（在线引擎），可用参数为0,1,2,3。。。（服务器端会动态增加，各值含义参考文档，以文档说明为准。0--普通女声，1--普通男声，2--特别男声，3--情感男声。。。）
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置Mix模式的合成策略
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
    }

    public SpeechSynthesizer getSpeechSynthesizer() {
        return mSpeechSynthesizer;
    }

    private void copyAssetsFile(Context context) {
       /* copyFromAssetsToSdcard(context, SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(context, SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(context, TEXT_MODEL_NAME);
        copyFromAssetsToSdcard(context, "english/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(context, "english/" + ENGLISH_SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(context, "english/" + ENGLISH_TEXT_MODEL_NAME);*/
        if (mCopyAuth != null)
            return;
        mCopyAuth = new CopyAsync(context);
        mCopyAuth.execute();
    }

    /**
     * 将sample工程需要的资源文件拷贝到SD卡中使用（授权文件为临时授权文件，请注册正式授权）
     *
     * @param context  是否覆盖已存在的目标文件
     * @param fileName assets文件夹的文件名（存储在SD卡的文件名）
     */
    private void copyFromAssetsToSdcard(Context context, String fileName) {
        File file = new File(FileUtil.path_voice, fileName.replace("english/", ""));
        if (!file.exists()) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = context.getResources().getAssets().open(fileName);
                fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int size;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtil.closeStream(fos);
                IOUtil.closeStream(is);
            }
        }
    }

    private class CopyAsync extends AsyncTask<Void, Void, Void> {
        private Context mContext;

        CopyAsync(Context context) {
            this.mContext = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            copyFromAssetsToSdcard(mContext, SPEECH_FEMALE_MODEL_NAME);
            copyFromAssetsToSdcard(mContext, SPEECH_MALE_MODEL_NAME);
            copyFromAssetsToSdcard(mContext, TEXT_MODEL_NAME);
            copyFromAssetsToSdcard(mContext, "english/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
            copyFromAssetsToSdcard(mContext, "english/" + ENGLISH_SPEECH_MALE_MODEL_NAME);
            copyFromAssetsToSdcard(mContext, "english/" + ENGLISH_TEXT_MODEL_NAME);
            return null;
        }
    }
}
