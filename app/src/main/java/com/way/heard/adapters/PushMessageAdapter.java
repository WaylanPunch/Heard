package com.way.heard.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.way.heard.R;
import com.way.heard.utils.PushMessageData.PushMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2016/5/7.
 */
public class PushMessageAdapter extends RecyclerView.Adapter<PushMessageAdapter.ViewHolder> {
    private final static String TAG = PushMessageAdapter.class.getName();

    private int lastPosition = -1;
    private Context mContext;
    private List<PushMessage> messageList;

    private OnAvatarClickListener onAvatarClickListener;

    public void setOnAvatarClickListener(OnAvatarClickListener onAvatarClickListener1) {
        this.onAvatarClickListener = onAvatarClickListener1;
    }

    public List<PushMessage> getPushMessage() {
        return messageList;
    }

    public void setPushMessage(List<PushMessage> messages) {
        if (messages != null) {
            this.messageList = messages;
        }
    }

    public PushMessageAdapter(Context context) {
        this.mContext = context;
        this.messageList = new ArrayList<>();
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.item_bottom_in);
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
        int layoutId = R.layout.item_notification_normal;
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //1. Get Item

        //2. Get Data From Item


        //3.Set Data

        setAnimation(holder.cvContainer, position);
    }

    @Override
    public int getItemCount() {
        return messageList == null ? 0 : messageList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cvContainer;
        ImageView ivAvatar;
        TextView tvUsername;
        TextView tvMessage;

        public ViewHolder(View contentView) {
            super(contentView);
            cvContainer = (CardView) contentView.findViewById(R.id.cv_item_notification_container);
            ivAvatar = (ImageView) contentView.findViewById(R.id.iv_item_notification_normal_avatar);
            tvUsername = (TextView) contentView.findViewById(R.id.tv_item_notification_normal_username);
            tvMessage = (TextView) contentView.findViewById(R.id.tv_item_notification_normal_message);
        }
    }

    public interface OnAvatarClickListener {
        void onAvatarClick(int pos);
    }
}
