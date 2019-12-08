package com.mwm.loyal.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseActivity;
import com.mwm.loyal.databinding.ActivitySimpleUcropBinding;
import com.mwm.loyal.utils.DisplayUtil;
import com.mwm.loyal.utils.FileUtil;
import com.mwm.loyal.utils.ImageUtil;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SimpleUCropActivity extends BaseActivity<ActivitySimpleUcropBinding> implements View.OnClickListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_test)
    Button test;

    @Override
    protected int actLayoutRes() {
        return R.layout.activity_simple_ucrop;
    }

    @Override
    public void afterOnCreate() {
        binding.setDrawable(ImageUtil.getBackground(this));
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        toolbar.setTitle("图片裁剪");
        setSupportActionBar(toolbar);
        test.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
