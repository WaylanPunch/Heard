package com.way.heard.adapters;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.way.heard.R;
import com.way.heard.models.BannerModel;
import com.way.heard.models.Image;
import com.way.heard.utils.GlideImageLoader;
import com.way.heard.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;

/**
 * Created by pc on 2016/5/7.
 */
public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {
    private final static String TAG = TopicAdapter.class.getName();

    private int lastPosition = -1;
    private FragmentActivity mContext;
    private OnImageClickListener onImageClickListener;

    public void setOnImageClickListener(OnImageClickListener onImageClickListener1) {
        this.onImageClickListener = onImageClickListener1;
    }

    public List<BannerModel> getBannerModels() {
        return mBannerModels;
    }

    public void setBannerModels(List<BannerModel> mBannerModels) {
        this.mBannerModels = mBannerModels;
    }

    private List<BannerModel> mBannerModels;

    public TopicAdapter(FragmentActivity context) {
        this.mContext = context;
        this.mBannerModels = new ArrayList<>();
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R
                    .anim.item_bottom_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        holder.rlContainer.clearAnimation();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.item_banner_view;
        View v = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //1. Get Item
        final BannerModel item = mBannerModels.get(position);

        //2. Get Data From Item
        String tagStr = item.getTag().getContent();
        List<Image> images = item.getImages();
        List<String> tags = new ArrayList<>();
        List<String> imgUrls = new ArrayList<>();
        for (Image img : images) {
            String imgUrl = img.getUrl();
            imgUrls.add(imgUrl);
            tags.add(tagStr);
        }
        //3.Set Data
        LogUtil.d(TAG, "onBindViewHolder debug, Images Count = " + imgUrls.size());
        LogUtil.d(TAG, "onBindViewHolder debug, Tags Count = " + tags.size());
        initZoomStack(holder.bgaBanner, imgUrls, tags);

        setAnimation(holder.rlContainer, position);
    }

    private void initZoomStack(BGABanner banner, List<String> imgUrls, List<String> tags) {
        int viewCOUNT = imgUrls.size();
        List<ImageView> mZoomStackViews = getViews(viewCOUNT);
        banner.setViews(mZoomStackViews);
        for (int i = 0; i < viewCOUNT; i++) {
            GlideImageLoader.displayImage(mContext, imgUrls.get(i), mZoomStackViews.get(i));
            //Glide.with(MainActivity.this).load(bannerModel.imgs.get(i)).placeholder(R.drawable.holder).error(R.drawable.holder).into(mZoomStackViews.get(i));
        }
        banner.setTips(tags);
    }

    private List<ImageView> getViews(int count) {
        List<ImageView> views = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            views.add((ImageView) mContext.getLayoutInflater().inflate(R.layout.item_banner_image, null));
        }
        return views;
    }

    @Override
    public int getItemCount() {
        return mBannerModels == null ? 0 : mBannerModels.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rlContainer;
        BGABanner bgaBanner;

        //TextView tvCommentsCount ;
        public ViewHolder(View contentView) {
            super(contentView);
            rlContainer = (RelativeLayout) contentView.findViewById(R.id.rl_banner_container);
            bgaBanner = (BGABanner) contentView.findViewById(R.id.bga_banner_topic);
        }
    }

    public interface OnImageClickListener {
        void onImageClick(int pos);
    }
}
