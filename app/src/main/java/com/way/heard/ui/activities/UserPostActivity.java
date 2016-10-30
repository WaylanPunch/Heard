package com.way.heard.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.victor.loading.rotate.RotateLoading;
import com.way.heard.R;
import com.way.heard.adapters.PostAdapter;
import com.way.heard.models.Image;
import com.way.heard.models.Post;
import com.way.heard.services.LeanCloudBackgroundTask;
import com.way.heard.services.LeanCloudDataService;
import com.way.heard.ui.views.autoloadrecyclerview.AutoLoadRecyclerView;
import com.way.heard.ui.views.autoloadrecyclerview.LoadMoreListener;
import com.way.heard.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class UserPostActivity extends BaseActivity {
    private final static String TAG = UserPostActivity.class.getName();
    public final static String USER_DETAIL = "UserDetail";
    public final static String DISPLAY_TYPE = "DisplayType";
    public final static int DISPLAY_TYPE_PUBLIC = 1;
    public final static int DISPLAY_TYPE_ALL = 0;

    private Context context;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AutoLoadRecyclerView mRecyclerView;
    private RotateLoading loading;
    private Toolbar toolbar;
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
        getParamData();
        initView();
        initData();
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Post");
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


        mAdapter = new PostAdapter(context);
        mAdapter.setOnDeleteClickListener(new PostAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(final int pos) {
                LogUtil.d(TAG, "onDeleteClick debug, Position = " + pos);
                new MaterialDialog.Builder(UserPostActivity.this)
                        .title("Delete")
                        .content("Delete This Post?")
                        .positiveText("OK")
                        .negativeText("CANCEL")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Post post = mPosts.get(pos);
                                if (post != null) {
                                    String objectid = post.getObjectId();
                                    deletePostAndComment(pos, objectid);
                                }
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
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
        loadFirst(true);
    }

    public void loadFirst(boolean isFirstTime) {
        pageIndex = 1;
        LogUtil.d(TAG, "loadFirst debug, Page Index = " + pageIndex);
        loadDataByNetworkType(isFirstTime);
    }

    public void loadNextPage(boolean isFirstTime) {
        pageIndex++;
        LogUtil.d(TAG, "loadNextPage debug, Page Index = " + pageIndex);
        loadDataByNetworkType(isFirstTime);
    }

    private void loadDataByNetworkType(final boolean isFirstTime) {
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
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }.execute();
    }

    private void deletePostAndComment(final int pos, final String objectid) {
        new LeanCloudBackgroundTask(context) {
            boolean isOK;

            @Override
            protected void onPre() {

            }

            @Override
            protected void doInBack() throws AVException {
                isOK = LeanCloudDataService.deletePostByObjectID(objectid);
            }

            @Override
            protected void onPost(AVException e) {
                if (e == null) {
                    if (isOK) {
                        mPosts.remove(pos);
                        mAdapter.setPosts(mPosts);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "You're not able to delete!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected void onCancel() {

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
