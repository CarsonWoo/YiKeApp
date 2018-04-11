package com.example.carson.yikeapp.Utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;

/**
 * Created by 84594 on 2018/4/9.
 */

public class AnimationUtils {

    public static final int TYPE_TRANSLATION_X = 0;
    public static final int TYPE_TRANSLATION_Y = 1;
    public static final int TYPE_TRANSLATION_BOTH = 2;

    public static final int TYPE_SCALE_X = 3;
    public static final int TYPE_SCALE_Y = 4;
    public static final int TYPE_SCALE_BOTH = 5;

    @SuppressLint("ObjectAnimatorBinding")
    public static void setScaleAnimation(Object obj, int duration, int type, float ...values) {
        ObjectAnimator scaleX, scaleY;
        switch (type) {
            case TYPE_SCALE_X:
                scaleX = ObjectAnimator.ofFloat(obj, "scaleX", values);
                scaleX.setDuration(duration).start();
                break;
            case TYPE_SCALE_Y:
                scaleY = ObjectAnimator.ofFloat(obj, "scaleY", values);
                scaleY.setDuration(duration).start();
                break;
            case TYPE_SCALE_BOTH:
                scaleX = ObjectAnimator.ofFloat(obj, "scaleX", values);
                scaleY = ObjectAnimator.ofFloat(obj, "scaleY", values);
                AnimatorSet set = new AnimatorSet();
                set.play(scaleX).with(scaleY);
                set.setDuration(duration);
                set.start();
                break;
        }
    }

    @SuppressLint("ObjectAnimatorBinding")
    public static void setTranslationAnimation(Object obj, int duration, int type, float ...values) {
        ObjectAnimator translationX, translationY;
        switch (type) {
            case TYPE_TRANSLATION_X:
                translationX = ObjectAnimator.ofFloat(obj, "translationX", values);
                translationX.setDuration(duration);
                translationX.start();
                break;
            case TYPE_TRANSLATION_Y:
                translationY = ObjectAnimator.ofFloat(obj, "translationY", values);
                translationY.setDuration(duration);
                translationY.start();
                break;
            case TYPE_TRANSLATION_BOTH:
                translationX = ObjectAnimator.ofFloat(obj, "translationX", values);
                translationY = ObjectAnimator.ofFloat(obj, "translationY", values);
                AnimatorSet set = new AnimatorSet();
                set.play(translationX).with(translationY);
                set.setDuration(duration).start();
                break;
        }
    }

}
