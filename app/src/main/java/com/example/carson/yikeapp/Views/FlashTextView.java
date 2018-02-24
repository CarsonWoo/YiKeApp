package com.example.carson.yikeapp.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.carson.yikeapp.Utils.FontUtils;

/**
 * Created by 84594 on 2018/2/24.
 */

public class FlashTextView extends android.support.v7.widget.AppCompatTextView {
    private int mViewWidth = 0;//textview的宽度

    private TextPaint mPaint;//画布

    private LinearGradient mLinearGradient;
    private Matrix mGradientMatrix;//通过矩阵移动控制颜色
    private int mTranslate = 0;//位移距离

    public FlashTextView(Context context) {
        this(context, null, 0);
        setTypeface(FontUtils.setArchRivalFont(context));
    }

    public FlashTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        setTypeface(FontUtils.setArchRivalFont(context));
    }

    public FlashTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(FontUtils.setArchRivalFont(context));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mGradientMatrix != null) {
            mTranslate += mViewWidth / 3;
            if (mTranslate > 2 * mViewWidth) {
                mTranslate = -mViewWidth;
            }
            mGradientMatrix.setTranslate(mTranslate, 0);
            mLinearGradient.setLocalMatrix(mGradientMatrix);
            postInvalidateDelayed(50);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mViewWidth == 0) {
            mViewWidth = getMeasuredWidth();
            if (mViewWidth > 0) {
                mPaint = getPaint();
                mLinearGradient = new LinearGradient(0, 0, mViewWidth, 0, new int[]{
                        Color.WHITE, Color.parseColor("#e26323"), Color.WHITE
                }, null, Shader.TileMode.CLAMP);
                mPaint.setShader(mLinearGradient);
                mGradientMatrix = new Matrix();
            }
        }
    }
}
