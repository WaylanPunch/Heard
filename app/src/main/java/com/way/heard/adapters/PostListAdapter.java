package com.way.heard.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.way.heard.R;
import com.way.heard.models.Post;
import com.way.heard.ui.views.ViewHolder;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pc on 2016/5/1.
 */
public class PostListAdapter extends BaseListAdapter<Post> {

    public PostListAdapter(Context ctx) {
        super(ctx);
    }

    @Override
    public View getView(int position, View conView, ViewGroup parent) {
        if (conView == null) {
            conView = inflater.inflate(R.layout.item_post_normal, null, false);
        }
        ImageView ivAvatar = ViewHolder.findViewById(conView, R.id.iv_post2_item_avatar);
        TextView tvUsername = ViewHolder.findViewById(conView, R.id.tv_post2_item_nickname);
        TextView tvCreateAt = ViewHolder.findViewById(conView, R.id.tv_post2_item_createat);
        TextView tvContent = ViewHolder.findViewById(conView, R.id.tv_post2_item_content);
        ImageView ivphoto = ViewHolder.findViewById(conView, R.id.iv_post2_item_photo);
        TextView tvTag = ViewHolder.findViewById(conView, R.id.tv_post2_item_tag);
        ImageView ivLikesButton = ViewHolder.findViewById(conView, R.id.iv_post2_item_likes_button);
        TextView tvLikesCount = ViewHolder.findViewById(conView, R.id.tv_post2_item_likes_count);
        ImageView ivCommentsButton = ViewHolder.findViewById(conView, R.id.iv_post2_item_comments_button);
        TextView tvCommentsCount = ViewHolder.findViewById(conView, R.id.tv_post2_item_comments_count);

        final Post post = datas.get(position);

        /*
        TextView nameView = ViewHolder.findViewById(conView, R.id.nameView);
        TextView textView = ViewHolder.findViewById(conView, R.id.statusText);
        ImageView avatarView = ViewHolder.findViewById(conView, R.id.avatarView);
        ImageView imageView = ViewHolder.findViewById(conView, R.id.statusImage);
        ImageView likeView = ViewHolder.findViewById(conView, R.id.likeView);
        TextView likeCountView = ViewHolder.findViewById(conView, R.id.likeCount);
        View likeLayout = ViewHolder.findViewById(conView, R.id.likeLayout);
        TextView timeView = ViewHolder.findViewById(conView, R.id.timeView);

        final Post post = datas.get(position);
        AVUser source = innerStatus.getSource();
        StatusUtils.displayAvatar(source, avatarView);
        nameView.setText(source.getUsername());

        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonActivity.go(ctx, innerStatus.getSource());
            }
        });

        if (TextUtils.isEmpty(innerStatus.getMessage())) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(innerStatus.getMessage());
            textView.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(innerStatus.getImageUrl()) == false) {
            imageView.setVisibility(View.VISIBLE);
            //ImageLoader.getInstance().displayImage(innerStatus.getImageUrl(), imageView, StatusUtils.normalImageOptions);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Intent intent = new Intent(ctx, ImageBrowserActivity.class);
                    //intent.putExtra("url", innerStatus.getImageUrl());
                    //ctx.startActivity(intent);
                }
            });
        } else {
            imageView.setVisibility(View.GONE);
        }


        final AVObject detail = status.getDetail();

        final List<String> likes;
        if (detail.get(App.LIKES) != null) {
            likes = (List<String>) detail.get(App.LIKES);
        } else {
            likes = new ArrayList<String>();
        }

        int n = likes.size();
        if (n > 0) {
            likeCountView.setText(n + "");
        } else {
            likeCountView.setText("");
        }

        final AVUser user = AVUser.getCurrentUser();
        final String userId = user.getObjectId();
        final boolean contains = likes.contains(userId);
        if (contains) {
            likeView.setImageResource(R.drawable.status_ic_player_liked);
        } else {
            likeView.setImageResource(R.drawable.ic_player_like);
        }
        likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveCallback saveCallback = new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (StatusUtils.filterException(ctx, e)) {
                            notifyDataSetChanged();
                        }
                    }
                };
                if (contains) {
                    likes.remove(userId);
                    StatusService.saveStatusLikes(detail, likes, saveCallback);
                } else {
                    likes.add(userId);
                    StatusService.saveStatusLikes(detail, likes, saveCallback);
                }
            }
        });

        timeView.setText(millisecs2DateString(innerStatus.getCreatedAt().getTime()));
        */
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

