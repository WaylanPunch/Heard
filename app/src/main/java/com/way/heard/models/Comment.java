package com.way.heard.models;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;

import java.util.List;

/**
 * Created by pc on 2016/4/23.
 */
@AVClassName("Comment")
public class Comment extends AVObject {
    public static final Creator CREATOR = AVObjectCreator.instance;
    public static final String POSTOBJECTID = "postobjectid";
    public static final String CONTENT = "content";
    public static final String AUTHOR = "author";
    public static final String REPLYTO = "replyto";
    public static final String LIKES = "likes";
    public static final String REPLYFOR = "replyfor";

    public String getPostObjectID() {
        return getString(POSTOBJECTID);
    }

    public void setPostObjectID(String content) {
        put(POSTOBJECTID, content);
    }


    public String getContent() {
        return getString(CONTENT);
    }

    public void setContent(String content) {
        put(CONTENT, content);
    }

    public AVUser getAuthor() {
        return getAVUser(AUTHOR);
    }

    public void setAuthor(AVUser author) {
        put(AUTHOR, author);
    }

    public AVUser getReplyTo() {
        return getAVUser(REPLYTO);
    }

    public void setReplyTo(AVUser author) {
        put(REPLYTO, author);
    }

    public Comment getReplyFor() {
        return getAVObject(REPLYFOR);
    }

    public void setReplyFor(Comment comment) {
        put(REPLYFOR, comment);
    }

    public List<String> getLikes() {
        return getList(LIKES);
    }

    public void setLikes(List<String> likeObjectIDs) {
        put(LIKES, likeObjectIDs);
    }


}
