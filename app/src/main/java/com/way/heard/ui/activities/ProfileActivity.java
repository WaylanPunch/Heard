package com.way.heard.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.way.heard.R;
import com.way.heard.adapters.ProfileViewPagerAdapter;
import com.way.heard.services.LeanCloudBackgroundTask;
import com.way.heard.services.LeanCloudDataService;
import com.way.heard.services.LeanCloudUserService;
import com.way.heard.ui.fragments.FolloweeFragment;
import com.way.heard.ui.fragments.FollowerFragment;
import com.way.heard.ui.fragments.ProfilePostFragment;
import com.way.heard.utils.GlideImageLoader;
import com.way.heard.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.activity.LCIMConversationActivity;
import cn.leancloud.chatkit.utils.LCIMConstants;

public class ProfileActivity extends BaseActivity {
    private final static String TAG = ProfileActivity.class.getName();

    public final static String USER_DETAIL = "UserDetail";

    private AppBarLayout app_bar_layout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar toolbar;
    private LinearLayout head_layout;
    private TabLayout toolbar_tab;
    private ViewPager main_vp_container;
    private TextView tvFollow;

    private List<Fragment> fragments;
    private String[] tabTitles;
    private AVUser currentUser;
    private int mShareCount;
    private int mLikeCount;
    private int mCommentCount;
    private boolean mIsMyFollowee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initToolBar();
        getParamData();
        initToolBatLayout();
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getParamData() {
        try {
            Bundle bundle = getIntent().getExtras();
            currentUser = bundle.getParcelable(ProfileActivity.USER_DETAIL);
            LogUtil.d(TAG, "getParamData debug, Username = " + currentUser.getUsername());
            LogUtil.d(TAG, "getParamData debug, Avatar = " + currentUser.getString("avatar"));
        } catch (Exception e) {
            LogUtil.e(TAG, "getParamData error", e);
        }
    }


