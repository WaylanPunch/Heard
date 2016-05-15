package com.way.heard.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.victor.loading.rotate.RotateLoading;
import com.way.heard.R;
import com.way.heard.adapters.FollowerAdapter;
import com.way.heard.services.LeanCloudBackgroundTask;
import com.way.heard.services.LeanCloudDataService;
import com.way.heard.ui.views.autoloadrecyclerview.AutoLoadRecyclerView;
import com.way.heard.ui.views.autoloadrecyclerview.LoadMoreListener;
import com.way.heard.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class FollowerListActivity extends AppCompatActivity {
    private final static String TAG = FollowerListActivity.class.getName();

    public final static String USER_OBJECT_ID = "UserObjectId";
    public final static String QUERY_TYPE = "QueryType";
    public final static int QUERY_TYPE_FOLLOWEE = 0;
    public final static int QUERY_TYPE_FOLLOWER = 1;

    private String userObjectID;
    private int queryType;

    private Context context;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AutoLoadRecyclerView mRecyclerView;
    private RotateLoading loading;

    private FollowerAdapter mAdapter;
    private int pageIndex = 1;
    private final static int pageSize = 5;
    private List<AVUser> mFollowers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_list);
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
        mFollowers = new ArrayList<>();
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLoadMoreListener(new LoadMoreListener() {
            @Override
            public void loadMore() {
                loadNextPage();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccentLight,
                R.color.colorAccent,
                R.color.colorAccentDark,
                R.color.colorAccentLight);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFirst();
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setOnPauseListenerParams(context, false, true);


        mAdapter = new FollowerAdapter(context, queryType);
        mAdapter.setOnImageClickListener(new FollowerAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(int pos) {
                LogUtil.d(TAG, "onImageClick debug, Position = " + pos);

            }
        });
        mRecyclerView.setAdapter(mAdapter);
        loadFirst();
    }

    public void loadFirst() {
        pageIndex = 1;
        LogUtil.d(TAG, "loadFirst debug, Page Index = " + pageIndex);
        loadUsersByQueryType();
    }

    public void loadNextPage() {
        pageIndex++;
        LogUtil.d(TAG, "loadNextPage debug, Page Index = " + pageIndex);
        loadUsersByQueryType();
    }

    private void loadUsersByQueryType() {
        new LeanCloudBackgroundTask(context) {

            @Override
            protected void onPre() {
                loading.start();
                mSwipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected void doInBack() throws AVException {
                List<AVUser> data = new ArrayList<AVUser>();
                if (queryType == QUERY_TYPE_FOLLOWEE) {//==1, public
                    data = LeanCloudDataService.followeeQuery(userObjectID);
                } else {//==0, all
                    data = LeanCloudDataService.followerQuery(userObjectID);
                }
                if (mFollowers == null) {
                    mFollowers = new ArrayList<AVUser>();
                }
                if (pageIndex == 1) {
                    mFollowers.clear();
                    mFollowers = data;
                } else {
                    mFollowers.addAll(data);
                }

            }

            @Override
            protected void onPost(AVException e) {
                mAdapter.setPosts(mFollowers);
                mAdapter.notifyDataSetChanged();
                loading.stop();
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }.execute();
    }

    public static void go(Context context, String userObjectID, int queryType) {
        Intent intent = new Intent(context, UserPostActivity.class);
        intent.putExtra(USER_OBJECT_ID, userObjectID);
        intent.putExtra(QUERY_TYPE, queryType);
        context.startActivity(intent);
    }


}
