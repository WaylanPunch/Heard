package com.way.heard.models;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;

import java.util.List;

/**
 * Created by pc on 2016/4/23.
 */
@AVClassName("Post")
public class Post extends AVObject {
    public static final Creator CREATOR = AVObjectCreator.instance;
    public static final String TAG = "tag";
    public static final String PHOTOS = "photos";
    public static final String CONTENT = "content";
    public static final String AUTHOR = "author";
    public static final String TYPE = "type";
    public static final String LIKES = "likes";
    public static final String COMMENTS = "comments";


    public String getTag() {
        return getString(TAG);
    }

    public void setTag(String tag) {
        put(TAG, tag);
    }

    public AVRelation<Image> getPhotos() {
        return getRelation(PHOTOS);
    }

    public void setPhotos(AVRelation<Image> photos) {
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

    /*************************/
    private List<Image> images;
    public void trySetPhotoList(List<Image> images){
        this.images = images;
    }
    public List<Image> tryGetPhotoList(){
        return this.images;
    }
}
