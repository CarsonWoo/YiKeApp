package com.example.carson.yikeapp.Utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by 84594 on 2018/2/24.
 */

public class FontUtils {

    private static String archRivalUrl = "SF_Arch_Rival.ttf";
    //Typeface是字体
    private static Typeface tf;

    /**
     * 设置字体
     */

    public static Typeface setArchRivalFont(Context context) {
        if (tf == null) {
            tf = Typeface.createFromAsset(context.getAssets(), archRivalUrl);
        }
        return tf;
    }

}
