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

import com.avos.avoscloud.AVUser;
import com.way.heard.R;
import com.way.heard.adapters.ProfileViewPagerAdapter;
import com.way.heard.ui.fragments.FolloweeFragment;
import com.way.heard.ui.fragments.FollowerFragment;
import com.way.heard.ui.fragments.ProfilePostFragment;
import com.way.heard.utils.GlideImageLoader;
import com.way.heard.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends BaseActivity {
    private final static String TAG = ProfileActivity.class.getName();

    public final static String USER_DETAIL = "UserDetail";

    private LinearLayout head_layout;
    private TabLayout toolbar_tab;
    private ViewPager main_vp_container;
    private List<Fragment> fragments;
    private String[] tabTitles;
    private AVUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getParamData();
        initToolBatLayout();
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
        AppBarLayout app_bar_layout = (AppBarLayout) findViewById(R.id.abl_profile_app_bar_layout);
        head_layout = (LinearLayout) findViewById(R.id.ll_profile_head_layout);

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_profile_toolbarlayout);
//        head_layout.setBackgroundDrawable(new BitmapDrawable(BlurUtil.fastblur(this, bitmap, 180)));
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_profile_toolbarlayout_blur);
        head_layout.setBackgroundDrawable(new BitmapDrawable(bitmap));

        //使用CollapsingToolbarLayout必须把title设置到CollapsingToolbarLayout上，设置到Toolbar上则不会显示
        final CollapsingToolbarLayout mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.ctl_profile_toolbar_layout);
//        mCollapsingToolbarLayout.setContentScrim(new BitmapDrawable(BlurUtil.fastblur(this, bitmap, 180)));
        mCollapsingToolbarLayout.setContentScrim(new BitmapDrawable(bitmap));
        app_bar_layout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset <= -head_layout.getHeight() / 2) {
                    mCollapsingToolbarLayout.setTitle("涩郎");
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
        initUserDetail(mCollapsingToolbarLayout);
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


    private void initUserDetail(View container) {
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
