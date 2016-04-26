package com.way.heard.ui.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVUser;
import com.way.heard.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment{
    private final static String TAG = MeFragment.class.getName();

    public static final String ME = "Me";

    private Bitmap bitmap;
    //private Button btnLogin;
    //private Button btnLogout;

    public MeFragment() {
        // Required empty public constructor
    }

    public static MeFragment newInstance(int param) {
        MeFragment meFragment = new MeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("MeParam", param);
        meFragment.setArguments(bundle);
        return meFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int param = getArguments().getInt("MeParam");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //this.btnLogin = (Button) view.findViewById(R.id.btn_me_login);
        //this.btnLogout = (Button) view.findViewById(R.id.btn_me_logout);

        AVUser currentUser = AVUser.getCurrentUser();
        if(currentUser == null){
            //btnLogin.setVisibility(View.VISIBLE);
            //btnLogout.setVisibility(View.GONE);
        }else {
            //btnLogin.setVisibility(View.GONE);
            //btnLogout.setVisibility(View.VISIBLE);
        }

        initView();
    }

    private void initView() {
        /*
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent); //这里用getActivity().startActivity(intent);
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent); //这里用getActivity().startActivity(intent);
            }
        });
        */
    }

}
