package com.way.heard.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.way.heard.R;
import com.way.heard.utils.GlideImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2016/5/7.
 */
public class ProfileFollowerAdapter extends RecyclerView.Adapter<ProfileFollowerAdapter.ViewHolder> {
    private final static String TAG = ProfileFollowerAdapter.class.getName();

    private int lastPosition = -1;
    private Context mContext;
    private List<AVUser> mUsers;
    private OnAvatarClickListener onAvatarClickListener;

    public void setOnAvatarClickListener(OnAvatarClickListener onAvatarClickListener1) {
        this.onAvatarClickListener = onAvatarClickListener1;
    }

    public List<AVUser> getUsers() {
        return mUsers;
    }

    public void setUsers(List<AVUser> users) {
        this.mUsers = users;
    }

    public ProfileFollowerAdapter(Context context) {
        this.mContext = context;
        this.mUsers = new ArrayList<>();
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
        holder.itemview.clearAnimation();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.item_profile_follower_normal;
        View v = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //1. Get Item
        final AVUser user = mUsers.get(position);

        //2. Get Data From Item
        String strUsername = user.getUsername();
        String strDisplayname = user.getString("displayname");
        String strAvatarUrl = user.getString("avatar");
        String strSignature = user.getString("signature");

        //3.Set Data
        if (!TextUtils.isEmpty(strDisplayname)) {
            holder.tvUsername.setText(strDisplayname);
        } else {
            holder.tvUsername.setText(strUsername);
        }
        if (!TextUtils.isEmpty(strSignature)) {
            holder.tvSignature.setText(strSignature);
        }
        if (!TextUtils.isEmpty(strAvatarUrl)) {
            GlideImageLoader.displayImage(mContext, strAvatarUrl, holder.ivAvatar);
        }

        holder.ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAvatarClickListener != null) {
                    onAvatarClickListener.onAvatarClick(position);
                }
            }
        });
        setAnimation(holder.itemview, position);
    }

    @Override
    public int getItemCount() {
        return mUsers == null ? 0 : mUsers.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        View itemview;
        //CardView cvContainer;
        ImageView ivAvatar;
        TextView tvUsername;
        TextView tvSignature;
        //Button btnFollow;

        public ViewHolder(View contentView) {
            super(contentView);
            itemview = contentView;
            //cvContainer = (CardView) contentView.findViewById(R.id.cv_item_follower_container);
            ivAvatar = (ImageView) contentView.findViewById(R.id.iv_item_follower_normal_avatar);
            tvUsername = (TextView) contentView.findViewById(R.id.tv_item_follower_normal_username);
            tvSignature = (TextView) contentView.findViewById(R.id.tv_item_follower_normal_signature);
            //btnFollow = (Button) contentView.findViewById(R.id.btn_item_follower_normal_follow);
        }
    }

    public interface OnAvatarClickListener {
        void onAvatarClick(int pos);
    }
}
