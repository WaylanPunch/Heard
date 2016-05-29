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
    public static final String TAGS = "tags";
    public static final String PHOTOS = "photos";
    public static final String CONTENT = "content";
    public static final String AUTHOR = "author";
    public static final String TYPE = "type";
    public static final String LIKES = "likes";
    public static final String COMMENTS = "comments";
    public static final String REPLYTO = "replyto";
    public static final String REPLYFOR = "replyfor";
    public static final String REPLYORIGINAL = "replyoriginal";
    public static final String FROM = "from";

    public List<String> getTags() {
        return getList(TAGS);
    }

    public void setTags(List<String> tags) {
        put(TAGS, tags);
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

    public AVUser getReplyTo() {
        return getAVUser(REPLYTO);
    }

    public void setReplyTo(AVUser author) {
        put(REPLYTO, author);
    }

    public Post getReplyFor() {
        return getAVObject(REPLYFOR);
    }

    public void setReplyFor(Post post) {
        put(REPLYFOR, post);
    }

    public Post getReplyOriginal() {
        return getAVObject(REPLYORIGINAL);
    }

    public void setReplyOriginal(Post post) {
        put(REPLYORIGINAL, post);
    }

    public int getFrom() {
        return getInt(FROM);
    }

    public void setFrom(int from) {
        put(FROM, from);
    }

    /*************************/
    private List<Image> images;

    public void trySetPhotoList(List<Image> images) {
        this.images = images;
    }

    public List<Image> tryGetPhotoList() {
        return this.images;
    }

    private Post postOriginal;

    public Post tryGetPostOriginal() {
        return postOriginal;
    }

    public void trySetPostOriginal(Post postOriginal) {
        this.postOriginal = postOriginal;
    }
}
