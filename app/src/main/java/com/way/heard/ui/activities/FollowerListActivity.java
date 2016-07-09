package com.way.heard.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFriendship;
import com.avos.avoscloud.AVFriendshipQuery;
import com.avos.avoscloud.AVUser;
import com.victor.loading.rotate.RotateLoading;
import com.way.heard.R;
import com.way.heard.adapters.FollowerAdapter;
import com.way.heard.services.LeanCloudBackgroundTask;
import com.way.heard.services.LeanCloudDataService;
import com.way.heard.services.LeanCloudUserService;
import com.way.heard.ui.views.autoloadrecyclerview.AutoLoadRecyclerView;
import com.way.heard.ui.views.autoloadrecyclerview.LoadMoreListener;
import com.way.heard.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class FollowerListActivity extends BaseActivity {
    private final static String TAG = FollowerListActivity.class.getName();

    public final static String USER_OBJECT_ID = "UserObjectId";
    public final static String QUERY_TYPE = "QueryType";
    public final static int QUERY_TYPE_FOLLOWEE = 0;//关注的用户
    public final static int QUERY_TYPE_FOLLOWER = 1;//我的粉丝

    private String userObjectID;
    private int queryType;
    private Toolbar toolbar;
    private Context context;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AutoLoadRecyclerView mRecyclerView;
    private RotateLoading loading;

    private FollowerAdapter mAdapter;
    private int pageIndex = 1;
    private final static int pageSize = 15;
    private List<AVUser> mUsers;
    private List<AVUser> mMyFollowers;
    private List<AVUser> mMyFollowees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Follower");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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
        initData();
    }

    private void getParamData() {
        try {
            userObjectID = getIntent().getStringExtra(USER_OBJECT_ID);
            queryType = getIntent().getIntExtra(QUERY_TYPE, 0);
            LogUtil.d(TAG, "getParamData debug, UserObjectId = " + userObjectID);
            LogUtil.d(TAG, "getParamData debug, QueryType = " + queryType);
        } catch (Exception e) {
            LogUtil.e(TAG, "getParamData error", e);
        }
    }

    private void initView() {
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_follower_list_swiperefreshlayout);
        this.mRecyclerView = (AutoLoadRecyclerView) findViewById(R.id.alrv_follower_list_recyclerview);
        this.loading = (RotateLoading) findViewById(R.id.loading);
    }

    private void initData() {
        mUsers = new ArrayList<>();
        mMyFollowers = new ArrayList<>();
        mMyFollowees = new ArrayList<>();
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLoadMoreListener(new LoadMoreListener() {
            @Override
            public void loadMore() {
                loadNextPage(false);
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccentLight,
                R.color.colorAccent,
                R.color.colorAccentDark,
                R.color.colorAccentLight);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFirst(false);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setOnPauseListenerParams(context, false, true);


        mAdapter = new FollowerAdapter(context, queryType);
        mAdapter.setOnAvatarClickListener(new FollowerAdapter.OnAvatarClickListener() {
            @Override
            public void onAvatarClick(int pos) {
                UserDisplayActivity.go(context, mUsers.get(pos));
            }
        });
        mAdapter.setOnFollowClickListener(new FollowerAdapter.OnFollowClickListener() {
            @Override
            public void onFollowClick(int pos, boolean followed) {
                followOrUnfollow(followed, mUsers.get(pos));
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        loadFirst(true);
    }

    public void loadFirst(boolean isFirstTime) {
        pageIndex = 1;
        LogUtil.d(TAG, "loadFirst debug, Page Index = " + pageIndex);
        loadUsersByQueryType(isFirstTime);
    }

    public void loadNextPage(boolean isFirstTime) {
        pageIndex++;
        LogUtil.d(TAG, "loadNextPage debug, Page Index = " + pageIndex);
        loadUsersByQueryType(isFirstTime);
    }

    private void loadUsersByQueryType(final boolean isFirstTime) {
        new LeanCloudBackgroundTask(context) {

            @Override
            protected void onPre() {
                if(isFirstTime) {
                    loading.start();
                }
                mSwipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected void doInBack() throws AVException {
                List<AVUser> data;
                if (queryType == QUERY_TYPE_FOLLOWEE) {//==0,followee
                    data = LeanCloudDataService.followeeQuery(userObjectID, (pageIndex - 1) * pageSize, pageSize);
                } else {//==0,follower
                    data = LeanCloudDataService.followerQuery(userObjectID, (pageIndex - 1) * pageSize, pageSize);
                }
                if (mUsers == null) {
                    mUsers = new ArrayList<AVUser>();
                }
                if (pageIndex == 1) {
                    mUsers.clear();
                    mUsers = data;
                } else {
                    mUsers.addAll(data);
                }

                if (pageIndex == 1) {
                    AVFriendshipQuery query = AVUser.friendshipQuery(AVUser.getCurrentUser().getObjectId(), AVUser.class);
                    query.include("followee");
                    query.include("follower");
                    AVFriendship friendship = query.get();
                    mMyFollowees = friendship.getFollowees(); //获取关注列表
                    mMyFollowers = friendship.getFollowers(); //获取粉丝
                }
            }

            @Override
            protected void onPost(AVException e) {
                mAdapter.setUsers(mUsers);
                if (pageIndex == 1) {
                    mAdapter.setMyFollowees(mMyFollowees);
                    mAdapter.setMyFollowers(mMyFollowers);
                }
                mAdapter.notifyDataSetChanged();
                if(isFirstTime) {
                    loading.stop();
                }
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            protected void onCancel() {
                if(isFirstTime) {
                    loading.stop();
                }
            }
        }.execute();
    }

    private void followOrUnfollow(boolean isMyFollowee, final AVUser user) {
        if (isMyFollowee) {//already followed
            LeanCloudUserService.unfollowUser(AVUser.getCurrentUser(), user.getObjectId(), new LeanCloudUserService.LeanCloudUserServiceListener() {
                @Override
                public void onSuccess() {
                    mMyFollowees.remove(user);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(context, "Unfollowed", Toast.LENGTH_SHORT).show();
                    LogUtil.d(TAG, "followOrUnfollow debug, unfollow succeeded.");
                }

                @Override
                public void onErrorMatter(String msg) {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    LogUtil.e(TAG, "followOrUnfollow error, " + msg);
                }

                @Override
                public void onErrorNoMatter(String msg) {

                }
            });
        } else {//unfollow
            LeanCloudUserService.followUser(AVUser.getCurrentUser(), user.getObjectId(), new LeanCloudUserService.LeanCloudUserServiceListener() {
                @Override
                public void onSuccess() {
                    mMyFollowees.add(user);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(context, "Followed", Toast.LENGTH_SHORT).show();
                    LogUtil.d(TAG, "followOrUnfollow debug, follow succeeded.");
                }

                @Override
                public void onErrorMatter(String msg) {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    LogUtil.e(TAG, "followOrUnfollow error, " + msg);
                }

                @Override
                public void onErrorNoMatter(String msg) {
                    Toast.makeText(context, "Already Followed", Toast.LENGTH_SHORT).show();
                    LogUtil.d(TAG, "followOrUnfollow debug, Already followed.");
                }
            });
        }
    }

    public static void go(Context context, String userObjectID, int queryType) {
        Intent intent = new Intent(context, FollowerListActivity.class);
        intent.putExtra(USER_OBJECT_ID, userObjectID);
        intent.putExtra(QUERY_TYPE, queryType);
        context.startActivity(intent);
    }


}
