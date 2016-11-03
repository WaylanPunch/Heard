package com.way.heard.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkOnClickListener;
import com.luseen.autolinklibrary.AutoLinkTextView;
import com.way.heard.R;
import com.way.heard.models.Comment;
import com.way.heard.ui.activities.UserDisplayActivity;
import com.way.heard.utils.GlideImageLoader;
import com.way.heard.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2016/5/8.
 */
public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int NORMAL_ITEM = 0;
    private static final int GROUP_ITEM = 1;

    private Context mContext;
    private List<Comment> mDataList;
    private LayoutInflater mLayoutInflater;
    private OnCommentItemReplyListener onCommentItemReplyListener;
    private OnCommentItemClickListener onCommentItemClickListener;
    private OnAutoLinkTextViewClickListener onAutoLinkTextViewClickListener;

    public List<Comment> getComments() {
        return mDataList;
    }

    public void setComments(List<Comment> mComments) {
        this.mDataList = mComments;
    }

    public void setOnCommentItemClickListener(OnCommentItemClickListener onCommentItemClickListener) {
        this.onCommentItemClickListener = onCommentItemClickListener;
    }

    public void setOnCommentItemReplyListener(OnCommentItemReplyListener onCommentItemReplyListener) {
        this.onCommentItemReplyListener = onCommentItemReplyListener;
    }

    public void setOnAutoLinkTextViewClickListener(OnAutoLinkTextViewClickListener onAutoLinkTextViewClickListener) {
        this.onAutoLinkTextViewClickListener = onAutoLinkTextViewClickListener;
    }

    public CommentAdapter(Context mContext, List<Comment> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    /**
     * 渲染具体的ViewHolder
     *
     * @param viewGroup ViewHolder的容器
     * @param viewType  一个标志，我们根据该标志可以实现渲染不同类型的ViewHolder
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == NORMAL_ITEM) {
            return new NormalItemHolder(mLayoutInflater.inflate(R.layout.item_comment_normal, viewGroup, false));
        } else {
            return new GroupItemHolder(mLayoutInflater.inflate(R.layout.item_comment_flag, viewGroup, false));
        }
    }

    /**
     * 绑定ViewHolder的数据。
     *
     * @param viewHolder
     * @param position   数据源list的下标
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Comment entity = mDataList.get(position);
        if (null == entity)
            return;

        if (viewHolder instanceof GroupItemHolder) {
            bindGroupItem(entity, (GroupItemHolder) viewHolder, position);
        } else {
            bindNormalItem(entity, (NormalItemHolder) viewHolder, position);
        }
    }

    void bindNormalItem(final Comment entity, NormalItemHolder holder, final int position) {
        //1. Get Data
        String strAvatarUrl = entity.getAuthor().getString("avatar");
        String strUsername = entity.getAuthor().getUsername();
        long longCreateAt = entity.getCreatedAt().getTime();
        String strContent = entity.getContent();
        List<String> likesObjectIDs = entity.getLikes();
        String strReplyTo = "";
        String strReplyFor = "";
        if (entity.getReplyTo() != null) {
            strReplyTo = entity.getReplyTo().getUsername();
        }
        if (entity.getReplyFor() != null) {
            strReplyFor = entity.getReplyFor().getContent();
        }

        //2. Set Data
        //2.1. Avatar
        GlideImageLoader.displayImage(mContext, strAvatarUrl, holder.ivAvatar);
        holder.ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDisplayActivity.go(mContext, entity.getAuthor());
            }
        });
        //2.2. Username
        holder.tvUsername.setText(strUsername);
        //2.3. CreateAt
        holder.tvCreateAt.setText(Util.millisecs2DateString(longCreateAt));
        //2.4. Content
        holder.altv_comment_content.setAutoLinkText(strContent);
        //2.5. SubContent
        if (TextUtils.isEmpty(strReplyTo)) {
            holder.altv_comment_sub_username.setVisibility(View.GONE);
            holder.altv_comment_sub_content.setVisibility(View.GONE);
        } else {
            holder.altv_comment_sub_username.setVisibility(View.VISIBLE);
            holder.altv_comment_sub_content.setVisibility(View.VISIBLE);
            holder.altv_comment_sub_username.setAutoLinkText("@" + strReplyTo);
            holder.altv_comment_sub_content.setAutoLinkText(strReplyFor);
        }
        //2.6. Likes
        final AVUser currentUser = AVUser.getCurrentUser();
        if (likesObjectIDs == null) {
            likesObjectIDs = new ArrayList<>();
        }
        final boolean isliked = likesObjectIDs.contains(currentUser.getObjectId());
        if (isliked) {
            holder.ivLike.setImageResource(R.drawable.ic_thumb_up_accent);
        } else {
            holder.ivLike.setImageResource(R.drawable.ic_thumb_up);
        }
        final List<String> finalLikesObjectIDs = likesObjectIDs;
        holder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isliked) {//Remove Like
                    finalLikesObjectIDs.remove(currentUser.getObjectId());
                    entity.setLikes(finalLikesObjectIDs);
                    entity.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                notifyDataSetChanged();
                            } else {
                                Util.toast(mContext, "Error, " + e.getMessage());
                            }
                        }
                    });
                } else {//Add Like
                    finalLikesObjectIDs.add(currentUser.getObjectId());
                    entity.setLikes(finalLikesObjectIDs);
                    entity.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                notifyDataSetChanged();
                            } else {
                                Util.toast(mContext, "Error, " + e.getMessage());
                            }
                        }
                    });
                }
            }
        });
        holder.ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCommentItemReplyListener != null) {
                    onCommentItemReplyListener.onCommentItemReply(position);
                }
            }
        });
    }

    public interface OnCommentItemReplyListener {
        void onCommentItemReply(int position);
    }

    public interface OnCommentItemClickListener {
        void onCommentItemClick(int position);
    }

    public interface OnAutoLinkTextViewClickListener {
        void onAutoLinkTextViewClick(AutoLinkMode autoLinkMode, String matchedText);
    }

    void bindGroupItem(Comment entity, GroupItemHolder holder, int position) {
        if (position == 0) {
            holder.tvFlag.setText("Latest");
        } else {
            holder.tvFlag.setText("Hotest");
        }
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    /**
     * 决定元素的布局使用哪种类型
     *
     * @param position 数据源List的下标
     * @return 一个int型标志，传递给onCreateViewHolder的第二个参数
     */
    @Override
    public int getItemViewType(int position) {
        //第一个要显示时间
        /*
        if (position == 0) {
            return GROUP_ITEM;
        }
        Comment currentItem = mDataList.get(position);
        return currentItem == null ? GROUP_ITEM : NORMAL_ITEM;
        */
        return NORMAL_ITEM;
    }


    /**
     * Comment
     */
    public class NormalItemHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvUsername;
        TextView tvCreateAt;
        //        RelativeLayout rlSubLayout;
//        TextView tvSubUsername;
//        TextView tvSubContent;
//        TextView tvContent;

        AutoLinkTextView altv_comment_content;
        AutoLinkTextView altv_comment_sub_username;
        AutoLinkTextView altv_comment_sub_content;
        ImageView ivReply;
        ImageView ivLike;

        public NormalItemHolder(View itemView) {
            super(itemView);
            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_comment_avatar);
            tvUsername = (TextView) itemView.findViewById(R.id.tv_comment_username);
            tvCreateAt = (TextView) itemView.findViewById(R.id.tv_comment_createat);
//            rlSubLayout = (RelativeLayout) itemView.findViewById(R.id.rl_comment_sub_layout);
//            tvSubUsername = (TextView) itemView.findViewById(R.id.tv_comment_subusername);
//            tvSubContent = (TextView) itemView.findViewById(R.id.tv_comment_subcontent);
//            tvContent = (TextView) itemView.findViewById(R.id.tv_comment_content);
            altv_comment_content = (AutoLinkTextView) itemView.findViewById(R.id.altv_comment_content);
            altv_comment_sub_username = (AutoLinkTextView) itemView.findViewById(R.id.altv_comment_sub_username);
            altv_comment_sub_content = (AutoLinkTextView) itemView.findViewById(R.id.altv_comment_sub_content);

            ivReply = (ImageView) itemView.findViewById(R.id.iv_comment_reply);
            ivLike = (ImageView) itemView.findViewById(R.id.iv_comment_like);

            altv_comment_content.addAutoLinkMode(
                    AutoLinkMode.MODE_HASHTAG,
                    AutoLinkMode.MODE_PHONE,
                    AutoLinkMode.MODE_URL,
                    AutoLinkMode.MODE_EMAIL,
                    AutoLinkMode.MODE_MENTION,
                    AutoLinkMode.MODE_CUSTOM);
            altv_comment_content.setHashtagModeColor(ContextCompat.getColor(mContext, R.color.colorTextHashTag));
            altv_comment_content.setPhoneModeColor(ContextCompat.getColor(mContext, R.color.colorTextPhone));
            altv_comment_content.setCustomModeColor(ContextCompat.getColor(mContext, R.color.colorTextCustom));
            altv_comment_content.setMentionModeColor(ContextCompat.getColor(mContext, R.color.colorTextMention));
            altv_comment_content.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
                @Override
                public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                    if (onAutoLinkTextViewClickListener != null) {
                        onAutoLinkTextViewClickListener.onAutoLinkTextViewClick(autoLinkMode, matchedText);
                    }
                }
            });

            altv_comment_sub_username.addAutoLinkMode(
                    AutoLinkMode.MODE_HASHTAG,
                    AutoLinkMode.MODE_PHONE,
                    AutoLinkMode.MODE_URL,
                    AutoLinkMode.MODE_EMAIL,
                    AutoLinkMode.MODE_MENTION,
                    AutoLinkMode.MODE_CUSTOM);
            altv_comment_sub_username.setHashtagModeColor(ContextCompat.getColor(mContext, R.color.colorTextHashTag));
            altv_comment_sub_username.setPhoneModeColor(ContextCompat.getColor(mContext, R.color.colorTextPhone));
            altv_comment_sub_username.setCustomModeColor(ContextCompat.getColor(mContext, R.color.colorTextCustom));
            altv_comment_sub_username.setMentionModeColor(ContextCompat.getColor(mContext, R.color.colorTextMention));
            altv_comment_sub_username.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
                @Override
                public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                    if (onAutoLinkTextViewClickListener != null) {
                        onAutoLinkTextViewClickListener.onAutoLinkTextViewClick(autoLinkMode, matchedText);
                    }
                }
            });

            altv_comment_sub_content.addAutoLinkMode(
                    AutoLinkMode.MODE_HASHTAG,
                    AutoLinkMode.MODE_PHONE,
                    AutoLinkMode.MODE_URL,
                    AutoLinkMode.MODE_EMAIL,
                    AutoLinkMode.MODE_MENTION,
                    AutoLinkMode.MODE_CUSTOM);
            altv_comment_sub_content.setHashtagModeColor(ContextCompat.getColor(mContext, R.color.colorTextHashTag));
            altv_comment_sub_content.setPhoneModeColor(ContextCompat.getColor(mContext, R.color.colorTextPhone));
            altv_comment_sub_content.setCustomModeColor(ContextCompat.getColor(mContext, R.color.colorTextCustom));
            altv_comment_sub_content.setMentionModeColor(ContextCompat.getColor(mContext, R.color.colorTextMention));
            altv_comment_sub_content.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
                @Override
                public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                    if (onAutoLinkTextViewClickListener != null) {
                        onAutoLinkTextViewClickListener.onAutoLinkTextViewClick(autoLinkMode, matchedText);
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCommentItemClickListener != null) {
                        onCommentItemClickListener.onCommentItemClick(getPosition());
                    }
                }
            });
        }
    }

    /**
     * Comment Flag
     */
    public class GroupItemHolder extends NormalItemHolder {
        TextView tvFlag;

        public GroupItemHolder(View itemView) {
            super(itemView);
            tvFlag = (TextView) itemView.findViewById(R.id.tv_comment_flag);
        }
    }
}
