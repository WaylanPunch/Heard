package com.way.heard.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.victor.loading.rotate.RotateLoading;
import com.way.heard.R;
import com.way.heard.models.Image;
import com.way.heard.services.LeanCloudBackgroundTask;
import com.way.heard.services.LeanCloudUserService;
import com.way.heard.utils.GlideImageLoader;
import com.way.heard.utils.LogUtil;

import java.util.List;

public class UserDisplayActivity extends BaseActivity {
    private final static String TAG = UserDisplayActivity.class.getName();
    private Context context;

    public final static String USER_DETAIL = "UserDetail";

    private AVUser user;

    private ImageView ivCover;
    private ImageView ivAvatar;
    private TextView tvDisplayName;
    private TextView tvUsername;
    private TextView tvFolloweeCount;
    private TextView tvFollowerCount;
    private LinearLayout llSignContainer;
    private TextView tvSignature;
    private LinearLayout llCityContainer;
    private TextView tvCity;
    private LinearLayout llPhotoContainer;
    private ImageView ivPhoto1;
    private ImageView ivPhoto2;
    private ImageView ivPhoto3;
    private TextView tvChatAction;
    private TextView tvFollowAction;
    private RotateLoading loading;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_display);

        getParamData();
        initToolBar();

        context = this;
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


        initView();
    }

    private void getParamData() {
        try {
            Bundle bundle = getIntent().getExtras();
            user = bundle.getParcelable(UserDisplayActivity.USER_DETAIL);
            LogUtil.d(TAG, "getParamData debug, Username = " + user.getUsername());
            LogUtil.d(TAG, "getParamData debug, Avatar = " + user.getString("avatar"));
        } catch (Exception e) {
            LogUtil.e(TAG, "getParamData error", e);
        }
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (user != null) {
            getSupportActionBar().setTitle(user.getUsername());
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void initView() {
        ivCover = (ImageView) findViewById(R.id.iv_user_display_cover);
        ivAvatar = (ImageView) findViewById(R.id.iv_user_display_avatar);
        tvDisplayName = (TextView) findViewById(R.id.tv_user_display_displayname);
        tvUsername = (TextView) findViewById(R.id.tv_user_display_username);
        tvFolloweeCount = (TextView) findViewById(R.id.tv_user_display_followee_count);
        tvFollowerCount = (TextView) findViewById(R.id.tv_user_display_follower_count);
        llSignContainer = (LinearLayout) findViewById(R.id.ll_user_display_signature_container);
        tvSignature = (TextView) findViewById(R.id.tv_user_display_signature);
        llCityContainer = (LinearLayout) findViewById(R.id.ll_user_display_city_container);
        tvCity = (TextView) findViewById(R.id.tv_user_display_city);
        llPhotoContainer = (LinearLayout) findViewById(R.id.ll_user_display_photo_container);
        ivPhoto1 = (ImageView) findViewById(R.id.iv_user_display_photo1);
        ivPhoto2 = (ImageView) findViewById(R.id.iv_user_display_photo2);
        ivPhoto3 = (ImageView) findViewById(R.id.iv_user_display_photo3);
        tvChatAction = (TextView) findViewById(R.id.tv_user_display_chat);
        tvFollowAction = (TextView) findViewById(R.id.tv_user_display_follow);
        loading = (RotateLoading) findViewById(R.id.loading);
        initData();
        getViewData(user);
    }


    private void initData() {
        try {
            String coverUrl = user.getString("cover");
            if (!TextUtils.isEmpty(coverUrl)) {
                GlideImageLoader.displayImage(context, coverUrl, ivCover);
            }
            String avatarUrl = user.getString("avatar");
            if (!TextUtils.isEmpty(avatarUrl)) {
                GlideImageLoader.displayImage(context, avatarUrl, ivAvatar);
            }
            String displayNameStr = user.getString("displayname");
            if (!TextUtils.isEmpty(displayNameStr)) {
                tvDisplayName.setText(displayNameStr);
            } else {
                tvDisplayName.setVisibility(View.GONE);
            }
            String usernameStr = user.getUsername();
            if (!TextUtils.isEmpty(usernameStr)) {
                tvUsername.setText(usernameStr);
            }
            String signatureStr = user.getString("signature");
            if (!TextUtils.isEmpty(signatureStr)) {
                tvSignature.setText(signatureStr);
            } else {
                llSignContainer.setVisibility(View.GONE);
            }
            String cityStr = user.getString("city");
            if (!TextUtils.isEmpty(cityStr)) {
                tvCity.setText(cityStr);
            } else {
                llCityContainer.setVisibility(View.GONE);
            }


            tvFolloweeCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FollowerListActivity.go(context, user.getObjectId(), FollowerListActivity.QUERY_TYPE_FOLLOWEE);
                }
            });
            tvFollowerCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FollowerListActivity.go(context, user.getObjectId(), FollowerListActivity.QUERY_TYPE_FOLLOWER);
                }
            });
            llPhotoContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserPostActivity.go(UserDisplayActivity.this, user, UserPostActivity.DISPLAY_TYPE_PUBLIC);
                }
            });
            tvChatAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            tvFollowAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFollowed) {//already followed
                        LeanCloudUserService.unfollowUser(AVUser.getCurrentUser(), user.getObjectId(), new LeanCloudUserService.LeanCloudUserServiceListener() {
                            @Override
                            public void onSuccess() {
                                isFollowed = false;
                                tvFollowAction.setText("Follow");
                                tvFollowAction.setBackgroundResource(R.drawable.tag_background);
                                Toast.makeText(context, "Unfollowed", Toast.LENGTH_SHORT).show();
                                LogUtil.d(TAG, "unfollowUser debug, unfollow succeeded.");
                            }

                            @Override
                            public void onErrorMatter(String msg) {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                LogUtil.e(TAG, "unfollowUser error, " + msg);
                            }

                            @Override
                            public void onErrorNoMatter(String msg) {

                            }
                        });
                    } else {//unfollow
                        LeanCloudUserService.followUser(AVUser.getCurrentUser(), user.getObjectId(), new LeanCloudUserService.LeanCloudUserServiceListener() {
                            @Override
                            public void onSuccess() {
                                isFollowed = true;
                                tvFollowAction.setText("Unfollow");
                                tvFollowAction.setBackgroundResource(R.drawable.tag_background_accent);
                                Toast.makeText(context, "Followed", Toast.LENGTH_SHORT).show();
                                LogUtil.d(TAG, "followUser debug, follow succeeded.");
                            }

                            @Override
                            public void onErrorMatter(String msg) {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                LogUtil.e(TAG, "followUser error, " + msg);
                            }

                            @Override
                            public void onErrorNoMatter(String msg) {
                                isFollowed = true;
                                tvFollowAction.setText("Unfollow");
                                tvFollowAction.setBackgroundResource(R.drawable.tag_background_accent);
                                Toast.makeText(context, "Already Followed", Toast.LENGTH_SHORT).show();
                                LogUtil.d(TAG, "followUser debug, Already followed.");
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "initData error", e);
        }
    }

    private int mFolloweeCount;
    private int mFollowerCount;
    private List<Image> photos;
    private boolean isFollowed;

    private void getViewData(final AVUser avUser) {
        new LeanCloudBackgroundTask(UserDisplayActivity.this) {

            @Override
            protected void onPre() {
                loading.start();
            }

            @Override
            protected void doInBack() throws AVException {
                // 其中 userA 是 AVUser 对象，你也可以使用 AVUser 的子类化对象进行查询
                AVQuery<AVUser> followerQuery = avUser.followerQuery(AVUser.class);
                // AVQuery<AVUser> followerQuery = AVUser.followerQuery(userA.getObjectId(),AVUser.class); 也可以使用这个静态方法来获取非登录用户的好友关系
                mFollowerCount = followerQuery.count(); //.find().size();

                //查询关注者
                AVQuery<AVUser> followeeQuery = AVUser.followeeQuery(avUser.getObjectId(), AVUser.class);
                //AVQuery<AVUser> followeeQuery = userB.followeeQuery(AVUser.class);
                mFolloweeCount = followeeQuery.count();
//                followeeQuery.findInBackground(new FindCallback<AVUser>() {
//                    @Override
//                    public void done(List<AVUser> avObjects, AVException avException) {
//                        //avObjects 就是用户的关注用户列表
//
//                    }
//                });

                AVQuery<AVUser> followQuery = AVUser.followeeQuery(AVUser.getCurrentUser().getObjectId(), AVUser.class);
                followQuery.whereEqualTo("followee", AVObject.createWithoutData("_User", avUser.getObjectId()));
                int count = followQuery.count();
                if (count == 0) {
                    isFollowed = false;
                } else {
                    isFollowed = true;
                }

                AVQuery<Image> query = AVQuery.getQuery("Image");
                query.whereExists(Image.TYPE);
                query.whereEqualTo(Image.TYPE, 1);//public
                query.whereExists(Image.FROM);
                query.whereEqualTo(Image.FROM, 1);//from post
                query.whereEqualTo(Image.AUTHOR, AVObject.createWithoutData("_User", avUser.getObjectId()));
                query.limit(3);
                query.orderByDescending("createdAt");
                photos = query.find();
            }

            @Override
            protected void onPost(AVException e) {
                loading.stop();
                if (e != null) {
                    Toast.makeText(UserDisplayActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                setMoreData();
            }

            @Override
            protected void onCancel() {
                loading.stop();
            }
        }.execute();
    }

    private void setMoreData() {
        tvFolloweeCount.setText(mFolloweeCount + "");
        tvFollowerCount.setText(mFollowerCount + "");
        if (isFollowed) {
            tvFollowAction.setText("Followed");
            tvFollowAction.setBackgroundResource(R.drawable.tag_background_accent);
        }
        if (photos == null || photos.size() == 0) {
            llPhotoContainer.setVisibility(View.GONE);
//            ivPhoto1.setVisibility(View.GONE);
//            ivPhoto2.setVisibility(View.GONE);
//            ivPhoto3.setVisibility(View.GONE);
        } else if (photos.size() == 1) {
            GlideImageLoader.displayImage(context, photos.get(0).getThumbnailurl(), ivPhoto1);
            ivPhoto2.setVisibility(View.GONE);
            ivPhoto3.setVisibility(View.GONE);
        } else if (photos.size() == 2) {
            GlideImageLoader.displayImage(context, photos.get(0).getThumbnailurl(), ivPhoto1);
            GlideImageLoader.displayImage(context, photos.get(1).getThumbnailurl(), ivPhoto2);
            ivPhoto3.setVisibility(View.GONE);
        } else if (photos.size() == 3) {
            GlideImageLoader.displayImage(context, photos.get(0).getThumbnailurl(), ivPhoto1);
            GlideImageLoader.displayImage(context, photos.get(1).getThumbnailurl(), ivPhoto2);
            GlideImageLoader.displayImage(context, photos.get(2).getThumbnailurl(), ivPhoto3);
        }
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    public static void go(Context context, AVUser user) {
        Intent intent = new Intent(context, UserDisplayActivity.class);
        intent.putExtra(UserDisplayActivity.USER_DETAIL, user);
        context.startActivity(intent);
    }


}
