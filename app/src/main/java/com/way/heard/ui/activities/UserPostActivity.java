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
import com.way.heard.adapters.PostAdapter;
import com.way.heard.models.Image;
import com.way.heard.models.Post;
import com.way.heard.services.LeanCloudDataService;
import com.way.heard.ui.views.autoloadrecyclerview.AutoLoadRecyclerView;
import com.way.heard.ui.views.autoloadrecyclerview.LoadMoreListener;
import com.way.heard.services.LeanCloudBackgroundTask;
import com.way.heard.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class UserPostActivity extends AppCompatActivity {
    private final static String TAG = UserPostActivity.class.getName();
    public final static String USER_DETAIL = "UserDetail";
    public final static String DISPLAY_TYPE = "DisplayType";
    public final static int DISPLAY_TYPE_PUBLIC = 1;
    public final static int DISPLAY_TYPE_ALL = 0;

    private Context context;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AutoLoadRecyclerView mRecyclerView;
    private RotateLoading loading;

    private String userobjectId;
    private int displayType = 1;
    private PostAdapter mAdapter;
    private int pageIndex = 1;
    private final static int pageSize = 5;
    private List<Post> mPosts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post);
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
            Bundle bundle = getIntent().getExtras();
            AVUser user = bundle.getParcelable(UserDisplayActivity.USER_DETAIL);
            userobjectId = user.getObjectId();
            displayType = getIntent().getIntExtra(UserPostActivity.DISPLAY_TYPE, 1);
            LogUtil.d(TAG, "getParamData debug, UserObjectId = " + userobjectId);
            LogUtil.d(TAG, "getParamData debug, DisplayType = " + displayType);
        } catch (Exception e) {
            LogUtil.e(TAG, "getParamData error", e);
        }
    }

    private void initView() {
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_user_post_swiperefreshlayout);
        this.mRecyclerView = (AutoLoadRecyclerView) findViewById(R.id.alrv_user_post_recyclerview);
        this.loading = (RotateLoading) findViewById(R.id.loading);
    }

    private void initData() {
        mPosts = new ArrayList<>();
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


        mAdapter = new PostAdapter(context);
        mAdapter.setOnImageClickListener(new PostAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(int pos) {
                LogUtil.d(TAG, "onImageClick debug, Position = " + pos);
                Post post = mPosts.get(pos);
                Image image = post.tryGetPhotoList().get(0);
                Intent intent = new Intent(context, ImageDisplayActivity.class);
                intent.putExtra(ImageDisplayActivity.IMAGE_POST_INDEX, pos);
                intent.putExtra(ImageDisplayActivity.IMAGE_DETAIL, image);
                startActivityForResult(intent, ImageDisplayActivity.IMAGE_DISPLAY_REQUEST);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        loadFirst();
    }

    public void loadFirst() {
        pageIndex = 1;
        LogUtil.d(TAG, "loadFirst debug, Page Index = " + pageIndex);
        loadDataByNetworkType();
    }

    public void loadNextPage() {
        pageIndex++;
        LogUtil.d(TAG, "loadNextPage debug, Page Index = " + pageIndex);
        loadDataByNetworkType();
    }

    private void loadDataByNetworkType() {
        new LeanCloudBackgroundTask(context) {

            @Override
            protected void onPre() {
                loading.start();
                mSwipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected void doInBack() throws AVException {
                List<Post> data = new ArrayList<Post>();
                if (displayType == DISPLAY_TYPE_PUBLIC) {//==1, public
                    data = LeanCloudDataService.getAnyPublicPostsByUserByPage(userobjectId, (pageIndex - 1) * pageSize, pageSize);
                } else {//==0, all
                    data = LeanCloudDataService.getAllPostsByUserByPage(userobjectId, (pageIndex - 1) * pageSize, pageSize);
                }
                if (mPosts == null) {
                    mPosts = new ArrayList<Post>();
                }
                if (pageIndex == 1) {
                    mPosts.clear();
                    mPosts = data;
                } else {
                    mPosts.addAll(data);
                }

            }

            @Override
            protected void onPost(AVException e) {
                mAdapter.setPosts(mPosts);
                mAdapter.notifyDataSetChanged();
                loading.stop();
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }.execute();
    }

    public static void go(Context context, AVUser user, int displayType) {
        Intent intent = new Intent(context, UserPostActivity.class);
        intent.putExtra(UserPostActivity.USER_DETAIL, user);
        intent.putExtra(UserPostActivity.DISPLAY_TYPE, displayType);
        context.startActivity(intent);
    }
}
