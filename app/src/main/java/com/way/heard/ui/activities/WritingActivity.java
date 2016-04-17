package com.way.heard.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.way.heard.R;
import com.way.heard.ui.views.TagCloudView;
import com.way.heard.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class WritingActivity extends ActionBarActivity {
    private final static String TAG = WritingActivity.class.getName();

    private List<String> tagsData = null;
    private EditText etTitle;
    private TagCloudView tcvTags;
    private EditText etTags;
    private ImageView ivAddTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        setToolBar();
        initData();
        initView();
    }


    private void setToolBar() {
        LogUtil.d(TAG, "setToolBar debug");
        Toolbar mToolbar = (Toolbar) findViewById(R.id.tb_writing_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //mToolbar.setNavigationIcon(R.drawable.btn_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initData() {
        tagsData = new ArrayList<>();
    }

    private void initView() {
        etTitle = (EditText) findViewById(R.id.et_writing_title);
        tcvTags = (TagCloudView) findViewById(R.id.tcv_writing_tags);
        etTags = (EditText) findViewById(R.id.et_writing_tags);
        ivAddTags = (ImageView) findViewById(R.id.iv_writing_addtags);

        ivAddTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etTags.getText())) {
                    Toast.makeText(WritingActivity.this, "No Tag Input!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(tagsData.size() > 4){
                    Toast.makeText(WritingActivity.this, "Only Five Tags Allowed!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String tagStr = etTags.getText().toString();
                tagsData.add(tagStr);
                tcvTags.setTags(tagsData);
                etTags.setText("");
            }
        });

        tcvTags.setTags(tagsData);
        tcvTags.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
            @Override
            public void onTagClick(int position) {
                if (position == -1) {
                    Toast.makeText(WritingActivity.this, "点击末尾文字",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WritingActivity.this, "点击 position : " + position,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        tcvTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(WritingActivity.this, "TagView onClick",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_writing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_publish:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
