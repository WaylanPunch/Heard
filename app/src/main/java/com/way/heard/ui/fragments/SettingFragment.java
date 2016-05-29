package com.way.heard.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.way.heard.R;
import com.way.heard.services.LeanCloudDataService;
import com.way.heard.ui.activities.LoginActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment{
    private final static String TAG = SettingFragment.class.getName();

    public static final String SETTING = "Setting";
    private static boolean isLogin = false;

    private LinearLayout ll_LoginContainer;
    private ImageView iv_LoginIcon;
    private TextView tv_LoginTip;

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
        ll_LoginContainer = (LinearLayout) view.findViewById(R.id.ll_setting_login_container);
        iv_LoginIcon = (ImageView) view.findViewById(R.id.iv_setting_login_icon);
        tv_LoginTip = (TextView) view.findViewById(R.id.tv_setting_login_tip);

        initData();
    }

    private void initData() {
        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser != null) {
            isLogin = true;
            iv_LoginIcon.setImageResource(R.drawable.ic_logout);
            tv_LoginTip.setText("Sign Out");
        } else {
            isLogin = false;
            iv_LoginIcon.setImageResource(R.drawable.ic_login);
            tv_LoginTip.setText("Sign In");
        }
        ll_LoginContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLogin){
                    LeanCloudDataService.logout();
                }
                LoginActivity.go(getContext());
                getActivity().finish();
            }
        });
    }

}
