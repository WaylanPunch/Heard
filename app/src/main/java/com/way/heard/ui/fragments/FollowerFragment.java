package com.way.heard.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.way.heard.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowerFragment extends Fragment {



    public FollowerFragment() {
        // Required empty public constructor
    }

    public static FollowerFragment newInstance(int param) {
        FollowerFragment followerFragment = new FollowerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("FollowerParam", param);
        followerFragment.setArguments(bundle);
        return followerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int param = getArguments().getInt("FollowerParam");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_follower, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
    }

    private void initData() {

    }

}
