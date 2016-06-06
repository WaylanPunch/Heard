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
public class ContactFragment extends Fragment{
    //private final static String TAG = ContactFragment.class.getName();

    public static final String CONTACT = "Contact";

    public ContactFragment() {
        // Required empty public constructor
    }

//    public static ContactFragment newInstance(int param) {
//        ContactFragment contactFragment = new ContactFragment();
//        Bundle bundle = new Bundle();
//        bundle.putInt("ContactParam", param);
//        contactFragment.setArguments(bundle);
//        return contactFragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //int param = getArguments().getInt("ContactParam");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


}
