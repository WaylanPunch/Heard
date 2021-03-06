package com.way.heard.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.victor.loading.rotate.RotateLoading;
import com.way.heard.R;
import com.way.heard.adapters.ProfileFollowerAdapter;
import com.way.heard.services.LeanCloudBackgroundTask;
import com.way.heard.services.LeanCloudDataService;
import com.way.heard.ui.views.autoloadrecyclerview.AutoLoadRecyclerView;
import com.way.heard.ui.views.autoloadrecyclerview.LoadMoreListener;
import com.way.heard.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowerFragment extends Fragment {
    private final static String TAG = FollowerFragment.class.getName();

    public static final String ARG_PAGE = "ARG_PAGE";
    private String userObjectID;

    private Context context;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AutoLoadRecyclerView mRecyclerView;
    private RotateLoading loading;

    private ProfileFollowerAdapter mAdapter;
    private int pageIndex = 1;
    private final static int pageSize = 15;
    private List<AVUser> mUsers;

    private View rootView = null;

    public static FollowerFragment newInstance(String userId) {
        Bundle args = new Bundle();
        args.putString(ARG_PAGE, userId);
        FollowerFragment followerFragment = new FollowerFragment();
        followerFragment.setArguments(args);
        return followerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userObjectID = getArguments().getString(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_follower, container, false);
        }
        return rootView;
        //return inflater.inflate(R.layout.fragment_follower, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_profile_follower_swiperefreshlayout);
        this.mRecyclerView = (AutoLoadRecyclerView) view.findViewById(R.id.alrv_profile_follower_recyclerview);
        this.loading = (RotateLoading) view.findViewById(R.id.loading);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            context = getContext();

            mUsers = new ArrayList<>();
            mRecyclerView.setHasFixedSize(false);
            mRecyclerView.setLoadMoreListener(new LoadMoreListener() {
                @Override
                public void loadMore() {
                    loadNextPage(false);
                }
            });
            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccentLight,
                    R.color.colorAccent,
                    R.color.colorAccentDark,
                    R.color.colorAccentLight);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadFirst(false);
                }
            });
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mRecyclerView.setOnPauseListenerParams(context, false, true);


            mAdapter = new ProfileFollowerAdapter(context);
            mRecyclerView.setAdapter(mAdapter);
            loadFirst(true);
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void loadFirst( boolean isFirstTime) {
        pageIndex = 1;
        LogUtil.d(TAG, "loadFirst debug, Page Index = " + pageIndex);
        loadUsersByQueryType(isFirstTime);
    }

    public void loadNextPage( boolean isFirstTime) {
        pageIndex++;
        LogUtil.d(TAG, "loadNextPage debug, Page Index = " + pageIndex);
        loadUsersByQueryType(isFirstTime);
    }

    private void loadUsersByQueryType(final boolean isFirstTime) {
        new LeanCloudBackgroundTask(context) {

            @Override
            protected void onPre() {
                if(isFirstTime) {
                    loading.start();
                }
                mSwipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected void doInBack() throws AVException {
                List<AVUser> data = LeanCloudDataService.followerQuery(userObjectID, (pageIndex - 1) * pageSize, pageSize);

                if (mUsers == null) {
                    mUsers = new ArrayList<AVUser>();
                }
                if (pageIndex == 1) {
                    mUsers.clear();
                    mUsers = data;
                } else {
                    mUsers.addAll(data);
                }
            }

            @Override
            protected void onPost(AVException e) {
                mAdapter.setUsers(mUsers);
                mAdapter.notifyDataSetChanged();
                if(isFirstTime) {
                    loading.stop();
                }
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            protected void onCancel() {
                if(isFirstTime) {
                    loading.stop();
                }
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }.execute();
    }

}
