package com.mwm.loyal.handle;

import android.support.v4.content.ContextCompat;
import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.activity.PersonalActivity;
import com.mwm.loyal.activity.QrCodeActivity;
import com.mwm.loyal.libs.album.Album;
import com.mwm.loyal.imp.Contact;
import com.mwm.loyal.utils.IntentUtil;

public class PersonalHandler {
    private final PersonalActivity personalActivity;

    public PersonalHandler(PersonalActivity activity) {
        personalActivity = activity;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_person_qr:
                IntentUtil.toStartActivity(personalActivity, QrCodeActivity.class);
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
        // Album.startAlbum(this, Int.reqCode_Main_UCrop, 9);

        // 2. 使用默认风格，不指定选择数量：
        // Album.startAlbum(this, Int.reqCode_Main_UCrop); // 第三个参数不填的话，可以选择无数个。

        // 3. 指定风格，并指定选择数量，如果不想限制数量传入Integer.MAX_VALUE;
        Album.startAlbum(personalActivity, Contact.Int.reqCode_Main_UCrop, 1                                                         // 指定选择数量。
                , ContextCompat.getColor(personalActivity, R.color.colorPrimary)        // 指定Toolbar的颜色。
                , ContextCompat.getColor(personalActivity, R.color.colorPrimaryDark));  // 指定状态栏的颜色。
    }
}
