package com.mwm.loyal.activity;

import android.Manifest;
import android.content.Intent;
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
import com.loyal.base.gps.GpsLocBean;
import com.loyal.base.gps.GpsLocation;
import com.loyal.kit.GsonUtil;
import com.loyal.kit.OutUtil;
import com.loyal.kit.StateBarUtil;
import com.loyal.kit.TimeUtil;
import com.loyal.rx.RxUtil;
import com.loyal.rx.impl.RxSubscriberListener;
import com.loyal.rx.impl.SinglePermissionListener;
import com.mwm.loyal.R;
import com.mwm.loyal.activity.settings.SettingsActivity;
import com.mwm.loyal.base.BaseActivity;
import com.mwm.loyal.beans.AccountBean;
import com.mwm.loyal.beans.ContactBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.beans.WeatherBean;
import com.mwm.loyal.databinding.ActivityMainBinding;
import com.mwm.loyal.handler.MainHandler;
import com.mwm.loyal.impl.IContactImpl;
import com.mwm.loyal.impl.ServerImpl;
import com.mwm.loyal.libs.album.AlbumActivity;
import com.mwm.loyal.libs.rxjava.RxProgressSubscriber;
import com.mwm.loyal.service.CheckUpdateService;
import com.mwm.loyal.service.ImgDownloadService;
import com.mwm.loyal.utils.DisplayUtil;
import com.mwm.loyal.utils.ImageUtil;
import com.mwm.loyal.utils.PreferUtil;
import com.mwm.loyal.utils.UIHandler;
import com.mwm.loyal.utils.WeatherUtil;

import java.lang.ref.WeakReference;
import java.security.Permissions;

