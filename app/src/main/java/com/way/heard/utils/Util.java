package com.way.heard.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.test.suitebuilder.annotation.Suppress;
import android.util.Log;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import rx.functions.Action1;

/**
 * Created by pc on 2016/4/26.
 */
public class Util {
    private final static String TAG = Util.class.getName();

    public static void toast(Context context, int strId) {
        toast(context, context.getString(strId));
    }

    public static void toast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static ProgressDialog showSpinnerDialog(Activity activity) {
        // activity = modifyDialogContext(activity);

        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setMessage("Loading");
        if (activity.isFinishing() == false) {
            dialog.show();
        }
        return dialog;
    }

    public static String getDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        return format.format(date);
    }

    public static String millisecs2DateString(long timestamp) {
        PrettyTime prettyTime = new PrettyTime();
        prettyTime.setLocale(Locale.ENGLISH);
        long gap = System.currentTimeMillis() - timestamp;
        if (gap < 1000 * 60 * 60 * 24) {
            String s = prettyTime.format(new Date(timestamp));
            return s.replace(" ", "");
        } else {
            return getDate(new Date(timestamp));
        }
    }

    public static boolean compare(String source, String other) {
        if (source == null) {
            return other == null;
        } else {
            return source.equals(other);
        }

    }


    public static void startCallPhone(final Context context, final String phoneNumber) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //下载需要写SD卡权限, targetSdkVersion>=23 需要动态申请权限
                RxPermissions.getInstance(context)
                        // 申请权限
                        .request(new String[]{Manifest.permission.CALL_PHONE})
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean granted) {
                                if (granted) {
                                    //请求成功
                                    //用intent启动拨打电话
                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                } else {
                                    // 请求失败
                                    Toast.makeText(context, "请求权限失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(context, "Failed to Call The Phone Number", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "startCallPhone error", e);
        }
    }

    public static void startSendEmail(final Context context, final String email) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //下载需要写SD卡权限, targetSdkVersion>=23 需要动态申请权限
                RxPermissions.getInstance(context)
                        // 申请权限
                        .request(new String[]{Manifest.permission.INTERNET})
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean granted) {
                                if (granted) {
                                    //请求成功
                                    Intent data = new Intent(Intent.ACTION_SENDTO);
                                    data.setData(Uri.parse("mailto:" + email));
                                    data.putExtra(Intent.EXTRA_SUBJECT, "这是标题");
                                    data.putExtra(Intent.EXTRA_TEXT, "这是内容");
                                    context.startActivity(data);
                                } else {
                                    // 请求失败
                                    Toast.makeText(context, "请求权限失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Intent data = new Intent(Intent.ACTION_SENDTO);
                data.setData(Uri.parse("mailto:" + email));
                data.putExtra(Intent.EXTRA_SUBJECT, "这是标题");
                data.putExtra(Intent.EXTRA_TEXT, "这是内容");
                context.startActivity(data);
            }
        } catch (Exception e) {
            Toast.makeText(context, "Failed to Send The Email", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "startSendEmail error", e);
        }
    }

    public static void startAccessUrl(final Context context, final String url) {
        try {
            final String urlAddr = adjustURLFormat(url);
            Log.d(TAG, "startAccessUrl debug, URL = " + urlAddr);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //下载需要写SD卡权限, targetSdkVersion>=23 需要动态申请权限
                RxPermissions.getInstance(context)
                        // 申请权限
                        .request(new String[]{Manifest.permission.INTERNET})
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean granted) {
                                if (granted) {
                                    //请求成功

                                    Intent intent = new Intent();
                                    intent.setData(Uri.parse(urlAddr));//urlAddr 就是你要打开的网址
                                    intent.setAction(Intent.ACTION_VIEW);
                                    context.startActivity(intent);
                                } else {
                                    // 请求失败
                                    Toast.makeText(context, "请求权限失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Intent intent = new Intent();
                intent.setData(Uri.parse(urlAddr));//urlAddr 就是你要打开的网址
                intent.setAction(Intent.ACTION_VIEW);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(context, "Failed to Access The Url", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "startAccessUrl error", e);
        }
    }

    public static String adjustURLFormat(String url) {
        String urlAddr = url.trim();

        if (!urlAddr.startsWith("http://")) {
            urlAddr = "http://" + urlAddr;
        }
        return urlAddr;
    }
}
