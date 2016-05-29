package com.avoscloud.leanchatlib.viewholder;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avoscloud.leanchatlib.R;
import com.avoscloud.leanchatlib.activity.ImageBrowserActivity;
import com.avoscloud.leanchatlib.utils.Constants;
import com.avoscloud.leanchatlib.utils.PathUtils;
import com.avoscloud.leanchatlib.utils.PhotoUtils;
import com.nostra13.universalimageloader.core.ImageLoader;


/**
 * Created by wli on 15/9/17.
 */
public class ChatItemImageHolder extends ChatItemHolder {

  protected ImageView contentView;

  public ChatItemImageHolder(Context context, ViewGroup root, boolean isLeft) {
    super(context, root, isLeft);
  }

  @Override
  public void initView() {
    super.initView();
    conventLayout.addView(View.inflate(getContext(), R.layout.chat_item_image_layout, null));
    contentView = (ImageView)itemView.findViewById(R.id.chat_item_image_view);
    if (isLeft) {
      contentView.setBackgroundResource(R.drawable.chat_left_qp);
    } else {
      contentView.setBackgroundResource(R.drawable.chat_right_qp);
    }

    contentView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          Intent intent = new Intent(getContext(), ImageBrowserActivity.class);
          intent.putExtra(Constants.IMAGE_LOCAL_PATH, PathUtils.getChatFilePath(getContext(), message.getMessageId()));
          intent.putExtra(Constants.IMAGE_URL, ((AVIMImageMessage)message).getFileUrl());
          getContext().startActivity(intent);
      }
    });
  }

  @Override
  public void bindData(Object o) {
    super.bindData(o);
    contentView.setImageResource(0);
    AVIMMessage message = (AVIMMessage)o;
    if (message instanceof AVIMImageMessage) {
      AVIMImageMessage imageMsg = (AVIMImageMessage) message;
      String localFilePath = imageMsg.getLocalFilePath();
      if (!TextUtils.isEmpty(localFilePath)) {
        ImageLoader.getInstance().displayImage("file://" + localFilePath, contentView);
      } else {
        PhotoUtils.displayImageCacheElseNetwork(contentView, PathUtils.getChatFilePath(getContext(), imageMsg.getMessageId()),
          imageMsg.getFileUrl());
      }
    }
  }
}