package com.mwm.loyal.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.loyal.base.util.StateBarUtil;
import com.loyal.base.util.TimeUtil;
import com.mwm.loyal.R;
import com.mwm.loyal.base.BasePermitActivity;
import com.mwm.loyal.base.RxProgressSubscriber;
import com.mwm.loyal.beans.ContactBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.beans.WeatherBean;
import com.mwm.loyal.databinding.ActivityMainBinding;
import com.mwm.loyal.handler.MainHandler;
import com.mwm.loyal.impl.IContact;
import com.mwm.loyal.impl.SubscribeListener;
import com.mwm.loyal.service.LocationService;
import com.mwm.loyal.utils.DisplayUtil;
import com.mwm.loyal.utils.ImageUtil;
import com.mwm.loyal.utils.PreferencesUtil;
import com.mwm.loyal.utils.RetrofitManage;
import com.mwm.loyal.utils.RxUtil;
import com.mwm.loyal.utils.ToastUtil;
import com.mwm.loyal.utils.WeatherUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.yanzhenjie.permission.RationaleListener;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BasePermitActivity<ActivityMainBinding> implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, RationaleListener, AppBarLayout.OnOffsetChangedListener, SubscribeListener<ResultBean>, IContact {
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
    private SimpleDraweeView navIcon;
    private TextView navSignature, navNickName;
    private HandlerClass mHandler;

    @Override
    protected int actLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    public void afterOnCreate() {
        DisplayUtil.initScreen(this);
        binding.setDrawable(ImageUtil.getBackground(this));
        toolbar.setTitle("测试");
        setSupportActionBar(toolbar);
        binding.setHandler(new MainHandler(this, binding));
        mHandler = new HandlerClass(this);
        initViews();
    }

    @Override
    public boolean isTransStatus() {
        return false;
    }

    private void initViews() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        navIcon = (SimpleDraweeView) view.findViewById(R.id.nav_icon);
        view.findViewById(R.id.nav_zxing).setOnClickListener(this);
        if (navIcon != null) {
            navIcon.setOnClickListener(this);
        }
        navNickName = (TextView) view.findViewById(R.id.nav_nickName);
        navSignature = (TextView) view.findViewById(R.id.nav_signature);
        updateAccount();
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

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        requestPermissions(Int.permissionLocation, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION});
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_icon:
                hasIntentParams(true);
                startActivityForResultByAct(PersonalActivity.class, Int.reqCode_Main_icon);
                break;
            case R.id.nav_zxing:
                hasIntentParams(true);
                startActivityForResultByAct(QrCodeActivity.class, Int.reqCode_Main_Zing);
                break;
            case R.id.text_weather:
                if (binding.getWeather() != null && !TextUtils.isEmpty(binding.getWeather().trim())) {
                    Intent intent = new Intent(this, WeatherActivity.class);
                    intent.putExtra("city", PreferencesUtil.getString(getApplicationContext(), IStr.KEY_CITY, IStr.defaultCity));
                    startActivityForResult(intent, Int.reqCode_Main_weather);
                }
                break;
        }
    }

    private void updateAccount() {
        updateIcon();
        doUpdateAccount(getIntent().getStringExtra("account"));
    }

    //更新头像
    private void updateIcon() {
        ImageUtil.clearFrescoTemp();
        if (navIcon == null)
            return;
        navIcon.setImageURI(Uri.parse(IStr.getServerUrl(IStr.method_showIcon) + "&account=" + getIntent().getStringExtra("account")));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Int.reqCode_Main_icon:
                updateAccount();
                break;
            case Int.reqCode_Main_weather:
                String city;
                if (data == null || TextUtils.isEmpty(data.getStringExtra("city")))
                    city = PreferencesUtil.getString(getApplicationContext(), IStr.KEY_CITY, IStr.defaultCity);
                else city = data.getStringExtra("city");
                resetCity(IStr.replaceNull(city));
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
                showToast("用户从设置回来了");
                break;
        }
    }

    //扫描并查询
    private void doScanQuery(String scanStr) {
        if (TextUtils.isEmpty(scanStr)) {
            ToastUtil.showToast(this, "未扫描到信息");
            return;
        }
        showDialog("处理中...");
        ContactBean contactBean = new ContactBean(getIntent().getStringExtra("account"), scanStr, TimeUtil.getDateTime());
        Observable<ResultBean> observable = RetrofitManage.getInstance().getObservableServer().doScan(contactBean.toString(), getPackageName());
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResultBean>() {
                    @Override
                    public void onCompleted() {
                        disMissDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        disMissDialog();
                        showErrorDialog(e.toString(), false);
                    }

                    @Override
                    public void onNext(ResultBean resultBean) {
                        if (resultBean != null) {
                            if (resultBean.getResultCode() == 1)
                                showToast(resultBean.getResultMsg());
                            else
                                showDialog(resultBean.getResultMsg(), false);
                        } else {
                            showErrorDialog("解析异常", false);
                        }
                    }
                });
    }

    private void doUpdateAccount(String account) {
        if (TextUtils.isEmpty(account)) {
            return;
        }
        RxProgressSubscriber<ResultBean> querySubscribe = new RxProgressSubscriber<>(this, this);
        RxUtil.rxExecuted(querySubscribe.doQueryAccount(account), querySubscribe);
    }

    @PermissionYes(IContact.Int.permissionCamera)
    private void onCameraSucceed(List<String> deniedPermissions) {
        Intent intent = new Intent();
        intent.setClass(this, CaptureActivity.class);
        intent.putExtra("title", "二维码/条码扫描");
        intent.putExtra("auto", true);
        startActivityForResult(intent, Int.reqCode_Main_Zing);
    }

    @PermissionNo(IContact.Int.permissionCamera)
    private void onCameraFailed(List<String> deniedPermissions) {
        if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            AndPermission.defaultSettingDialog(this, IContact.Int.permissionCamera).show();
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
            AndPermission.defaultSettingDialog(this, IContact.Int.permissionLocation).show();
        } else {
            ToastUtil.showDialog(this, "您已拒绝开启定位权限，程序将不能正常使用定位功能", false);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int height = appBarLayout.getHeight() - (getSupportActionBar() == null ? 0 : getSupportActionBar().
                getHeight()) - StateBarUtil.getStatusBarHeight(MainActivity.this);
        int alpha = 255 * (0 - verticalOffset) / height;
        collapsing_toolbar.setExpandedTitleColor(Color.argb(0, 255, 255, 255));
        collapsing_toolbar.setCollapsedTitleTextColor(Color.argb(alpha, 255, 255, 255));
    }

    @Override
    public void onResult(int what, Object tag, ResultBean resultBean) {
        if (resultBean != null) {
            if (resultBean.getResultCode() == 1) {
                navNickName.setText(replaceNull(resultBean.getResultMsg()));
                navSignature.setText(replaceNull(resultBean.getExceptMsg()));
            } else
                showDialog(resultBean.getResultMsg(), false);
        } else {
            showErrorDialog("解析异常", false);
        }
    }

    @Override
    public void onError(int what, Object tag, Throwable e) {
        showErrorDialog(e.toString(), false);
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
                        case R.id.nav_gallery:
                        case R.id.nav_scan:
                            activity.requestPermissions(IContact.Int.permissionCamera, new String[]{Manifest.permission.CAMERA});
                            break;
                        case R.id.nav_share:
                            activity.startActivityByAct(ShareActivity.class);
                            break;
                        case R.id.nav_voice:
                            activity.startActivityByAct(VoiceActivity.class);
                            break;
                        case R.id.nav_settings:
                            activity.startActivityForResultByAct(SettingsActivity.class, Int.reqCode_Main_Setting);
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
                city = PreferencesUtil.getString(getApplicationContext(), IStr.KEY_CITY, IStr.defaultCity);
            } else {
                PreferencesUtil.putString(getApplicationContext(), IStr.KEY_CITY, city);
            }
            binding.setCity(city);
            WeatherUtil.getCityWeather(city, mHandler);
        } catch (UnsupportedEncodingException e) {
            String defaultCity = PreferencesUtil.getString(getApplicationContext(), IStr.KEY_CITY, IStr.defaultCity);
            binding.setCity(defaultCity);
        }
    }

    private void resetWeather(String weather) {
        if (TextUtils.isEmpty(weather)) {
            String defaultWeather = PreferencesUtil.getString(getApplicationContext(), IStr.KEY_WEATHER, IStr.defaultWeather);
            binding.setWeather(defaultWeather + "°");
        } else {
            binding.setWeather(weather + "°");
            PreferencesUtil.putString(getApplicationContext(), IStr.KEY_WEATHER, weather);
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
        intentFilter.addAction(IStr.service_action_loc);
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
            if (TextUtils.equals(IStr.service_action_loc, action)) {
                if (!TextUtils.isEmpty(city)) {
                    resetCity(city);
                }
            }
        }
    }
}