package com.way.heard.adapters;

import android.content.Context;
import android.widget.ImageView;

import com.way.heard.ui.views.GridImageView;

import java.util.List;

/**
 * Created by pc on 2016/4/18.
 */
public abstract class NineGridImageViewAdapter<T> {
    public abstract void onDisplayImage(Context context, ImageView imageView, T t);

    public void onItemImageClick(Context context, int index, List<T> list) {
    }

    public ImageView generateImageView(Context context) {
        GridImageView imageView = new GridImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }
}