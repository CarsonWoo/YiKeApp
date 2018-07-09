package com.example.carson.yikeapp.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.example.carson.yikeapp.R;

/**
 * Created by 84594 on 2018/7/9.
 */

public class RoundRectImg extends android.support.v7.widget.AppCompatImageView {

    private Path path = null;

    private int topLeftRadius = 0;
    private int topRightRadius = 0;
    private int bottomLeftRadius = 0;
    private int bottomRightRadius = 0;

    private int DEFAULT_RADIUS = 30;

    private int mWidth;
    private int mHeight;

    public RoundRectImg(Context context) {
        this(context, null);
    }

    public RoundRectImg(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundRectImg(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundRectImg);
        if (a.hasValue(R.styleable.RoundRectImg_top_left_radius) &&
                a.hasValue(R.styleable.RoundRectImg_top_right_radius) &&
                a.hasValue(R.styleable.RoundRectImg_bottom_left_radius) &&
                a.hasValue(R.styleable.RoundRectImg_bottom_right_radius)) {
            topLeftRadius = a.getInt(R.styleable.RoundRectImg_top_left_radius, DEFAULT_RADIUS);
            topRightRadius = a.getInt(R.styleable.RoundRectImg_top_right_radius, DEFAULT_RADIUS);
            bottomLeftRadius = a.getInt(R.styleable.RoundRectImg_bottom_left_radius, DEFAULT_RADIUS);
            bottomRightRadius = a.getInt(R.styleable.RoundRectImg_bottom_right_radius, DEFAULT_RADIUS);
        } else {
            if (a.hasValue(R.styleable.RoundRectImg_radius)) {
                DEFAULT_RADIUS = a.getInt(R.styleable.RoundRectImg_radius, DEFAULT_RADIUS);
            }
            topLeftRadius = DEFAULT_RADIUS;
            topRightRadius = DEFAULT_RADIUS;
            bottomLeftRadius = DEFAULT_RADIUS;
            bottomRightRadius = DEFAULT_RADIUS;
        }
        a.recycle();
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mHeight = getHeight();
        mWidth = getWidth();
        updateRoundedPath();
    }

    private void updateRoundedPath() {
        path = new Path();
        path.addRoundRect(new RectF(0, 0, mWidth, mHeight),
                new float[] {topLeftRadius, topLeftRadius, topRightRadius, topRightRadius,
                        bottomLeftRadius, bottomLeftRadius, bottomRightRadius, bottomRightRadius},
                Path.Direction.CW);
    }

    private void init() {
        super.setScaleType(ScaleType.CENTER_CROP);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (path != null) {
            canvas.clipPath(path);
        }
        super.onDraw(canvas);
    }
}
