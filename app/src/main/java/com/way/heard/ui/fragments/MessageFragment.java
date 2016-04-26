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
public class MessageFragment extends Fragment{
    private final static String TAG = MessageFragment.class.getName();

    public static final String MESSAGE = "Message";

    public MessageFragment() {
        // Required empty public constructor
    }

    public static MessageFragment newInstance(int param) {
        MessageFragment messageFragment = new MessageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("MessageParam", param);
        messageFragment.setArguments(bundle);
        return messageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int param = getArguments().getInt("MessageParam");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
