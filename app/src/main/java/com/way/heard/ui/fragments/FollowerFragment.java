package com.way.heard.ui.fragments;


import android.os.Bundle;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_follower, container, false);
    }

}
