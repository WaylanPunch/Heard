package com.way.heard.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.way.heard.R;
import com.way.heard.utils.LogUtil;

public class TestActivity extends ActionBarActivity {
    private final static String TAG = TestActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Button btnAdd = (Button) findViewById(R.id.btn_test_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 测试 SDK 是否正常工作的代码
                AVObject testObject = new AVObject("Article");
                testObject.put("title","Hello World!");
                testObject.put("content","Hello World! I Am David!");
                testObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if(e == null){
                            LogUtil.d(TAG, "onCreate debug, Add Data Successful");
                        }else {
                            LogUtil.e(TAG, "onCreate debug, Add Data Failed, " + e.getMessage());
                        }
                    }
                });
            }
        });
    }
}
