package com.mwm.loyal.handler;

import androidx.core.content.ContextCompat;
import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.activity.PersonalActivity;
import com.mwm.loyal.activity.QrCodeActivity;
import com.mwm.loyal.base.BaseClickHandler;
import com.mwm.loyal.databinding.ActivityPersonalBinding;
import com.mwm.loyal.libs.album.Album;

public class PersonalHandler extends BaseClickHandler<ActivityPersonalBinding> {
    public PersonalHandler(PersonalActivity activity) {
        super(activity);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_person_qr:
                hasIntentParams(true);
                startActivityByAct(QrCodeActivity.class);
                break;
            case R.id.simple_personal_icon:
                selectImage();
                break;
        }
    }

    /**
     * 选择图片。
     */
    private void selectImage() {
        // 1. 使用默认风格，并指定选择数量：
        // 第一个参数Activity/Fragment； 第二个request_code； 第三个允许选择照片的数量，不填可以无限选择。
        // Album.startAlbum(this, IntImpl.reqCodeUCrop, 9);

        // 2. 使用默认风格，不指定选择数量：
        // Album.startAlbum(this, IntImpl.reqCodeUCrop); // 第三个参数不填的话，可以选择无数个。

        // 3. 指定风格，并指定选择数量，如果不想限制数量传入Integer.MAX_VALUE;
        Album.startAlbum(activity, IntImpl.reqCodeUCrop, 1                                                         // 指定选择数量。
                , ContextCompat.getColor(activity, R.color.colorPrimary)        // 指定Toolbar的颜色。
                , ContextCompat.getColor(activity, R.color.colorPrimaryDark));  // 指定状态栏的颜色。
    }
}
