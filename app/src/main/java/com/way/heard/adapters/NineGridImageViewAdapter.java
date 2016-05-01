package com.way.heard.adapters;

import android.content.Context;
import android.widget.ImageView;

import java.util.List;

import cn.finalteam.galleryfinal.widget.GFImageView;

/**
 * Created by pc on 2016/4/18.
 */
public abstract class NineGridImageViewAdapter<T> {
    public abstract void onDisplayImage(Context context, GFImageView imageView, T t);

    public void onItemImageClick(Context context, int index, List<T> list) {
    }

    public GFImageView generateImageView(Context context) {
        GFImageView imageView = new GFImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }
}