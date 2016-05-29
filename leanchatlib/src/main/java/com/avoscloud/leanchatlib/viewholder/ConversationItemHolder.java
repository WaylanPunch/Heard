package com.avoscloud.leanchatlib.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMSingleMessageQueryCallback;
import com.avoscloud.leanchatlib.R;
import com.avoscloud.leanchatlib.controller.ConversationHelper;
import com.avoscloud.leanchatlib.event.ConversationItemClickEvent;
import com.avoscloud.leanchatlib.model.ConversationType;
import com.avoscloud.leanchatlib.model.Room;
import com.avoscloud.leanchatlib.utils.ConversationManager;
import com.avoscloud.leanchatlib.utils.PhotoUtils;
import com.avoscloud.leanchatlib.utils.ThirdPartUserUtils;
import com.avoscloud.leanchatlib.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.greenrobot.event.EventBus;

/**
 * Created by wli on 15/10/8.
 */
public class ConversationItemHolder extends CommonViewHolder {

  ImageView avatarView;
  TextView unreadView;
  TextView messageView;
  TextView timeView;
  TextView nameView;
  RelativeLayout avatarLayout;
  LinearLayout contentLayout;

  public ConversationItemHolder(ViewGroup root) {
    super(root.getContext(), root, R.layout.conversation_item);
    initView();
  }

  public void initView() {
    avatarView = (ImageView)itemView.findViewById(R.id.conversation_item_iv_avatar);
    nameView = (TextView)itemView.findViewById(R.id.conversation_item_tv_name);
    timeView = (TextView)itemView.findViewById(R.id.conversation_item_tv_time);
    unreadView = (TextView)itemView.findViewById(R.id.conversation_item_tv_unread);
    messageView = (TextView)itemView.findViewById(R.id.conversation_item_tv_message);
    avatarLayout = (RelativeLayout)itemView.findViewById(R.id.conversation_item_layout_avatar);
    contentLayout = (LinearLayout)itemView.findViewById(R.id.conversation_item_layout_content);
  }

  @Override
  public void bindData(Object o) {
    final Room room = (Room) o;
    AVIMConversation conversation = room.getConversation();
    if (null != conversation) {
      if (ConversationHelper.typeOfConversation(conversation) == ConversationType.Single) {
        String userId = ConversationHelper.otherIdOfConversation(conversation);
        String avatar = ThirdPartUserUtils.getInstance().getUserAvatar(userId);
        ImageLoader.getInstance().displayImage(avatar, avatarView, PhotoUtils.avatarImageOptions);
      } else {
        avatarView.setImageBitmap(ConversationManager.getConversationIcon(conversation));
      }
      nameView.setText(ConversationHelper.nameOfConversation(conversation));

      int num = room.getUnreadCount();
      unreadView.setText(num + "");
      unreadView.setVisibility(num > 0 ? View.VISIBLE : View.GONE);

      conversation.getLastMessage(new AVIMSingleMessageQueryCallback() {
        @Override
        public void done(AVIMMessage avimMessage, AVIMException e) {
          if (null != avimMessage) {
            Date date = new Date(avimMessage.getTimestamp());
            SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
            timeView.setText(format.format(date));
            messageView.setText(Utils.getMessageeShorthand(getContext(), avimMessage));
          } else {
            timeView.setText("");
            messageView.setText("");
          }
        }
      });
      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          EventBus.getDefault().post(new ConversationItemClickEvent(room.getConversationId()));
        }
      });
    }
  }

  public static ViewHolderCreator HOLDER_CREATOR = new ViewHolderCreator<ConversationItemHolder>() {
    @Override
    public ConversationItemHolder createByViewGroupAndType(ViewGroup parent, int viewType) {
      return new ConversationItemHolder(parent);
    }
  };
}
