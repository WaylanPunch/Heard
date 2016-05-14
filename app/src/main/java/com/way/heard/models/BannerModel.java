package com.way.heard.models;

import java.util.List;

/**
 * Created by pc on 2016/5/12.
 */
public class BannerModel {
    private Tag tag;
    private List<Image> images;

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}
