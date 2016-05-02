package com.way.heard.utils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.way.heard.models.Article;
import com.way.heard.models.Image;
import com.way.heard.models.Post;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2016/4/26.
 */
public class LeanCloudHelper {
    private final static String TAG = LeanCloudHelper.class.getName();

    private LeanCloudHelper() {
        /** cannot be instantiated **/
        throw new UnsupportedOperationException("cannot be instantiated");
    }



    // https://github.com/leancloud/leanchat-android
    public static boolean test1() {
        try {
            AVFile photoa = AVFile.withAbsoluteLocalPath("a.png", "/storage/emulated/0/Pictures/Screenshots/a.png");
            photoa.save();
            AVFile photob = AVFile.withAbsoluteLocalPath("b.png", "/storage/emulated/0/Pictures/Screenshots/b.png");
            photob.save();
            AVFile photoc = AVFile.withAbsoluteLocalPath("c.png", "/storage/emulated/0/Pictures/Screenshots/c.png");
            photoc.save();
            AVFile photod = AVFile.withAbsoluteLocalPath("d.png", "/storage/emulated/0/Pictures/Screenshots/d.png");
            photod.save();
        } catch (AVException e) {
            LogUtil.e(TAG, "test1 debug, Failed", e);
            return false;
        } catch (FileNotFoundException e) {
            LogUtil.e(TAG, "test1 debug, Failed", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean test2(List<String> filepaths){
        try{
            if(filepaths != null && filepaths.size() > 0){
                int photoindex = 0;
                for(String filepath : filepaths){
                    photoindex++;
                    AVFile photo = AVFile.withAbsoluteLocalPath("aaaaaa-" + photoindex, filepath);
                    photo.save();
                }
            }
        } catch(AVException e){
            LogUtil.e(TAG, "test2 debug, Failed", e);
            return false;
        } catch (FileNotFoundException e) {
            LogUtil.e(TAG, "test2 debug, Failed", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean publishArticle(String title, List<String> tags, List<String> filepaths, String content, AVUser currentUser){
        try{
            List<AVFile> photos = new ArrayList<>();
            if(filepaths != null && filepaths.size() > 0){
                int photoindex = 0;
                for(String filepath : filepaths){
                    photoindex++;
                    AVFile photo = AVFile.withAbsoluteLocalPath("aaaaaa-" + photoindex, filepath);
                    photo.save();
                    photos.add(photo);
                }
            }

            Article article  = new Article();
            article.setTitle(title);
            if(tags != null && tags.size() > 0) {
                article.setTags(tags);
            }
            article.setContent(content);
            article.setAuthor(currentUser);
            article.setPhotos(photos);

            article.save();
        } catch(AVException e){
            LogUtil.e(TAG, "publishArticle debug, Failed", e);
            return false;
        } catch (FileNotFoundException e) {
            LogUtil.e(TAG, "publishArticle debug, Failed", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static List<Article> getMyArticlesByPage(String userObjectId, boolean isMe, int page, int pagesize){
        List<Article> articles = new ArrayList<>();
        try{
            if(page > 0 && pagesize > 0){
                AVQuery<Article> query = AVQuery.getQuery("Article");
                query.whereEqualTo("author", userObjectId);
                if(!isMe){
                    query.whereEqualTo("type", 1);
                }
                query.skip((page - 1)*pagesize);
                query.limit(pagesize);
                query.orderByDescending("createdAt");
                articles = query.find();
            }
        } catch(AVException e){
            LogUtil.e(TAG, "getMyArticlesByPage debug, Failed", e);
            return null;
        }
        return articles;
    }

    public static List<Article> getAnyPublicArticlesByPage(int page, int pagesize){
        List<Article> articles = new ArrayList<>();
        try{
            if(page > 0 && pagesize > 0){
                AVQuery<Article> query = AVQuery.getQuery("Article");
                query.whereEqualTo("type", 1);// public article
                query.skip((page - 1)*pagesize);
                query.limit(pagesize);
                query.orderByDescending("createdAt");
                articles = query.find();
            }
        } catch(AVException e){
            LogUtil.e(TAG, "getAnyPublicArticlesByPage debug, Failed", e);
            return null;
        }
        return articles;
    }

    public static List<Article> getAllMyFolloweesArticlesByPage(String userObjectId, int page, int pagesize){
        List<Article> articles = new ArrayList<>();
        try{
            if(page > 0 && pagesize > 0){
                AVQuery<AVUser> queryFollowee = AVUser.followeeQuery(userObjectId, AVUser.class);
                List<AVUser> followees = queryFollowee.find();

                //AVQuery<AVUser> queryFollowee = AVQuery.getQuery("_Followee");
                //queryFollowee.whereEqualTo("user", currentUser);
                //List<AVUser> followees = queryFollowee.find();

                AVQuery<Article> queryArticle = AVQuery.getQuery("Article");
                queryArticle.whereContainedIn("author", followees);
                queryArticle.whereEqualTo("type", 1);
                queryArticle.skip((page - 1)*pagesize);
                queryArticle.limit(pagesize);
                queryArticle.orderByDescending("createdAt");
                articles = queryArticle.find();
            }
        } catch(AVException e){
            LogUtil.e(TAG, "getAllMyFolloweesArticlesByPage debug, Failed", e);
            return null;
        }
        return articles;
    }

    public static List<Article> getAnyPublicArticlesByTagsByPage(List<String> tags, int page, int pagesize){
        List<Article> articles = new ArrayList<>();
        try{
            if(page > 0 && pagesize > 0){
                AVQuery<Article> query = AVQuery.getQuery("Article");
                query.whereEqualTo("type", 1);
                query.whereContainedIn("tags", tags);
                query.skip((page - 1)*pagesize);
                query.limit(pagesize);
                query.orderByDescending("createdAt");
                articles = query.find();
            }
        } catch(AVException e){
            LogUtil.e(TAG, "getAnyPublicArticlesByTagsByPage debug, Failed", e);
            return null;
        }
        return articles;
    }

	/*
	public static List<AVUser> getMyFollowees(){
		List<AVUser> followees;
		try{
			AVQuery<AVUser> query = AVQuery.getQuery("_Followee");
			query.whereEqualTo("user", currentUser);
			query.limit(10);
			query.orderByDescending("createdAt");
			followees = q.find();
		} catch(AVException e){
			LogUtil.e(TAG, "getMyFollowees debug, Failed", e);
			return null;
		}
		return followees;
	}
	*/

    public static List<AVUser> getMyFolloweesByPage(String userObjectId, int page, int pagesize){
        List<AVUser> followees = new ArrayList<>();
        try{
            if(page > 0 && pagesize > 0){
                AVQuery<AVUser> query = AVUser.followeeQuery(userObjectId, AVUser.class);
                query.skip((page - 1)*pagesize);
                query.limit(pagesize);
                query.orderByDescending("createdAt");
                followees = query.find();
            }
        } catch(AVException e){
            LogUtil.e(TAG, "getMyFolloweesByPage debug, Failed", e);
            return null;
        }
        return followees;
    }

	/*
	public static List<AVUser> getMyFollowers(){
		List<AVUser> followers;
		try{
			AVQuery<AVUser> query = AVQuery.getQuery("_Follower");
			query.whereEqualTo("user", currentUser);
			query.limit(10);
			query.orderByDescending("createdAt");
			followers = q.find();
		} catch(AVException e){
			LogUtil.e(TAG, "getMyFollowers debug, Failed", e);
			return null;
		}
		return followers;
	}
	*/

    public static List<AVUser> getMyFollowersByPage(String userObjectId, int page, int pagesize){
        List<AVUser> followers = new ArrayList<>();
        try{
            if(page > 0 && pagesize > 0){
                AVQuery<AVUser> query = AVUser.followerQuery(userObjectId, AVUser.class);
                query.skip((page - 1)*pagesize);
                query.limit(pagesize);
                query.orderByDescending("createdAt");
                followers = query.find();
            }
        } catch(AVException e){
            LogUtil.e(TAG, "getMyFollowersByPage debug, Failed", e);
            return null;
        }
        return followers;
    }

    public static boolean loginWithUsername(String username, String password){
        try{
            AVUser.logIn(username, password);
        } catch(AVException e){
            LogUtil.e(TAG, "loginWithUsername debug, Failed", e);
            return false;
        }
        return true;
    }

    public static boolean signUpWithUsername(String username, String password){
        try{
            AVUser user = new AVUser();// 新建 AVUser 对象实例
            user.setUsername(username);// 设置用户名
            user.setPassword(password);// 设置密码
            //user.setEmail("tom@leancloud.cn");// 设置邮箱
            user.signUp();
        } catch(AVException e){
            LogUtil.e(TAG, "signUpWithUsername debug, Failed", e);
            return false;
        }
        return true;
    }

    public static void logout(){
        AVUser.logOut();
    }

    public static String getAvatarUrl(AVUser user, boolean isThumbUrl) {
        String url = "";
        AVFile avatar = user.getAVFile("avatar");
        if (avatar != null) {
            if(!isThumbUrl){
                url = avatar.getUrl();
            } else{
                url = avatar.getThumbnailUrl(true, 100, 100);
            }
        }
        return url;
    }

    public static boolean saveAvatar(AVUser user, String path) {
        try {
            AVFile file = AVFile.withAbsoluteLocalPath(user.getUsername(), path);
            user.put("avatar", file);
            file.save();
            user.save();
	      	/*
	      	file.saveInBackground(new SaveCallback() {
		        @Override
		        public void done(AVException e) {
		        	if (null == e) {
		            	user.saveInBackground(saveCallback);
		          	} else {
		            	if (null != saveCallback) {
		              	user.saveCallback.done(e);
		            }
		          }
		        }
	      	});
	      	*/
        } catch (AVException e) {
            LogUtil.e(TAG, "saveAvatar debug, Failed", e);
            return false;
        } catch (FileNotFoundException e) {
            LogUtil.e(TAG, "saveAvatar debug, Failed", e);
            e.printStackTrace();
        }
        return true;
    }

    public static List<Post> getAnyPublicPostsByPage(int skip, int limit){
        List<Post> posts = new ArrayList<>();
        try{
            if(limit > 0){
                AVQuery<Post> query = AVQuery.getQuery("Post");
                query.whereEqualTo("type", 1);// public article
                query.skip(skip);
                query.limit(limit);
                query.include(Post.AUTHOR);
                query.orderByDescending("createdAt");
                posts = query.find();

                for(Post post : posts) {
                    AVRelation<Image> photos = post.getPhotos();
                    AVQuery<Image> photoQuery = photos.getQuery();
                    List<Image> images = photoQuery.find();
                    post.trySetPhotoList(images);
                }
            }
        } catch(AVException e){
            LogUtil.e(TAG, "getAnyPublicPostsByPage debug, Failed", e);
            return null;
        }
        return posts;
    }
}
