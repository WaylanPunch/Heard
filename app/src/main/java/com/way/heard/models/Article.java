package com.way.heard.models;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;

import java.util.List;

/**
 * Created by pc on 2016/4/23.
 */
@AVClassName("Article")
public class Article extends AVObject {
    public static final Creator CREATOR = AVObjectCreator.instance;
    public static final String TITLE = "title";
    public static final String TAGS = "tags";
    public static final String PHOTOS = "photos";
    public static final String CONTENT = "content";
    public static final String AUTHOR = "author";
    public static final String TYPE = "type";
    public static final String LIKES = "likes";
    public static final String COMMENTS = "comments";

    public String getTitle() {
        return getString(TITLE);
    }

    public void setTitle(String title) {
        put(TITLE, title);
    }

    public List<String> getTags() {
        return getList(TAGS);
    }

    public void setTags(List<String> tags) {
        put(TAGS, tags);
    }

    public List<AVFile> getPhotos() {
        return getList(PHOTOS);
    }

    public void setPhotos(List<AVFile> photos) {
        put(PHOTOS, photos);
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

    public int getType() {
        return getInt(TYPE);
    }

    public void setType(int type) {
        put(TYPE, type);
    }

    public AVRelation<AVUser> getLikes() {
        return getRelation(LIKES);
    }

    public void setLikes(AVRelation<AVUser> likes) {
        put(LIKES, likes);
    }

    public AVRelation<Comment> getComments() {
        return getRelation(COMMENTS);
    }

    public void setComments(AVRelation<Comment> comments) {
        put(COMMENTS, comments);
    }
}
