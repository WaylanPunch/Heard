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
public class FolloweeFragment extends Fragment {
    private final static String TAG = FolloweeFragment.class.getName();



    public FolloweeFragment() {
        // Required empty public constructor
    }

    public static FolloweeFragment newInstance(int param) {
        FolloweeFragment followeeFragment = new FolloweeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("FolloweeParam", param);
        followeeFragment.setArguments(bundle);
        return followeeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int param = getArguments().getInt("FolloweeParam");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_followee, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initData();
    }

    private void initData() {

    }

}
