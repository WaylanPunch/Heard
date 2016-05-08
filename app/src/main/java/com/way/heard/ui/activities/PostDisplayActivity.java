package com.way.heard.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.victor.loading.rotate.RotateLoading;
import com.way.heard.R;
import com.way.heard.adapters.CommentAdapter;
import com.way.heard.models.Comment;
import com.way.heard.models.Post;
import com.way.heard.ui.views.autoloadrecyclerview.AutoLoadRecyclerView;
import com.way.heard.ui.views.autoloadrecyclerview.LoadMoreListener;
import com.way.heard.utils.LeanCloudBackgroundTask;
import com.way.heard.utils.LeanCloudHelper;
import com.way.heard.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class PostDisplayActivity extends AppCompatActivity {
    private final static String TAG = PostDisplayActivity.class.getName();

    public final static String POST_DETAIL = "PostDetail";
    private static String postObjectID;

    private Context context;
    private LinearLayout llCommentContainer;
    private EditText etCommentContent;
    private ImageView ivCommentSend;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AutoLoadRecyclerView mRecyclerView;
    private RotateLoading loading;

    private CommentAdapter mAdapter;
    private int pageIndex = 1;
    private final static int pageSize = 15;
    private List<Comment> mComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_display);

        context = this;
        getParamData();
        setToolBar();
        initView();
        initData();
    }

    private void getParamData() {
        try {
            Bundle bundle = getIntent().getExtras();
            Post param = bundle.getParcelable(PostDisplayActivity.POST_DETAIL);
            postObjectID = param.getObjectId();
            LogUtil.d(TAG, "getParamData debug, Post Object ID = " + postObjectID);
        } catch (Exception e) {
            LogUtil.e(TAG, "getParamData error", e);
        }
    }

    private void setToolBar() {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            //mToolbar.setNavigationIcon(R.drawable.btn_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "onCreate error", e);
        }
    }

    private void initView() {
        try {
            this.llCommentContainer = (LinearLayout) findViewById(R.id.ll_post_display_commentcontainer);
            this.etCommentContent = (EditText) findViewById(R.id.et_post_display_commentcontent);
            this.ivCommentSend = (ImageView) findViewById(R.id.iv_post_display_commentsend);

            this.mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_post_display_swiperefreshlayout);
            this.mRecyclerView = (AutoLoadRecyclerView) findViewById(R.id.alrv_post_display_recyclerview);
            this.loading = (RotateLoading) findViewById(R.id.loading);
        } catch (Exception e) {
            LogUtil.e(TAG, "onCreate error", e);
        }
    }

    private void initData() {
        ivCommentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etCommentContent.getText().toString())) {
                    Toast.makeText(PostDisplayActivity.this, "No Comment", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendComment(postObjectID, etCommentContent.getText().toString(), null, null);
                etCommentContent.setText("");
            }
        });


        mComments = new ArrayList<>();
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLoadMoreListener(new LoadMoreListener() {
            @Override
            public void loadMore() {
                loadNextPage();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFirst();
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setOnPauseListenerParams(context, false, true);

        mAdapter = new CommentAdapter(context, mComments);
        mAdapter.setOnCommentReplyListener(new CommentAdapter.OnCommentReplyListener() {
            @Override
            public void onCommentReply(int position) {
                Comment item = mAdapter.getComments().get(position);
                etCommentContent.setText("");
                etCommentContent.setHint("Reply to @" + item.getAuthor().getUsername() + ":");
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
                List<Comment> data = LeanCloudHelper.getAllCommentsByPostObjectID(postObjectID, (pageIndex - 1) * pageSize, pageSize);
                if (mComments == null) {
                    mComments = new ArrayList<Comment>();
                }
                if (pageIndex == 1) {
                    mComments.clear();
                    mComments = data;
                } else {
                    mComments.addAll(data);
                }
            }

            @Override
            protected void onPost(AVException e) {
                mAdapter.setComments(mComments);
                mAdapter.notifyDataSetChanged();
                loading.stop();
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }.execute();
    }

    private void sendComment(final String postID, final String content, final AVUser replyTo, final Comment replyFor) {
        new LeanCloudBackgroundTask(context) {

            @Override
            protected void onPre() {
                loading.start();
            }

            @Override
            protected void doInBack() throws AVException {
                Comment comment = LeanCloudHelper.saveComment(postID, content, replyTo, replyFor);
                if (comment != null) {
                    mComments.add(0, comment);
                }
            }

            @Override
            protected void onPost(AVException e) {
                mAdapter.setComments(mComments);
                mAdapter.notifyDataSetChanged();
                loading.stop();
            }
        }.execute();
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_publish:
                showCommentContainer();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showCommentContainer() {
        if (llCommentContainer.getVisibility() == View.GONE) {
            llCommentContainer.setVisibility(View.VISIBLE);
        } else {
            llCommentContainer.setVisibility(View.GONE);
        }
    }
    */

    public static void go(Context context, Post post) {
        Intent intent = new Intent(context, PostDisplayActivity.class);
        intent.putExtra(PostDisplayActivity.POST_DETAIL, post);
        context.startActivity(intent);
    }
}
