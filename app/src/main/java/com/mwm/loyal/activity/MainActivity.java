package com.mwm.loyal.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseActivity;
import com.mwm.loyal.beans.ContactBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.beans.WeatherBean;
import com.mwm.loyal.databinding.ActivityMainBinding;
import com.mwm.loyal.service.LocationService;
import com.mwm.loyal.utils.DisplayUtil;
import com.mwm.loyal.utils.ImageUtil;
import com.mwm.loyal.utils.IntentUtil;
import com.mwm.loyal.utils.PreferencesUtil;
import com.mwm.loyal.utils.ResUtil;
import com.mwm.loyal.utils.RetrofitManage;
import com.mwm.loyal.utils.StringUtil;
import com.mwm.loyal.utils.TimeUtil;
import com.mwm.loyal.utils.ToastUtil;
import com.mwm.loyal.utils.TransManage;
import com.mwm.loyal.utils.WeatherUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, RationaleListener, AppBarLayout.OnOffsetChangedListener {
    @BindView(R.id.pub_toolbar)
    Toolbar toolbar;
    @BindView(R.id.pub_drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.pub_nav_view)
    NavigationView navigationView;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsing_toolbar;
    private SimpleDraweeView mSimple_Icon;
    private String account = "";
    private HandlerClass mHandler;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayUtil.initScreen(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setDrawable(ResUtil.getBackground(this));
        ButterKnife.bind(this);
        toolbar.setTitle("测试");
        setSupportActionBar(toolbar);
        TransManage.setTranslucentStatus(this);
        mHandler = new HandlerClass(this);
        initViews();
        initPermission(Int.permissionLocation, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
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
        updateIcon();
        view.findViewById(R.id.nav_zxing).setOnClickListener(this);
        if (mSimple_Icon != null) {
            mSimple_Icon.setOnClickListener(this);
        }
        TextView text_account = (TextView) view.findViewById(R.id.nav_account);
        text_account.setText(account);
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        drawer.closeDrawer(GravityCompat.START);
        Message message = new Message();
        message.what = Int.delayed2Activity;
        message.arg1 = id;
        mHandler.sendMessageDelayed(message, 380);
        return true;
    }

    private void initPermission(int requestCode, String... permissions) {
        AndPermission.with(this)
                .requestCode(requestCode)
                .permission(permissions)
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
                IntentUtil.toStartActivityForResult(this, QrCodeActivity.class, Int.reqCode_Main_Zing);
                break;
            case R.id.text_weather:
                if (!TextUtils.isEmpty(binding.getWeather().trim())) {
                    Intent intent = new Intent(this, WeatherActivity.class);
                    intent.putExtra("city", PreferencesUtil.getString(getApplicationContext(), Str.KEY_CITY, Str.defaultCity));
                    IntentUtil.toStartActivityForResult(this, intent, Int.reqCode_Main_weather);
                }
                break;
        }
    }

    //更新头像
    private void updateIcon() {
        ImageUtil.clearFrescoTemp();
        if (mSimple_Icon == null)
            return;
        mSimple_Icon.setImageURI(Uri.parse(Str.getServerUrl(Str.action_showIcon) + "&account=" + account));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Int.reqCode_Main_icon:
                updateIcon();
                break;
            case Int.reqCode_Main_weather:
                String city;
                if (data == null || TextUtils.isEmpty(data.getStringExtra("city")))
                    city = PreferencesUtil.getString(getApplicationContext(), Str.KEY_CITY, Str.defaultCity);
                else city = data.getStringExtra("city");
                resetCity(StringUtil.replaceNull(city));
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

    //扫描并查询
    private void doScanQuery(String scanStr) {
        if (StringUtil.isEmpty(scanStr)) {
            ToastUtil.showToast(this, "未扫描到信息");
            return;
        }
        progressDialog.setMessage("处理中...");
        ContactBean contactBean = new ContactBean(account, scanStr, TimeUtil.getDateTime());
        Observable<ResultBean> observable = RetrofitManage.getInstance().getObservableServer().doScan(contactBean.toString(), getPackageName());
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResultBean>() {
                    @Override
                    public void onCompleted() {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        StringUtil.showErrorDialog(MainActivity.this, e.toString(), false);
                    }

                    @Override
                    public void onNext(ResultBean resultBean) {
                        if (resultBean != null && resultBean.getResultCode() == 1) {
                            ToastUtil.showDialog(MainActivity.this, resultBean.getResultMsg(), false);
                        } else {
                            ToastUtil.showToast(MainActivity.this, null == resultBean ? "解析异常" : StringUtil.replaceNull(resultBean.getResultMsg()));
                        }
                    }
                });
    }

    @Override
    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
        String tips = "为确保程序的正常使用，请开启";
        switch (requestCode) {
            case Int.permissionLocation:
                tips += "定位权限";
                break;
            case Int.permissionCamera:
                tips += "相机权限";
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
        IntentUtil.toStartActivityForResult(this, intent, Int.reqCode_Main_Zing);
    }

    @PermissionNo(Int.permissionCamera)
    private void onCameraFailed(List<String> deniedPermissions) {
        if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            AndPermission.defaultSettingDialog(this, Int.permissionCamera).show();
        } else {
            ToastUtil.showDialog(this, "您已拒绝开启相机权限，程序将不能正常使用相机", false);
        }
    }

    @PermissionYes(Int.permissionLocation)
    private void onLocationSucceed(List<String> deniedPermissions) {
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
    }

    @PermissionNo(Int.permissionLocation)
    private void onLocationFailed(List<String> deniedPermissions) {
        if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            AndPermission.defaultSettingDialog(this, Int.permissionLocation).show();
        } else {
            ToastUtil.showDialog(this, "您已拒绝开启定位权限，程序将不能正常使用定位功能", false);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int height = appBarLayout.getHeight() - (getSupportActionBar() == null ? 0 : getSupportActionBar().
                getHeight()) - TransManage.getStatusBarHeight(MainActivity.this);
        int alpha = 255 * (0 - verticalOffset) / height;
        collapsing_toolbar.setExpandedTitleColor(Color.argb(0, 255, 255, 255));
        collapsing_toolbar.setCollapsedTitleTextColor(Color.argb(alpha, 255, 255, 255));
    }

    private static class HandlerClass extends Handler {
        private WeakReference<MainActivity> weakReference;

        HandlerClass(MainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = weakReference.get();
            switch (msg.what) {
                case Int.delayed2Activity:
                    int id = msg.arg1;
                    switch (id) {
                        case R.id.nav_camera:
                            activity.initPermission(Int.permissionCamera, Manifest.permission.CAMERA);
                            break;
                        case R.id.nav_gallery:
                            activity.initPermission(Int.permissionCamera, Manifest.permission.CAMERA);
                            break;
                        case R.id.nav_scan:
                            activity.initPermission(Int.permissionCamera, Manifest.permission.CAMERA);
                            break;
                        case R.id.nav_voice:
                            IntentUtil.toStartActivity(activity, VoiceActivity.class);
                            break;
                        case R.id.nav_settings:
                            IntentUtil.toStartActivityForResult(activity, SettingsActivity.class, Int.reqCode_Main_Setting);
                            break;
                    }
                    break;
                case Int.rx2Weather:
                    WeatherBean weatherBean = (WeatherBean) msg.obj;
                    try {
                        if (weatherBean != null && weatherBean.getStatus() == 1000) {
                            String weather = weatherBean.getData().getWendu();
                            activity.resetWeather(weather);
                        } else {
                            activity.resetWeather("");
                        }
                    } catch (Exception e) {
                        activity.resetWeather("");
                    }
                    break;
            }
        }
    }

    private void resetCity(String city) {
        try {
            if (TextUtils.isEmpty(city)) {
                city = PreferencesUtil.getString(getApplicationContext(), Str.KEY_CITY, Str.defaultCity);
            } else {
                PreferencesUtil.putString(getApplicationContext(), Str.KEY_CITY, city);
            }
            binding.setCity(city);
            WeatherUtil.getCityWeather(city, mHandler);
        } catch (UnsupportedEncodingException e) {
            String defaultCity = PreferencesUtil.getString(getApplicationContext(), Str.KEY_CITY, Str.defaultCity);
            binding.setCity(defaultCity);
        }
    }

    private void resetWeather(String weather) {
        if (TextUtils.isEmpty(weather)) {
            String defaultWeather = PreferencesUtil.getString(getApplicationContext(), Str.KEY_WEATHER, Str.defaultWeather);
            binding.setWeather(defaultWeather + "°");
        } else {
            binding.setWeather(weather + "°");
            PreferencesUtil.putString(getApplicationContext(), Str.KEY_WEATHER, weather);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

    private LocatedReceiver locatedReceiver;

    @Override
    protected void onResume() {
        super.onResume();
        locatedReceiver = new LocatedReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Str.service_action_loc);
        registerReceiver(locatedReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locatedReceiver != null) {
            unregisterReceiver(locatedReceiver);
        }
    }

    private class LocatedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String city = intent.getStringExtra("city");
            if (TextUtils.equals(action, Str.service_action_loc)) {
                if (!TextUtils.isEmpty(city)) {
                    resetCity(city);
                }
            }
            intent.setClass(MainActivity.this, LocationService.class);
            stopService(intent);
        }
    }
}