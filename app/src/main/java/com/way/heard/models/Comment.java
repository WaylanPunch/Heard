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
    public static final String CONTENT = "content";
    public static final String AUTHOR = "author";
    public static final String LIKES = "likes";
    public static final String COMMENTS = "comments";

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

    public List<String> getLikes() {
        return getList(LIKES);
    }

    public void setLikes(List<String> likeObjectIDs) {
        put(LIKES, likeObjectIDs);
    }

    public List<String> getComments() {
        return getList(COMMENTS);
    }

    public void setComments(List<String> commentObjectIDs) {
        put(COMMENTS, commentObjectIDs);
    }
}
