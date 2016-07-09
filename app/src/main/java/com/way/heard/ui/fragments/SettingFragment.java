package com.way.heard.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVUser;
import com.way.heard.R;
import com.way.heard.base.HeardApp;
import com.way.heard.services.LeanCloudDataService;
import com.way.heard.ui.activities.AboutActivity;
import com.way.heard.ui.activities.LoginActivity;
import com.way.heard.ui.activities.TestActivity;
import com.way.heard.utils.FileUtil;
import com.way.heard.utils.LogUtil;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {
    private final static String TAG = SettingFragment.class.getName();

    public static final String SETTING = "Setting";
    private static boolean isLogin = false;

    //    private LinearLayout ll_LoginContainer;
//    private ImageView iv_LoginIcon;
    private TextView tv_LoginTip;
    private TextView tv_CacheTip;
    private TextView tv_CacheValue;
    private TextView tv_AboutTip;
    private FloatingActionButton fab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int param = getArguments().getInt("SettingParam");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        ll_LoginContainer = (LinearLayout) view.findViewById(R.id.ll_setting_login_container);
//        iv_LoginIcon = (ImageView) view.findViewById(R.id.iv_setting_login_icon);
        tv_LoginTip = (TextView) view.findViewById(R.id.tv_setting_login_tip);
        tv_CacheTip = (TextView) view.findViewById(R.id.tv_setting_cache_tip);
        tv_CacheValue = (TextView) view.findViewById(R.id.tv_setting_cache_value);
        tv_AboutTip = (TextView) view.findViewById(R.id.tv_setting_about_tip);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initCache();
        initAbout();
        initLoginButton();
        initFab();
    }

    private void initCache() {
        String cacheValue = "0KB";
        try {
            cacheValue = FileUtil.getTotalCacheSize(HeardApp.getContext());
        } catch (Exception e) {
            cacheValue = "0B";
            LogUtil.e(TAG, "onActivityCreated error", e);
        }
        tv_CacheValue.setText(cacheValue);
        tv_CacheTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getContext())
                        .title("Clear All Cache Data?")
                        .content(tv_CacheValue.getText().toString())
                        .positiveText("OK")
                        .negativeText("CANCEL")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                FileUtil.clearAllCache(getContext());
                                tv_CacheValue.setText("0K");
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    private void initAbout() {
        tv_AboutTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initFab() {
        AVUser user = AVUser.getCurrentUser();
        if (user != null) {
            String username = user.getUsername();
            if (username.equalsIgnoreCase("test") || username.equalsIgnoreCase("admin")) {
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), TestActivity.class);
                        startActivity(intent);
                    }
                });
            } else {
                fab.setVisibility(View.GONE);
            }
        } else {
            fab.setVisibility(View.GONE);
        }

    }

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance(int param) {
        SettingFragment settingFragment = new SettingFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("SettingParam", param);
        settingFragment.setArguments(bundle);
        return settingFragment;
    }


    private void initLoginButton() {
        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser != null) {
            isLogin = true;
            //iv_LoginIcon.setImageResource(R.drawable.ic_logout_gray);
            tv_LoginTip.setText("Sign Out");
        } else {
            isLogin = false;
            //iv_LoginIcon.setImageResource(R.drawable.ic_login_gray);
            tv_LoginTip.setText("Sign In");
        }
        tv_LoginTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogin) {
                    LeanCloudDataService.logout();
                }
                LoginActivity.go(getContext());
                getActivity().finish();
            }
        });
    }

}
