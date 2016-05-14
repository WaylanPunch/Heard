package com.way.heard.models;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

/**
 * Created by pc on 2016/5/11.
 */
@AVClassName("Tag")
public class Tag extends AVObject {
    public static final Creator CREATOR = AVObjectCreator.instance;
    public static final String CONTENT = "content";
    public static final String USAGE = "usage";

    public String getContent() {
        return getString(CONTENT);
    }

    public void setContent(String content) {
        put(CONTENT, content);
    }

    public int getUsage() {
        return getInt(USAGE);
    }

    public void setUsage(int usage) {
        put(USAGE, usage);
    }
}
