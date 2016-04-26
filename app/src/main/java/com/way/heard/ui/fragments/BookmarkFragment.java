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
public class BookmarkFragment extends Fragment{
    private final static String TAG = BookmarkFragment.class.getName();

    public static final String BOOKMARK = "Bookmark";


    public BookmarkFragment() {
        // Required empty public constructor
    }

    public static BookmarkFragment newInstance(int param) {
        BookmarkFragment bookmarkFragment = new BookmarkFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("BookmarkParam", param);
        bookmarkFragment.setArguments(bundle);
        return bookmarkFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int param = getArguments().getInt("BookmarkParam");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmark, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}
