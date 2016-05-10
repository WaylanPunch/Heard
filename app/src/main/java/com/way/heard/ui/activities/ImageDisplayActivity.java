package com.way.heard.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.victor.loading.rotate.RotateLoading;
import com.way.heard.R;
import com.way.heard.models.Image;
import com.way.heard.utils.GlideImageLoader;
import com.way.heard.utils.LeanCloudBackgroundTask;
import com.way.heard.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class ImageDisplayActivity extends AppCompatActivity {
    private final static String TAG = ImageDisplayActivity.class.getName();
    public final static String IMAGE_DETAIL = "ImageDetail";

    private ImageView ivPhoto;
    private TextView tvSave;
    private TextView tvLike;
    private Image photo;
    private RotateLoading loading;
    private List<String> likeObjectIDs;
    private boolean isLiked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        getParamData();
        initView();
    }

    private void getParamData() {
        Bundle bundle = getIntent().getExtras();
        photo = bundle.getParcelable(IMAGE_DETAIL);
        likeObjectIDs = photo.getLikes();
        if (likeObjectIDs == null) {
            likeObjectIDs = new ArrayList<>();
        }
        LogUtil.d(TAG, "getParamData debug, Image Url = " + photo.getUrl());
    }

    private void initView() {
        ivPhoto = (ImageView) findViewById(R.id.iv_image_photo);
        tvSave = (TextView) findViewById(R.id.tv_image_save);
        tvLike = (TextView) findViewById(R.id.tv_image_like);
        loading = (RotateLoading) findViewById(R.id.loading);

        if (likeObjectIDs.contains(AVUser.getCurrentUser().getObjectId())) {
            isLiked = true;
            Drawable drawable = getResources().getDrawable(R.drawable.ic_thumb_up_accent);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvLike.setCompoundDrawables(drawable, null, null, null);
        } else {
            isLiked = false;
        }
        GlideImageLoader.displayImage(ImageDisplayActivity.this, photo.getUrl(), ivPhoto);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SaveTask(ImageDisplayActivity.this).execute();
            }
        });
        tvLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LikeTask(ImageDisplayActivity.this).execute();
            }
        });
    }

    class LikeTask extends LeanCloudBackgroundTask {

        protected LikeTask(Context ctx) {
            super(ctx);
        }

        @Override
        protected void onPre() {
            loading.start();
        }

        @Override
        protected void doInBack() throws AVException {
            if (isLiked) {//already like
                likeObjectIDs.remove(AVUser.getCurrentUser().getObjectId());
                photo.setLikes(likeObjectIDs);
                photo.save();
                isLiked = false;
            } else {
                likeObjectIDs.add(AVUser.getCurrentUser().getObjectId());
                photo.setLikes(likeObjectIDs);
                photo.save();
                isLiked = true;
            }
        }

        @Override
        protected void onPost(AVException e) {
            loading.stop();
            if(isLiked){
                Drawable drawable = getResources().getDrawable(R.drawable.ic_thumb_up_accent);
                // 这一步必须要做,否则不会显示.
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tvLike.setCompoundDrawables(drawable, null, null, null);
            }else {
                Drawable drawable = getResources().getDrawable(R.drawable.ic_thumb_up_white);
                // 这一步必须要做,否则不会显示.
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tvLike.setCompoundDrawables(drawable, null, null, null);
            }
            if (e != null) {
                Toast.makeText(ImageDisplayActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    class SaveTask extends LeanCloudBackgroundTask {

        protected SaveTask(Context ctx) {
            super(ctx);
        }

        @Override
        protected void onPre() {
            loading.start();
        }

        @Override
        protected void doInBack() throws AVException {

        }

        @Override
        protected void onPost(AVException e) {
            loading.stop();
            if (e != null) {
                Toast.makeText(ImageDisplayActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void go(Context context, Image image) {
        Intent intent = new Intent(context, ImageDisplayActivity.class);
        intent.putExtra(ImageDisplayActivity.IMAGE_DETAIL, image);
        context.startActivity(intent);
    }
}