import butterknife.BindView;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,
        SinglePermissionListener, AppBarLayout.OnOffsetChangedListener, RxSubscriberListener<String>, IContactImpl, GpsLocation.LocationListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsing_toolbar;
    private SimpleDraweeView navIcon;
    private TextView navSignature, navNickName;
    private HandlerClass mHandler;
    private GpsLocation gpsLocation;

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
        toolbar.setNavigationIcon(R.drawable.ic_add);
        //toolbar.inflateMenu();
        singlePermission(IntImpl.permissionLocation, this, PerMission.ACCESS_COARSE_LOCATION, PerMission.ACCESS_FINE_LOCATION);
        binding.setHandler(new MainHandler(this, binding));
        mHandler = new HandlerClass(this);
        String city = PreferUtil.getString(getApplicationContext(), StrImpl.KEY_CITY, StrImpl.defaultCity);
        binding.setCity(city);
        resetCity(city);
        initViews();
        startLocation();
    }

    private void startLocation() {
        if (null == gpsLocation)
            gpsLocation = GpsLocation.getInstance(this);
        gpsLocation.stop();
        gpsLocation.start();
        gpsLocation.setLocationListener(this);
    }

    private void initViews() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        navIcon = view.findViewById(R.id.nav_icon);
        view.findViewById(R.id.nav_zxing).setOnClickListener(this);
        if (navIcon != null) {
            navIcon.setOnClickListener(this);
        }
        navNickName = view.findViewById(R.id.nav_nickName);
        navSignature = view.findViewById(R.id.nav_signature);
        updateAccount();
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        UIHandler.delay2Enable(item, 380);
        drawer.closeDrawer(GravityCompat.START);
        Message message = new Message();
        message.what = IntImpl.delayed2Activity;
        message.arg1 = id;
        mHandler.sendMessageDelayed(message, 380);
        return true;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        singlePermission(IntImpl.permissionLocation, this, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA);
        CheckUpdateService.startAction(this);
        ImgDownloadService.startAction(this, getIntent().getStringExtra("account"));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_icon:
                hasIntentParams(true);
                startActivityForResultByAct(PersonalActivity.class, IntImpl.reqCodeIcon);
                break;
            case R.id.nav_zxing:
                hasIntentParams(true);
                startActivityForResultByAct(QrCodeActivity.class, IntImpl.reqCodeZXing);
                break;
            case R.id.text_weather:
                if (binding.getWeather() != null && !TextUtils.isEmpty(binding.getWeather().trim())) {
                    intentBuilder.putExtra("city", PreferUtil.getString(getApplicationContext(), StrImpl.KEY_CITY, StrImpl.defaultCity));
                    startActivityForResultByAct(WeatherActivity.class, IntImpl.reqCodeWeather);
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
        if (navIcon == null)
            return;
        ImageUtil.clearFrescoTemp();
        navIcon.setImageURI(Uri.parse(ServerImpl.showAvatar(getIntent().getStringExtra("account"))));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IntImpl.reqCodeIcon:
                updateAccount();
                break;
            case IntImpl.reqCodeWeather:
                String city;
                if (data == null || TextUtils.isEmpty(data.getStringExtra("city")))
                    city = PreferUtil.getString(getApplicationContext(), StrImpl.KEY_CITY, StrImpl.defaultCity);
                else city = data.getStringExtra("city");
                resetCity(replaceNull(city));
                break;
        }
        if (RESULT_OK != resultCode)
            return;
        switch (requestCode) {
            case IntImpl.reqCodeSetting:
                startActivityByAct(LoginActivity.class);
                finish();
                break;
            case IntImpl.reqCodeZXing:
                doScanQuery(data.getStringExtra("mwm_id"));
                break;
            case IntImpl.permissionCamera:
                showToast("用户从设置回来了");
                break;
        }
    }

    //扫描并查询
    private void doScanQuery(String scanStr) {
        if (TextUtils.isEmpty(scanStr)) {
            showToast("未扫描到信息");
            return;
        }
        showDialog("处理中...");
        ContactBean contactBean = new ContactBean(getIntent().getStringExtra("account"), scanStr, TimeUtil.getDateTime());
        /*Observable<ResultBean> observable = RetrofitManage.getInstance("").createServer().scan(contactBean.toString(), getPackageName());
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
                });*/
    }

    private void doUpdateAccount(String account) {
        if (TextUtils.isEmpty(account)) {
            return;
        }
        RxProgressSubscriber<String> subscriber = new RxProgressSubscriber<>(this);
        subscriber.setDialogMessage("...").showProgressDialog(true);
        subscriber.setSubscribeListener(this);
        RxUtil.rxExecute(subscriber.queryAccount(account), subscriber);
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
    public void onResult(int what, Object tag, String result) {
        OutUtil.println(result);
        if (TextUtils.isEmpty(result)){
            showDialog("未返回数据");
            return;
        }
        ResultBean<AccountBean> resultBean = (ResultBean<AccountBean>) GsonUtil.json2BeanObject(result, ResultBean.class, AccountBean.class);
        if (null == resultBean) {
            showDialog("数据格式错误");
            return;
        }
        String code = replaceNull(resultBean.getCode());
        String message = replaceNull(resultBean.getMessage());
        AccountBean accountBean = resultBean.getObj();
        if (TextUtils.equals("1", code)) {
            navNickName.setText(replaceNull(null == accountBean ? "" : accountBean.getNickname()));
            navSignature.setText(replaceNull(null == accountBean ? "" : accountBean.getSignature()));
        } else
            showDialog(message);
    }

    @Override
    public void onError(int what, Object tag, Throwable e) {
        showErrorDialog("获取用户数据失败", e);
    }

    @Override
    public void onSinglePermission(int reqCode, boolean successful) {
        switch (reqCode) {
            case IntImpl.permissionLocation:
                if (successful) {
                    startLocation();
                } else showDialog("您已拒绝开启定位权限");
                break;
            case IntImpl.permissionCamera:
                if (successful) {
                    intentBuilder.putExtra("title", "二维码/条码扫描");
                    intentBuilder.putExtra("auto", true);
                    startActivityForResultByAct(CaptureActivity.class, IntImpl.reqCodeZXing);
                } else showToast("已拒绝授予相机权限");
                break;
        }
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
                case IntImpl.delayed2Activity:
                    int id = msg.arg1;
                    switch (id) {
                        case R.id.nav_camera:
                            break;
                        case R.id.nav_gallery:
                            activity.startActivityByAct(AlbumActivity.class);
                            break;
                        case R.id.nav_scan:
                            activity.singlePermission(IntImpl.permissionCamera, activity, PerMission.CAMERA);
                            break;
                        case R.id.nav_share:
                            activity.startActivityByAct(ShareActivity.class);
                            break;
                        case R.id.nav_voice:
                            activity.startActivityByAct(VoiceActivity.class);
                            break;
                        case R.id.nav_settings:
                            activity.hasIntentParams(true);
                            activity.startActivityForResultByAct(SettingsActivity.class, IntImpl.reqCodeSetting);
                            break;
                    }
                    break;
                case IntImpl.rx2Weather:
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
        if (TextUtils.isEmpty(city)) {
            city = PreferUtil.getString(getApplicationContext(), StrImpl.KEY_CITY, StrImpl.defaultCity);
        } else {
            PreferUtil.putString(getApplicationContext(), StrImpl.KEY_CITY, city);
        }
        binding.setCity(city);
        WeatherUtil.getCityWeather(city, mHandler);
    }

    private void resetWeather(String weather) {
        if (TextUtils.isEmpty(weather)) {
            String defaultWeather = PreferUtil.getString(getApplicationContext(), StrImpl.KEY_WEATHER, StrImpl.defaultWeather);
            binding.setWeather(defaultWeather + "°");
        } else {
            binding.setWeather(weather + "°");
            PreferUtil.putString(getApplicationContext(), StrImpl.KEY_WEATHER, weather);
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
                showToast("再次点击返回键退出");
                lastTime = System.currentTimeMillis();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onLocation(GpsLocBean gpsLocBean) {
        String city = null == gpsLocBean ? "" : replaceNull(gpsLocBean.getCityName());
        binding.setCity(city);
        resetCity(city);
    }
}