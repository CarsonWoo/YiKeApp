package com.example.carson.yikeapp.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.carson.yikeapp.Utils.FontUtils;

/**
 * Created by 84594 on 2018/2/24.
 */

public class ArchRivalTextView extends android.support.v7.widget.AppCompatTextView {

    public ArchRivalTextView(Context context) {
        super(context);
        setTypeface(FontUtils.setArchRivalFont(context));
    }

    public ArchRivalTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setTypeface(FontUtils.setArchRivalFont(context));
    }

    public ArchRivalTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(FontUtils.setArchRivalFont(context));
    }
}
