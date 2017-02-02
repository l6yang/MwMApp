package com.mwm.loyal.activity;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseActivity;
import com.mwm.loyal.beans.LoginBean;
import com.mwm.loyal.databinding.ActivityLoginBinding;
import com.mwm.loyal.handle.LoginHandler;
import com.mwm.loyal.imp.TextChangedListener;
import com.mwm.loyal.service.UpdateService;
import com.mwm.loyal.utils.FileUtil;
import com.mwm.loyal.utils.PreferencesUtil;
import com.mwm.loyal.utils.ResUtil;
import com.mwm.loyal.utils.ToastUtil;
import com.mwm.loyal.utils.TransManage;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity implements RationaleListener {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        TransManage.compat(this);
        ButterKnife.bind(this);
        LoginBean bean = PreferencesUtil.getLoginBean(this);
        binding.setLoginbean(bean);
        binding.setClick(new LoginHandler(this, binding));
        binding.setDrawable(ResUtil.getBackground(this));
        initViews();
        initPermission();
    }

    private void initPermission() {
        AndPermission.with(this)
                .requestCode(Int.permissionMemory)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .rationale(this)
                .send();
    }

    private void initViews() {
        binding.account.addTextChangedListener(new TextChangedListener(binding.accountClear));
        binding.password.addTextChangedListener(new TextChangedListener(binding.passwordClear));
        binding.server.addTextChangedListener(new TextChangedListener(binding.serverClear));
        String arr[] = getResources().getStringArray(R.array.server);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(arr));//配置Adaptor
        binding.server.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
        String tips = "";
        switch (requestCode) {
            case Int.permissionMemory:
                tips = "为确保程序的正常使用，请开启存储权限，否则导致程序更新异常";
                break;
        }
        ToastUtil.permissionDialog(this, tips, rationale);
    }

    @PermissionYes(Int.permissionMemory)
    private void onMemorySuccess(List<String> grantedPermissions) {
        File file = new File(FileUtil.path_apk, FileUtil.apkFileName);
        if (file.exists())
            FileUtil.deleteFile(file);
        FileUtil.createFiles();
        UpdateService.startActionUpdate(this, Str.ACTION_UPDATE, null);
    }

    @PermissionNo(Int.permissionMemory)
    private void onMemoryFailed(List<String> deniedPermissions) {
        if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            // 第一种：用默认的提示语。
            AndPermission.defaultSettingDialog(this, Int.permissionMemory).show();
        } else {
            ToastUtil.showDialog(this, "您已拒绝开启存储权限，程序将退出", true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Int.permissionMemory:
                ToastUtil.showToast(this, "用户从设置回来了");
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
                ToastUtil.showToast(this, "再次点击返回键退出");
                lastTime = System.currentTimeMillis();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}