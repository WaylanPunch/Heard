package com.way.heard.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.way.heard.R;
import com.way.heard.models.Image;
import com.way.heard.models.Post;
import com.way.heard.ui.activities.PostDisplayActivity;
import com.way.heard.ui.activities.UserDisplayActivity;
import com.way.heard.ui.views.TagCloudView;
import com.way.heard.utils.GlideImageLoader;
import com.way.heard.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2016/5/7.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private int lastPosition = -1;
    private Context mContext;
    private OnImageClickListener onImageClickListener;

    public void setOnImageClickListener(OnImageClickListener onImageClickListener1) {
        this.onImageClickListener = onImageClickListener1;
    }

    public List<Post> getPosts() {
        return mPosts;
    }

    public void setPosts(List<Post> mPosts) {
        this.mPosts = mPosts;
    }

    private List<Post> mPosts;

    public PostAdapter(Context context) {
        this.mContext = context;
        this.mPosts = new ArrayList<>();
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
        holder.cvContainer.clearAnimation();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.item_post_normal;
        View v = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //1. Get Item
        final Post post = mPosts.get(position);

        //2. Get Data From Item
        String strAvatarUrl = post.getAuthor().getString("avatar");
        String strUsername = post.getAuthor().getUsername();
        long longCreateAt = post.getCreatedAt().getTime();
        String strContent = post.getContent();
        final List<Image> images = post.tryGetPhotoList();
        String strImageUrl = "";
        if (images != null && images.size() > 0) {
            strImageUrl = images.get(0).getUrl();
        }
        List<String> strTags = post.getTags();
        List<String> likesObjectIDs = post.getLikes();
        List<String> commentObjectIDs = post.getComments();

        //3.Set Data
        //3.1.Avatar
        GlideImageLoader.displayImage(mContext, strAvatarUrl, holder.ivAvatar);
        holder.ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDisplayActivity.go(mContext, post.getAuthor());
            }
        });
        //3.2.Username
        holder.tvUsername.setText(strUsername);
        //3.3.CreateAt
        holder.tvCreateAt.setText(Util.millisecs2DateString(longCreateAt));
        //3.4.Content
        if (!TextUtils.isEmpty(strContent)) {
            holder.tvContent.setVisibility(View.VISIBLE);
            holder.tvContent.setText(strContent);
        } else {
            holder.tvContent.setVisibility(View.GONE);
        }
        //3.5.Photo
        if (!TextUtils.isEmpty(strImageUrl)) {
            holder.ivPhoto.setVisibility(View.VISIBLE);
            final String finalStrImageUrl = strImageUrl;
            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ImageDisplayActivity.go(mContext, images.get(0), position);
                    if (onImageClickListener != null) {
                        onImageClickListener.onImageClick(position);
                    }
                }
            });
            GlideImageLoader.displayImage(mContext, strImageUrl, holder.ivPhoto);
        } else {
            holder.ivPhoto.setVisibility(View.GONE);
        }
        //3.6.Tag
        if (strTags != null && strTags.size() > 0) {
            holder.tcvTags.setVisibility(View.VISIBLE);
            holder.tcvTags.setTags(strTags);
        } else {
            holder.tcvTags.setVisibility(View.GONE);
        }
        //3.7.Likes
        final AVUser currentUser = AVUser.getCurrentUser();
        if (likesObjectIDs == null) {
            likesObjectIDs = new ArrayList<>();
        }
        final boolean isliked = likesObjectIDs.contains(currentUser.getObjectId());
        if (isliked) {
            //tvLikesCount.setTextColor(context.getResources().getColor(R.color.colorAccent));
            holder.ivLikesButton.setImageResource(R.drawable.ic_thumb_up_accent);
        } else {
            holder.ivLikesButton.setImageResource(R.drawable.ic_thumb_up);
        }

        final List<String> finalLikesObjectIDs = likesObjectIDs;
        holder.ivLikesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isliked) {//Remove Like
                    finalLikesObjectIDs.remove(currentUser.getObjectId());
                    post.setLikes(finalLikesObjectIDs);
                    post.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                notifyDataSetChanged();
                                //ivLikesButton.setImageResource(R.drawable.ic_thumb_up);
                            } else {
                                Util.toast(mContext, "Error, " + e.getMessage());
                            }
                        }
                    });
                } else {//Add Like
                    finalLikesObjectIDs.add(currentUser.getObjectId());
                    post.setLikes(finalLikesObjectIDs);
                    post.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                notifyDataSetChanged();
                                //ivLikesButton.setImageResource(R.drawable.ic_thumb_up_accent);
                            } else {
                                Util.toast(mContext, "Error, " + e.getMessage());
                            }
                        }
                    });
                }
            }
        });

        //3.8.Comments
        holder.ivCommentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostDisplayActivity.go(mContext, post);
            }
        });

        setAnimation(holder.cvContainer, position);
    }

    @Override
    public int getItemCount() {
        return mPosts == null ? 0 : mPosts.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cvContainer;
        ImageView ivAvatar;
        TextView tvUsername;
        TextView tvCreateAt;
        TextView tvContent;
        ImageView ivPhoto;
        //TextView tvTag;
        TagCloudView tcvTags;
        ImageView ivLikesButton;
        //TextView tvLikesCount ;
        ImageView ivCommentsButton;

        //TextView tvCommentsCount ;
        public ViewHolder(View contentView) {
            super(contentView);
            cvContainer = (CardView) contentView.findViewById(R.id.cv_post2_item_container);
            ivAvatar = (ImageView) contentView.findViewById(R.id.iv_post2_item_avatar);
            tvUsername = (TextView) contentView.findViewById(R.id.tv_post2_item_nickname);
            tvCreateAt = (TextView) contentView.findViewById(R.id.tv_post2_item_createat);
            tvContent = (TextView) contentView.findViewById(R.id.tv_post2_item_content);
            ivPhoto = (ImageView) contentView.findViewById(R.id.iv_post2_item_photo);
            //tvTag = (TextView) contentView.findViewById(R.id.tv_post2_item_tag);
            tcvTags = (TagCloudView) contentView.findViewById(R.id.tcv_post2_item_tags);
            ivLikesButton = (ImageView) contentView.findViewById(R.id.iv_post2_item_likes_button);
            // tvLikesCount =  (TextView)contentView.findViewById( R.id.tv_post2_item_likes_count);
            ivCommentsButton = (ImageView) contentView.findViewById(R.id.iv_post2_item_comments_button);
            // tvCommentsCount = (TextView) contentView.findViewById( R.id.tv_post2_item_comments_count);
        }
    }

    public interface OnImageClickListener {
        void onImageClick(int pos);
    }

}
