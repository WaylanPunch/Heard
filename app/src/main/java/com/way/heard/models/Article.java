package com.way.heard.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by pc on 2016/4/18.
 */
public class Article implements Parcelable {

    private String id;
    private String title;
    private List<String> tags;
    private List<ArticlePhoto> photos;
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<ArticlePhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<ArticlePhoto> photos) {
        this.photos = photos;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeStringList(this.tags);
        dest.writeTypedList(photos);
        dest.writeString(this.content);
    }

    public Article() {
    }

    protected Article(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.tags = in.createStringArrayList();
        this.photos = in.createTypedArrayList(ArticlePhoto.CREATOR);
        this.content = in.readString();
    }

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
