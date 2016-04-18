package com.way.heard.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pc on 2016/4/19.
 */
public class ArticlePhoto implements Parcelable {
    private String id;
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.url);
    }

    public ArticlePhoto() {
    }

    public ArticlePhoto(String id, String url) {
        this.id = id;
        this.url = url;
    }

    protected ArticlePhoto(Parcel in) {
        this.id = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<ArticlePhoto> CREATOR = new Parcelable.Creator<ArticlePhoto>() {
        public ArticlePhoto createFromParcel(Parcel source) {
            return new ArticlePhoto(source);
        }

        public ArticlePhoto[] newArray(int size) {
            return new ArticlePhoto[size];
        }
    };
}