    private void initToolBatLayout() {
        app_bar_layout = (AppBarLayout) findViewById(R.id.abl_profile_app_bar_layout);
        head_layout = (LinearLayout) findViewById(R.id.ll_profile_head_layout);

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_profile_toolbarlayout);
//        head_layout.setBackgroundDrawable(new BitmapDrawable(BlurUtil.fastblur(this, bitmap, 180)));
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_profile_toolbarlayout_blur);
        head_layout.setBackgroundDrawable(new BitmapDrawable(bitmap));

        //使用CollapsingToolbarLayout必须把title设置到CollapsingToolbarLayout上，设置到Toolbar上则不会显示
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.ctl_profile_toolbar_layout);
//        mCollapsingToolbarLayout.setContentScrim(new BitmapDrawable(BlurUtil.fastblur(this, bitmap, 180)));
        mCollapsingToolbarLayout.setContentScrim(new BitmapDrawable(bitmap));
        app_bar_layout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset <= -head_layout.getHeight() / 2) {
                    mCollapsingToolbarLayout.setTitle(currentUser.getUsername() + "");
                } else {
                    mCollapsingToolbarLayout.setTitle(" ");
                }
            }
        });
        toolbar_tab = (TabLayout) findViewById(R.id.tl_profile_toolbar_tab);
        main_vp_container = (ViewPager) findViewById(R.id.vp_profile_contentpager);

        initFragements();

        ProfileViewPagerAdapter vpAdapter = new ProfileViewPagerAdapter(getSupportFragmentManager(), this, fragments, tabTitles);
        main_vp_container.setOffscreenPageLimit(fragments.size() - 1);
        main_vp_container.setAdapter(vpAdapter);
        main_vp_container.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(toolbar_tab));
        toolbar_tab.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(main_vp_container));
        //toolbar_tab.set
        //tablayout和viewpager建立联系为什么不用下面这个方法呢？自己去研究一下，可能收获更多
        //toolbar_tab.setupWithViewPager(main_vp_container);
        //initUserDetail(mCollapsingToolbarLayout);
        getDataFromCloud();
    }

    private void initFragements() {
        tabTitles = new String[]{"", "FOLLOWEE", "FOLLOWER"};
        fragments = new ArrayList<>();
        ProfilePostFragment fragment1 = ProfilePostFragment.newInstance(currentUser.getObjectId());
        FolloweeFragment fragment2 = FolloweeFragment.newInstance(currentUser.getObjectId());
        FollowerFragment fragment3 = FollowerFragment.newInstance(currentUser.getObjectId());
        fragments.add(fragment1);
        fragments.add(fragment2);
        fragments.add(fragment3);
    }

    private void getDataFromCloud() {
        new LeanCloudBackgroundTask(ProfileActivity.this) {

            @Override
            protected void onPre() {
                //loading.start();
            }

            @Override
            protected void doInBack() throws AVException {
                //String currentuserId = AVUser.getCurrentUser().getObjectId();
                mShareCount = LeanCloudDataService.getRepostCountByUser(currentUser.getObjectId());
                mLikeCount = LeanCloudDataService.getLikeCountByUser(currentUser.getObjectId());
                mCommentCount = LeanCloudDataService.getCommentCountByUser(currentUser.getObjectId());
                mIsMyFollowee = LeanCloudDataService.isMyFollowee(currentUser.getObjectId());
//                mPostCount = LeanCloudDataService.getPostCountByUser(currentuserId);
//                mFolloweeCount = LeanCloudDataService.getFolloweeCountByUser(currentuserId);
//                mFollowerCount = LeanCloudDataService.getFollowerCountByUser(currentuserId);
            }

            @Override
            protected void onPost(AVException e) {
                if (e != null) {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                initUserDetail(mCollapsingToolbarLayout);
            }

            @Override
            protected void onCancel() {
                //loading.stop();
            }
        }.execute();
    }

    private void initUserDetail(View container) {
        ((TextView) container.findViewById(R.id.tv_profile_sharecount)).setText(mShareCount + "");
        ((TextView) container.findViewById(R.id.tv_profile_likecount)).setText(mLikeCount + "");
        ((TextView) container.findViewById(R.id.tv_profile_commentcount)).setText(mCommentCount + "");

        tvFollow = ((TextView) container.findViewById(R.id.tv_profile_follow));
        if (mIsMyFollowee) {
            tvFollow.setText("UNFOLLOW");
        } else {
            tvFollow.setText("FOLLOW");
        }
        tvFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsMyFollowee) {//already followed
                    LeanCloudUserService.unfollowUser(AVUser.getCurrentUser(), currentUser.getObjectId(), new LeanCloudUserService.LeanCloudUserServiceListener() {
                        @Override
                        public void onSuccess() {
                            mIsMyFollowee = false;
                            tvFollow.setText("FOLLOW");
                            Toast.makeText(ProfileActivity.this, "Unfollowed", Toast.LENGTH_SHORT).show();
                            LogUtil.d(TAG, "unfollowUser debug, unfollow succeeded.");
                        }

                        @Override
                        public void onErrorMatter(String msg) {
                            Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                            LogUtil.e(TAG, "unfollowUser error, " + msg);
                        }

                        @Override
                        public void onErrorNoMatter(String msg) {

                        }
                    });
                } else {//unfollow
                    LeanCloudUserService.followUser(AVUser.getCurrentUser(), currentUser.getObjectId(), new LeanCloudUserService.LeanCloudUserServiceListener() {
                        @Override
                        public void onSuccess() {
                            mIsMyFollowee = true;
                            tvFollow.setText("UNFOLLOW");
                            Toast.makeText(ProfileActivity.this, "Followed", Toast.LENGTH_SHORT).show();
                            LogUtil.d(TAG, "followUser debug, follow succeeded.");
                        }

                        @Override
                        public void onErrorMatter(String msg) {
                            Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                            LogUtil.e(TAG, "followUser error, " + msg);
                        }

                        @Override
                        public void onErrorNoMatter(String msg) {
                            mIsMyFollowee = true;
                            tvFollow.setText("UNFOLLOW");
                            Toast.makeText(ProfileActivity.this, "Already Followed", Toast.LENGTH_SHORT).show();
                            LogUtil.d(TAG, "followUser debug, Already followed.");
                        }
                    });
                }
            }
        });

        if (!TextUtils.isEmpty(currentUser.getString("displayname"))) {
            ((TextView) container.findViewById(R.id.tv_profile_displayname)).setText(currentUser.getString("displayname"));
        }
        if (!TextUtils.isEmpty(currentUser.getUsername())) {
            ((TextView) container.findViewById(R.id.tv_profile_username)).setText(currentUser.getUsername());
        }
        if (!TextUtils.isEmpty(currentUser.getString("signature"))) {
            ((TextView) container.findViewById(R.id.tv_profile_signature)).setText(currentUser.getString("signature"));
        }
        if (!TextUtils.isEmpty(currentUser.getString("avatar"))) {
            GlideImageLoader.displayImage(ProfileActivity.this, currentUser.getString("avatar"), (ImageView) container.findViewById(R.id.iv_profile_avatar));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_chat) {
            LCChatKit.getInstance().open(AVUser.getCurrentUser().getUsername(), new AVIMClientCallback() {
                @Override
                public void done(AVIMClient avimClient, AVIMException e) {
                    if (e != null) {
                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(ProfileActivity.this, LCIMConversationActivity.class);
                    intent.putExtra(LCIMConstants.PEER_ID, currentUser.getUsername());
                    startActivity(intent);
                }
            });
//            Intent intent = new Intent(ProfileActivity.this, LCIMConversationActivity.class);
//            intent.putExtra(LCIMConstants.PEER_ID, currentUser.getUsername());
//            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void go(Context context, AVUser user) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(ProfileActivity.USER_DETAIL, user);
        context.startActivity(intent);
    }
}
