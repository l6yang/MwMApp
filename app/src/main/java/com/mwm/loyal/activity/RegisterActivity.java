package com.mwm.loyal.activity;

import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.mwm.loyal.R;
import com.mwm.loyal.base.BaseSwipeActivity;
import com.mwm.loyal.beans.ObservableAccountBean;
import com.mwm.loyal.databinding.ActivityRegisterBinding;
import com.mwm.loyal.handler.RegisterHandler;
import com.mwm.loyal.impl.TextChangedListener;
import com.mwm.loyal.utils.ImageUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class RegisterActivity extends BaseSwipeActivity<ActivityRegisterBinding> {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected int actLayoutRes() {
        return R.layout.activity_register;
    }

    @Override
    public void afterOnCreate() {
        ObservableAccountBean observableAccountBean = new ObservableAccountBean();
        binding.setObservableAccountBean(observableAccountBean);
        String account = getIntent().getStringExtra("account");
        observableAccountBean.account.set(account);
        observableAccountBean.editable.set(TextUtils.isEmpty(account));
        binding.setDrawable(ImageUtil.getBackground(this));
        boolean fromLogin = TextUtils.isEmpty(account);
        String extra = getIntent().getStringExtra("extra");
        binding.setClick(new RegisterHandler(this, binding));
        initViews(fromLogin, extra);
    }

    private void initViews(boolean fromLogin, String extra) {
        if (TextUtils.isEmpty(extra)) {
            toolbar.setTitle(fromLogin ? "快速注册" : "修改密码");
            binding.accountClear.setImageResource(fromLogin ? R.mipmap.edit_clear : R.color.white);
            binding.accountClear.setEnabled(fromLogin);
        } else {
            toolbar.setTitle(R.string.destroy_account);
            binding.accountClear.setImageResource(R.color.white);
            binding.accountClear.setEnabled(false);
            binding.password.setVisibility(View.GONE);
            binding.repeatMm.setVisibility(View.GONE);
        }
        setSupportActionBar(toolbar);
        binding.account.addTextChangedListener(new TextChangedListener(binding.accountClear));
        binding.nickname.addTextChangedListener(new TextChangedListener(binding.nicknameClear));
        binding.password.addTextChangedListener(new TextChangedListener(binding.passwordClear));
        binding.repeatMm.addTextChangedListener(new TextChangedListener(binding.repeatClear));
    }

    @Override
    public int setEdgePosition() {
        return LEFT;
    }

    @OnClick({
            R.id.account_clear, R.id.nickname_clear,
            R.id.password_clear, R.id.repeat_clear
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.account_clear:

                break;
            case R.id.nickname_clear:

                break;
            case R.id.password_clear:

                break;
            case R.id.repeat_clear:

                break;
        }
    }
}
