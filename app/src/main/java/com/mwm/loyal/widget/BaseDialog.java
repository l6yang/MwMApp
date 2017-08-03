package com.mwm.loyal.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mwm.loyal.R;
import com.mwm.loyal.imp.Contact;
import com.mwm.loyal.imp.DialogClickListener;
import com.mwm.loyal.utils.StringUtil;

public class BaseDialog extends Dialog implements Contact {

    private BaseDialog(@NonNull Context context) {
        this(context, 0);
    }

    private BaseDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    private Object objTag;

    public Object getTag() {
        return objTag;
    }

    private void setTag(Object objTag) {
        this.objTag = objTag;
    }

    public static class Builder implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (listener != null)
                listener.onClick(baseDialog, v);
        }

        private Context mContext;
        private CharSequence sequenceTitle = "温馨提示", sequenceContent, sequenceOk, sequenceCancel;
        private DialogClickListener listener;
        private BaseDialog baseDialog;
        private TYPE type = TYPE.NONE;
        private TextView textTitle, textContent;
        private View layoutOk, layoutCancel;
        private Button btnOk, btnCancel;
        private boolean cancelable = false, outsideCancelable = false;
        private Object objectTag;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Context getContext() {
            return mContext;
        }

        public Builder setTitle(@StringRes int strId) {
            setTitle(mContext.getResources().getString(strId));
            return this;
        }

        public Builder setTitle(CharSequence sequence) {
            this.sequenceTitle = sequence;
            return this;
        }

        public Builder setTag(Object objectTag) {
            this.objectTag = objectTag;
            return this;
        }

        public CharSequence getTitle() {
            return sequenceTitle;
        }

        public TextView getTitleView() {
            return textTitle;
        }

        public Builder setContent(@StringRes int strId) {
            setTitle(mContext.getResources().getString(strId));
            return this;
        }

        public Builder setContent(CharSequence sequence) {
            this.sequenceContent = sequence;
            return this;
        }

        public CharSequence getContent() {
            return sequenceContent;
        }

        public TextView getContentView() {
            return textContent;
        }

        public Builder setLeftBtnText(CharSequence sequence) {
            this.sequenceCancel = sequence;
            return this;
        }

        public CharSequence getLeftText() {
            return sequenceCancel;
        }

        public Builder setRightBtnText(CharSequence sequence) {
            this.sequenceOk = sequence;
            return this;
        }

        public Builder setBtnText(@Size(min = 1, max = 2) @NonNull String[] sequence) {
            switch (type) {
                case LEFT:
                    setLeftBtnText(sequence[0]);
                    break;
                case RIGHT:
                    setRightBtnText(sequence[0]);
                    break;
                case NONE:
                    setLeftBtnText(sequence[0]);
                    setRightBtnText(sequence[1]);
                    break;
            }
            return this;
        }

        public CharSequence getRightText() {
            return sequenceOk;
        }

        public Builder setRightBtnText(@StringRes int strId) {
            setTitle(mContext.getResources().getString(strId));
            return this;
        }

        public Builder setLeftBtnText(@StringRes int strId) {
            setTitle(mContext.getResources().getString(strId));
            return this;
        }

        public Builder setClickListener(DialogClickListener listener) {
            this.listener = listener;
            return this;
        }

        public DialogClickListener getListener() {
            return listener;
        }

        public Builder setBottomBtnType(TYPE type) {
            this.type = type;
            return this;
        }

        public TYPE getType() {
            return type;
        }

        public Button getLeftBtn() {
            return btnCancel;
        }

        public Button getRightBtn() {
            return btnOk;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public boolean getCancelable() {
            return cancelable;
        }

        public Builder setOutsideCancel(boolean outsideCancelable) {
            this.outsideCancelable = outsideCancelable;
            return this;
        }

        public boolean getOutsideCancelable() {
            return outsideCancelable;
        }

        public BaseDialog create() {
            baseDialog = new BaseDialog(mContext, R.style.DialogTheme);
            baseDialog.setContentView(R.layout.dialog_permission);
            baseDialog.setCancelable(cancelable);
            baseDialog.setCanceledOnTouchOutside(outsideCancelable);
            baseDialog.setTag(objectTag);
            initDialogView();
            switch (type) {
                case NONE:
                    if (layoutOk != null)
                        layoutOk.setVisibility(View.VISIBLE);
                    if (layoutCancel != null)
                        layoutCancel.setVisibility(View.VISIBLE);
                    break;
                case LEFT:
                    if (layoutOk != null)
                        layoutOk.setVisibility(View.GONE);
                    if (layoutCancel != null)
                        layoutCancel.setVisibility(View.VISIBLE);
                    break;
                case RIGHT:
                    if (layoutOk != null)
                        layoutOk.setVisibility(View.VISIBLE);
                    if (layoutCancel != null)
                        layoutCancel.setVisibility(View.GONE);
                    break;
            }
            return baseDialog;
        }

        public BaseDialog show() {
            final BaseDialog dialog = create();
            dialog.show();
            return dialog;
        }

        private void initDialogView() {
            textTitle = (TextView) baseDialog.findViewById(R.id.text_title);
            textTitle.setText(replaceNull(sequenceTitle));
            textContent = (TextView) baseDialog.findViewById(R.id.text_content);
            textContent.setText(replaceNull(sequenceContent));
            layoutOk = baseDialog.findViewById(R.id.dialog_layout_ok);
            layoutCancel = baseDialog.findViewById(R.id.dialog_layout_cancel);
            btnOk = (Button) baseDialog.findViewById(R.id.dialog_btn_ok);
            btnOk.setText(sequenceOk);
            btnCancel = (Button) baseDialog.findViewById(R.id.dialog_btn_cancel);
            btnCancel.setText(replaceNull(sequenceCancel));
            btnOk.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
        }

        private String replaceNull(Object object) {
            return StringUtil.replaceNull(object);
        }
    }
}
