package com.way.heard.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.way.heard.R;

import cn.leancloud.chatkit.LCChatKitUser;
import cn.leancloud.chatkit.activity.LCIMConversationActivity;
import cn.leancloud.chatkit.utils.LCIMConstants;
import cn.leancloud.chatkit.viewholder.LCIMCommonViewHolder;

/**
 * Created by pc on 2016/6/10.
 */
public class ContactItemHolder extends LCIMCommonViewHolder<LCChatKitUser> {

    TextView nameView;
    ImageView avatarView;

    public LCChatKitUser lcChatKitUser;

    public ContactItemHolder(Context context, ViewGroup root) {
        super(context, root, R.layout.common_user_item);
        initView();
    }

    public void initView() {
        nameView = (TextView) itemView.findViewById(R.id.tv_friend_name);
        avatarView = (ImageView) itemView.findViewById(R.id.img_friend_avatar);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LCIMConversationActivity.class);
                intent.putExtra(LCIMConstants.PEER_ID, lcChatKitUser.getUserId());
                getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void bindData(LCChatKitUser lcChatKitUser) {
        this.lcChatKitUser = lcChatKitUser;
        Picasso.with(getContext()).load(lcChatKitUser.getAvatarUrl()).placeholder(R.drawable.lcim_default_avatar_icon).error(R.drawable.lcim_default_avatar_icon).into(avatarView);
        nameView.setText(lcChatKitUser.getUserName());
    }

    public static ViewHolderCreator HOLDER_CREATOR = new ViewHolderCreator<ContactItemHolder>() {
        @Override
        public ContactItemHolder createByViewGroupAndType(ViewGroup parent, int viewType) {
            return new ContactItemHolder(parent.getContext(), parent);
        }
    };
}

