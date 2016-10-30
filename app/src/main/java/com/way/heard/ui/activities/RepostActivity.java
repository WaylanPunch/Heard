package com.way.heard.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkOnClickListener;
import com.luseen.autolinklibrary.AutoLinkTextView;
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

    private Toolbar toolbar;
    private Context context;
    private EditText etContent;
    private ImageView ivOrinPhoto;
    private AutoLinkTextView altv_repost_original_username;
    private AutoLinkTextView altv_repost_original_content;
    private RotateLoading loading;

    private int repostFrom;
    private Post originalPost;
    List<Image> originalPhotos;
    private Post rePost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repost);
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
        getSupportActionBar().setTitle("Repost");
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
        altv_repost_original_username = (AutoLinkTextView) findViewById(R.id.altv_repost_original_username);
        altv_repost_original_content = (AutoLinkTextView) findViewById(R.id.altv_repost_original_content);
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
            altv_repost_original_username.addAutoLinkMode(
                    AutoLinkMode.MODE_HASHTAG,
                    AutoLinkMode.MODE_PHONE,
                    AutoLinkMode.MODE_URL,
                    AutoLinkMode.MODE_MENTION,
                    AutoLinkMode.MODE_CUSTOM);
            altv_repost_original_username.setHashtagModeColor(ContextCompat.getColor(context, R.color.colorTextHashTag));
            altv_repost_original_username.setPhoneModeColor(ContextCompat.getColor(context, R.color.colorTextPhone));
            altv_repost_original_username.setCustomModeColor(ContextCompat.getColor(context, R.color.colorTextCustom));
            altv_repost_original_username.setMentionModeColor(ContextCompat.getColor(context, R.color.colorTextMention));
            altv_repost_original_username.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
                @Override
                public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                    showDialog(matchedText, "Mode is: " + autoLinkMode.toString());
                }
            });
            altv_repost_original_username.setAutoLinkText("@" + username);
            altv_repost_original_content.addAutoLinkMode(
                    AutoLinkMode.MODE_HASHTAG,
                    AutoLinkMode.MODE_PHONE,
                    AutoLinkMode.MODE_URL,
                    AutoLinkMode.MODE_MENTION,
                    AutoLinkMode.MODE_CUSTOM);
            altv_repost_original_content.setHashtagModeColor(ContextCompat.getColor(context, R.color.colorTextHashTag));
            altv_repost_original_content.setPhoneModeColor(ContextCompat.getColor(context, R.color.colorTextPhone));
            altv_repost_original_content.setCustomModeColor(ContextCompat.getColor(context, R.color.colorTextCustom));
            altv_repost_original_content.setMentionModeColor(ContextCompat.getColor(context, R.color.colorTextMention));
            altv_repost_original_content.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
                @Override
                public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                    showDialog(matchedText, "Mode is: " + autoLinkMode.toString());
                }
            });
            altv_repost_original_content.setAutoLinkText(content);
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

    private void showDialog(String title, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
