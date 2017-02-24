package com.mwm.loyal.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.mwm.loyal.R;

public class SideBar extends View {
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    private String[] SingleWord = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
    private int choose = -1;
    private Paint paint = new Paint();
    private TextView textView;

    public SideBar(Context paramContext) {
        super(paramContext);
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();// 点击y坐标
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y / getHeight() * SingleWord.length);// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.
        switch (action) {
            case MotionEvent.ACTION_UP:
                setBackground(new ColorDrawable(0x00000000));
                choose = -1;//
                invalidate();
                if (textView != null) {
                    textView.setVisibility(View.INVISIBLE);
                }
                break;
            default:
                setBackgroundResource(R.drawable.bg_sidebar);
                if (oldChoose != c) {
                    if (c >= 0 && c < SingleWord.length) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(SingleWord[c]);
                        }
                        if (textView != null) {
                            textView.setText(SingleWord[c]);
                            textView.setVisibility(View.VISIBLE);
                        }
                        choose = c;
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (SingleWord.length <= 0) return;
        // 获取焦点改变背景颜色.
        int height = getHeight();// 获取对应高度
        int width = getWidth(); // 获取对应宽度
        int singleHeight = height / SingleWord.length;// 获取每一个字母的高度
        for (int i = 0; i < SingleWord.length; i++) {
            paint.setColor(Color.rgb(33, 65, 98));
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);
            paint.setTextSize(40);
            // 选中的状态
            if (i == choose) {
                paint.setColor(Color.parseColor("#3399ff"));
                paint.setFakeBoldText(true);
            }
            // x坐标等于中间-字符串宽度的一半.
            float xPos = width / 2 - paint.measureText(SingleWord[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(SingleWord[i], xPos, yPos, paint);
            paint.reset();// 重置画笔
        }
    }

    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener parambe) {
        onTouchingLetterChangedListener = parambe;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void setTextColor(int paramInt) {
        paint.setColor(getResources().getColor(paramInt, null));
        invalidate();
    }

    public void setTextView(TextView paramTextView) {
        textView = paramTextView;
    }

    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String str);
    }
}