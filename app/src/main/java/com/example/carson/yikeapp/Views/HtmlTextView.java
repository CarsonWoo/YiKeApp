package com.example.carson.yikeapp.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.example.carson.yikeapp.Utils.HtmlTagHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by 84594 on 2018/3/26.
 */

public class HtmlTextView extends android.support.v7.widget.AppCompatTextView {

    public static final String TAG = "HtmlTextView";
    public static final boolean DEBUG = false;

    public HtmlTextView(Context context) {
        super(context);
    }

    public HtmlTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HtmlTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");

        return s.hasNext() ? s.next() : "";
    }

    /**
     * Parses String containing HTML to Android's Spannable format and displays
     * it in this TextView.
     *
     * @param html String containing HTML, for example: "<b>Hello world!</b>"
     */
    public void setHtmlFromString(String html) {
        Html.ImageGetter imageGetter = new UrlImageGetter(getContext(), this);
        // this uses Android's Html class for basic parsing, and HtmlTagHandler
        //TODO 重写HtmlTagHandler 以实现想要的切割效果
        setText(Html.fromHtml(html, imageGetter, new HtmlTagHandler()));
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    public class UrlImageGetter implements Html.ImageGetter {

        Context c;
        TextView tv;
        int width;

        public UrlImageGetter(Context context, TextView tv) {
            this.c = context;
            this.tv = tv;
            width = getResources().getDisplayMetrics().widthPixels;
        }

        @Override
        public Drawable getDrawable(String source) {
            final UrlDrawable urlDrawable = new UrlDrawable();
            ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                    .createDefault(getContext());
            ImageLoader.getInstance().init(configuration);
            ImageLoader.getInstance().loadImage(source, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    // 计算缩放比例
                    float scaleWidth = ((float) width)/loadedImage.getWidth();
                    // 取得想要缩放的matrix参数
                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, scaleWidth);
                    loadedImage = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(),
                            loadedImage.getHeight(), matrix, true);
                    urlDrawable.bitmap = loadedImage;
                    urlDrawable.setBounds(0, 0, loadedImage.getWidth(),
                            loadedImage.getHeight());
                    tv.invalidate();
                    tv.setText(tv.getText());
                }
            });
            return urlDrawable;
        }
    }

    public class UrlDrawable extends BitmapDrawable {
        protected Bitmap bitmap;

        @Override
        public void draw(Canvas canvas) {
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, 0, 0, getPaint());
            }
        }
    }

}
