package com.way.heard.models;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;

import java.util.List;

/**
 * Created by pc on 2016/4/23.
 */
@AVClassName("Image")
public class Image extends AVObject {
    public static final Creator CREATOR = AVObjectCreator.instance;
    public static final String URL = "url";
    public static final String THUMBNAILURL = "thumbnailurl";
    public static final String AUTHOR = "author";
    public static final String TYPE = "type";
    public static final String FROM = "from";
    public static final String LIKES = "likes";
    public static final String TAGS = "tags";

    public String getUrl() {
        return getString(URL);
    }

    public void setUrl(String url) {
        put(URL, url);
    }

    public String getThumbnailurl() {
        return getString(THUMBNAILURL);
    }

    public void setThumbnailurl(String thumbnailurl) {
        put(THUMBNAILURL, thumbnailurl);
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

    public int getFrom() {
        return getInt(FROM);
    }

    public void setFrom(int from) {
        put(FROM, from);
    }

    public List<String> getLikes() {
        return getList(LIKES);
    }

    public void setLikes(List<String> likeObjectIDs) {
        put(LIKES, likeObjectIDs);
    }

    public List<String> getTags() {
        return getList(TAGS);
    }

    public void setTags(List<String> tags) {
        put(TAGS, tags);
    }
}
