package com.avoscloud.leanchatlib.utils;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.model.ConversationType;
import com.avoscloud.leanchatlib.model.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lzw on 15/2/11.
 */
public class ConversationManager {
  private static ConversationManager conversationManager;

  public ConversationManager() {
  }

  public static synchronized ConversationManager getInstance() {
    if (conversationManager == null) {
      conversationManager = new ConversationManager();
    }
    return conversationManager;
  }

  public void findAndCacheRooms(final Room.MultiRoomsCallback callback) {
    final List<Room> rooms = ChatManager.getInstance().findRecentRooms();
    List<String> conversationIds = new ArrayList<>();
    for (Room room : rooms) {
      conversationIds.add(room.getConversationId());
    }

    if (conversationIds.size() > 0) {
      AVIMConversationCacheUtils.cacheConversations(conversationIds, new AVIMConversationCacheUtils.CacheConversationCallback() {
        @Override
        public void done(AVException e) {
          if (e != null) {
            callback.done(rooms, e);
          } else {
            callback.done(rooms, null);
          }
        }
      });
    } else {
      callback.done(rooms, null);
    }
  }

  public void updateName(final AVIMConversation conv, String newName, final AVIMConversationCallback callback) {
    conv.setName(newName);
    conv.updateInfoInBackground(new AVIMConversationCallback() {
      @Override
      public void done(AVIMException e) {
        if (e != null) {
          if (callback != null) {
            callback.done(e);
          }
        } else {
          if (callback != null) {
            callback.done(null);
          }
        }
      }
    });
  }

  public void findGroupConversationsIncludeMe(AVIMConversationQueryCallback callback) {
    AVIMConversationQuery conversationQuery = ChatManager.getInstance().getConversationQuery();
    if (null != conversationQuery) {
      conversationQuery.containsMembers(Arrays.asList(ChatManager.getInstance().getSelfId()));
      conversationQuery.whereEqualTo(ConversationType.ATTR_TYPE_KEY, ConversationType.Group.getValue());
      conversationQuery.orderByDescending(Constants.UPDATED_AT);
      conversationQuery.limit(1000);
      conversationQuery.findInBackground(callback);
    } else if (null != callback) {
      callback.done(new ArrayList<AVIMConversation>(), null);
    }
  }

  public static Bitmap getConversationIcon(AVIMConversation conversation) {
    return ColoredBitmapProvider.getInstance().createColoredBitmapByHashString(conversation.getConversationId());
  }
}