package com.mwm.loyal.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.databinding.ActivitySimpleUcropBinding;
import com.mwm.loyal.utils.DisplayUtil;
import com.mwm.loyal.utils.FileUtil;
import com.mwm.loyal.utils.ImageUtil;
import com.mwm.loyal.utils.ToastUtil;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SimpleUCropActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.pub_title)
    TextView pubTitle;
    @BindView(R.id.pub_back)
    ImageView pubBack;
    @BindView(R.id.pub_menu)
    ImageView pubMenu;
    @BindView(R.id.btn_test)
    Button test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySimpleUcropBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_simple_ucrop);
        binding.setDrawable(ImageUtil.getBackground(this));
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        pubMenu.setImageResource(R.drawable.src_ok_save_img);
        pubTitle.setText("图片裁剪");
        pubBack.setOnClickListener(this);
        pubMenu.setOnClickListener(this);
        test.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pub_back:
                finish();
                break;
            case R.id.pub_menu:
                ToastUtil.showToast(this, "todo...");
                break;
            case R.id.btn_test:
                int size = DisplayUtil.dip2px(this, 200f);
                String resUrl = "http://star.xiziwang.net/uploads/allimg/140512/19_140512150412_1.jpg";
                UCrop.Options options = new UCrop.Options();
                options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                options.setCompressionQuality(100);
                Uri destinationUri = Uri.fromFile(new File(FileUtil.path_temp, "t_icon.jpeg"));
                UCrop uCrop = UCrop.of(Uri.parse(resUrl), destinationUri);
                uCrop = uCrop.withAspectRatio(size, size);
                uCrop = uCrop.withOptions(options);
                uCrop.start(this);
                break;
        }
    }
}
