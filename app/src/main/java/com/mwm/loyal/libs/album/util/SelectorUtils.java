package com.mwm.loyal.libs.album.util;

import android.content.res.ColorStateList;

public class SelectorUtils {

    /**
     * 选中效果。
     *
     * @param normal  正常颜色。
     * @param checked 选中颜色。
     * @return {@link ColorStateList}.
     */
    public static ColorStateList createColorStateList(int normal, int checked) {
        int[] colors = new int[]{checked, normal};
        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_checked};
        states[1] = new int[]{};
        return new ColorStateList(states, colors);
    }

}
