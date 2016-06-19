package com.way.heard.base;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.PushService;
import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Model;
import com.tencent.bugly.crashreport.CrashReport;
import com.way.heard.R;
import com.way.heard.models.Article;
import com.way.heard.models.Comment;
import com.way.heard.models.Image;
import com.way.heard.models.Post;
import com.way.heard.models.Tag;
import com.way.heard.services.CustomUserProvider;
import com.way.heard.ui.activities.NotificationActivity;
import com.way.heard.utils.LogQuickSearchData.LogQuickSearch;
import com.way.heard.utils.LogUtil;
import com.way.heard.utils.PushMessageData.PushMessage;

import java.util.ArrayList;
import java.util.List;

import cn.leancloud.chatkit.LCChatKit;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by pc on 2016/4/19.
 */
public class HeardApp extends Application {
    private final static String TAG = HeardApp.class.getName();

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "onCreate debug");
        mContext = this;

        // Tencent Bugly
        CrashReport.initCrashReport(getApplicationContext(), CONFIG.BuglyAppId, false);

        // 关于 CustomUserProvider 可以参看后面的文档
        LCChatKit.getInstance().setProfileProvider(CustomUserProvider.getInstance());
        LCChatKit.getInstance().init(mContext, CONFIG.LeanCloudAppID, CONFIG.LeanCloudAppKey);

        // 初始化参数依次为 this, AppId, AppKey
        //AVOSCloud.initialize(mContext, CONFIG.LeanCloudAppID, CONFIG.LeanCloudAppKey);
        AVObject.registerSubclass(Article.class);
        AVObject.registerSubclass(Comment.class);
        AVObject.registerSubclass(Post.class);
        AVObject.registerSubclass(Image.class);
        AVObject.registerSubclass(Tag.class);
        // 应该放在 Application 的 onCreate 中，开启调式日志打印
        AVOSCloud.setDebugLogEnabled(CONFIG.LeanCloudIsDebugLogEnabled);
        // 应该放在 Application 的 onCreate 中，开启全局省流量模式
        AVOSCloud.setLastModifyEnabled(CONFIG.LeanCloudIsLastModifyEnabled);
        // 应该放在 Application 的 onCreate 中，设置网络超时限制
        AVOSCloud.setNetworkTimeout(AVOSCloud.DEFAULT_NETWORK_TIMEOUT);//15秒的网络超时限制

        initPushService(true);

        // Font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/RobotoSlab-Thin.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        initializeDatabase();
    }

    private void initPushService(boolean isSubscribed) {
        if (isSubscribed) {
            // 设置默认打开的 Activity
            PushService.setDefaultPushCallback(this, NotificationActivity.class);
            // 订阅频道，当该频道消息到来的时候，打开对应的 Activity
            // 参数依次为：当前的 context、频道名称、回调对象的类
            PushService.subscribe(this, CONFIG.Push_Channel_Public, NotificationActivity.class);
            PushService.subscribe(this, CONFIG.Push_Channel_Private, NotificationActivity.class);
            PushService.subscribe(this, CONFIG.Push_Channel_Protected, NotificationActivity.class);
        } else {
            PushService.unsubscribe(mContext, CONFIG.Push_Channel_Protected);
            //退订之后需要重新保存 Installation
            AVInstallation.getCurrentInstallation().saveInBackground();
        }
    }

    public static Context getContext() {
        return mContext;
    }

    private void initializeDatabase() {
        List<Class<? extends Model>> models = new ArrayList<>(0);
        models.add(LogQuickSearch.class);
        models.add(PushMessage.class);
        String dbName = CONFIG.DATABASE_NAME;
        DatabaseAdapter.setDatabaseName(dbName);
        DatabaseAdapter adapter = new DatabaseAdapter(mContext);
        adapter.setModels(models);
    }
}
