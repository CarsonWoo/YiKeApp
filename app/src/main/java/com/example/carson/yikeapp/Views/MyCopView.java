package com.example.carson.yikeapp.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.example.carson.yikeapp.R;


/**
 * Created by Administrator on 2017/7/8.
 */

public class MyCopView extends android.support.v7.widget.AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener
        ,ScaleGestureDetector.OnScaleGestureListener{

    private static final String TAG = "MyCopView";
    private RectF shelterR;
    private Paint mPaint;
    private int mRadius = 200;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private int lastPointerCout;
    private float mLastX,mLastY;
    private RectF circleR;
    private Matrix mScaleMatrix = new Matrix();
    private float matrixValues[] = new float[9];
    private static float SCALE_MAX = 3.0f;
    private float initScale = 1.0f;
    private boolean once = true , isAutoScale;
    private static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG
            | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
            | Canvas.CLIP_TO_LAYER_SAVE_FLAG;
    private Bitmap mBitmap;

    public MyCopView(Context context){
        this(context,null);
    }

    public MyCopView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyCopView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setScaleType(ImageView.ScaleType.MATRIX);

        init(context);
    }

    private void init(Context context){
        //定义画笔
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(2);
        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(R.color.cropShadow));

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {

                if (isAutoScale == true)
                    return true;
                float x = e.getX();
                float y = e.getY();
                if (getScale() < initScale*3)
                {
                    MyCopView.this.postDelayed(
                            new AutoScaleRunnable(initScale*3, x, y), 16);
                    isAutoScale = true;
                } else
                {
                    MyCopView.this.postDelayed(
                            new AutoScaleRunnable(initScale, x, y), 16);
                    isAutoScale = true;
                }

                return true;
            }
        });
        mScaleGestureDetector = new ScaleGestureDetector(context,this);

    }

    /**
     * 获得当前的缩放比例
     *
     * @return
     */
    public final float getScale()
    {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    private float getTranslateX(){
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MTRANS_X];
    }

    private float getTranslateY(){
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MTRANS_Y];
    }

    /**
     * 确保阴影盖住图片
     * @return
     */
    private RectF getShelterRectF(){
        float x = (int) getTranslateX();
        float y = (int) getTranslateY();
        float width = getDrawable().getIntrinsicWidth() * getScale();
        float height = getDrawable().getIntrinsicHeight() * getScale();
        if(shelterR == null){
            shelterR = new RectF(x,y,width+x,height+y);
        }else {
            shelterR.set(x,y,width+x,height+y);
        }
        return shelterR;
    }

    public void setImageResource(int resourceId){
        super.setImageResource(resourceId);
        mBitmap = BitmapFactory.decodeResource(getResources(),resourceId);
    }

    public void setImageBitmap(Bitmap bitmap)
    {
        if(getDrawable()!=null) {
            Log.d(TAG,"NotFirstImage");
            super.setImageBitmap(bitmap);
            draw(new Canvas());
            once =true;
        }else {
            super.setImageBitmap(bitmap);
        }
        mBitmap = bitmap;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if(getDrawable() == null){
            return;
        }

        //画圆外阴影
        getShelterRectF();
        canvas.saveLayer(shelterR,null,LAYER_FLAGS);
        canvas.drawRect(shelterR,mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
//        Log.d(TAG,"width:"+getWidth());
//        Log.d(TAG,"height:"+getHeight());
        mRadius = Math.min(getWidth(),getHeight())/2;
        Log.d(TAG,"radius:"+mRadius);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(getWidth()/2,getHeight()/2,mRadius,mPaint);
        if(circleR == null){
            circleR = new RectF(getWidth()/2 - mRadius,getHeight()/2 - mRadius,getWidth()/2 + mRadius,getHeight()/2 + mRadius);
        }
        canvas.restore();
        mPaint.setXfermode(null);
        mPaint.setColor(getResources().getColor(R.color.cropShadow));
    }

    public Bitmap clipBitmap(){
        final Paint resultPaint = new Paint();
        resultPaint.setAntiAlias(true);
        int dw = getDrawable().getIntrinsicWidth();
        int dh = getDrawable().getIntrinsicHeight();
        float x = getTranslateX() - (getWidth() - dw) / 2;
        float y = getTranslateY() - (getHeight() - dh) / 2;
        mBitmap = zoomBitmap(mBitmap);
        Bitmap target = Bitmap.createBitmap(mRadius * 2, mRadius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(mRadius, mRadius, mRadius, resultPaint);
        resultPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(mBitmap,- (dw / 2 - mRadius) + x ,- (dh / 2 - mRadius) + y, resultPaint);
        return target;
    }

    private Bitmap zoomBitmap(Bitmap bitmap){
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(getScale(),getScale());
        Bitmap imageBitmap = Bitmap.createBitmap(bitmap,0,0,imageWidth,
                imageHeight,matrix,true);
        return imageBitmap;
    }


    private class AutoScaleRunnable implements Runnable {

        static final float BIGGER = 1.07f;
        static final float SMALLER = 0.93f;
        private float mTargetScale;
        private float tmpScale;

        /**
         * 缩放中心
         */
        private float x;
        private float y;

        /**
         * 传入目标缩放值，根据目标值与当前值，判断应该放大还是缩小
         *
         * @param targetScale
         */
        public AutoScaleRunnable(float targetScale, float x ,float y){
            this.mTargetScale = targetScale;
            this.x = x;
            this.y = y;
            if(getScale() < mTargetScale){
                tmpScale = BIGGER;
            }else {
                tmpScale = SMALLER;
            }
        }

        @Override
        public void run() {
            //缩放
            mScaleMatrix.postScale(tmpScale,tmpScale,x,y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);

            final float currnetScale = getScale();
            //如果值在合理范围内，继续缩放
            if ((tmpScale > 1.0f && currnetScale < mTargetScale)
                    ||(tmpScale < 1.0f && currnetScale > mTargetScale)){
                MyCopView.this.postDelayed(this,16);
            }else//缩放或放大过头了
            {
                final float deltaScale = mTargetScale / currnetScale;
                mScaleMatrix.postScale(deltaScale,deltaScale,x,y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }
        }
    }

    /**
     * 在缩放时，进行图片显示范围的控制
     */
    private void checkBorderAndCenterWhenScale()
    {

        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        // 如果宽或高大于屏幕，则控制范围
        if (rect.width() >= width)
        {
            if (rect.left > 0)
            {
                deltaX = -rect.left;
            }
            if (rect.right < width)
            {
                deltaX = width - rect.right;
            }
        }
        if (rect.height() >= height)
        {
            if (rect.top > 0)
            {
                deltaY = -rect.top;
            }
            if (rect.bottom < height)
            {
                deltaY = height - rect.bottom;
            }
        }
        // 如果宽或高小于屏幕，则让其居中
        if (rect.width() < width)
        {
            deltaX = width * 0.5f - rect.right + 0.5f * rect.width();
        }
        if (rect.height() < height)
        {
            deltaY = height * 0.5f - rect.bottom + 0.5f * rect.height();
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 根据当前图片的Matrix获得图片的范围
     *
     * @return
     */
    private RectF getMatrixRectF()
    {
        Matrix matrix = mScaleMatrix;
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (null != d)
        {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(getDrawable() == null){
            return;
        }

        //画圆外阴影
        getShelterRectF();
        canvas.saveLayer(shelterR,null,LAYER_FLAGS);
        canvas.drawRect(shelterR,mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
//        Log.d(TAG,"width:"+getWidth());
//        Log.d(TAG,"height:"+getHeight());
        mRadius = Math.min(getWidth(),getHeight())/2;
        Log.d(TAG,"radius:"+mRadius);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(getWidth()/2,getHeight()/2,mRadius,mPaint);
        if(circleR == null){
            circleR = new RectF(getWidth()/2 - mRadius,getHeight()/2 - mRadius,getWidth()/2 + mRadius,getHeight()/2 + mRadius);
        }
        canvas.restore();
        mPaint.setXfermode(null);
        mPaint.setColor(getResources().getColor(R.color.cropShadow));
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }else {
            getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        mScaleGestureDetector.onTouchEvent(event);
        float x = 0, y = 0;
        //拿到触摸点的个数：
        final int pointerCount = event.getPointerCount();
        //得到多个触摸点的X与Y均值：
        for(int i = 0; i < pointerCount; i++){
            x += event.getX(i);
            y += event.getY(i);
        }
        x = x / pointerCount;
        y = y / pointerCount;
        /**
         * 每当触摸点发生变化，重置mLasX,mLasY
         */
        if(pointerCount != lastPointerCout){
            mLastX = x;
            mLastY = y;
        }
        lastPointerCout = pointerCount;

        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                float dx = x - mLastX;
                float dy = y - mLastY;

                if (shelterR.left + dx >= circleR.left || shelterR.right + dx <= circleR.right){
                    dx = 0;
                }
                if (shelterR.top + dy >  circleR.top || shelterR.bottom + dy <= circleR.bottom){
                    dy = 0;
                }
                mScaleMatrix.postTranslate(dx, dy);
                setImageMatrix(mScaleMatrix);
                mLastX = x;
                mLastY = y;
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG,"TouchEventAction:UP-"+ MotionEvent.ACTION_UP);
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG,"TouchEventAction:CANCEL-"+ MotionEvent.ACTION_CANCEL);
                lastPointerCout = 0;
                break;
        }
        return true;
    }

    //配置图形宽高比圆形大
    @Override
    public void onGlobalLayout() {
        if(once) {
            Drawable d = getDrawable();
            if(d==null){
                return;
            }
            int width = getWidth();
            int height = getHeight();
            mRadius = Math.min(getWidth(),getHeight())/2;
            Log.d(TAG,"配置圆形宽高比圆形大_mRadius:"+mRadius);

            int diameter = mRadius * 2;
            Log.d(TAG,"配置圆形宽高比圆形大_diameter:"+diameter);
            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();
            Log.d(TAG,"配置圆形宽高比圆形大_dw:"+dw);
            Log.d(TAG,"配置圆形宽高比圆形大_dh:"+dh);
            float scale = 1.0f;
            if(dw < diameter && dh > diameter){
                scale = diameter * 1.0f / dw;
            }
            if(dh < diameter && dw > diameter){
                scale = diameter * 1.0f / dh;
            }
            if(dw < diameter && dh < diameter){
                scale = Math.max(diameter * 1.0f / dw,diameter * 1.0f / dh);
            }
            if(dw > diameter && dh > diameter){
                Log.d(TAG,"ImageTooBig");
                scale = Math.max(diameter * 1.0f / dw,diameter * 1.0f / dh);
            }

            initScale = scale;
            //图片移动：
            Log.d(TAG,"initScale:"+initScale);
            Log.d(TAG,"Translate:"+(width-dw)/2+"-"+(height-dh)/2);
            mScaleMatrix.postTranslate((width-dw)/2,(height-dh)/2);
            mScaleMatrix.setScale(scale,scale);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
            once = false;
        }
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();

        if(getDrawable() == null){
            return true;
        }

        /**
         * 控制缩放范围
         */
        if((scale<SCALE_MAX && scaleFactor >1.0f)
                ||(scale > initScale && scaleFactor < 1.0f)){
            /**
             * 最大最小值判断
             */
            if (scaleFactor * scale < initScale){
                scaleFactor = initScale / scale;
            }
            if(scaleFactor * scale > SCALE_MAX){
                scaleFactor = SCALE_MAX / scale;
            }
            /**
             * 设置缩放比例
             */
            mScaleMatrix.postScale(scaleFactor,scaleFactor,detector.getFocusX(),detector.getFocusY());
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
        }

        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

}

