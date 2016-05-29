package com.avoscloud.leanchatlib.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avoscloud.leanchatlib.R;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.event.ImTypeMessageResendEvent;
import com.avoscloud.leanchatlib.event.LeftChatItemClickEvent;
import com.avoscloud.leanchatlib.utils.PhotoUtils;
import com.avoscloud.leanchatlib.utils.ThirdPartUserUtils;
import com.avoscloud.leanchatlib.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import de.greenrobot.event.EventBus;

/**
 * Created by wli on 15/9/17.
 */
public class ChatItemHolder extends CommonViewHolder {

  protected boolean isLeft;

  protected AVIMMessage message;
  protected ImageView avatarView;
  protected TextView timeView;
  protected TextView nameView;
  protected LinearLayout conventLayout;
  protected FrameLayout statusLayout;
  protected ProgressBar progressBar;
  protected TextView statusView;
  protected ImageView errorView;

  public ChatItemHolder(Context context, ViewGroup root, boolean isLeft) {
    super(context, root, isLeft ? R.layout.chat_item_left_layout : R.layout.chat_item_right_layout);
    this.isLeft = isLeft;
    initView();
  }

  public void initView() {
    if (isLeft) {
      avatarView = (ImageView)itemView.findViewById(R.id.chat_left_iv_avatar);
      timeView = (TextView)itemView.findViewById(R.id.chat_left_tv_time);
      nameView = (TextView)itemView.findViewById(R.id.chat_left_tv_name);
      conventLayout = (LinearLayout)itemView.findViewById(R.id.chat_left_layout_content);
      statusLayout = (FrameLayout)itemView.findViewById(R.id.chat_left_layout_status);
      statusView = (TextView)itemView.findViewById(R.id.chat_left_tv_status);
      progressBar = (ProgressBar)itemView.findViewById(R.id.chat_left_progressbar);
      errorView = (ImageView)itemView.findViewById(R.id.chat_left_tv_error);
    } else {
      avatarView = (ImageView)itemView.findViewById(R.id.chat_right_iv_avatar);
      timeView = (TextView)itemView.findViewById(R.id.chat_right_tv_time);
      nameView = (TextView)itemView.findViewById(R.id.chat_right_tv_name);
      conventLayout = (LinearLayout)itemView.findViewById(R.id.chat_right_layout_content);
      statusLayout = (FrameLayout)itemView.findViewById(R.id.chat_right_layout_status);
      progressBar = (ProgressBar)itemView.findViewById(R.id.chat_right_progressbar);
      errorView = (ImageView)itemView.findViewById(R.id.chat_right_tv_error);
      statusView = (TextView)itemView.findViewById(R.id.chat_right_tv_status);
    }
  }

  @Override
  public void bindData(Object o) {
    message = (AVIMMessage)o;
    timeView.setText(Utils.millisecsToDateString(message.getTimestamp()));

    String userId = message.getFrom();
    nameView.setText(ThirdPartUserUtils.getInstance().getUserName(userId));
    ImageLoader.getInstance().displayImage(ThirdPartUserUtils.getInstance().getUserAvatar(userId), avatarView, PhotoUtils.avatarImageOptions);

    switch (message.getMessageStatus()) {
      case AVIMMessageStatusFailed:
        statusLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        statusView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        break;
      case AVIMMessageStatusSent:
        statusLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        statusView.setVisibility(View.VISIBLE);
        statusView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        break;
      case AVIMMessageStatusSending:
        statusLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        statusView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        break;
      case AVIMMessageStatusNone:
      case AVIMMessageStatusReceipt:
        statusLayout.setVisibility(View.GONE);
        break;
    }

    ChatManager.getInstance().getRoomsTable().clearUnread(message.getConversationId());
  }

  public void onErrorClick(View view) {
    ImTypeMessageResendEvent event = new ImTypeMessageResendEvent();
    event.message = message;
    EventBus.getDefault().post(event);
  }

  public void onNameClick(View view) {
    LeftChatItemClickEvent clickEvent = new LeftChatItemClickEvent();
    clickEvent.userId = nameView.getText().toString();
    EventBus.getDefault().post(clickEvent);
  }

  public void showTimeView(boolean isShow) {
    timeView.setVisibility(isShow ? View.VISIBLE : View.GONE);
  }

  public void showUserName(boolean isShow) {
    nameView.setVisibility(isShow ? View.VISIBLE : View.GONE);
  }
}

