package com.way.heard.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.SendCallback;
import com.way.heard.R;
import com.way.heard.base.CONFIG;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationSendActivity extends AppCompatActivity {
    private static final String TAG = NotificationSendActivity.class.getName();

    private Toolbar toolbar;
    private EditText etContent;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_send);

        initToolBar();
        initView();
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notification");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void initView() {
        etContent = (EditText) findViewById(R.id.et_notification_send_content);
        btnSend = (Button) findViewById(R.id.btn_notification_send_action);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etContent.getText().toString())) {
                    Toast.makeText(NotificationSendActivity.this, "No Message Will Be Sent.", Toast.LENGTH_SHORT).show();
                } else {
                    sendTestPushToAll();
                }
            }
        });
    }


//    private void initFloatingActionButton() {
//        fab = (FloatingActionButton) findViewById(R.id.fab);
//        String testAdminUsername = AVUser.getCurrentUser().getUsername();
//        if (testAdminUsername.equalsIgnoreCase("test")) {// == test
//            fab.setVisibility(View.VISIBLE);
//            fab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    sendTestPushToAll();
//                }
//            });
//        } else {// != test
//            fab.setVisibility(View.GONE);
//        }
//    }

    private void sendTestPushToAll() {
        try {
            AVPush push = new AVPush();
            JSONObject object = new JSONObject();
            object.put("action", "com.way.heard.ui.activities.NotificationActivity.action");
            object.put("alert", etContent.getText().toString());
            push.setPushToAndroid(true);
            push.setData(object);
            push.sendInBackground(new SendCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        Toast.makeText(NotificationSendActivity.this, "Push To All Successfully", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    } else {
                        Toast.makeText(NotificationSendActivity.this, "Push To All Failed," + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (JSONException e) {
            Toast.makeText(NotificationSendActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendTestPushToChannel() {
        try {
            AVQuery pushQuery = AVInstallation.getQuery();
            pushQuery.whereEqualTo("channels", CONFIG.Push_Channel_Public);
            AVPush push = new AVPush();
            push.setQuery(pushQuery);
            //push.setMessage("Push To Channel, push message to public channel");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("action", "com.way.heard.ui.activities.NotificationActivity.action");
            jsonObject.put("alert", "Push To Channel, push message to public channel");

            push.setData(jsonObject);
            push.setPushToAndroid(true);
            push.sendInBackground(new SendCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        Toast.makeText(NotificationSendActivity.this, "Push To Channel Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NotificationSendActivity.this, "Push To Channel Failed," + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (JSONException e) {
            Toast.makeText(NotificationSendActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendTestPushToUser() {
        AVQuery pushQuery = AVInstallation.getQuery();
        // 假设 THE_INSTALLATION_ID 是保存在用户表里的 installationId，
        // 可以在应用启动的时候获取并保存到用户表
        pushQuery.whereEqualTo("installationId", AVInstallation.getCurrentInstallation().getInstallationId());
        AVPush.sendMessageInBackground("Push To User, push message to installation", pushQuery, new SendCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Toast.makeText(NotificationSendActivity.this, "Push To User Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NotificationSendActivity.this, "Push To User Failed," + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void go(Context context) {
        Intent intent = new Intent(context, NotificationSendActivity.class);
        context.startActivity(intent);
    }
}
