package com.avoscloud.leanchatlib.event;

/**
 * Created by wli on 15/10/9.
 * 因为 RecyclerView 没有 onItemClickListener，所以添加此事件
 */
public class ConversationItemClickEvent {
  public String conversationId;
  public ConversationItemClickEvent(String conversationId) {
    this.conversationId = conversationId;
  }
}
