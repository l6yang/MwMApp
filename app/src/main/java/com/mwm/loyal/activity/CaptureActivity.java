package com.mwm.loyal.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;

import com.google.zxing.Result;
import com.loyal.kit.OutUtil;
import com.mwm.loyal.R;
import com.mwm.loyal.libs.zxing.AutoScannerView;
import com.mwm.loyal.libs.zxing.BaseCaptureActivity;
import com.mwm.loyal.libs.zxing.ViewfinderView;

import butterknife.BindView;

/**
 * 默认的扫描界面
 */
public class CaptureActivity extends BaseCaptureActivity implements View.OnClickListener {

    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;
    @BindView(R.id.viewfinder_view)
    ViewfinderView viewfinderView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.flashView)
    View flashView;
    @BindView(R.id.autoScannerView)
    AutoScannerView autoScannerView;
    private boolean autoScan = false;
    int count = 0;

    @Override
    protected int actLayoutRes() {
        return R.layout.activity_capture;
    }

    @Override
    public void afterOnCreate() {
        String title = getIntent().getStringExtra("title");
        toolbar.setTitle(TextUtils.isEmpty(title) ? "扫一扫" : title);
        setSupportActionBar(toolbar);
        autoScan = getIntent().getBooleanExtra("auto", false);
        autoScannerView.setVisibility(autoScan ? View.VISIBLE : View.GONE);
        viewfinderView.setVisibility(autoScan ? View.GONE : View.VISIBLE);
        flashView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.flashView:
                if (count == 0) {
                    view.setSelected(true);
                    cameraManager.openLight();
                    count = 1;
                } else {
                    view.setSelected(false);
                    cameraManager.offLight();
                    count = 0;
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (autoScan)
            autoScannerView.setCameraManager(cameraManager);
    }

    @Override
    public SurfaceView getSurfaceView() {
        return (surfaceView == null) ? (SurfaceView) findViewById(R.id.surfaceView) : surfaceView;
    }

    @Override
    public ViewfinderView getViewfinderHolder() {
        return (viewfinderView == null) ? (ViewfinderView) findViewById(R.id.viewfinder_view) : viewfinderView;
    }

    @Override
    public void dealDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        OutUtil.println("dealDecode ~~~~~ " + rawResult.getText() + " " + barcode + " " + scaleFactor);
        playBeepSoundAndVibrate();
        Intent intent = new Intent();
        intent.putExtra("mwm_id", rawResult.getText());
        setResult(RESULT_OK, intent);
        finish();
        //对此次扫描结果不满意可以调用
        //reScan();
    }
}
