package com.mwm.loyal.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.beans.LoginBean;
import com.mwm.loyal.databinding.ActivityRegisterBinding;
import com.mwm.loyal.handle.RegisterHandler;
import com.mwm.loyal.imp.TextChangedListener;
import com.mwm.loyal.utils.ResUtil;

import butterknife.BindView;

public class RegisterActivity extends BaseSwipeActivity<ActivityRegisterBinding> implements View.OnClickListener {
    @BindView(R.id.pub_back)
    ImageView pubBack;
    @BindView(R.id.pub_title)
    TextView pubTitle;
    @BindView(R.id.pub_menu)
    ImageView pubMenu;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_register;
    }

    @Override
    public void afterOnCreate() {
        LoginBean loginBean = new LoginBean();
        binding.setLoginBean(loginBean);
        String account = getIntent().getStringExtra("account");
        loginBean.account.set(account);
        loginBean.editable.set(TextUtils.isEmpty(account));
        binding.setDrawable(ResUtil.getBackground(this));
        boolean fromLogin = TextUtils.isEmpty(account);
        binding.setClick(new RegisterHandler(this, binding,fromLogin));
        initViews(fromLogin);
    }

    @Override
    public boolean isTransStatus() {
        return false;
    }

    private void initViews(boolean fromLogin) {
        pubBack.setOnClickListener(this);
        pubTitle.setText(fromLogin ? "快速注册" : "修改密码");
        pubMenu.setVisibility(View.GONE);
        binding.account.addTextChangedListener(new TextChangedListener(binding.accountClear));
        binding.nickname.addTextChangedListener(new TextChangedListener(binding.nicknameClear));
        binding.password.addTextChangedListener(new TextChangedListener(binding.passwordClear));
        binding.repeatMm.addTextChangedListener(new TextChangedListener(binding.repeatClear));
        binding.accountClear.setImageResource(fromLogin ? R.mipmap.edit_clear : R.color.white);
        binding.accountClear.setEnabled(fromLogin);
    }

    @Override
    public int setEdgePosition() {
        return LEFT;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pub_back:
                finish();
                break;
        }
    }
}
