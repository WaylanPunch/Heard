package com.way.heard.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.way.heard.R;
import com.way.heard.adapters.PostListAdapter;
import com.way.heard.models.Post;
import com.way.heard.ui.activities.PostActivity;
import com.way.heard.ui.views.BaseListView;
import com.way.heard.utils.LeanCloudBackgroundTask;
import com.way.heard.utils.LeanCloudHelper;
import com.way.heard.utils.LogUtil;
import com.way.heard.utils.Util;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by pc on 2016/4/13.
 */
public class HomeFragment extends Fragment {
    private final static String TAG = HomeFragment.class.getName();
    private static final int POST_PUBLISH_REQUEST = 1001;
    private Context context;

    public static final String CLOSE = "Close";
    public static final String HOME = "Home";
    public static final String EXIT = "Exit";

    private FloatingActionButton fab;
    private BaseListView<Post> blvPostList;
    private static int PAGE_INDEX = 1;

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
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, "onViewCreated debug");
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        this.fab = (FloatingActionButton) view.findViewById(R.id.fab_home_action);
        this.blvPostList = (BaseListView<Post>) view.findViewById(R.id.blv_post2_list);
        initView();
    }

    private void initView() {
        LogUtil.d(TAG, "initView debug");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                //intent.setClass(getActivity(), WritingActivity.class);
                intent.setClass(getActivity(), PostActivity.class);
                startActivityForResult(intent, POST_PUBLISH_REQUEST); //这里用getActivity().startActivity(intent);
            }
        });

        initListView();
        blvPostList.setToastIfEmpty(false);
        new Handler().postDelayed(new Runnable()
        {
            public void run()
            {

                blvPostList.onRefresh();
            }
        }, 2000);

    }

    private void initListView() {
        blvPostList.init(new BaseListView.DataInterface<Post>() {
            @Override
            public List<Post> getDatas(int skip, int limit, List<Post> currentDatas) throws AVException {

                return LeanCloudHelper.getAnyPublicPostsByPage(skip, limit);
//                long maxId;
//                maxId = getMaxId(skip, currentDatas);
//                if (maxId == 0) {
//                    return new ArrayList<>();
//                } else {
//                    return StatusService.getStatusDatas(maxId, limit);
//                }
            }

            @Override
            public void onItemLongPressed(final Post item) {
                //AVStatus innerStatus = item.getInnerStatus();
                AVUser friend = item.getAuthor();
                if (friend.getObjectId().equals(AVUser.getCurrentUser().getObjectId())) {

                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure?")
                            .setContentText("Won't be able to recover this post!")
                            .setCancelText("No,cancel plx!")
                            .setConfirmText("Yes,delete it!")
                            .showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(final SweetAlertDialog sDialog) {
                                    new LeanCloudBackgroundTask(context) {
                                        @Override
                                        protected void onPre() {

                                        }

                                        @Override
                                        protected void doInBack() throws AVException {
                                            //StatusService.deleteStatus(item);
                                        }

                                        @Override
                                        protected void onPost(AVException e) {
                                            if (e != null) {
                                                Util.toast(context, e.getMessage());
                                            } else {
                                                sDialog.setTitleText("Deleted!")
                                                        .setContentText("Your post has been deleted!")
                                                        .setConfirmText("OK")
                                                        .showCancelButton(false)
                                                        .setCancelClickListener(null)
                                                        .setConfirmClickListener(null)
                                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                                blvPostList.onRefresh();
                                            }
                                        }
                                    }.execute();
                                }
                            })
                            .show();
                }
            }
        }, new PostListAdapter(getContext()));
    }

//    public static long getMaxId(int skip, List<Post> currentDatas) {
//        long maxId;
//        if (skip == 0) {
//            maxId = Long.MAX_VALUE;
//        } else {
//
//            Post lastStatus = currentDatas.get(currentDatas.size() - 1);
//            maxId = lastStatus.getMessageId() - 1;
//        }
//        return maxId;
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(TAG, "Request Code = " + requestCode + ", Result Code = " + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == POST_PUBLISH_REQUEST) {
                blvPostList.onRefresh();
            }
        }
    }
}