package com.way.heard.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.way.heard.R;
import com.way.heard.models.Image;
import com.way.heard.models.Post;
import com.way.heard.ui.activities.ImageDisplayActivity;
import com.way.heard.ui.activities.PostDisplayActivity;
import com.way.heard.ui.activities.UserDisplayActivity;
import com.way.heard.ui.views.ViewHolder;
import com.way.heard.utils.GlideImageLoader;
import com.way.heard.utils.Util;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pc on 2016/5/1.
 */
public class PostListAdapter extends BaseListAdapter<Post> {

    private Context context;

    public PostListAdapter(Context ctx) {
        super(ctx);
        context = ctx;
    }

    @Override
    public View getView(int position, View conView, ViewGroup parent) {
        if (conView == null) {
            conView = inflater.inflate(R.layout.item_post_normal, null, false);
        }
        //1.Get View
        ImageView ivAvatar = ViewHolder.findViewById(conView, R.id.iv_post2_item_avatar);
        TextView tvUsername = ViewHolder.findViewById(conView, R.id.tv_post2_item_nickname);
        TextView tvCreateAt = ViewHolder.findViewById(conView, R.id.tv_post2_item_createat);
        TextView tvContent = ViewHolder.findViewById(conView, R.id.tv_post2_item_content);
        ImageView ivPhoto = ViewHolder.findViewById(conView, R.id.iv_post2_item_photo);
        TextView tvTag = ViewHolder.findViewById(conView, R.id.tv_post2_item_tag);
        ImageView ivLikesButton = ViewHolder.findViewById(conView, R.id.iv_post2_item_likes_button);
        //TextView tvLikesCount = ViewHolder.findViewById(conView, R.id.tv_post2_item_likes_count);
        ImageView ivCommentsButton = ViewHolder.findViewById(conView, R.id.iv_post2_item_comments_button);
        //TextView tvCommentsCount = ViewHolder.findViewById(conView, R.id.tv_post2_item_comments_count);

        //2.Get Data
        final Post post = datas.get(position);
        String strAvatarUrl = post.getAuthor().getString("avatar");
        String strUsername = post.getAuthor().getUsername();
        long longCreateAt = post.getCreatedAt().getTime();
        String strContent = post.getContent();

        List<Image> images = post.tryGetPhotoList();
        String strImageUrl = "";
        if (images != null && images.size() > 0) {
            strImageUrl = images.get(0).getUrl();
        }
        String strTag = post.getTag();
        List<String> likesObjectIDs = post.getLikes();
        List<String> commentObjectIDs = post.getComments();

        //3.Set Data
        //3.1.Avatar
        GlideImageLoader.displayImage(context, strAvatarUrl, ivAvatar);
        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDisplayActivity.go(context, post.getAuthor());
            }
        });
        //3.2.Username
        tvUsername.setText(strUsername);
        //3.3.CreateAt
        tvCreateAt.setText(millisecs2DateString(longCreateAt));
        //3.4.Content
        if (!TextUtils.isEmpty(strContent)) {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(strContent);
        }else {
            tvContent.setVisibility(View.GONE);
        }
        //3.5.Photo
        if (!TextUtils.isEmpty(strImageUrl)) {
            ivPhoto.setVisibility(View.VISIBLE);
            final String finalStrImageUrl = strImageUrl;
            ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageDisplayActivity.go(context, finalStrImageUrl);
                }
            });
            GlideImageLoader.displayImage(context, strImageUrl, ivPhoto);
        } else {
            ivPhoto.setVisibility(View.GONE);
        }
        //3.6.Tag
        if (!TextUtils.isEmpty(strTag)) {
            tvTag.setVisibility(View.VISIBLE);
            tvTag.setText(strTag);
        }else {
            tvTag.setVisibility(View.GONE);
        }
        //3.7.Likes
        final AVUser currentUser = AVUser.getCurrentUser();
        if (likesObjectIDs == null) {
            likesObjectIDs = new ArrayList<>();
        }
        final boolean isliked = likesObjectIDs.contains(currentUser.getObjectId());
        if(isliked){
            //tvLikesCount.setTextColor(context.getResources().getColor(R.color.colorAccent));
            ivLikesButton.setImageResource(R.drawable.ic_thumb_up_accent);
        }else {
            ivLikesButton.setImageResource(R.drawable.ic_thumb_up);
        }

        final List<String> finalLikesObjectIDs = likesObjectIDs;
        ivLikesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isliked){//Remove Like
                    finalLikesObjectIDs.remove(currentUser.getObjectId());
                    post.setLikes(finalLikesObjectIDs);
                    post.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if(e==null){
                                notifyDataSetChanged();
                                //ivLikesButton.setImageResource(R.drawable.ic_thumb_up);
                            }else {
                                Util.toast(context,"Error, " + e.getMessage());
                            }
                        }
                    });
                }else {//Add Like
                    finalLikesObjectIDs.add(currentUser.getObjectId());
                    post.setLikes(finalLikesObjectIDs);
                    post.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if(e==null){
                                notifyDataSetChanged();
                                //ivLikesButton.setImageResource(R.drawable.ic_thumb_up_accent);
                            }else {
                                Util.toast(context,"Error, " + e.getMessage());
                            }
                        }
                    });
                }
            }
        });

        //3.8.Comments
        ivCommentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostDisplayActivity.go(context, post);
            }
        });

        return conView;
    }

    public static PrettyTime prettyTime = new PrettyTime();

    public static String getDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        return format.format(date);
    }

    public static String millisecs2DateString(long timestamp) {
        long gap = System.currentTimeMillis() - timestamp;
        if (gap < 1000 * 60 * 60 * 24) {
            String s = prettyTime.format(new Date(timestamp));
            return s.replace(" ", "");
        } else {
            return getDate(new Date(timestamp));
        }
    }

}

