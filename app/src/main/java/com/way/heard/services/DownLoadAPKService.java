package com.way.heard.services;

import android.Manifest;
import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;

import rx.functions.Action1;

/**
 * Created by pc on 2016/10/22.
 */

public class DownLoadAPKService extends Service {
    private final static String TAG = DownLoadAPKService.class.getName();

    private final static String APP_DIRECTORY = "com.way.heard";
    /**
     * 广播接受者
     */
    private BroadcastReceiver receiver;
    /**
     * 系统下载管理器
     */
    private DownloadManager dm;
    /**
     * 系统下载器分配的唯一下载任务id，可以通过这个id查询或者处理下载任务
     */
    private long enqueue;

    /**
     * TODO下载地址 需要自己修改,这里随便找了一个
     */
    //
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        final String fileUrl = bundle.getString("FILE_URL");
        final String fileName = bundle.getString("FILE_NAME");
        Log.d(TAG, "onStartCommand debug, FILE_URL = " + fileUrl);
        Log.d(TAG, "onStartCommand debug, FILE_NAME = " + fileName);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                startInstall(context, fileName);
                //销毁当前的Service
                stopSelf();
            }
        };
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        //下载需要写SD卡权限, targetSdkVersion>=23 需要动态申请权限
        RxPermissions.getInstance(this)
                // 申请权限
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) {
                            //请求成功
                            startDownload(fileUrl, fileName);
                        } else {
                            // 请求失败回收当前服务
                            stopSelf();

                        }
                    }
                });
        return Service.START_STICKY;
    }

    /**
     * 通过隐式意图调用系统安装程序安装APK
     */
//    public static void install(Context context) {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        // 由于没有在Activity环境下启动Activity,设置下面的标签
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setDataAndType(Uri.fromFile(
//                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "JianShu-1.11.2.apk")),
//                "application/vnd.android.package-archive");
//        context.startActivity(intent);
//    }
    public static void startInstall(Context context, String fileName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(APP_DIRECTORY), fileName);
        // 参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
        Uri apkUri = FileProvider.getUriForFile(context, "com.way.heard.fileprovider", file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //添加这一句表示对目标应用临时授权该Uri所代表的文件
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    @Override
    public void onDestroy() {
        //服务销毁的时候 注销广播
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void startDownload(String fileUrl, String fileName) {
        //获得系统下载器
        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        //设置下载地址
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        //设置下载文件的类型
        request.setMimeType("application/vnd.android.package-archive");
        //设置下载存放的文件夹和文件名字
        request.setDestinationInExternalPublicDir(APP_DIRECTORY, fileName);
        //设置下载时或者下载完成时，通知栏是否显示
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle("下载新版本");
        //执行下载，并返回任务唯一id
        enqueue = dm.enqueue(request);
    }
}
