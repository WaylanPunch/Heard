package com.way.heard.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.LogUtil;
import com.way.heard.R;
import com.way.heard.ui.activities.NotificationActivity;
import com.way.heard.utils.PushMessageData.PushMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by pc on 2016/6/19.
 */
public class MyCustomReceiver extends BroadcastReceiver {
    private static final String TAG = MyCustomReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.log.d(TAG, "onReceive debug");
        try {
            String action = intent.getAction();
            String channel = intent.getExtras().getString("com.avos.avoscloud.Channel");
            LogUtil.log.d(TAG, "onReceive debug, Action = " + action);
            LogUtil.log.d(TAG, "onReceive debug, Channel = " + channel);

            JSONObject json = new JSONObject(intent.getExtras().getString("com.avos.avoscloud.Data"));
            final String message = json.getString("alert");

            savePushMessage("test", message);

            Intent resultIntent = new Intent(AVOSCloud.applicationContext, NotificationActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(AVOSCloud.applicationContext, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(AVOSCloud.applicationContext)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(AVOSCloud.applicationContext.getResources().getString(R.string.app_name))
                    .setContentText(message)
                    .setTicker(message);
            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setAutoCancel(true);

            int mNotificationId = 10086;
            NotificationManager mNotifyMgr = (NotificationManager) AVOSCloud.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        } catch (JSONException e) {
            Log.e(TAG, "onReceive error, JSONException: " + e.getMessage());
        }
    }

    private void savePushMessage(String username, String alert) {
        PushMessage message = new PushMessage();
        message.setMessageFrom(username);
        message.setMessageContent(alert);
        message.setDate(new Date());
        message.save();
    }
}
