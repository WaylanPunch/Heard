package com.way.heard.ui.activities;

import android.os.Bundle;
import android.widget.Button;

import com.way.heard.R;
import com.way.heard.ui.views.AdaptiveImageView;
import com.way.heard.utils.GlideImageLoader;

public class TestActivity extends BaseActivity {
    private final static String TAG = TestActivity.class.getName();
    private Button btnTest;
    private AdaptiveImageView ivTest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        btnTest = (Button) findViewById(R.id.btn_test);
        ivTest = (AdaptiveImageView) findViewById(R.id.iv_test);
        GlideImageLoader.displayImage(this, "http://img1.ph.126.net/G2936LeFDLbErI-n8c8bSw==/6631651606373252428.jpg", ivTest);
    }

}