package com.way.heard.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * Created by pc on 2016/7/3.
 */
public class AdaptiveImageView extends ImageView {
    // 控件默认长、宽
    private int defaultWidth = 0;
    private int defaultHeight = 0;


    // 比例
    private float scale = 0;


    public AdaptiveImageView(Context context) {
        super(context);
    }


    public AdaptiveImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public AdaptiveImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        this.measure(0, 0);
        if (drawable.getClass() == NinePatchDrawable.class)
            return;
        //Drawable drawable1 = ((TransitionDrawable) drawable).findDrawableByLayerId(android.R.id.background);
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
        if (bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
            return;
        }
        if (defaultWidth == 0) {
            defaultWidth = getWidth();


        }
        if (defaultHeight == 0) {
            defaultHeight = getHeight();
        }


        scale = (float) defaultWidth / (float) bitmap.getWidth();
        defaultHeight = Math.round(bitmap.getHeight() * scale);
        LayoutParams params = this.getLayoutParams();
        params.width = defaultWidth;
        params.height = defaultHeight;
        this.setLayoutParams(params);
        super.onDraw(canvas);
    }
}
