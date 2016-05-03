package com.way.heard.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.way.heard.R;
import com.way.heard.utils.GlideImageLoader;
import com.way.heard.utils.LogUtil;

public class UserDisplayActivity extends AppCompatActivity {
    private final static String TAG = UserDisplayActivity.class.getName();
    private Context context;

    public final static String USER_DETAIL = "UserDetail";

    private AVUser user;

    private ImageView ivCover;
    private ImageView ivAvatar;
    private TextView tvDisplayName;
    private TextView tvUsername;
    private TextView tvSignature;
    private TextView tvFolloweeCount;
    private TextView tvFollowerCount;
    private TextView tvCity;
    private ImageView ivPhoto1;
    private ImageView ivPhoto2;
    private ImageView ivPhoto3;
    private TextView tvChatAction;
    private TextView tvFollowAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        getParamData();
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

    private void initView() {
        ivCover = (ImageView) findViewById(R.id.iv_user_display_cover);
        ivAvatar = (ImageView) findViewById(R.id.iv_user_display_avatar);
        tvDisplayName = (TextView) findViewById(R.id.tv_user_display_displayname);
        tvUsername = (TextView) findViewById(R.id.tv_user_display_username);
        tvSignature = (TextView) findViewById(R.id.tv_user_display_signature);
        tvFolloweeCount = (TextView) findViewById(R.id.tv_user_display_followee_count);
        tvFollowerCount = (TextView) findViewById(R.id.tv_user_display_follower_count);
        tvCity = (TextView) findViewById(R.id.tv_user_display_city);
        ivPhoto1 = (ImageView) findViewById(R.id.iv_user_display_photo1);
        ivPhoto2 = (ImageView) findViewById(R.id.iv_user_display_photo2);
        ivPhoto3 = (ImageView) findViewById(R.id.iv_user_display_photo3);
        tvChatAction = (TextView) findViewById(R.id.tv_user_display_chat);
        tvFollowAction = (TextView) findViewById(R.id.tv_user_display_follow);

        initData();
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
            }
            String usernameStr = user.getUsername();
            if (!TextUtils.isEmpty(usernameStr)) {
                tvUsername.setText(usernameStr);
            }
            String signatureStr = user.getString("signature");
            if (!TextUtils.isEmpty(signatureStr)) {
                tvSignature.setText(signatureStr);
            }
            String cityStr = user.getString("city");
            if (!TextUtils.isEmpty(cityStr)) {
                tvSignature.setText(cityStr);
            }
            GlideImageLoader.displayImage(context, "http://img0.ph.126.net/Yb9EDjXQWy2xrrlrl7p6Pw==/1996502009910115188.jpg", ivPhoto1);
        } catch (Exception e) {
            LogUtil.e(TAG, "initData error", e);
        }
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if(listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for(int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
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
