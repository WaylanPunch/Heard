package com.way.heard.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.SendCallback;
import com.way.heard.R;
import com.way.heard.adapters.PushMessageAdapter;
import com.way.heard.base.CONFIG;
import com.way.heard.utils.LogUtil;
import com.way.heard.utils.PushMessageData.PushMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private static final String TAG = NotificationActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        LogUtil.d(TAG, "onCreate debug");

        initToolBar();

        initView();
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private RecyclerView recyclerView;
    private List<PushMessage> messages;
    private PushMessageAdapter adapter;

    private void initView() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllPushMessage();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.id_notification_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(NotificationActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PushMessageAdapter(NotificationActivity.this);
        recyclerView.setAdapter(adapter);

        //refreshData();
    }

    private void refreshData() {
        LogUtil.d(TAG, "refreshData debug");
        try {
            messages = PushMessage.all();
            adapter.setPushMessage(messages);
            adapter.notifyDataSetChanged();
            if (messages != null) {
                LogUtil.d(TAG, "refreshData debug, Message Count = " + messages.size());
            } else {
                LogUtil.d(TAG, "refreshData debug, Message == NULL ");
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "refreshData error", e);
        }
    }

    private void clearAllPushMessage() {

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.d(TAG, "onRestart debug");
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d(TAG, "onStart debug");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume debug");
        refreshData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d(TAG, "onPause debug");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d(TAG, "onStop debug");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy debug");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_notification_pushtoall) {
            sendTestPushToAll();
            return true;
        } else if (id == R.id.action_notification_pushtochannel) {
            sendTestPushToChannel();
            return true;
        } else if (id == R.id.action_notification_pushtouser) {
            sendTestPushToUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendTestPushToAll() {
        try {
            AVPush push = new AVPush();
            JSONObject object = new JSONObject();
            object.put("alert", "Push To All, push message to all android device directly");
            push.setPushToAndroid(true);
            push.setData(object);
            push.sendInBackground(new SendCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        Toast.makeText(NotificationActivity.this, "Push To All Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NotificationActivity.this, "Push To All Failed," + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (JSONException e) {
            Toast.makeText(NotificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(NotificationActivity.this, "Push To Channel Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NotificationActivity.this, "Push To Channel Failed," + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (JSONException e) {
            Toast.makeText(NotificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(NotificationActivity.this, "Push To User Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NotificationActivity.this, "Push To User Failed," + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void go(Context context) {
        Intent intent = new Intent(context, NotificationActivity.class);
        context.startActivity(intent);
    }

}
