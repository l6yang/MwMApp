package com.mwm.loyal.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseProgressSubscriber;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.beans.LoginBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityPersonalBinding;
import com.mwm.loyal.handler.PersonalHandler;
import com.mwm.loyal.imp.SubscribeListener;
import com.mwm.loyal.utils.FileUtil;
import com.mwm.loyal.utils.GsonUtil;
import com.mwm.loyal.utils.ImageUtil;
import com.mwm.loyal.utils.ResUtil;
import com.mwm.loyal.utils.RetrofitManage;
import com.mwm.loyal.utils.StringUtil;
import com.mwm.loyal.utils.ToastUtil;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class PersonalActivity extends BaseSwipeActivity<ActivityPersonalBinding> implements View.OnClickListener, SubscribeListener<ResultBean> {
    @BindView(R.id.pub_back)
    ImageView pubBack;
    @BindView(R.id.pub_menu)
    ImageView pubMenu;
    @BindView(R.id.pub_title)
    TextView pubTitle;
    private LoginBean loginBean;
    private UpdateAccount mUpdateAuth;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_personal;
    }

    @Override
    public void afterOnCreate() {
        binding.setDrawable(ResUtil.getBackground(this));
        binding.setClick(new PersonalHandler(this));
        queryAccount();
        initViews();
    }

    private void queryAccount() {
        String account = getIntent().getStringExtra("account");
        BaseProgressSubscriber<ResultBean> querySubscribe = new BaseProgressSubscriber<>(this, -1, this);
        RetrofitManage.rxExecuted(querySubscribe.doQueryAccount(account), querySubscribe);
    }

    @Override
    public boolean isTransStatus() {
        return false;
    }

    private void initViews() {
        pubTitle.setText("个人资料");
        pubMenu.setImageResource(R.drawable.src_edit_img);
        pubMenu.setOnClickListener(this);
        pubBack.setOnClickListener(this);
        showIcon();
    }

    private void editData() {
        if (!loginBean.editable.get()) {
            loginBean.editable.set(true);
            pubMenu.setImageResource(R.drawable.src_ok_save_img);
            return;
        }
        pubMenu.setImageResource(R.drawable.src_edit_img);
        String nickname = binding.personalNickname.getText().toString().trim();
        String signature = binding.personalSignature.getText().toString().trim();
        loginBean.nickname.set(nickname);
        loginBean.sign.set(signature);
        if (mUpdateAuth != null)
            return;
        mUpdateAuth = new UpdateAccount(loginBean);
        mUpdateAuth.execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pub_back:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.pub_menu:
                ToastUtil.hideInput(this, binding.personalNickname.getWindowToken());
                editData();
                break;
        }
    }

    @Override
    public void onResult(int what, ResultBean resultBean) {
        if (resultBean != null) {
            if (resultBean.getResultCode() == 1) {
                loginBean = new LoginBean();
                loginBean.account.set(getIntent().getStringExtra("account"));
                loginBean.nickname.set(resultBean.getResultMsg());
                loginBean.sign.set(resultBean.getExceptMsg());
                loginBean.editable.set(false);
                binding.setLoginBean(loginBean);
            } else showDialog(resultBean.getResultMsg(), false);
        } else
            showErrorDialog("解析个人信息失败", false);
    }

    @Override
    public void onError(int what, Throwable e) {
        showErrorDialog(e.toString(), true);
    }

    @Override
    public void onCompleted(int what) {

    }

    private class UpdateAccount extends AsyncTask<Void, View, String> {
        private final LoginBean loginBean;

        UpdateAccount(LoginBean bean) {
            loginBean = bean;
        }

        @Override
        protected String doInBackground(Void... voids) {
            Call<String> call = RetrofitManage.getInstance().getRequestServer().doUpdateAccount(loginBean.toString(), "personal", "");
            try {
                return RetrofitManage.doExecuteStr(call);
            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mUpdateAuth = null;
            System.out.println(result);
            if (StringUtil.showErrorToast(PersonalActivity.this, result))
                return;
            ResultBean bean = GsonUtil.getBeanFromJson(result, ResultBean.class);
            if (bean.getResultCode() == 1)
                ToastUtil.showToast(PersonalActivity.this, "信息更改成功");
            else
                ToastUtil.showToast(PersonalActivity.this, bean.getResultMsg());
            loginBean.editable.set(bean.getResultCode() != 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Int.reqCode_Main_UCrop:
                if (resultCode == RESULT_OK) {// 成功选择了照片。
                    // 选择好了照片后，调用这个方法解析照片路径的List。
                    Uri uri = UCrop.getOutput(data);
                    if (uri == null)
                        return;
                    releasePic(uri.getPath());
                } else if (resultCode == RESULT_CANCELED) {
                    // 用户取消选择。
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.title_dialog_hint)
                            .setMessage(R.string.cancel_select_photo_hint);
                }
                break;
        }
    }

    private void releasePic(String path) {
        if (TextUtils.isEmpty(path) || !new File(path).exists())
            return;
        if (mDealAuth != null)
            return;
        mDealAuth = new DealImagesAsync(path);
        mDealAuth.execute();

    }

    private void copyImage(String path) {
        if (TextUtils.isEmpty(path) || !new File(path).exists())
            return;
        if (mUpImageAuth != null)
            return;
        mUpImageAuth = new UploadIcon(path);
        mUpImageAuth.execute();
    }

    private UploadIcon mUpImageAuth;
    private DealImagesAsync mDealAuth;

    /**
     * 图片处理
     */
    private class DealImagesAsync extends AsyncTask<Void, Void, String> implements DialogInterface.OnCancelListener {
        private final String path;
        private final ProgressDialog mDialog;

        DealImagesAsync(String resPath) {
            path = resPath;
            mDialog = new ProgressDialog(PersonalActivity.this);
            mDialog.setMessage("处理中...");
            Window window = mDialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.alpha = 0.7f;// 透明度
                lp.dimAmount = 0.8f;// 黑暗度
                window.setAttributes(lp);
            }
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setOnCancelListener(this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            //删除相机拍的照片
            try {
                boolean delete = FileUtil.deleteFile(FileUtil.path_temp, FileUtil.pic_temp);
                String upPath = "";
                if (delete)
                    //保存裁剪之后压缩的照片
                    upPath = ImageUtil.saveToFile(FileUtil.path_temp + loginBean.account.get() + ".jpg", ImageUtil.releasePics(path, 200));
                if (!StringUtil.isEmpty(upPath))
                    //保存完之后删除裁剪的照片
                    FileUtil.deleteFile(FileUtil.path_temp, FileUtil.pic_UCrop);
                return upPath;
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mDialog.dismiss();
            mDealAuth = null;
            if (StringUtil.showErrorToast(PersonalActivity.this, result))
                return;
            copyImage(result);
        }

        @Override
        public void onCancel(DialogInterface dialogInterface) {
            DealImagesAsync.this.cancel(true);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mDealAuth = null;
        }
    }

    /**
     * 处理完之后上传
     */
    private class UploadIcon extends AsyncTask<Void, Void, String> implements DialogInterface.OnCancelListener {
        private final String path;
        private final ProgressDialog mDialog;

        UploadIcon(String resPath) {
            path = resPath;
            mDialog = new ProgressDialog(PersonalActivity.this);
            mDialog.setMessage("上传中...");
            Window window = mDialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.alpha = 0.7f;// 透明度
                lp.dimAmount = 0.8f;// 黑暗度
                window.setAttributes(lp);
            }
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setOnCancelListener(this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                File file = new File(path);
                /* RequestBody body = new MultipartBody.Builder()
                        .addFormDataPart(loginBean.account.get(), path, RequestBody.create(MediaType.parse("image/jpeg"), file))
                        .setType(MultipartBody.FORM)
                        .build();*/
                //return OkHttpClientManager.getInstance().post_jsonDemo(StringUtil.getServiceUrl(Str.method_update_icon), body);
                RequestBody body = RequestBody.create(MediaType.parse("image/jpeg"), file);
                MultipartBody.Part part = MultipartBody.Part.createFormData(loginBean.account.get(), file.getName(), body);
                //Call<String> call = RetrofitManage.getInstance().getRequestServer().doUpdateIcon(body, part);
                Map<String, RequestBody> map = new HashMap<>();
                //map.put("file" + "\"; filename=\"" + file.getName(), body);
                map.put("file" + "\"; filename=\"" + file.getName(), body);
                Call<String> call = RetrofitManage.getInstance().getRequestServer().doUpdateIcon(loginBean.account.get(), map);
                return RetrofitManage.doExecute(call);
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mDialog.dismiss();
            mUpImageAuth = null;
            if (StringUtil.showErrorToast(PersonalActivity.this, result))
                return;
            ResultBean bean = GsonUtil.getBeanFromJson(result, ResultBean.class);
            if (bean.getResultCode() == 1) {
                FileUtil.deleteFile(new File(path));
                ImageUtil.clearFrescoTemp();
                ToastUtil.showToast(PersonalActivity.this, "上传成功");
            } else
                ToastUtil.showToast(PersonalActivity.this, bean.getResultMsg());
            showIcon();
        }

        @Override
        public void onCancel(DialogInterface dialogInterface) {
            UploadIcon.this.cancel(true);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mUpImageAuth = null;
        }
    }

    private void showIcon() {
        String account = getIntent().getStringExtra("account");
        if (binding.simplePersonalIcon == null)
            return;
        binding.simplePersonalIcon.setImageURI(Uri.parse(Str.getServerUrl(Str.method_showIcon) + "&account=" + account));
    }

    @Override
    public int setEdgePosition() {
        return LEFT;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
