package com.mwm.loyal.activity;

import android.Manifest;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.loyal.base.impl.OnMultiplePermissionsListener;
import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseActivity;
import com.mwm.loyal.beans.LoginBean;
import com.mwm.loyal.databinding.ActivityLoginBinding;
import com.mwm.loyal.handler.LoginHandler;
import com.mwm.loyal.impl.TextChangedListener;
import com.mwm.loyal.service.UpdateService;
import com.mwm.loyal.utils.FileUtil;
import com.mwm.loyal.utils.ImageUtil;
import com.mwm.loyal.utils.PreferencesUtil;

import java.io.File;
import java.util.Arrays;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> implements OnMultiplePermissionsListener, TextView.OnEditorActionListener {

    @Override
    protected int actLayoutRes() {
        return R.layout.activity_login;
    }

    @Override
    public void afterOnCreate() {
        LoginBean bean = PreferencesUtil.getLoginBean(this);
        binding.setLoginBean(bean);
        binding.setClick(new LoginHandler(this, binding));
        binding.setDrawable(ImageUtil.getBackground(this));
        initViews();
        multiplePermissions(this, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void initViews() {
        binding.account.addTextChangedListener(new TextChangedListener(binding.accountClear));
        binding.password.addTextChangedListener(new TextChangedListener(binding.passwordClear));
        binding.server.addTextChangedListener(new TextChangedListener(binding.serverClear));
        binding.password.setOnEditorActionListener(this);
        String arr[] = getResources().getStringArray(R.array.server);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(arr));//配置Adaptor
        binding.server.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IntImpl.permissionMemory:
                showToast("用户从设置回来了");
                break;
        }
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case IntImpl.reqCode_register:
                binding.account.setText(data.getStringExtra("account"));
                break;
        }
    }

    private long lastTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - lastTime <= 2000) {
                finish();
            } else {
                showToast("再次点击返回键退出");
                lastTime = System.currentTimeMillis();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        System.out.println("onEditorAction" + actionId);
        if (actionId == R.id.login || actionId == EditorInfo.IME_NULL) {
            System.out.println("回车键已按下");
            // attemptLogin();
            return true;
        }
        return false;
    }

    @Override
    public void onMultiplePermissions(String permissionName, boolean successful, boolean shouldShow) {
        switch (permissionName) {
            case Manifest.permission.READ_PHONE_STATE:
                if (successful) {
                } else if (shouldShow) {
                } else showDialog("您已拒绝获取设备状态权限", true);
                break;
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                if (successful) {
                    File file = new File(FileUtil.path_apk, FileUtil.apkFileName);
                    if (file.exists())
                        FileUtil.deleteFile(file);
                    FileUtil.createFiles();
                    UpdateService.startActionUpdate(this, StrImpl.actionUpdate, null);
                } else if (shouldShow) {

                } else showDialog("您已拒绝存储权限", true);
                break;
        }
    }
}