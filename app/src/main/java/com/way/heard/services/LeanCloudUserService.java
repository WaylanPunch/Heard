package com.way.heard.services;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FollowCallback;

import java.io.ByteArrayOutputStream;

/**
 * Created by pc on 2016/4/26.
 */
public class LeanCloudUserService {
    private final static String TAG = LeanCloudUserService.class.getName();

    private LeanCloudUserService() {
        /** cannot be instantiated **/
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    public static void followUser(AVUser currentUser, String userObjectID, final LeanCloudUserServiceListener leanCloudUserServiceListener) {
        //关注
        currentUser.followInBackground(userObjectID, new FollowCallback() {
            @Override
            public void done(AVObject object, AVException e) {
                if (e == null) {
                    leanCloudUserServiceListener.onSuccess();
                } else if (e.getCode() == AVException.DUPLICATE_VALUE) {
                    leanCloudUserServiceListener.onErrorNoMatter(e.getMessage());
                } else {
                    leanCloudUserServiceListener.onErrorMatter(e.getMessage());
                }
            }
        });
    }

    public static void unfollowUser(AVUser currentUser, String userObjectID, final LeanCloudUserServiceListener leanCloudUserServiceListener) {
        //关注
        currentUser.unfollowInBackground(userObjectID, new FollowCallback() {
            @Override
            public void done(AVObject object, AVException e) {
                if (e == null) {
                    leanCloudUserServiceListener.onSuccess();

                } else {
                    leanCloudUserServiceListener.onErrorMatter(e.getMessage());

                }
            }
        });
    }

    public static boolean saveUserProfile(Bitmap avatar, String displayname, String email, String gender, String city, String signature) {
        boolean isOK = false;
        try {
            AVUser currentUser = AVUser.getCurrentUser();
            boolean isNeeded = false;

            if (avatar != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                avatar.compress(Bitmap.CompressFormat.JPEG, 80, out);
                byte[] data = out.toByteArray();
                AVFile avfile = new AVFile(currentUser.getUsername() + "-avatar.jpg", data);
                avfile.save();
                String thumbnailurl = avfile.getThumbnailUrl(true, 100, 100);
                currentUser.put("avatar", thumbnailurl);
                isNeeded = true;
            }
            if (!TextUtils.isEmpty(displayname)) {
                currentUser.put("displayname", displayname);
                isNeeded = true;
            }
            if (!TextUtils.isEmpty(email)) {
                currentUser.setEmail(email);
                isNeeded = true;
            }
            if (!TextUtils.isEmpty(gender)) {
                currentUser.put("gender", gender);
                isNeeded = true;
            }
            if (!TextUtils.isEmpty(city)) {
                currentUser.put("city", city);
                isNeeded = true;
            }
            if (!TextUtils.isEmpty(signature)) {
                currentUser.put("signature", signature);
                isNeeded = true;
            }
            if (isNeeded) {
                currentUser.setFetchWhenSave(true);
                currentUser.save();
            }
            isOK = true;
        } catch (AVException e) {
            isOK = false;
        }
        return isOK;
    }

    public interface LeanCloudUserServiceListener {
        void onSuccess();

        void onErrorMatter(String msg);

        void onErrorNoMatter(String msg);
    }
}
