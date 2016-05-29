package com.avoscloud.leanchatlib.utils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avoscloud.leanchatlib.controller.ChatManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wli on 15/10/8.
 * TODO：稍后需要把几个 cache 相关的合并到一个里边
 */
public class AVIMConversationCacheUtils {

  private static Map<String, AVIMConversation> conversationMap;

  static {
    conversationMap = new HashMap<String, AVIMConversation>();
  }

  public static AVIMConversation getCacheConversation(String conversationId) {
    return conversationMap.get(conversationId);
  }

  public static void findConversationsByConversationIds(List<String> ids, AVIMConversationQueryCallback callback) {
    AVIMConversationQuery conversationQuery = ChatManager.getInstance().getConversationQuery();
    if (ids.size() > 0 && null != conversationQuery) {
      conversationQuery.whereContainsIn(Constants.OBJECT_ID, ids);
      conversationQuery.setLimit(1000);
      conversationQuery.findInBackground(callback);
    } else if (null != callback) {
      callback.done(new ArrayList<AVIMConversation>(), null);
    }
  }

  public static void cacheConversations(List<String> ids, final CacheConversationCallback callback) {
    List<String> uncachedIds = new ArrayList<String>();
    for (String id : ids) {
      if (!conversationMap.containsKey(id)) {
        uncachedIds.add(id);
      }
    }

    if (uncachedIds.isEmpty()) {
      if (null != callback) {
        callback.done(null);
        return;
      }
    }

    findConversationsByConversationIds(uncachedIds, new AVIMConversationQueryCallback() {
      @Override
      public void done(List<AVIMConversation> list, AVIMException e) {
        if (null == e) {
          for (AVIMConversation conversation : list) {
            conversationMap.put(conversation.getConversationId(), conversation);
          }
        }
        callback.done(e);
      }
    });
  }

  public static abstract class CacheConversationCallback {
    public abstract void done(AVException e);
  }
}
