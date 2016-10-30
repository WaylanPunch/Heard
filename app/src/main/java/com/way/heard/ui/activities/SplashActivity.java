package com.way.heard.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;
import com.way.heard.R;
import com.way.heard.base.HeardApp;
import com.way.heard.utils.PreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;
import rx.functions.Action1;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //下载需要写SD卡权限, targetSdkVersion>=23 需要动态申请权限
        RxPermissions.getInstance(this)
                // 申请权限
                .request(new String[]{Manifest.permission_group.STORAGE,Manifest.permission_group.CAMERA,Manifest.permission_group.PHONE})
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) {
                            //请求成功
                            //Toast.makeText(SplashActivity.this, "请求权限成功", Toast.LENGTH_SHORT).show();
                        } else {
                            // 请求失败
                            Toast.makeText(SplashActivity.this, "请求权限失败", Toast.LENGTH_SHORT).show();
                            //finish();
                        }
                        isFirstTimeInstallation();
                    }
                });

    }

    private void isFirstTimeInstallation() {
        boolean isFirstTime = PreferencesUtil.getBoolean(HeardApp.getContext(), "FIRST_TIME", true);
        if (isFirstTime) {
            PreferencesUtil.putBoolean(HeardApp.getContext(), "FIRST_TIME", false);
            initView();
        } else {
            startActivity(new Intent(SplashActivity.this, IndexActivity.class));
            finish();
        }
    }

    private void initView() {
        BGABanner banner = (BGABanner) findViewById(R.id.banner_splash_pager);
        // 用Java代码方式设置切换动画
        banner.setTransitionEffect(BGABanner.TransitionEffect.Rotate);
        // banner.setPageTransformer(new RotatePageTransformer());
        // 设置page切换时长
        banner.setPageChangeDuration(1000);
        List<View> views = new ArrayList<>();
        views.add(getPageView(R.drawable.guide_1));
        views.add(getPageView(R.drawable.guide_2));
        views.add(getPageView(R.drawable.guide_3));

        View lastView = getLayoutInflater().inflate(R.layout.splash_view_last, null);
        views.add(lastView);

//        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/dragon.TTF");
//        Button btnIn = (Button) lastView.findViewById(R.id.btn_last_main);
//        btnIn.setTypeface(tf);
        lastView.findViewById(R.id.btn_last_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashActivity.this, IndexActivity.class));
                finish();
            }
        });
        banner.setViews(views);
        // banner.setCurrentItem(1);
    }

    private View getPageView(@DrawableRes int resid) {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(resid);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }
}
