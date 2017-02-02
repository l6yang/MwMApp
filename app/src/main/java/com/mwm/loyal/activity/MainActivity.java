package com.mwm.loyal.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseActivity;
import com.mwm.loyal.beans.ContactBean;
import com.mwm.loyal.databinding.ActivityMainBinding;
import com.mwm.loyal.utils.DisplayUtil;
import com.mwm.loyal.utils.ImageUtil;
import com.mwm.loyal.utils.IntentUtil;
import com.mwm.loyal.utils.ResUtil;
import com.mwm.loyal.utils.RetrofitManage;
import com.mwm.loyal.utils.StringUtil;
import com.mwm.loyal.utils.TimeUtil;
import com.mwm.loyal.utils.ToastUtil;
import com.mwm.loyal.utils.TransManage;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, RationaleListener {
    @BindView(R.id.pub_toolbar)
    Toolbar toolbar;
    @BindView(R.id.pub_drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.pub_nav_view)
    NavigationView navigationView;
    private SimpleDraweeView mSimple_Icon;
    private String account = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayUtil.initScreen(this);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setDrawable(ResUtil.getBackground(this));
        ButterKnife.bind(this);
        toolbar.setTitle("测试");
        setSupportActionBar(toolbar);
        TransManage.compat(this);
        initViews();
    }

    private void initViews() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        account = getIntent().getStringExtra("account");
        mSimple_Icon = (SimpleDraweeView) view.findViewById(R.id.nav_icon);
        upDateIcon();
        view.findViewById(R.id.nav_zxing).setOnClickListener(this);
        if (mSimple_Icon != null) {
            mSimple_Icon.setOnClickListener(this);
        }
        TextView text_account = (TextView) view.findViewById(R.id.nav_account);
        text_account.setText(String.format("账号：%s", account));
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent = new Intent();
        switch (id) {
            case R.id.nav_camera:
                intent.setClass(this, CaptureActivity.class);
                intent.putExtra("title", "二维码/条码扫描");
                intent.putExtra("auto", false);
                IntentUtil.toStartActivity(this, intent);
                break;
            case R.id.nav_gallery:
                intent.setClass(this, CaptureActivity.class);
                intent.putExtra("title", "二维码/条码扫描");
                intent.putExtra("auto", true);
                IntentUtil.toStartActivity(this, intent);
                break;
            case R.id.nav_slideshow:
                IntentUtil.toStartActivity(this,TestImageActivity.class);
                break;
            case R.id.nav_manage:
                break;
            case R.id.nav_scan:
                initPermission();
                break;
            case R.id.nav_settings:
                IntentUtil.toStartActivityForResult(this, SettingsActivity.class, Int.reqCode_Main_Setting);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initPermission() {
        AndPermission.with(this)
                .requestCode(Int.permissionCamera)
                .permission(Manifest.permission.CAMERA)
                .rationale(this)
                .send();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_icon:
                IntentUtil.toStartActivityForResult(this, PersonalActivity.class, Int.reqCode_Main_icon);
                break;
            case R.id.nav_zxing:
                IntentUtil.toStartActivity(this, QrCodeActivity.class);
                break;
        }
    }

    private void upDateIcon() {
        ImageUtil.clearFrescoTemp();
        if (mSimple_Icon == null)
            return;
        mSimple_Icon.setImageURI(Uri.parse(Str.getServerUrl(Str.action_showIcon) + "&account=" + account));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Int.reqCode_Main_icon:
                upDateIcon();
                break;
        }
        if (RESULT_OK != resultCode)
            return;
        switch (requestCode) {
            case Int.reqCode_Main_Setting:
                Intent intent = new Intent();
                intent.setClass(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case Int.reqCode_Main_Zing:
                doScanQuery(data.getStringExtra("mwm_id"));
                break;
            case Int.permissionCamera:
                ToastUtil.showToast(this, "用户从设置回来了");
                break;
        }
    }

    private void doScanQuery(String scanStr) {
        if (StringUtil.isEmpty(scanStr)) {
            ToastUtil.showToast(this, "未扫描到信息");
            return;
        }
        if (mScanAuth != null)
            return;
        mScanAuth = new ScanAsync(new ContactBean(account, scanStr, TimeUtil.getDateTime()));
        mScanAuth.execute();
    }

    private ScanAsync mScanAuth;

    @Override
    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
        String tips = "";
        switch (requestCode) {
            case Int.permissionCamera:
                tips = "为确保程序的正常使用，请开启相机权限";
                break;
        }
        ToastUtil.permissionDialog(this, tips, rationale);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionYes(Int.permissionCamera)
    private void onCameraSucceed(List<String> deniedPermissions) {
        Intent intent = new Intent();
        intent.setClass(this, CaptureActivity.class);
        intent.putExtra("title", "二维码/条码扫描");
        intent.putExtra("auto", true);
        IntentUtil.toStartActivityForResult(this, intent,Int.reqCode_Main_Zing);
    }

    @PermissionNo(Int.permissionCamera)
    private void onCameraFailed(List<String> deniedPermissions) {
        if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            AndPermission.defaultSettingDialog(this, Int.permissionMemory).show();
        } else {
            ToastUtil.showDialog(this, "您已拒绝开启相机权限，程序将不能正常使用相机", false);
        }
    }

    private class ScanAsync extends AsyncTask<Void, Void, String> implements DialogInterface.OnCancelListener {
        private final ContactBean contactBean;
        private final ProgressDialog mDialog;

        ScanAsync(ContactBean bean) {
            this.contactBean = bean;
            mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("处理中...");
            Window window = mDialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.alpha = 0.7f;// 透明度
                lp.dimAmount = 0.8f;// 黑暗度
                window.setAttributes(lp);
            }
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setOnCancelListener(this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Call<String> call = RetrofitManage.getInstance().getRequestServer().doScan(contactBean.toString(), getPackageName());
                return RetrofitManage.doExecuteStr(call);
            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mScanAuth = null;
            System.out.println(result);
            mDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mScanAuth = null;
        }

        @Override
        public void onCancel(DialogInterface dialogInterface) {
            ScanAsync.this.cancel(true);
        }
    }

    private long lastTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else if (System.currentTimeMillis() - lastTime <= 2000) {
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