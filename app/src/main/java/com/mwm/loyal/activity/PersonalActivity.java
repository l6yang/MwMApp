package com.mwm.loyal.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.loyal.kit.GsonUtil;
import com.loyal.kit.OutUtil;
import com.loyal.rx.RxUtil;
import com.loyal.rx.impl.RxSubscriberListener;
import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.beans.AccountBean;
import com.mwm.loyal.beans.ObservableAccountBean;
import com.mwm.loyal.beans.ResultBean;
import com.mwm.loyal.databinding.ActivityPersonalBinding;
import com.mwm.loyal.handler.PersonalHandler;
import com.mwm.loyal.impl.ServerImpl;
import com.mwm.loyal.libs.rxjava.RxProgressSubscriber;
import com.mwm.loyal.utils.FileUtil;
import com.mwm.loyal.utils.ImageUtil;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PersonalActivity extends BaseSwipeActivity<ActivityPersonalBinding> implements RxSubscriberListener<String> {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private ObservableAccountBean observableAccountBean;
    private final int queryWhat = 2;
    private final int updateWhat = 4;

    @Override
    protected int actLayoutRes() {
        return R.layout.activity_personal;
    }

    @Override
    public void afterOnCreate() {
        toolbar.setTitle("个人资料");
        setSupportActionBar(toolbar);
        binding.setDrawable(ImageUtil.getBackground(this));
        binding.setClick(new PersonalHandler(this));
        showIcon();
        queryAccount();
    }

    private void queryAccount() {
        String account = getIntent().getStringExtra("account");
        RxProgressSubscriber<String> subscriber = new RxProgressSubscriber<>(this);
        subscriber.setDialogMessage("...").showProgressDialog(true).setTag(observableAccountBean);
        subscriber.setWhat(queryWhat).setSubscribeListener(this);
        RxUtil.rxExecute(subscriber.queryAccount(account), subscriber);
    }

    private void updatePersonal(AccountBean accountBean) {
        String account = getIntent().getStringExtra("account");
        accountBean.setAccount(account);
        RxProgressSubscriber<String> subscriber = new RxProgressSubscriber<>(this);
        subscriber.setDialogMessage("...").showProgressDialog(true).setTag(observableAccountBean);
        subscriber.setWhat(updateWhat).setSubscribeListener(this);
        RxUtil.rxExecute(subscriber.accountUpdate(GsonUtil.bean2Json(accountBean)), subscriber);
    }

    @Override
    public void onResult(int what, Object tag, String result) {
        OutUtil.println("person-" + what, result);
        try {
            switch (what) {
                case queryWhat: {
                    ResultBean<AccountBean> resultBean = (ResultBean<AccountBean>) GsonUtil.json2Object(result, ResultBean.class, AccountBean.class);
                    if (null == resultBean) {
                        showDialog("解析个人信息失败", true);
                        return;
                    }
                    String code = replaceNull(resultBean.getCode());
                    String message = replaceNull(resultBean.getMessage());
                    if (TextUtils.equals("1", code)) {
                        observableAccountBean = new ObservableAccountBean();
                        AccountBean accountBean = resultBean.getObj();
                        observableAccountBean.account.set(null == accountBean ? "" : accountBean.getAccount());
                        observableAccountBean.nickname.set(null == accountBean ? "" : accountBean.getNickname());
                        observableAccountBean.signature.set(null == accountBean ? "" : accountBean.getSignature());
                        observableAccountBean.editable.set(false);
                        binding.setObservableAccountBean(observableAccountBean);
                    } else showDialog(message);
                }
                break;
                case updateWhat:
                    ResultBean resultBean = GsonUtil.json2Bean(result, ResultBean.class);
                    if (null == resultBean) {
                        showDialog("解析个人信息失败", true);
                        return;
                    }
                    String code = replaceNull(resultBean.getCode());
                    String message = replaceNull(resultBean.getMessage());
                    showToast(message);
                    boolean success = TextUtils.equals("1", code);
                    observableAccountBean.editable.set(!success);
                    if (success) {
                        toolbar.getMenu().clear();
                        toolbar.inflateMenu(R.menu.menu_edit);
                        toolbar.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setResult(RESULT_OK);
                                finish();
                            }
                        },800);
                    }
                    break;
            }
        } catch (Exception e) {
            onError(what, tag, e);
        }
    }

    @Override
    public void onError(int what, Object tag, Throwable e) {
        switch (what) {
            case queryWhat:
                showErrorDialog("加载数据失败", e, true);
                break;
            case updateWhat:
                showErrorDialog("更新失败", e);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IntImpl.reqCodeUCrop:
                if (resultCode == RESULT_OK) {// 成功选择了照片。
                    // 选择好了照片后，调用这个方法解析照片路径的List。
                    Uri uri = UCrop.getOutput(data);
                    if (uri == null)
                        return;
                    releasePic(uri.getPath());
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
                    upPath = ImageUtil.saveToFile(FileUtil.path_temp + observableAccountBean.account.get() + ".jpg", ImageUtil.releasePics(path, 200));
                if (!TextUtils.isEmpty(upPath))
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
            if (TextUtils.isEmpty(result))
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
                        .addFormDataPart(observableAccountBean.account.get(), path, RequestBody.create(MediaType.parse("image/jpeg"), file))
                        .setType(MultipartBody.FORM)
                        .build();*/
                //return OkHttpClientManager.getInstance().post_jsonDemo(StringUtil.getServiceUrl(StrImpl.method_update_icon), body);
                RequestBody body = RequestBody.create(MediaType.parse("image/jpeg"), file);
                MultipartBody.Part part = MultipartBody.Part.createFormData(observableAccountBean.account.get(), file.getName(), body);
                //Call<String> call = RetrofitManage.getInstance().getRequestServer().updateAvatar(body, part);
                Map<String, RequestBody> map = new HashMap<>();
                //map.put("file" + "\"; filename=\"" + file.getName(), body);
                map.put("file" + "\"; filename=\"" + file.getName(), body);
                //Call<String> call = RetrofitManage.getInstance().getRequestServer().updateAvatar(observableAccountBean.account.get(), map);
                //return RetrofitManage.doExecute(call);
                return "";
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
            if (TextUtils.isEmpty(result))
                return;
            ResultBean resultBean = GsonUtil.json2Bean(result, ResultBean.class);
            if (TextUtils.equals("1", resultBean.getCode())) {
                FileUtil.deleteFile(new File(path));
                ImageUtil.clearFrescoTemp();
                showToast("上传成功");
            } else
                showToast(resultBean.getMessage());
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
        binding.simplePersonalIcon.setImageURI(Uri.parse(ServerImpl.showAvatar(account)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                if (!observableAccountBean.editable.get()) {
                    observableAccountBean.editable.set(true);
                    toolbar.getMenu().clear();
                    toolbar.inflateMenu(R.menu.menu_save);
                }
                return true;
            case R.id.menu_save:
                hideKeyBoard(toolbar);
                String nickname = binding.personalNickname.getText().toString().trim();
                String signature = binding.personalSignature.getText().toString().trim();
                observableAccountBean.nickname.set(nickname);
                observableAccountBean.signature.set(signature);
                AccountBean accountBean = new AccountBean();
                accountBean.setNickname(nickname);
                accountBean.setSignature(signature);
                updatePersonal(accountBean);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public int setEdgePosition() {
        return LEFT;
    }
}