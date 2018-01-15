package com.example.carson.yikeapp.Views;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;
import android.util.AttributeSet;

import com.example.carson.yikeapp.R;

/**
 * Created by 84594 on 2017/12/23.
 */

public class CircleImageView extends android.support.v7.widget.AppCompatImageView {

    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;

    private static final int COLORDRAWABLE_DIMENSION = 2;

    //默认边界宽度
    private static final int DEFAULT_BORDER_WIDTH = 0;
    //默认边界颜色
    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;
    private static final boolean DEFAULT_BORDER_OVERLAY = false;

    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();

    private final Matrix mShaderMatrix = new Matrix();

    private final Paint mBitmapPaint = new Paint();
    private final Paint mBorderPaint = new Paint();

    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader; //位图渲染
    private int mBitmapWidth; //位图宽度
    private int mBitmapHeight; //位图高度

    private float mDrawableRadius; //图片半径
    private float mBorderRadius; //边框的半径

    private ColorFilter mColorFilter;

    private boolean mReady;
    private boolean mSetupPending;
    private boolean mBorderOverlay;


    public CircleImageView(Context context) {
        super(context);
        init();
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CircleImageView, defStyle, 0);
        //获取边界宽度
        mBorderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_border_width,
                DEFAULT_BORDER_WIDTH);
        //获取边界颜色
        mBorderColor = a.getColor(R.styleable.CircleImageView_border_color,
                DEFAULT_BORDER_COLOR);
        mBorderOverlay = a.getBoolean(R.styleable.CircleImageView_border_overlay,
                DEFAULT_BORDER_OVERLAY);
        //调用recycle() 方便后面使用
        a.recycle();

        init();
    }

    private void init() {
        //将ScaleType强制设为CENTER_CROP 即水平垂直居中 进行缩放
        super.setScaleType(SCALE_TYPE);
        mReady = true;

        if (mSetupPending) {
            setup();
            mSetupPending = false;
        }

    }

    @Override
    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    //此图片只支持CENTER_CROP属性


    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException("Not Supported!");
        }
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (getDrawable() == null) {
            return ;
        }
        //绘制内圆形 画笔为mBitmapPaint
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mDrawableRadius, mBitmapPaint);
        //绘制外框 画笔为mBorderPaint 但需判断其宽度是否为0 为0则不画
        if (mBorderWidth != 0) {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, mBorderRadius, mBorderPaint);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        if (this.mBorderColor == borderColor) {
            return;
        }

        this.mBorderColor = borderColor;
        mBorderPaint.setColor(mBorderColor);
        //重绘
        invalidate();
    }

    public void setBorderColorResource(int res) {
        setBorderColor(getContext().getResources().getColor(res));
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (this.mBorderWidth == borderWidth) {
            return;
        }

        this.mBorderWidth = borderWidth;
        setup();
    }

    public boolean isBorderOverlay() {
        return mBorderOverlay;
    }

    public void setBorderOverlay(boolean overlay) {
        if (overlay == this.mBorderOverlay) {
            return;
        }

        this.mBorderOverlay = overlay;
        setup();
    }

    /**
     * 以下四个函数都是
     * 复写ImageView的setImageXxx()方法
     * @param bm
     */
    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        setup();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        System.out.println("setImageDrawable -- setup");
        setup();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (cf == mColorFilter) {
            return;
        }

        mColorFilter = cf;
        mBitmapPaint.setColorFilter(mColorFilter);
        invalidate();
    }
    /**
     * Drawable转Bitmap
     * @param drawable
     * @return
     */
    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION,
                        COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    private void setup() {
        //第一次进入时mReady为false 故直接返回（未初始化）
        if (!mReady) {
            mSetupPending = true;
            return ;
        }

        if (mBitmap == null) {
            return;
        }

        //构造渲染器 若图片太小则通过参数拉伸
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);
        //设置画笔样式
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth); //边界宽度

        //取原图宽高
        mBitmapWidth = mBitmap.getWidth();
        mBitmapHeight = mBitmap.getHeight();

        //设置含边界显示区域 取的是CircleImageView的实际大小
        mBorderRect.set(0, 0, getWidth(), getHeight());
        //外圆半径需剪去宽边距
        mBorderRadius = Math.min((mBorderRect.width() - mBorderWidth) / 2,
                (mBorderRect.height() - mBorderWidth) / 2);

        mDrawableRect.set(mBorderRect);
        if (!mBorderOverlay) {
            mDrawableRect.inset(mBorderWidth, mBorderWidth);
        }

        mDrawableRadius = Math.min(mDrawableRect.width() / 2, mDrawableRect.height() / 2);

        //设置渲染器的变换矩阵也即是mBitmap用何种缩放形式填充
        updateShaderMatrix();
        //手动触发ondraw()函数 完成最终的绘制
        invalidate();

    }

    /**
     * 这个函数为设置BitmapShader的Matrix参数，设置最小缩放比例，平移参数。
     * 作用：保证图片损失度最小和始终绘制图片正中央的那部分
     */
    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);
        // 这里不好理解 这个不等式也就是
        // (mBitmapWidth / mDrawableRect.width()) > (mBitmapHeight / mDrawableRect.height())
        //取最小的缩放比例
        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            //y轴缩放 x轴平移 使得图片的y轴方向的边的尺寸缩放到图片显示区域（mDrawableRect）一样）
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            //x轴缩放 y轴平移 使得图片的x轴方向的边的尺寸缩放到图片显示区域（mDrawableRect）一样）
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }
        // shaeder的变换矩阵，我们这里主要用于放大或者缩小。
        mShaderMatrix.setScale(scale, scale);
        // 平移
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mDrawableRect.left,
                (int) (dy + 0.5f) + mDrawableRect.top);
        // 设置变换矩阵
        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }


}
