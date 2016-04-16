package com.way.heard.ui.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.way.heard.R;
import com.way.heard.ui.activities.WritingActivity;
import com.way.heard.utils.LogUtil;

import yalantis.com.sidemenu.interfaces.ScreenShotable;

/**
 * Created by pc on 2016/4/13.
 */
public class HomeFragment extends Fragment implements ScreenShotable {
    private final static String TAG = HomeFragment.class.getName();

    public static final String CLOSE = "Close";
    public static final String HOME = "Home";

    private View containerView;
    private FloatingActionButton fab;
    //protected ImageView mImageView;
    //protected int res;
    private Bitmap bitmap;

    public static HomeFragment newInstance(int param) {
        LogUtil.d(TAG, "newInstance debug");
        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("HomeParam", param);
        homeFragment.setArguments(bundle);
        return homeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreate debug");
        super.onCreate(savedInstanceState);
        int param = getArguments().getInt("HomeParam");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreateView debug");
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        //mImageView = (ImageView) rootView.findViewById(R.id.image_content);
//        mImageView.setClickable(true);
//        mImageView.setFocusable(true);
//        mImageView.setImageResource(res);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, "onViewCreated debug");
        super.onViewCreated(view, savedInstanceState);
        this.containerView = view.findViewById(R.id.fl_home_container);
        this.fab = (FloatingActionButton) view.findViewById(R.id.fab_home_action);

        initView();
    }

    private void initView() {
        LogUtil.d(TAG, "initView debug");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), WritingActivity.class);
                startActivity(intent); //这里用getActivity().startActivity(intent);
            }
        });
    }

    @Override
    public void takeScreenShot() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(containerView.getWidth(),
                        containerView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                containerView.draw(canvas);
                HomeFragment.this.bitmap = bitmap;
            }
        };

        thread.start();

    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }
}