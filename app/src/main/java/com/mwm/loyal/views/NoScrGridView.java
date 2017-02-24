package com.mwm.loyal.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class NoScrGridView extends GridView {
    public NoScrGridView(Context context) {
        super(context);
    }

    public NoScrGridView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public NoScrGridView(Context context, AttributeSet attr, int defStyleAttr) {
        super(context, attr, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
