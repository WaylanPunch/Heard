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
        AVOSCloud.initialize(this, CONSTANTS.LeanCloudAppID, CONSTANTS.LeanCloudAppKey);
        //AVOSCloud.setDebugLogEnabled(true);
    }
}
