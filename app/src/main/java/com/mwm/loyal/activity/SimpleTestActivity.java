package com.mwm.loyal.activity;

import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseActivity;

import butterknife.BindView;

public class SimpleTestActivity extends BaseActivity {
    @BindView(R.id.simple_ii)
    SimpleDraweeView simple_loyal_ii;
    @BindView(R.id.simple_ib)
    SimpleDraweeView simple_loyal_ib;
    @BindView(R.id.simple_ir_i)
    SimpleDraweeView simple_admin_ii;
    @BindView(R.id.simple_ir_b)
    SimpleDraweeView simple_admin_ib;
    @BindView(R.id.simple_i_io)
    SimpleDraweeView simple_io;
    @BindView(R.id.simple_i_b)
    SimpleDraweeView simple_b;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_simple_test;
    }

    @Override
    public void afterOnCreate() {
        String url = Str.getServerUrl(Str.method_showIcon+"&account=");
        simple_io.setImageURI(Uri.parse(url + "admin"));
        simple_b.setImageURI(Uri.parse(url + "loyal"));
        simple_admin_ib.setImageURI(Uri.parse(url + "l6yang"));
        //test Image_url = "http://h.hiphotos.baidu.com/zhidao/pic/item/279759ee3d6d55fb65c51e786c224f4a20a4dd69.jpg";
    }

    @Override
    public boolean isTransStatus() {
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fresco.getImagePipeline().clearCaches();
        Fresco.getImagePipeline().clearDiskCaches();
        Fresco.getImagePipeline().clearMemoryCaches();
    }
}
