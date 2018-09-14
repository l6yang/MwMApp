package com.mwm.loyal.activity;

import android.Manifest;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BasePermitActivity;
import com.mwm.loyal.beans.LoginBean;
import com.mwm.loyal.databinding.ActivityLoginBinding;
import com.mwm.loyal.handler.LoginHandler;
import com.mwm.loyal.impl.TextChangedListener;
import com.mwm.loyal.service.UpdateService;
import com.mwm.loyal.utils.FileUtil;
import com.mwm.loyal.utils.ImageUtil;
import com.mwm.loyal.utils.PreferencesUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.yanzhenjie.permission.RationaleListener;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends BasePermitActivity<ActivityLoginBinding> implements RationaleListener, TextView.OnEditorActionListener {

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
        requestPermissions(IntImpl.permissionReadPhone, new String[]{Manifest.permission.READ_PHONE_STATE});
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

    @PermissionYes(IntImpl.permissionReadPhone)
    private void onReadPhoneSuccess(List<String> grantedPermissions) {
        requestPermissions(IntImpl.permissionMemory, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
    }

    @PermissionNo(IntImpl.permissionReadPhone)
    private void onReadPhoneFail(List<String> deniedPermissions) {
        if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            // 第一种：用默认的提示语。
            AndPermission.defaultSettingDialog(this, IntImpl.permissionReadPhone).show();
        } else {
            showDialog("您已拒绝\"获取设备状态权限\"，程序将退出", true);
        }
    }

    @PermissionYes(IntImpl.permissionMemory)
    private void onMemorySuccess(List<String> grantedPermissions) {
        File file = new File(FileUtil.path_apk, FileUtil.apkFileName);
        if (file.exists())
            FileUtil.deleteFile(file);
        FileUtil.createFiles();
        UpdateService.startActionUpdate(this, StrImpl.actionUpdate, null);
    }

    @PermissionNo(IntImpl.permissionMemory)
    private void onMemoryFailed(List<String> deniedPermissions) {
        if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            // 第一种：用默认的提示语。
            AndPermission.defaultSettingDialog(this, IntImpl.permissionMemory).show();
        } else {
            showDialog("您已拒绝\"存储权限\"，程序将退出", true);
        }
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
}