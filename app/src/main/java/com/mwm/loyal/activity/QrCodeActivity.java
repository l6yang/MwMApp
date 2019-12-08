package com.mwm.loyal.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyal.basex.impl.ToolBarBackListener;
import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.databinding.ActivityQrCodeBinding;
import com.mwm.loyal.impl.ServerImpl;
import com.mwm.loyal.utils.FileUtil;
import com.mwm.loyal.utils.ImageUtil;
import com.mwm.loyal.utils.QRCodeUtil;

import java.io.File;

import butterknife.BindView;

public class QrCodeActivity extends BaseSwipeActivity<ActivityQrCodeBinding> implements View.OnClickListener, ToolBarBackListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.menu_parent)
    View pubMenuParent;
    @BindView(R.id.menu_save)
    TextView pubMenuSave;
    @BindView(R.id.menu_scan)
    TextView pubMenuScan;
    @BindView(R.id.menu_share)
    TextView pubMenuShare;
    @BindView(R.id.img_qr)
    ImageView mImage_qr;
    private Bitmap bmp_qr;
    private String account;

    @Override
    protected int actLayoutRes() {
        return R.layout.activity_qr_code;
    }

    @Override
    public void afterOnCreate() {
        binding.setDrawable(ImageUtil.getBackground(this));
        initViews();
    }

    private void initViews() {
        toolbar.setTitle("我的二维码");
        setSupportActionBar(toolbar);
        setToolbarBackListener(this);
        account = getIntent().getStringExtra("account");
        Bitmap logoBitmap = BitmapFactory.decodeFile(FileUtil.path_icon + "icon_" + account + ".jpg");
        String str = ServerImpl.getQrCodeContent(account);
        bmp_qr = QRCodeUtil.buildQrBitmap(this, logoBitmap, str, 700, 700);
        pubMenuSave.setOnClickListener(this);
        pubMenuScan.setOnClickListener(this);
        pubMenuShare.setOnClickListener(this);
        pubMenuParent.setOnClickListener(this);
        mImage_qr.setImageBitmap(bmp_qr);
    }

    @Override
    public int setEdgePosition() {
        return LEFT;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_save:
                pubMenuParent.setVisibility(pubMenuParent.isShown() ? View.GONE : View.VISIBLE);
                if (bmp_qr != null) {
                    FileUtil.deleteFile(FileUtil.path_icon + "qr_" + account + ".jpg");
                    String tr = ImageUtil.saveToFile(FileUtil.path_icon + "qr_" + account + ".jpg", bmp_qr);
                    if (!TextUtils.isEmpty(tr) && new File(tr).exists())
                        showToast("图片已保存在" + tr + "路径下");
                }
                break;
            case R.id.menu_scan:
                pubMenuParent.setVisibility(View.GONE);
                break;
            case R.id.menu_share:
                pubMenuParent.setVisibility(View.GONE);
                break;
            case R.id.menu_parent:
                if (pubMenuParent.isShown())
                    pubMenuParent.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_menu:
                pubMenuParent.setVisibility(pubMenuParent.isShown() ? View.GONE : View.VISIBLE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (pubMenuParent.isShown())
            pubMenuParent.setVisibility(View.GONE);
        else
            super.onBackPressed();
    }

    @Override
    public void onBack() {
        if (pubMenuParent.isShown())
            pubMenuParent.setVisibility(View.GONE);
        else
            finish();
    }
}
