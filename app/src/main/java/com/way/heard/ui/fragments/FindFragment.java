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
public class FindFragment extends Fragment{
    private final static String TAG = FindFragment.class.getName();

    public static final String FIND = "Find";


    public static FindFragment newInstance(int param) {
        FindFragment findFragment = new FindFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("FindParam", param);
        findFragment.setArguments(bundle);
        return findFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int param = getArguments().getInt("FindParam");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}
