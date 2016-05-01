package com.way.heard.models;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;

/**
 * Created by pc on 2016/4/23.
 */
@AVClassName("Image")
public class Image extends AVObject {
    public static final Creator CREATOR = AVObjectCreator.instance;
    public static final String URL = "url";
    public static final String THUMBNAILURL = "thumbnailurl";
    public static final String AUTHOR = "author";

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


}
