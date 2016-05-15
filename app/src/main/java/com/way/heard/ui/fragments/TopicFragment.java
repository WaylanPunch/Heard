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

import com.avos.avoscloud.AVException;
import com.victor.loading.rotate.RotateLoading;
import com.way.heard.R;
import com.way.heard.adapters.TopicAdapter;
import com.way.heard.models.BannerModel;
import com.way.heard.services.LeanCloudDataService;
import com.way.heard.ui.views.autoloadrecyclerview.AutoLoadRecyclerView;
import com.way.heard.ui.views.autoloadrecyclerview.LoadMoreListener;
import com.way.heard.services.LeanCloudBackgroundTask;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class TopicFragment extends Fragment {
    private final static String TAG = TopicFragment.class.getName();

    public static final String TOPIC = "Topic";
    private Context context;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AutoLoadRecyclerView mRecyclerView;
    private RotateLoading loading;

    private TopicAdapter mAdapter;
    private List<BannerModel> mBannerModels;

    public TopicFragment() {
        // Required empty public constructor
    }

    public static TopicFragment newInstance(int param) {
        TopicFragment topicFragment = new TopicFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("TopicParam", param);
        topicFragment.setArguments(bundle);
        return topicFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int param = getArguments().getInt("TopicParam");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_topic, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_mark_swiperefreshlayout);
        this.mRecyclerView = (AutoLoadRecyclerView) view.findViewById(R.id.alrv_mark_recyclerview);
        this.loading = (RotateLoading) view.findViewById(R.id.loading);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getContext();

        mBannerModels = new ArrayList<>();
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLoadMoreListener(new LoadMoreListener() {
            @Override
            public void loadMore() {
                //loadNextPage();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFirst();
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setOnPauseListenerParams(context, false, true);


        mAdapter = new TopicAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        loadFirst();

    }

    public void loadFirst() {
        loadDataByNetworkType();
    }

    private void loadDataByNetworkType() {
        new LeanCloudBackgroundTask(context) {

            @Override
            protected void onPre() {
                loading.start();
                mSwipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected void doInBack() throws AVException {
                List<BannerModel> data = LeanCloudDataService.getBanners();
                if (mBannerModels == null) {
                    mBannerModels = new ArrayList<BannerModel>();
                }

                mBannerModels.clear();
                mBannerModels.addAll(data);
            }

            @Override
            protected void onPost(AVException e) {
                mAdapter.setBannerModels(mBannerModels);
                mAdapter.notifyDataSetChanged();
                loading.stop();
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }.execute();
    }
}
