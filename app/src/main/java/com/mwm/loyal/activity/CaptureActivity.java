package com.mwm.loyal.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.Result;
import com.mwm.loyal.R;
import com.mwm.loyal.libs.zxing.AutoScannerView;
import com.mwm.loyal.libs.zxing.BaseCaptureActivity;
import com.mwm.loyal.libs.zxing.ViewfinderView;
import com.mwm.loyal.utils.StateBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 默认的扫描界面
 */
public class CaptureActivity extends BaseCaptureActivity implements View.OnClickListener {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;
    @BindView(R.id.viewfinder_view)
    ViewfinderView viewfinderView;
    @BindView(R.id.pub_back)
    ImageView pubBack;
    @BindView(R.id.pub_title)
    TextView pubTitle;
    @BindView(R.id.pub_menu)
    ImageView pubMenu;
    @BindView(R.id.pub_layout)
    View pubLayout;
    @BindView(R.id.autoScannerView)
    AutoScannerView autoScannerView;
    private boolean autoScan = false;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        ButterKnife.bind(this);
        StateBarUtil.setTranslucentStatus(this);
        autoScan = getIntent().getBooleanExtra("auto", false);
        pubBack.setOnClickListener(this);
        pubTitle.setText(getIntent().getStringExtra("title"));
        pubMenu.setImageResource(R.drawable.src_flash_img);
        pubLayout.setBackgroundResource(R.color.pub_color_title);
        autoScannerView.setVisibility(autoScan ? View.VISIBLE : View.GONE);
        viewfinderView.setVisibility(autoScan ? View.GONE : View.VISIBLE);
        pubMenu.setOnClickListener(this);
    }

   /* @Override
    public int setEdgePosition() {
        return LEFT;
    }*/

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pub_back:
                System.gc();
                finish();
                break;
            case R.id.pub_menu:
                if (count == 0) {
                    pubMenu.setSelected(true);
                    cameraManager.openLight();
                    count = 1;
                } else {
                    pubMenu.setSelected(false);
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
        Log.i(TAG, "dealDecode ~~~~~ " + rawResult.getText() + " " + barcode + " " + scaleFactor);
        playBeepSoundAndVibrate();
        Intent intent = new Intent();
        intent.putExtra("mwm_id", rawResult.getText());
        setResult(RESULT_OK, intent);
        finish();
        //对此次扫描结果不满意可以调用
        //reScan();
    }
}
