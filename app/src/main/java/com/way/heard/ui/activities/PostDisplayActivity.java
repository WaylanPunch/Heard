package com.way.heard.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.victor.loading.rotate.RotateLoading;
import com.way.heard.R;
import com.way.heard.adapters.CommentAdapter;
import com.way.heard.adapters.PostAdapter;
import com.way.heard.models.Comment;
import com.way.heard.models.Post;
import com.way.heard.services.LeanCloudBackgroundTask;
import com.way.heard.services.LeanCloudDataService;
import com.way.heard.ui.views.HidingScrollBottomListener;
import com.way.heard.ui.views.RecycleViewDivider;
import com.way.heard.ui.views.autoloadrecyclerview.AutoLoadRecyclerView;
import com.way.heard.ui.views.autoloadrecyclerview.LoadMoreListener;
import com.way.heard.utils.LogUtil;
import com.way.heard.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class PostDisplayActivity extends BaseActivity {
    private final static String TAG = PostDisplayActivity.class.getName();

    public final static String POST_DETAIL = "PostDetail";
    private static String postObjectID;

    private Toolbar toolbar;
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

    private AVUser replyToUser;
    private Comment replyForComment;
    private Post post;

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
            post = bundle.getParcelable(PostDisplayActivity.POST_DETAIL);
            postObjectID = post.getObjectId();
            LogUtil.d(TAG, "getParamData debug, Post Object ID = " + postObjectID);
        } catch (Exception e) {
            LogUtil.e(TAG, "getParamData error", e);
        }
    }

    private void setToolBar() {
        try {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            //getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setDisplayShowTitleEnabled(false);
            //mToolbar.setNavigationIcon(R.drawable.btn_back);
            getSupportActionBar().setTitle("Comment");
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
                sendComment(postObjectID, etCommentContent.getText().toString(), replyToUser, replyForComment);
                etCommentContent.setText("");
                etCommentContent.setHint("Anything you want to say...");
                replyToUser = null;
                replyForComment = null;
            }
        });


        mComments = new ArrayList<>();
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLoadMoreListener(new LoadMoreListener() {
            @Override
            public void loadMore() {
                loadNextPage(false);
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFirst(false);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setOnPauseListenerParams(context, false, true);

        mAdapter = new CommentAdapter(context, mComments);
        mAdapter.setOnCommentItemReplyListener(new CommentAdapter.OnCommentItemReplyListener() {
            @Override
            public void onCommentItemReply(int position) {
                showViews();
                Comment item = mAdapter.getComments().get(position);
                etCommentContent.setText("");
                etCommentContent.setHint("Reply to @" + item.getAuthor().getUsername() + ":");
                replyToUser = item.getAuthor();
                replyForComment = item;
            }
        });
        mAdapter.setOnCommentItemClickListener(new CommentAdapter.OnCommentItemClickListener() {
            @Override
            public void onCommentItemClick(int position) {
                showViews();
                etCommentContent.setText("");
                etCommentContent.setHint("Anything you want to say...");
                replyToUser = null;
                replyForComment = null;
            }
        });
        mAdapter.setOnAutoLinkTextViewClickListener(new CommentAdapter.OnAutoLinkTextViewClickListener() {
            @Override
            public void onAutoLinkTextViewClick(AutoLinkMode autoLinkMode, String matchedText) {
                if (autoLinkMode == AutoLinkMode.MODE_CUSTOM) {

                } else if (autoLinkMode == AutoLinkMode.MODE_EMAIL) {
                    Util.startSendEmail(context, matchedText);
                } else if (autoLinkMode == AutoLinkMode.MODE_HASHTAG) {

                } else if (autoLinkMode == AutoLinkMode.MODE_MENTION) {
                    if (matchedText.startsWith("@")) {
//                            showDialog(matchedText.substring(1), "Mode is: " + autoLinkMode.toString());
                        findUserByUsername(matchedText.substring(1));
                    }
                } else if (autoLinkMode == AutoLinkMode.MODE_PHONE) {
                    Util.startCallPhone(context, matchedText);
                } else if (autoLinkMode == AutoLinkMode.MODE_URL) {
                    Util.startAccessUrl(context, matchedText);
                }
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecycleViewDivider(PostDisplayActivity.this, LinearLayoutManager.VERTICAL));
        //mRecyclerView.addItemDecoration(new RecycleViewDivider(PostDisplayActivity.this, LinearLayoutManager.VERTICAL, R.drawable.ic_divider_dots_vertical));
        mRecyclerView.addOnScrollListener(new HidingScrollBottomListener() {
            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }
        });
        loadFirst(true);
    }

    private void hideViews() {
        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) llCommentContainer.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        llCommentContainer.animate().translationY(llCommentContainer.getHeight() + fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    private void showViews() {
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        llCommentContainer.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
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
                List<Comment> data = LeanCloudDataService.getAllCommentsByPostObjectID(postObjectID, (pageIndex - 1) * pageSize, pageSize);
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

    private void sendComment(final String postID, final String content, final AVUser replyTo, final Comment replyFor) {
        new LeanCloudBackgroundTask(context) {

            @Override
            protected void onPre() {
                loading.start();
            }

            @Override
            protected void doInBack() throws AVException {
                LogUtil.d(TAG, "sendComment debug");
                Comment comment = LeanCloudDataService.saveComment(postID, content, replyTo, replyFor);
                LogUtil.d(TAG, "sendComment debug, Comment Count = " + mComments.size());
                if (comment != null) {
                    mComments.add(0, comment);
                }
                LogUtil.d(TAG, "sendComment debug, Comment Count = " + mComments.size());

                List<String> commentObjectIDs = new ArrayList<String>();
                for (Comment item : mComments) {
                    commentObjectIDs.add(item.getObjectId());
                }

                AVObject latestPost = AVObject.createWithoutData("Post", post.getObjectId()).fetchIfNeeded();
                latestPost.put(Post.COMMENTS, commentObjectIDs);
                latestPost.save();
//                post.setComments(commentObjectIDs);
//                post.save();
            }

            @Override
            protected void onPost(AVException e) {
                mAdapter.setComments(mComments);
                mAdapter.notifyDataSetChanged();
                loading.stop();
            }

            @Override
            protected void onCancel() {
                loading.stop();
            }
        }.execute();
    }

    private void findUserByUsername(final String usrname) {
        new LeanCloudBackgroundTask(context) {

            AVUser user;

            @Override
            protected void onPre() {

            }

            @Override
            protected void doInBack() throws AVException {
                user = LeanCloudDataService.getUserByUsername(usrname);
            }

            @Override
            protected void onPost(AVException e) {
                if (e == null) {
                    if (null != user) {
                        ProfileActivity.go(context, user);
                    } else {
                        Toast.makeText(context, "There Is No Such User!", Toast.LENGTH_SHORT).show();
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
            case R.id.action_reply:
                showViews();
                etCommentContent.setText("");
                etCommentContent.setHint("Anything you want to say...");
                replyToUser = null;
                replyForComment = null;
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
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
