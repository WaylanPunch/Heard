package com.way.heard.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVException;
import com.victor.loading.rotate.RotateLoading;
import com.way.heard.R;
import com.way.heard.adapters.PostAdapter;
import com.way.heard.models.Image;
import com.way.heard.models.Post;
import com.way.heard.services.LeanCloudBackgroundTask;
import com.way.heard.services.LeanCloudDataService;
import com.way.heard.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;

public class TopicResultActivity extends AppCompatActivity {
    private final static String TAG = TopicResultActivity.class.getName();
    public final static String TAG_DETAIL = "TagDetail";
    public static final int POST_REPOST_REQUEST = 1014;

    private String tag;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private RotateLoading loading;
    private List<Post> searchPosts;
    private PostAdapter searchPostAdapter;
    private LeanCloudBackgroundTask backgroundTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_result);
        getParamData();
        initToolBar();
        initView();
        getSearchResult(tag);
    }

    private void getParamData() {
        try {
            Bundle bundle = getIntent().getExtras();
            tag = bundle.getString(TopicResultActivity.TAG_DETAIL);
        } catch (Exception e) {
            LogUtil.e(TAG, "getParamData error", e);
        }
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(tag);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.rv_topic_result_list);
        loading = (RotateLoading) findViewById(R.id.loading);

        searchPosts = new ArrayList<>();
        searchPostAdapter = new PostAdapter(TopicResultActivity.this);
        searchPostAdapter.setPosts(searchPosts);
        searchPostAdapter.setOnDeleteClickListener(new PostAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(final int pos) {
                LogUtil.d(TAG, "onDeleteClick debug, Position = " + pos);
                new MaterialDialog.Builder(TopicResultActivity.this)
                        .title("Delete")
                        .content("Delete This Post?")
                        .positiveText("OK")
                        .negativeText("CANCEL")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Post post = searchPosts.get(pos);
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
        searchPostAdapter.setOnImageClickListener(new PostAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(int pos) {
                LogUtil.d(TAG, "onImageClick debug, Position = " + pos);
                Post post = searchPosts.get(pos);
                if (0 == post.getFrom()) {
                    Image image = post.tryGetPhotoList().get(0);
                    Intent intent = new Intent(TopicResultActivity.this, ImageDisplayActivity.class);
                    intent.putExtra(ImageDisplayActivity.IMAGE_POST_INDEX, pos);
                    intent.putExtra(ImageDisplayActivity.IMAGE_DETAIL, image);
                    startActivityForResult(intent, ImageDisplayActivity.IMAGE_DISPLAY_REQUEST);
                } else {
                    Post postOriginal = post.tryGetPostOriginal();
                    Image image = postOriginal.tryGetPhotoList().get(0);
                    Intent intent = new Intent(TopicResultActivity.this, ImageDisplayActivity.class);
                    intent.putExtra(ImageDisplayActivity.IMAGE_POST_INDEX, pos);
                    intent.putExtra(ImageDisplayActivity.IMAGE_DETAIL, image);
                    startActivityForResult(intent, ImageDisplayActivity.IMAGE_DISPLAY_REQUEST);
                }
            }
        });
        searchPostAdapter.setOnRepostClickListener(new PostAdapter.OnRepostClickListener() {
            @Override
            public void onRepostClick(int pos) {
                LogUtil.d(TAG, "onRepostClick debug, Position = " + pos);
                Post post = searchPosts.get(pos);
                if (0 == post.getFrom()) {
                    Intent intent = new Intent(TopicResultActivity.this, RepostActivity.class);
                    intent.putExtra(RepostActivity.REPOST_FROM, post.getFrom());
                    intent.putExtra(RepostActivity.POST_ORIGINAL_DETAIL, post);
                    intent.putExtra(RepostActivity.PHOTO_ORIGINAL_LIST, (ArrayList) post.tryGetPhotoList());
                    startActivityForResult(intent, POST_REPOST_REQUEST);
                } else {
                    Intent intent = new Intent(TopicResultActivity.this, RepostActivity.class);
                    intent.putExtra(RepostActivity.REPOST_FROM, post.getFrom());
                    intent.putExtra(RepostActivity.REPOST_DETAIL, post);
                    intent.putExtra(RepostActivity.POST_ORIGINAL_DETAIL, post.tryGetPostOriginal());
                    intent.putExtra(RepostActivity.PHOTO_ORIGINAL_LIST, (ArrayList) post.tryGetPostOriginal().tryGetPhotoList());
                    startActivityForResult(intent, POST_REPOST_REQUEST);
                }
            }
        });
        recyclerView.setAdapter(searchPostAdapter);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(TopicResultActivity.this));
    }


    private void getSearchResult(final String tag) {
        LogUtil.d(TAG, "getSearchResult debug");
        backgroundTask = new LeanCloudBackgroundTask(TopicResultActivity.this) {

            @Override
            protected void onPre() {
                loading.start();
            }

            @Override
            protected void doInBack() throws AVException {
                searchPosts = LeanCloudDataService.getAnyPublicPostsByTag(tag);
                if (searchPosts == null) {
                    searchPosts = new ArrayList<Post>();
                }
            }

            @Override
            protected void onPost(AVException e) {
                loading.stop();
                LogUtil.d(TAG, "getSearchResult debug, Result Count = " + searchPosts.size());
                if (e == null) {
                    searchPostAdapter.setPosts(searchPosts);
                    searchPostAdapter.notifyDataSetChanged();
                    if (searchPosts != null && searchPosts.size() > 0) {
                        LogUtil.d(TAG, "getSearchResult debug, View.VISIBLE");
                        TranslateAnimation slide = new TranslateAnimation(0, 0, recyclerView.getHeight(), 0);
                        slide.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                recyclerView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        slide.setDuration(300);
                        recyclerView.startAnimation(slide);
                        recyclerView.setVerticalScrollbarPosition(0);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    Toast.makeText(TopicResultActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected void onCancel() {
                loading.stop();
            }
        };

        backgroundTask.execute();
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
                        searchPosts.remove(pos);
                        searchPostAdapter.setPosts(searchPosts);
                        searchPostAdapter.notifyDataSetChanged();
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

    public static void go(Context context, String tag) {
        Intent intent = new Intent(context, TopicResultActivity.class);
        intent.putExtra(TopicResultActivity.TAG_DETAIL, tag);
        context.startActivity(intent);
    }
}
