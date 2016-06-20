package com.way.heard.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVUser;
import com.way.heard.R;
import com.way.heard.adapters.PushMessageAdapter;
import com.way.heard.utils.LogUtil;
import com.way.heard.utils.PushMessageData.PushMessage;

import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private static final String TAG = NotificationActivity.class.getName();

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private List<PushMessage> messages;
    private PushMessageAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        LogUtil.d(TAG, "onCreate debug");

        initToolBar();
        initFloatingActionButton();
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

    private void initFloatingActionButton() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        String testAdminUsername = AVUser.getCurrentUser().getUsername();
        if (testAdminUsername.equalsIgnoreCase("test")) {// == test
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NotificationSendActivity.go(NotificationActivity.this);
                }
            });
        } else {// != test
            fab.setVisibility(View.GONE);
        }
    }

    private void initView() {
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
        if (id == R.id.action_notification_clearall) {
            int count = messages == null ? 0 : messages.size();
            new MaterialDialog.Builder(NotificationActivity.this)
                    .title("Clear All Notifications?")
                    .content(count+" " + "Notifications")
                    .positiveText("DELETE")
                    .negativeText("CANCEL")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            clearAllPushMessage();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    }).show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearAllPushMessage() {
        if(messages!=null&&messages.size()>0){
            for(PushMessage item :messages){
                item.delete();
            }
        }
        refreshData();
    }


    public static void go(Context context) {
        Intent intent = new Intent(context, NotificationActivity.class);
        context.startActivity(intent);
    }

}
