package com.way.heard.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.way.heard.R;
import com.way.heard.adapters.PhotoViewAdapter;
import com.way.heard.ui.views.TagCloudView;
import com.way.heard.utils.GlideLoader;
import com.way.heard.utils.LogUtil;
import com.yancy.imageselector.ImageConfig;
import com.yancy.imageselector.ImageSelector;
import com.yancy.imageselector.ImageSelectorActivity;

import java.util.ArrayList;
import java.util.List;

public class EditPostActivity extends AppCompatActivity {
    private final static String TAG = EditPostActivity.class.getName();
    public final static int REQUEST_CODE = 1000;

    private EditText etContent;
    private ImageView ivPhoto;
    private RecyclerView rvPhotoList;
    private ImageView ivLocation;
    private TagCloudView tcvTags;
    private EditText etTag;
    private CheckBox cbPrivate;


    private PhotoViewAdapter adapter;

    private ArrayList<String> photopaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();
        initData();
    }

    private void initView() {
        etContent = (EditText) findViewById(R.id.et_post_content);
        etTag = (EditText) findViewById(R.id.et_post_tag);
        ivPhoto = (ImageView) findViewById(R.id.iv_post_photo);
        ivLocation = (ImageView) findViewById(R.id.iv_post_location);
        rvPhotoList = (RecyclerView) findViewById(R.id.rv_post_photo_list);
        tcvTags = (TagCloudView) findViewById(R.id.tcv_post_tags);
        cbPrivate = (CheckBox) findViewById(R.id.cb_post_private);
    }

    private void initData() {

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.d(TAG, "initData debug, OnClick");
                try {
                    Uri uri = Uri.parse("content://media/external/images/media");
                    grantUriPermission(getPackageName(), uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    LogUtil.d(TAG, "initData debug, grantUriPermission()");
                    ImageConfig imageConfig
                            = new ImageConfig.Builder(
                            // GlideLoader 可用自己用的缓存库
                            new GlideLoader())
                            // 如果在 4.4 以上，则修改状态栏颜色 （默认黑色）
                            .steepToolBarColor(getResources().getColor(R.color.blue))
                            // 标题的背景颜色 （默认黑色）
                            .titleBgColor(getResources().getColor(R.color.blue))
                            // 提交按钮字体的颜色  （默认白色）
                            .titleSubmitTextColor(getResources().getColor(R.color.white))
                            // 标题颜色 （默认白色）
                            .titleTextColor(getResources().getColor(R.color.white))
                            // 开启多选   （默认为多选）  (单选 为 singleSelect)
                            //.singleSelect()
                            //.crop()
                            // 多选时的最大数量   （默认 9 张）
                            .mutiSelectMaxSize(9)
                            // 已选择的图片路径
                            .pathList(photopaths)
                            // 拍照后存放的图片路径（默认 /temp/picture）
                            .filePath("/Heard/Pictures")
                            // 开启拍照功能 （默认开启）
                            //.showCamera()
                            .requestCode(REQUEST_CODE)
                            .build();


                    ImageSelector.open(EditPostActivity.this, imageConfig);   // 开启图片选择器
                }catch (Exception e){
                    Toast.makeText(EditPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rvPhotoList.setLayoutManager(gridLayoutManager);
        adapter = new PhotoViewAdapter(this, photopaths);
        rvPhotoList.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);

            for (String path : pathList) {
                Log.i("ImagePathList", path);
            }

            photopaths.clear();
            photopaths.addAll(pathList);
            adapter.notifyDataSetChanged();


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_publish:
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
