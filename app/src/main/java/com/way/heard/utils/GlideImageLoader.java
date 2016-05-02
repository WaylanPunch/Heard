package com.way.heard.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.way.heard.R;

import cn.finalteam.galleryfinal.widget.GFImageView;

/**
 * Created by pc on 2016/4/18.
 */
public class GlideImageLoader {
    private final static String TAG = GlideImageLoader.class.getName();

    public static void displayImageFromPath(Context context, String path, final GFImageView imageView, int resourceId, int errorResourceId) {
        try {
            Glide.with(context)
                    .load("file://" + path)
                    .placeholder(resourceId)
                    .error(errorResourceId)
                    //.override(width, height)
                    //.diskCacheStrategy(DiskCacheStrategy.NONE) //不缓存到SD卡
                    //.skipMemoryCache(true)
                    //.centerCrop()
                    .into(new ImageViewTarget<GlideDrawable>(imageView) {
                        @Override
                        protected void setResource(GlideDrawable resource) {
                            imageView.setImageDrawable(resource);
                        }

                        @Override
                        public void setRequest(Request request) {
                            imageView.setTag(R.id.adapter_item_tag_key,request);
                        }

                        @Override
                        public Request getRequest() {
                            return (Request) imageView.getTag(R.id.adapter_item_tag_key);
                        }
                    });
        } catch (Exception e) {
            LogUtil.e(TAG, "displayImageFromPath error", e);
        }
    }

    public static void displayImageFromUrl(Context context, String url, final GFImageView imageView, int resourceId, int errorResourceId) {
        try {
            Glide.with(context)
                    .load(url)
                    .placeholder(resourceId)
                    .error(errorResourceId)
                    //.override(width, height)
                    //.diskCacheStrategy(DiskCacheStrategy.NONE) //不缓存到SD卡
                    //.skipMemoryCache(true)
                    .centerCrop()
                    .into(new ImageViewTarget<GlideDrawable>(imageView) {
                        @Override
                        protected void setResource(GlideDrawable resource) {
                            imageView.setImageDrawable(resource);
                        }

                        @Override
                        public void setRequest(Request request) {
                            imageView.setTag(R.id.adapter_item_tag_key,request);
                        }

                        @Override
                        public Request getRequest() {
                            return (Request) imageView.getTag(R.id.adapter_item_tag_key);
                        }
                    });
        } catch (Exception e) {
            LogUtil.e(TAG, "displayImageFromUrl error", e);
        }
    }

    public static void displayImage(Context context, String url, final ImageView imageView) {
        try {
            Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.ic_picture_default)
                    .error(R.drawable.ic_picture_default)
                    //.override(width, height)
                    //.diskCacheStrategy(DiskCacheStrategy.NONE) //不缓存到SD卡
                    //.skipMemoryCache(true)
                    //.fitCenter()
                    .into(new ImageViewTarget<GlideDrawable>(imageView) {
                        @Override
                        protected void setResource(GlideDrawable resource) {
                            imageView.setImageDrawable(resource);
                        }

                        @Override
                        public void setRequest(Request request) {
                            imageView.setTag(R.id.adapter_item_tag_key,request);
                        }

                        @Override
                        public Request getRequest() {
                            return (Request) imageView.getTag(R.id.adapter_item_tag_key);
                        }
                    });
        } catch (Exception e) {
            LogUtil.e(TAG, "displayImageFromUrl error", e);
        }
    }

    public static void clearMemoryCache() {
    }
}
