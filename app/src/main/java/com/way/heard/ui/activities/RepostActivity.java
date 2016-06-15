package com.way.heard.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.victor.loading.rotate.RotateLoading;
import com.way.heard.R;
import com.way.heard.models.Image;
import com.way.heard.models.Post;
import com.way.heard.services.LeanCloudBackgroundTask;
import com.way.heard.services.LeanCloudDataService;
import com.way.heard.utils.GlideImageLoader;
import com.way.heard.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class RepostActivity extends BaseActivity {
    private final static String TAG = RepostActivity.class.getName();

    public final static String POST_ORIGINAL_DETAIL = "PostDetail";
    public final static String PHOTO_ORIGINAL_LIST = "PhotoList";
    public final static String REPOST_FROM = "RepostFrom";
    public final static String REPOST_DETAIL = "RepostDetail";

    private Context context;
    private EditText etContent;
    private ImageView ivOrinPhoto;
    private TextView tvOrinUsername;
    private TextView tvOrinContetnt;
    private RotateLoading loading;

    private int repostFrom;
    private Post originalPost;
    List<Image> originalPhotos;
    private Post rePost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repost);
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
            originalPost = bundle.getParcelable(RepostActivity.POST_ORIGINAL_DETAIL);
            originalPhotos = (ArrayList) bundle.getSerializable(RepostActivity.PHOTO_ORIGINAL_LIST);
            repostFrom = bundle.getInt(RepostActivity.REPOST_FROM);
            if (1 == repostFrom) {
                rePost = bundle.getParcelable(RepostActivity.REPOST_DETAIL);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "getParamData error", e);
        }
    }

    private void initView() {
        loading = (RotateLoading) findViewById(R.id.loading);
        etContent = (EditText) findViewById(R.id.et_repost_content);
        ivOrinPhoto = (ImageView) findViewById(R.id.iv_repost_original_photo);
        tvOrinUsername = (TextView) findViewById(R.id.tv_repost_original_username);
        tvOrinContetnt = (TextView) findViewById(R.id.tv_repost_original_content);
    }

    private void initData() {
        try {
            if (0 == repostFrom) {
                LogUtil.d(TAG, "initData debug, getFrom = " + 0);
                etContent.setText("");
            } else {
                LogUtil.d(TAG, "initData debug, getFrom = " + 1);
                //getString(R.string.repost_content, rePost.getAuthor().getUsername(), rePost.getContent());
                etContent.setText(getString(R.string.repost_content, rePost.getAuthor().getUsername(), rePost.getContent()));
            }
            String username = originalPost.getAuthor().getUsername();
            String photoUrl = "";
            //List<Image> photos = post.tryGetPhotoList();
            if (originalPhotos != null && originalPhotos.size() > 0) {
                photoUrl = originalPhotos.get(0).getThumbnailurl();
            }
            LogUtil.d(TAG, "initData debug, Photo Url = " + photoUrl);
            String content = originalPost.getContent();
            tvOrinUsername.setText(username);
            tvOrinContetnt.setText(content);
            if (!TextUtils.isEmpty(photoUrl)) {
                GlideImageLoader.displayImage(context, photoUrl, ivOrinPhoto);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "initData error", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_repost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_repost:
                repost();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void repost() {
        new LeanCloudBackgroundTask(RepostActivity.this) {

            @Override
            protected void onPre() {
                loading.start();
            }

            @Override
            protected void doInBack() throws AVException {
                String content = etContent.getText().toString();
                if (0 == repostFrom) {
                    LeanCloudDataService.saveRepost(AVUser.getCurrentUser(), originalPost.getAuthor(), originalPost, originalPost, content, false);
                } else {
                    LeanCloudDataService.saveRepost(AVUser.getCurrentUser(), rePost.getAuthor(), rePost, originalPost, content, false);
                }
            }

            @Override
            protected void onPost(AVException e) {
                loading.stop();
                setResult(RESULT_OK);
                if (e == null) {
                    Toast.makeText(RepostActivity.this, "Done", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RepostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected void onCancel() {
                loading.stop();
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    /*
    public static void go(Context context, Post repost, Post originalpost) {
        if (0 == repost.getFrom()) {
            Intent intent = new Intent(context, RepostActivity.class);
            intent.putExtra(RepostActivity.REPOST_FROM, repost.getFrom());
            intent.putExtra(RepostActivity.POST_ORIGINAL_DETAIL, repost);
            intent.putExtra(RepostActivity.PHOTO_ORIGINAL_LIST, (ArrayList) repost.tryGetPhotoList());
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context, RepostActivity.class);
            intent.putExtra(RepostActivity.REPOST_FROM, repost.getFrom());
            intent.putExtra(RepostActivity.REPOST_DETAIL, repost);
            intent.putExtra(RepostActivity.POST_ORIGINAL_DETAIL, originalpost);
            intent.putExtra(RepostActivity.PHOTO_ORIGINAL_LIST, (ArrayList) originalpost.tryGetPhotoList());
            context.startActivity(intent);
        }

    }
    */
}
