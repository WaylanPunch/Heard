package com.way.heard.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.avos.avoscloud.AVUser;
import com.way.heard.R;
import com.way.heard.utils.LogUtil;

public class UserDisplayActivity extends AppCompatActivity {
    private final static String TAG = UserDisplayActivity.class.getName();

    public final static String USER_DETAIL = "UserDetail";

    private AVUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        getParamData();
    }

    private void getParamData() {
        try {
            Bundle bundle = getIntent().getExtras();
            user = bundle.getParcelable(UserDisplayActivity.USER_DETAIL);
            LogUtil.d(TAG, "getParamData debug, Username = " + user.getUsername());
            LogUtil.d(TAG, "getParamData debug, Avatar = " + user.getString("avatar"));
        } catch (Exception e) {
            LogUtil.e(TAG, "getParamData error", e);
        }
    }

    public static void go(Context context, AVUser user) {
        Intent intent = new Intent(context, UserDisplayActivity.class);
        intent.putExtra(UserDisplayActivity.USER_DETAIL, user);
        context.startActivity(intent);
    }


}
