package com.example.carson.yikeapp.Utils;

import android.support.v7.widget.LinearSnapHelper;

/**
 * Created by 84594 on 2018/4/2.
 */

public class MyLinearSnapHelper extends LinearSnapHelper {

    //边界判定
    public static boolean mStateIdle = false;

    @Override
    public int[] calculateScrollDistance(int velocityX, int velocityY) {
        if (mStateIdle) {
            return new int[2];
        }
        return super.calculateScrollDistance(velocityX, velocityY);
    }
}
