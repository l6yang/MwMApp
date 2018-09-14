package com.mwm.loyal.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.beans.LoginBean;
import com.mwm.loyal.databinding.ActivityRegisterBinding;
import com.mwm.loyal.handler.RegisterHandler;
import com.mwm.loyal.impl.TextChangedListener;
import com.mwm.loyal.utils.ImageUtil;

import butterknife.BindView;

public class RegisterActivity extends BaseSwipeActivity<ActivityRegisterBinding> implements View.OnClickListener {
    @BindView(R.id.pub_back)
    View pubBack;
    @BindView(R.id.pub_title)
    TextView pubTitle;

    @Override
    protected int actLayoutRes() {
        return R.layout.activity_register;
    }

    @Override
    public void afterOnCreate() {
        LoginBean loginBean = new LoginBean();
        binding.setLoginBean(loginBean);
        String account = getIntent().getStringExtra("account");
        loginBean.account.set(account);
        loginBean.editable.set(TextUtils.isEmpty(account));
        binding.setDrawable(ImageUtil.getBackground(this));
        boolean fromLogin = TextUtils.isEmpty(account);
        String extra = getIntent().getStringExtra("extra");
        binding.setClick(new RegisterHandler(this, binding));
        initViews(fromLogin, extra);
    }

    private void initViews(boolean fromLogin, String extra) {
        pubBack.setOnClickListener(this);
        if (TextUtils.isEmpty(extra)) {
            pubTitle.setText(fromLogin ? "快速注册" : "修改密码");
            binding.accountClear.setImageResource(fromLogin ? R.mipmap.edit_clear : R.color.white);
            binding.accountClear.setEnabled(fromLogin);
        } else {
            pubTitle.setText(R.string.destroy_account);
            binding.accountClear.setImageResource(R.color.white);
            binding.accountClear.setEnabled(false);
            binding.password.setVisibility(View.GONE);
            binding.repeatMm.setVisibility(View.GONE);
        }
        binding.account.addTextChangedListener(new TextChangedListener(binding.accountClear));
        binding.nickname.addTextChangedListener(new TextChangedListener(binding.nicknameClear));
        binding.password.addTextChangedListener(new TextChangedListener(binding.passwordClear));
        binding.repeatMm.addTextChangedListener(new TextChangedListener(binding.repeatClear));
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
