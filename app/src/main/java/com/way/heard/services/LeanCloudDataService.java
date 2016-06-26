package com.way.heard.services;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVFriendship;
import com.avos.avoscloud.AVFriendshipQuery;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.way.heard.base.CONFIG;
import com.way.heard.models.Article;
import com.way.heard.models.BannerModel;
import com.way.heard.models.Comment;
import com.way.heard.models.Image;
import com.way.heard.models.Post;
import com.way.heard.models.Tag;
import com.way.heard.utils.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2016/4/26.
 */
public class LeanCloudDataService {
    private final static String TAG = LeanCloudDataService.class.getName();

    private LeanCloudDataService() {
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

    public static boolean test2(List<String> filepaths) {
        try {
            if (filepaths != null && filepaths.size() > 0) {
                int photoindex = 0;
                for (String filepath : filepaths) {
                    photoindex++;
                    AVFile photo = AVFile.withAbsoluteLocalPath("aaaaaa-" + photoindex, filepath);
                    photo.save();
                }
            }
        } catch (AVException e) {
            LogUtil.e(TAG, "test2 debug, Failed", e);
            return false;
        } catch (FileNotFoundException e) {
            LogUtil.e(TAG, "test2 debug, Failed", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean publishArticle(String title, List<String> tags, List<String> filepaths, String content, AVUser currentUser) {
        try {
            List<AVFile> photos = new ArrayList<>();
            if (filepaths != null && filepaths.size() > 0) {
                int photoindex = 0;
                for (String filepath : filepaths) {
                    photoindex++;
                    AVFile photo = AVFile.withAbsoluteLocalPath("aaaaaa-" + photoindex, filepath);
                    photo.save();
                    photos.add(photo);
                }
            }

            Article article = new Article();
            article.setTitle(title);
            if (tags != null && tags.size() > 0) {
                article.setTags(tags);
            }
            article.setContent(content);
            article.setAuthor(currentUser);
            article.setPhotos(photos);

            article.save();
        } catch (AVException e) {
            LogUtil.e(TAG, "publishArticle debug, Failed", e);
            return false;
        } catch (FileNotFoundException e) {
            LogUtil.e(TAG, "publishArticle debug, Failed", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static List<Article> getMyArticlesByPage(String userObjectId, boolean isMe, int page, int pagesize) {
        List<Article> articles = new ArrayList<>();
        try {
            if (page > 0 && pagesize > 0) {
                AVQuery<Article> query = AVQuery.getQuery("Article");
                query.whereEqualTo("author", userObjectId);
                if (!isMe) {
                    query.whereEqualTo("type", 1);
                }
                query.skip((page - 1) * pagesize);
                query.limit(pagesize);
                query.orderByDescending("createdAt");
                articles = query.find();
            }
        } catch (AVException e) {
            LogUtil.e(TAG, "getMyArticlesByPage debug, Failed", e);
            return null;
        }
        return articles;
    }

    public static List<Article> getAnyPublicArticlesByPage(int page, int pagesize) {
        List<Article> articles = new ArrayList<>();
        try {
            if (page > 0 && pagesize > 0) {
                AVQuery<Article> query = AVQuery.getQuery("Article");
                query.whereEqualTo("type", 1);// public article
                query.skip((page - 1) * pagesize);
                query.limit(pagesize);
                query.orderByDescending("createdAt");
                articles = query.find();
            }
        } catch (AVException e) {
            LogUtil.e(TAG, "getAnyPublicArticlesByPage debug, Failed", e);
            return null;
        }
        return articles;
    }

    public static List<Article> getAllMyFolloweesArticlesByPage(String userObjectId, int page, int pagesize) {
        List<Article> articles = new ArrayList<>();
        try {
            if (page > 0 && pagesize > 0) {
                AVQuery<AVUser> queryFollowee = AVUser.followeeQuery(userObjectId, AVUser.class);
                List<AVUser> followees = queryFollowee.find();

                //AVQuery<AVUser> queryFollowee = AVQuery.getQuery("_Followee");
                //queryFollowee.whereEqualTo("user", currentUser);
                //List<AVUser> followees = queryFollowee.find();

                AVQuery<Article> queryArticle = AVQuery.getQuery("Article");
                queryArticle.whereContainedIn("author", followees);
                queryArticle.whereEqualTo("type", 1);
                queryArticle.skip((page - 1) * pagesize);
                queryArticle.limit(pagesize);
                queryArticle.orderByDescending("createdAt");
                articles = queryArticle.find();
            }
        } catch (AVException e) {
            LogUtil.e(TAG, "getAllMyFolloweesArticlesByPage debug, Failed", e);
            return null;
        }
        return articles;
    }

    public static List<Article> getAnyPublicArticlesByTagsByPage(List<String> tags, int page, int pagesize) {
        List<Article> articles = new ArrayList<>();
        try {
            if (page > 0 && pagesize > 0) {
                AVQuery<Article> query = AVQuery.getQuery("Article");
                query.whereEqualTo("type", 1);
                query.whereContainedIn("tags", tags);
                query.skip((page - 1) * pagesize);
                query.limit(pagesize);
                query.orderByDescending("createdAt");
                articles = query.find();
            }
        } catch (AVException e) {
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

    public static List<AVUser> getMyFolloweesByPage(String userObjectId, int page, int pagesize) {
        List<AVUser> followees = new ArrayList<>();
        try {
            if (page > 0 && pagesize > 0) {
                AVQuery<AVUser> query = AVUser.followeeQuery(userObjectId, AVUser.class);
                query.skip((page - 1) * pagesize);
                query.limit(pagesize);
                query.orderByDescending("createdAt");
                followees = query.find();
            }
        } catch (AVException e) {
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

    public static List<AVUser> getMyFollowersByPage(String userObjectId, int page, int pagesize) {
        List<AVUser> followers = new ArrayList<>();
        try {
            if (page > 0 && pagesize > 0) {
                AVQuery<AVUser> query = AVUser.followerQuery(userObjectId, AVUser.class);
                query.skip((page - 1) * pagesize);
                query.limit(pagesize);
                query.orderByDescending("createdAt");
                followers = query.find();
            }
        } catch (AVException e) {
            LogUtil.e(TAG, "getMyFollowersByPage debug, Failed", e);
            return null;
        }
        return followers;
    }

    public static boolean loginWithUsername(String username, String password) {
        try {
            LogUtil.d(TAG, "loginWithUsername");
            AVUser.logIn(username, password);

            //AVInstallation
            AVInstallation.getCurrentInstallation().save();
            String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
            LogUtil.d(TAG, "loginWithUsername, InstallationId = " + installationId);
            if (!TextUtils.isEmpty(installationId)) {
                AVUser currentuser = AVUser.getCurrentUser();
                currentuser.put(CONFIG.AVUser_InstallationId, installationId);
                currentuser.save();
            }
        } catch (AVException e) {
            LogUtil.e(TAG, "loginWithUsername debug, Failed", e);
            return false;
        }
        return true;
    }

    public static boolean signUpWithUsername(String username, String password) {
        try {
            LogUtil.d(TAG, "signUpWithUsername");
            //AVInstallation
            AVInstallation.getCurrentInstallation().save();
            String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
            LogUtil.d(TAG, "loginWithUsername, InstallationId = " + installationId);
            if (!TextUtils.isEmpty(installationId)) {
                AVUser user = new AVUser();// 新建 AVUser 对象实例
                user.setUsername(username);// 设置用户名
                user.setPassword(password);// 设置密码
                //user.setEmail("tom@leancloud.cn");// 设置邮箱

                user.put(CONFIG.AVUser_InstallationId, installationId);
                user.signUp();
            }
        } catch (AVException e) {
            LogUtil.e(TAG, "signUpWithUsername debug, Failed", e);
            return false;
        }
        return true;
    }

    public static boolean updateEmail(String email) {
        try {
            LogUtil.d(TAG, "updateEmail");
            AVUser user = AVUser.getCurrentUser();
            if (null != user && !TextUtils.isEmpty(email)) {
                user.setEmail(email);// 设置邮箱
                user.save();
            }
        } catch (AVException e) {
            LogUtil.e(TAG, "updateEmail debug, Failed", e);
            return false;
        }
        return true;
    }

    public static void logout() {
        AVUser.logOut();
    }

    public static String getAvatarUrl(AVUser user, boolean isThumbUrl) {
        String url = "";
        AVFile avatar = user.getAVFile("avatar");
        if (avatar != null) {
            if (!isThumbUrl) {
                url = avatar.getUrl();
            } else {
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

    public static List<Post> getAnyPublicPostsByPage(int skip, int limit) {
        List<Post> posts = new ArrayList<>();
        try {
            if (limit > 0) {
                AVQuery<Post> query = AVQuery.getQuery("Post");
                query.whereEqualTo(Post.TYPE, 1);// public post
                query.skip(skip);
                query.limit(limit);
                query.include(Post.AUTHOR);
                query.include(Post.REPLYORIGINAL);
                query.orderByDescending("createdAt");
                posts = query.find();

                for (Post post : posts) {
                    if (0 == post.getFrom()) {//original post
                        AVRelation<Image> photos = post.getPhotos();
                        AVQuery<Image> photoQuery = photos.getQuery();
                        List<Image> images = photoQuery.find();
                        post.trySetPhotoList(images);
                    } else {//repost
                        AVQuery<Post> repostQuery = AVQuery.getQuery("Post");
                        repostQuery.include(Post.AUTHOR);
                        Post repost = repostQuery.get(post.getReplyOriginal().getObjectId());

                        AVRelation<Image> repostPhotos = repost.getPhotos();
                        AVQuery<Image> repostPhotoQuery = repostPhotos.getQuery();
                        List<Image> repostImages = repostPhotoQuery.find();
                        repost.trySetPhotoList(repostImages);

                        post.trySetPostOriginal(repost);
                    }
                }
            }
        } catch (AVException e) {
            LogUtil.e(TAG, "getAnyPublicPostsByPage debug, Failed", e);
            return null;
        }
        return posts;
    }

    public static List<Post> getAnyPublicPostsByUserByPage(String userObjectId, int skip, int limit) {
        List<Post> posts = new ArrayList<>();
        try {
            if (limit > 0) {
                AVQuery<Post> query = AVQuery.getQuery("Post");
                query.whereEqualTo(Post.TYPE, 1);// public post
                query.whereEqualTo(Post.AUTHOR, AVObject.createWithoutData("_User", userObjectId));
                query.skip(skip);
                query.limit(limit);
                query.include(Post.AUTHOR);
                query.include(Post.REPLYORIGINAL);
                query.orderByDescending("createdAt");
                posts = query.find();

                for (Post post : posts) {
                    if (0 == post.getFrom()) {//original post
                        AVRelation<Image> photos = post.getPhotos();
                        AVQuery<Image> photoQuery = photos.getQuery();
                        List<Image> images = photoQuery.find();
                        post.trySetPhotoList(images);
                    } else {//repost
                        AVQuery<Post> repostQuery = AVQuery.getQuery("Post");
                        repostQuery.include(Post.AUTHOR);
                        Post repost = repostQuery.get(post.getReplyOriginal().getObjectId());

                        AVRelation<Image> repostPhotos = repost.getPhotos();
                        AVQuery<Image> repostPhotoQuery = repostPhotos.getQuery();
                        List<Image> repostImages = repostPhotoQuery.find();
                        repost.trySetPhotoList(repostImages);

                        post.trySetPostOriginal(repost);
                    }
                }
            }
        } catch (AVException e) {
            LogUtil.e(TAG, "getAnyPublicPostsByUserByPage debug, Failed", e);
            return null;
        }
        return posts;
    }

    public static List<Post> getAllPostsByUserByPage(String userObjectId, int skip, int limit) {
        List<Post> posts = new ArrayList<>();
        try {
            if (limit > 0) {
                AVQuery<Post> query = AVQuery.getQuery("Post");
                query.whereEqualTo("author", AVObject.createWithoutData("_User", userObjectId));
                query.skip(skip);
                query.limit(limit);
                query.include(Post.AUTHOR);
                query.include(Post.REPLYORIGINAL);
                query.orderByDescending("createdAt");
                posts = query.find();

                for (Post post : posts) {
                    AVRelation<Image> photos = post.getPhotos();
                    AVQuery<Image> photoQuery = photos.getQuery();
                    List<Image> images = photoQuery.find();
                    post.trySetPhotoList(images);
                }
            }
        } catch (AVException e) {
            LogUtil.e(TAG, "getAnyPublicPostsByPage debug, Failed", e);
            return null;
        }
        return posts;
    }

    public static List<Comment> getAllCommentsByPostObjectID(String postObjectId, int skip, int limit) {
        List<Comment> comments = new ArrayList<>();
        try {
            if (limit > 0) {
                AVQuery<Comment> query = AVQuery.getQuery("Comment");
                query.whereEqualTo("postobjectid", postObjectId);
                query.skip(skip);
                query.limit(limit);
                query.include(Comment.AUTHOR);
                query.include(Comment.REPLYTO);
                query.include(Comment.REPLYFOR);
                query.orderByDescending("createdAt");
                comments = query.find();
                //comments.add(0, null);
            }
        } catch (AVException e) {
            LogUtil.e(TAG, "getAllCommentsByPostObjectID debug, Failed", e);
            return null;
        }
        return comments;
    }

    public static Post savePost(AVUser currentUser, Bitmap bitmap, boolean isPrivate, String content, List<String> tags) {
        Post post;
        try {
            post = new Post();

            String url = "";
            String thumbnailurl = "";
            boolean isFileSaved = false;
            if (bitmap != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                byte[] data = out.toByteArray();
                AVFile avfile = new AVFile(currentUser.getUsername() + "-post.jpg", data);
                avfile.save();
                url = avfile.getUrl();
                thumbnailurl = avfile.getThumbnailUrl(true, 100, 100);

                isFileSaved = true;
            }

            boolean isImageSaved = false;
            Image img = new Image();
            if (isFileSaved) {
                img.setAuthor(currentUser);
                img.setThumbnailurl(thumbnailurl);
                img.setUrl(url);
                if (isPrivate) {
                    img.setType(0);//private
                } else {
                    img.setType(1);//private
                }
                img.setFrom(1);//from post
                if (tags != null && tags.size() > 0) {
                    img.setTags(tags);
                }
                img.save();

                isImageSaved = true;
            }

            post.setAuthor(currentUser);
            post.setFrom(0);
            if (!TextUtils.isEmpty(content)) {
                post.setContent(content);
            }
            if (tags != null && tags.size() > 0) {
                post.setTags(tags);
            }
            if (isPrivate) {
                post.setType(0);//private
            } else {
                post.setType(1);//private
            }
            if (isImageSaved) {
                AVRelation<Image> photos = post.getPhotos();
                photos.add(img);
            }
            post.save();

            if (tags != null && tags.size() > 0) {
                for (String tag : tags) {
                    AVQuery<Tag> query = AVQuery.getQuery("Tag");
                    query.whereEqualTo(Tag.CONTENT, tag);
                    Tag tag1 = query.getFirst();
                    if (tag1 == null) {
                        tag1 = new Tag();
                        tag1.setContent(tag);
                        tag1.setUsage(1);
                        tag1.save();
                    } else {
                        int usage = tag1.getUsage();
                        tag1.setUsage(usage + 1);
                        tag1.save();
                    }
                }
            }
        } catch (AVException e) {
            return null;
        }
        return post;
    }

    public static Post saveRepost(AVUser currentUser, AVUser replyTo, Post replyFor, Post replOriginal, String newContent, boolean isPrivate) {
        Post newPost;
        try {
            newPost = new Post();
            newPost.setAuthor(currentUser);
            newPost.setFrom(1);
            if (!TextUtils.isEmpty(newContent)) {
                newPost.setContent(newContent);
            }
            if (isPrivate) {
                newPost.setType(0);//private
            } else {
                newPost.setType(1);//public
            }
            if (replyTo != null) {
                newPost.setReplyTo(replyTo);
            }
            if (replyFor != null) {
                newPost.setReplyFor(replyFor);
            }
            if (replOriginal != null) {
                newPost.setReplyOriginal(replOriginal);
            }
            newPost.save();
        } catch (AVException e) {
            return null;
        }
        return newPost;
    }

    public static Comment saveComment(String postID, String content, AVUser replyTo, Comment replyFor) {
        Comment comment;
        try {
            comment = new Comment();
            comment.setContent(content);
            comment.setAuthor(AVUser.getCurrentUser());
            comment.setPostObjectID(postID);
            if (replyTo != null) {
                comment.setReplyTo(replyTo);
            }
            if (replyFor != null) {
                comment.setReplyFor(replyFor);
            }
            comment.save();
        } catch (AVException e) {
            LogUtil.e(TAG, "saveComment debug, Failed", e);
            comment = null;
        }
        return comment;
    }

    public static List<Image> getAnyPublicImageByPage(int skip, int limit) {
        List<Image> images = new ArrayList<>();
        try {
            if (limit > 0) {
                LogUtil.e(TAG, "getAnyPublicImageByPage debug, Skip = " + skip);
                LogUtil.e(TAG, "getAnyPublicImageByPage debug, Limit = " + limit);
                AVQuery<Image> query = AVQuery.getQuery("Image");
                query.whereExists(Image.TYPE);
                query.whereEqualTo(Image.TYPE, 1);//public
                query.whereExists(Image.FROM);
                query.whereEqualTo(Image.FROM, 1);//from post

                query.skip(skip);
                query.limit(limit);
                query.include(Image.AUTHOR);
                query.orderByDescending("createdAt");
                images = query.find();
                LogUtil.e(TAG, "getAnyPublicImageByPage debug, Successful, Images Count = " + images.size());
            }
        } catch (AVException e) {
            LogUtil.e(TAG, "getAnyPublicImageByPage debug, Failed", e);
            return null;
        }
        return images;
    }

    public static List<BannerModel> getBanners() {
        List<BannerModel> banners = new ArrayList<>();
        try {
            //1.query tags
            List<Tag> tags = new ArrayList<>();
            AVQuery<Tag> queryTag = AVQuery.getQuery("Tag");
            queryTag.limit(6);
            queryTag.orderByDescending(Tag.USAGE);
            tags = queryTag.find();

            //2.query images
            if (tags != null && tags.size() > 0) {
                for (Tag tag : tags) {
                    List<Image> images = new ArrayList<>();
                    AVQuery<Image> queryImage = AVQuery.getQuery("Image");
                    queryImage.whereContains(Image.TAGS, tag.getContent());
                    //queryImage.whereExists(Image.TYPE);
                    queryImage.whereEqualTo(Image.TYPE, 1);//public
                    //queryImage.whereExists(Image.FROM);
                    queryImage.whereEqualTo(Image.FROM, 1);//from post
                    queryImage.limit(6);
                    //queryImage.include(Image.AUTHOR);
                    queryImage.orderByDescending("createdAt");
                    images = queryImage.find();

                    //need > 3
                    if (images != null && images.size() > 2) {
                        BannerModel banner = new BannerModel();
                        banner.setTag(tag);
                        banner.setImages(images);
                        banners.add(banner);
                    }
                }
            }
        } catch (AVException e) {
            LogUtil.e(TAG, "getBanners debug, Failed", e);
            return null;
        }
        return banners;
    }

    public static List<AVUser> followerQuery(String userObjectID, int skip, int limit) {
        List<AVUser> followers = new ArrayList<>();
        try {
            // 其中 userA 是 AVUser 对象，你也可以使用 AVUser 的子类化对象进行查询
            // vhaxun 粉丝
            //AVQuery<AVUser> followerQuery = user.followerQuery(AVUser.class);
            AVQuery<AVUser> followerQuery = AVUser.followerQuery(userObjectID, AVUser.class);// 也可以使用这个静态方法来获取非登录用户的好友关系
            followerQuery.include("follower");
            followerQuery.skip(skip);
            followerQuery.limit(limit);
            followers = followerQuery.find();
        } catch (AVException e) {
            LogUtil.e(TAG, "followerQuery error", e);
            return null;
        }
        return followers;
    }

    public static List<AVUser> followeeQuery(String userObjectID, int skip, int limit) {
        List<AVUser> followers = new ArrayList<>();
        try {
            //查询关注者
            AVQuery<AVUser> followeeQuery = AVUser.followeeQuery(userObjectID, AVUser.class);
            //AVQuery<AVUser> followeeQuery = userB.followeeQuery(AVUser.class);
            followeeQuery.include("followee");
            followeeQuery.skip(skip);
            followeeQuery.limit(limit);
            followers = followeeQuery.find();
        } catch (AVException e) {
            LogUtil.e(TAG, "followerQuery error", e);
            return null;
        }
        return followers;
    }

    public static List<AVUser> getAllMyFriends(String userObjectID) {
        List<AVUser> friends = new ArrayList<>();
        try {
            AVQuery<AVUser> followerQuery = AVUser.followerQuery(userObjectID, AVUser.class);
            followerQuery.include("follower");
            List<AVUser> followers = followerQuery.find();

            AVQuery<AVUser> followeeQuery = AVUser.followeeQuery(userObjectID, AVUser.class);
            followeeQuery.include("followee");
            followeeQuery.whereContainedIn("followee", followers);
            friends = followeeQuery.find();
        } catch (AVException e) {
            LogUtil.e(TAG, "getAllMyFriends error", e);
            return null;
        }
        return friends;
    }

    public static void allFriendshipQuery(String userObjectId) {
        try {
            AVFriendshipQuery query = AVUser.friendshipQuery(userObjectId, AVUser.class);
            query.include("followee");
            query.include("follower");
            AVFriendship friendship = query.get();
            List<AVUser> followers = friendship.getFollowers(); //获取粉丝
            List<AVUser> followees = friendship.getFollowees(); //获取关注列表
            AVUser user = friendship.getUser(); //获取用户对象本身
        } catch (AVException e) {
            LogUtil.e(TAG, "allFriendshipQuery error", e);
        }
    }

    public static List<AVUser> getAllFolloweesByUser(String userObjectId) {
        List<AVUser> followees;
        try {
            AVFriendshipQuery query = AVUser.friendshipQuery(userObjectId, AVUser.class);
            query.include("followee");
            AVFriendship friendship = query.get();
            followees = friendship.getFollowees(); //获取关注列表
        } catch (AVException e) {
            LogUtil.e(TAG, "getAllFolloweesByUser error", e);
            return null;
        }
        return followees;
    }

    public static List<AVUser> getAllFollowersByUser(String userObjectId) {
        List<AVUser> followers;
        try {
            AVFriendshipQuery query = AVUser.friendshipQuery(userObjectId, AVUser.class);
            query.include("follower");
            AVFriendship friendship = query.get();
            followers = friendship.getFollowers(); //获取粉丝
        } catch (AVException e) {
            LogUtil.e(TAG, "getAllFollowersByUser error", e);
            return null;
        }
        return followers;
    }


    public static int getPostCountByUser(String userObjectId) {
        int COUNT = 0;
        try {
            AVQuery<Post> query = AVQuery.getQuery("Post");
            query.whereEqualTo(Post.AUTHOR, AVObject.createWithoutData("_User", userObjectId));
            COUNT = query.count();
        } catch (AVException e) {
            LogUtil.e(TAG, "getPostCountByUser error", e);
            COUNT = 0;
        }
        return COUNT;
    }

    public static int getRepostCountByUser(String userObjectId) {
        int COUNT = 0;
        try {
            AVQuery<Post> query = AVQuery.getQuery("Post");
            query.whereEqualTo(Post.AUTHOR, AVObject.createWithoutData("_User", userObjectId));
            query.whereEqualTo(Post.FROM, 1);
            COUNT = query.count();
        } catch (AVException e) {
            LogUtil.e(TAG, "getRepostCountByUser error", e);
            COUNT = 0;
        }
        return COUNT;
    }

    public static int getLikeCountByUser(String userObjectId) {
        int COUNT = 0;
        try {
            AVQuery<Post> query = AVQuery.getQuery("Post");
            query.whereContains(Post.LIKES, userObjectId);
            COUNT = query.count();
        } catch (AVException e) {
            LogUtil.e(TAG, "getLikeCountByUser error", e);
            COUNT = 0;
        }
        return COUNT;
    }

    public static int getCommentCountByUser(String userObjectId) {
        int COUNT = 0;
        try {
            AVQuery<Comment> query = AVQuery.getQuery("Comment");
            query.whereEqualTo(Comment.AUTHOR, AVObject.createWithoutData("_User", userObjectId));
            COUNT = query.count();
        } catch (AVException e) {
            LogUtil.e(TAG, "getCommentCountByUser error", e);
            COUNT = 0;
        }
        return COUNT;
    }

    public static int getFollowerCountByUser(String userObjectID) {
        int COUNT = 0;
        try {
            AVQuery<AVUser> followerQuery = AVUser.followerQuery(userObjectID, AVUser.class);
            followerQuery.include("follower");
            COUNT = followerQuery.count();
        } catch (AVException e) {
            LogUtil.e(TAG, "getFollowerCountByUser error", e);
            COUNT = 0;
        }
        return COUNT;
    }

    public static int getFolloweeCountByUser(String userObjectID) {
        int COUNT = 0;
        try {
            AVQuery<AVUser> followeeQuery = AVUser.followeeQuery(userObjectID, AVUser.class);
            followeeQuery.include("followee");
            COUNT = followeeQuery.count();
        } catch (AVException e) {
            LogUtil.e(TAG, "getFolloweeCountByUser error", e);
            COUNT = 0;
        }
        return COUNT;
    }

    public static List<AVUser> getAnyUserByUsernameFuzy(String username) {
        List<AVUser> users;
        try {
            AVQuery<AVUser> query = AVUser.getQuery();
            query.whereContains("username", username);
            users = query.find();
        } catch (AVException e) {
            LogUtil.e(TAG, "getAnyUserByUsername error", e);
            return null;
        }
        return users;
    }

    public static List<Tag> getAnyTagByContentFuzy(String content) {
        List<Tag> tags;
        try {
            AVQuery<Tag> query = AVQuery.getQuery("Tag");
            query.whereContains(Tag.CONTENT, content);
            tags = query.find();
        } catch (AVException e) {
            LogUtil.e(TAG, "getAnyTagByContentFuzy error", e);
            return null;
        }
        return tags;
    }

    public static List<Post> getAnyPublicPostsByContentFuzy(String content) {
        List<Post> posts = new ArrayList<>();
        try {
            AVQuery<Post> query = AVQuery.getQuery("Post");
            query.whereEqualTo(Post.TYPE, 1);// public post
            query.whereExists(Post.CONTENT);
            query.whereContains(Post.CONTENT, content);
            query.include(Post.AUTHOR);
            query.include(Post.REPLYORIGINAL);
            query.orderByDescending("createdAt");
            posts = query.find();

            for (Post post : posts) {
                if (0 == post.getFrom()) {//original post
                    AVRelation<Image> photos = post.getPhotos();
                    AVQuery<Image> photoQuery = photos.getQuery();
                    List<Image> images = photoQuery.find();
                    post.trySetPhotoList(images);
                } else {//repost
                    AVQuery<Post> repostQuery = AVQuery.getQuery("Post");
                    repostQuery.include(Post.AUTHOR);
                    Post repost = repostQuery.get(post.getReplyOriginal().getObjectId());

                    AVRelation<Image> repostPhotos = repost.getPhotos();
                    AVQuery<Image> repostPhotoQuery = repostPhotos.getQuery();
                    List<Image> repostImages = repostPhotoQuery.find();
                    repost.trySetPhotoList(repostImages);

                    post.trySetPostOriginal(repost);
                }
            }
        } catch (AVException e) {
            LogUtil.e(TAG, "getAnyPublicPostsByContentFuzy debug, Failed", e);
            return null;
        }
        return posts;
    }

    public static List<Post> getAnyPublicPostsByTag(String tag) {
        List<Post> posts = new ArrayList<>();
        try {
            AVQuery<Post> query = AVQuery.getQuery("Post");
            query.whereEqualTo(Post.TYPE, 1);// public post
            query.whereExists(Post.CONTENT);
            query.whereContains(Post.TAGS, tag);
            query.include(Post.AUTHOR);
            query.include(Post.REPLYORIGINAL);
            query.orderByDescending("createdAt");
            posts = query.find();

            for (Post post : posts) {
                if (0 == post.getFrom()) {//original post
                    AVRelation<Image> photos = post.getPhotos();
                    AVQuery<Image> photoQuery = photos.getQuery();
                    List<Image> images = photoQuery.find();
                    post.trySetPhotoList(images);
                } else {//repost
                    AVQuery<Post> repostQuery = AVQuery.getQuery("Post");
                    repostQuery.include(Post.AUTHOR);
                    Post repost = repostQuery.get(post.getReplyOriginal().getObjectId());

                    AVRelation<Image> repostPhotos = repost.getPhotos();
                    AVQuery<Image> repostPhotoQuery = repostPhotos.getQuery();
                    List<Image> repostImages = repostPhotoQuery.find();
                    repost.trySetPhotoList(repostImages);

                    post.trySetPostOriginal(repost);
                }
            }
        } catch (AVException e) {
            LogUtil.e(TAG, "getAnyPublicPostsByTag debug, Failed", e);
            return null;
        }
        return posts;
    }

}
