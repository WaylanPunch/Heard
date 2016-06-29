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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.way.heard.R;
import com.way.heard.adapters.ProfileViewPagerAdapter;
import com.way.heard.services.LeanCloudBackgroundTask;
import com.way.heard.services.LeanCloudDataService;
import com.way.heard.ui.fragments.FolloweeFragment;
import com.way.heard.ui.fragments.FollowerFragment;
import com.way.heard.ui.fragments.ProfilePostFragment;
import com.way.heard.utils.GlideImageLoader;
import com.way.heard.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class ProfileScanActivity extends AppCompatActivity {
    private final static String TAG = ProfileScanActivity.class.getName();

    public final static String USER_DETAIL = "UserDetail";

    private Toolbar toolbar;
    private AppBarLayout app_bar_layout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private LinearLayout head_layout;
    private TabLayout toolbar_tab;
    private ViewPager main_vp_container;
    private List<Fragment> fragments;
    private String[] tabTitles;
    //private AVUser currentUser;
    private String userdetail;
    private String userobjectid;
    private String username;
    private AVUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_scan);
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
            userdetail = bundle.getString(ProfileScanActivity.USER_DETAIL);
            String[] stringArr = userdetail.split(":");
            userobjectid = stringArr[0];
            username = stringArr[1];
            LogUtil.d(TAG, "getParamData debug, UserObjectId = " + stringArr[0] + ", Username = " + stringArr[1]);
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
                    mCollapsingToolbarLayout.setTitle(username + "");
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

        getUserByObjectId(userobjectid);
        //initUserDetail(mCollapsingToolbarLayout);
    }

    private void initFragements() {
        tabTitles = new String[]{"", "FOLLOWEE", "FOLLOWER"};
        fragments = new ArrayList<>();
        ProfilePostFragment fragment1 = ProfilePostFragment.newInstance(userobjectid);
        FolloweeFragment fragment2 = FolloweeFragment.newInstance(userobjectid);
        FollowerFragment fragment3 = FollowerFragment.newInstance(userobjectid);
        fragments.add(fragment1);
        fragments.add(fragment2);
        fragments.add(fragment3);
    }


    private void initUserDetail(View container) {
        if (user != null) {
            if (!TextUtils.isEmpty(user.getString("displayname"))) {
                ((TextView) container.findViewById(R.id.tv_profile_displayname)).setText(user.getString("displayname"));
            }
            if (!TextUtils.isEmpty(username)) {
                ((TextView) container.findViewById(R.id.tv_profile_username)).setText(username);
            }
            if (!TextUtils.isEmpty(user.getString("signature"))) {
                ((TextView) container.findViewById(R.id.tv_profile_signature)).setText(user.getString("signature"));
            }
            if (!TextUtils.isEmpty(user.getString("avatar"))) {
                GlideImageLoader.displayImage(ProfileScanActivity.this, user.getString("avatar"), (ImageView) container.findViewById(R.id.iv_profile_avatar));
            }
        }
    }


    private void getUserByObjectId(final String objectid) {
        LeanCloudBackgroundTask task = new LeanCloudBackgroundTask(ProfileScanActivity.this) {
            @Override
            protected void onPre() {

            }

            @Override
            protected void doInBack() throws AVException {
                user = LeanCloudDataService.getUserByObjectId(objectid);
            }

            @Override
            protected void onPost(AVException e) {
                if (e != null) {
                    Toast.makeText(ProfileScanActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                initUserDetail(mCollapsingToolbarLayout);
            }

            @Override
            protected void onCancel() {

            }
        };
        task.execute();
    }

    public static void go(Context context, String userdetail) {
        Intent intent = new Intent(context, ProfileScanActivity.class);
        intent.putExtra(ProfileScanActivity.USER_DETAIL, userdetail);
        context.startActivity(intent);
    }

}
