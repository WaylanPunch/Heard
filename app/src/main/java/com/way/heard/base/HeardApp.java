package com.way.heard.base;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.way.heard.utils.LogUtil;

import im.fir.sdk.FIR;

/**
 * Created by pc on 2016/4/19.
 */
public class HeardApp extends Application {
    private final static String TAG = HeardApp.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "onCreate debug");

        //BugHD
        FIR.init(this);
        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this, CONFIG.LeanCloudAppID, CONFIG.LeanCloudAppKey);
        // 应该放在 Application 的 onCreate 中，开启调式日志打印
        AVOSCloud.setDebugLogEnabled(CONFIG.LeanCloudIsDebugLogEnabled);
        // 应该放在 Application 的 onCreate 中，开启全局省流量模式
        AVOSCloud.setLastModifyEnabled(CONFIG.LeanCloudIsLastModifyEnabled);
        // 应该放在 Application 的 onCreate 中，设置网络超时限制
        AVOSCloud.setNetworkTimeout(AVOSCloud.DEFAULT_NETWORK_TIMEOUT);//15秒的网络超时限制
    }
}
