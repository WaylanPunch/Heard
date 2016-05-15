package com.way.heard.services;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FollowCallback;

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



    public interface LeanCloudUserServiceListener{
        void onSuccess();
        void onErrorMatter(String msg);
        void onErrorNoMatter(String msg);
    }
}
