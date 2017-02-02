package com.mwm.loyal.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.databinding.ActivityQrCodeBinding;
import com.mwm.loyal.utils.QRCodeUtil;
import com.mwm.loyal.utils.CipherUtil;
import com.mwm.loyal.utils.FileUtil;
import com.mwm.loyal.utils.ImageUtil;
import com.mwm.loyal.utils.ResUtil;
import com.mwm.loyal.utils.ToastUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mwm.loyal.imp.ResListener.Str.*;

public class QrCodeActivity extends BaseSwipeActivity implements View.OnClickListener {
    @BindView(R.id.pub_back)
    ImageView pubBack;
    @BindView(R.id.pub_menu)
    ImageView pubMenu;
    @BindView(R.id.pub_title)
    TextView pubTitle;
    @BindView(R.id.pub_menu_parent)
    View pubMenuParent;
    @BindView(R.id.pub_menu_save)
    TextView pubMenuSave;
    @BindView(R.id.pub_menu_scan)
    TextView pubMenuScan;
    @BindView(R.id.pub_menu_share)
    TextView pubMenuShare;
    @BindView(R.id.img_qr)
    ImageView mImage_qr;
    private Bitmap bmp_qr;
    private String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityQrCodeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_qr_code);
        ButterKnife.bind(this);
        binding.setDrawable(ResUtil.getBackground(this));
        initViews();
    }

    private void initViews() {
        pubTitle.setText("我的二维码");
        pubMenu.setImageResource(R.drawable.src_menu_img);
        pubMenu.setOnClickListener(this);
        pubBack.setOnClickListener(this);
        account = getIntent().getStringExtra("account");
        Bitmap logoBitmap = BitmapFactory.decodeFile(FileUtil.path_icon + "icon_" + account + ".jpg");
        String str = Server_BaseUrl+Server_Method+action_scan+"&k=" + CipherUtil.encodeStr(account);
        pubMenuSave.setOnClickListener(this);
        pubMenuScan.setOnClickListener(this);
        pubMenuShare.setOnClickListener(this);
        pubMenuParent.setOnClickListener(this);
        bmp_qr = QRCodeUtil.buildQrBitmap(this, logoBitmap, str, 700, 700);
        mImage_qr.setImageBitmap(bmp_qr);
    }

    @Override
    public int setEdgePosition() {
        return LEFT;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pub_back:
                if (pubMenuParent.isShown())
                    pubMenuParent.setVisibility(View.GONE);
                else
                    finish();
                break;
            case R.id.pub_menu:
                pubMenuParent.setVisibility(pubMenuParent.isShown() ? View.GONE : View.VISIBLE);
                break;
            case R.id.pub_menu_save:
                pubMenuParent.setVisibility(pubMenuParent.isShown() ? View.GONE : View.VISIBLE);
                if (bmp_qr != null) {
                    FileUtil.deleteFile(FileUtil.path_icon + "qr_" + account + ".jpg");
                    String tr = ImageUtil.saveToFile(FileUtil.path_icon + "qr_" + account + ".jpg", bmp_qr);
                    if (!TextUtils.isEmpty(tr) && new File(tr).exists())
                        ToastUtil.showToast(this, "图片已保存在" + tr + "路径下");
                }
                break;
            case R.id.pub_menu_scan:
                pubMenuParent.setVisibility(View.GONE);
                break;
            case R.id.pub_menu_share:
                pubMenuParent.setVisibility(View.GONE);
                break;
            case R.id.pub_menu_parent:
                if (pubMenuParent.isShown())
                    pubMenuParent.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (pubMenuParent.isShown())
            pubMenuParent.setVisibility(View.GONE);
        else
            super.onBackPressed();
    }
}
