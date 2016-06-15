package com.way.heard.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.victor.loading.rotate.RotateLoading;
import com.way.heard.R;
import com.way.heard.adapters.PostAdapter;
import com.way.heard.models.Image;
import com.way.heard.models.Post;
import com.way.heard.services.LeanCloudBackgroundTask;
import com.way.heard.services.LeanCloudDataService;
import com.way.heard.ui.activities.ImageDisplayActivity;
import com.way.heard.ui.activities.PostActivity;
import com.way.heard.ui.activities.RepostActivity;
import com.way.heard.ui.views.autoloadrecyclerview.AutoLoadRecyclerView;
import com.way.heard.ui.views.autoloadrecyclerview.LoadMoreListener;
import com.way.heard.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2016/4/13.
 */
public class HomeFragment extends Fragment {
    private final static String TAG = HomeFragment.class.getName();
    private static final int POST_PUBLISH_REQUEST = 1001;
    public static final int POST_REPOST_REQUEST = 1006;

    public static final String CLOSE = "Close";
    public static final String HOME = "Home";
    public static final String EXIT = "Exit";

    private Context context;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AutoLoadRecyclerView mRecyclerView;
    private RotateLoading loading;
    private FloatingActionButton fab;

    private PostAdapter mAdapter;
    private static int pageIndex = 1;
    private final static int pageSize = 10;
    private List<Post> mPosts;

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
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_home_swiperefreshlayout);
        this.mRecyclerView = (AutoLoadRecyclerView) view.findViewById(R.id.alrv_home_recyclerview);
        this.loading = (RotateLoading) view.findViewById(R.id.loading);
        this.fab = (FloatingActionButton) view.findViewById(R.id.fab_home_action);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            context = getContext();

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), PostActivity.class);
                    startActivityForResult(intent, POST_PUBLISH_REQUEST); //这里用getActivity().startActivity(intent);
                }
            });

            mPosts = new ArrayList<>();
            mRecyclerView.setHasFixedSize(false);
            mRecyclerView.setLoadMoreListener(new LoadMoreListener() {
                @Override
                public void loadMore() {
                    loadNextPage();
                }
            });
            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccentLight,
                    R.color.colorAccent,
                    R.color.colorAccentDark,
                    R.color.colorAccentLight);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadFirst();
                }
            });
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mRecyclerView.setOnPauseListenerParams(context, false, true);


            mAdapter = new PostAdapter(getContext());
            mAdapter.setOnImageClickListener(new PostAdapter.OnImageClickListener() {
                @Override
                public void onImageClick(int pos) {
                    LogUtil.d(TAG, "onImageClick debug, Position = " + pos);
                    Post post = mPosts.get(pos);
                    if (0 == post.getFrom()) {
                        Image image = post.tryGetPhotoList().get(0);
                        Intent intent = new Intent(context, ImageDisplayActivity.class);
                        intent.putExtra(ImageDisplayActivity.IMAGE_POST_INDEX, pos);
                        intent.putExtra(ImageDisplayActivity.IMAGE_DETAIL, image);
                        startActivityForResult(intent, ImageDisplayActivity.IMAGE_DISPLAY_REQUEST);
                    } else {
                        Post postOriginal = post.tryGetPostOriginal();
                        Image image = postOriginal.tryGetPhotoList().get(0);
                        Intent intent = new Intent(context, ImageDisplayActivity.class);
                        intent.putExtra(ImageDisplayActivity.IMAGE_POST_INDEX, pos);
                        intent.putExtra(ImageDisplayActivity.IMAGE_DETAIL, image);
                        startActivityForResult(intent, ImageDisplayActivity.IMAGE_DISPLAY_REQUEST);
                    }
                }
            });
            mAdapter.setOnRepostClickListener(new PostAdapter.OnRepostClickListener() {
                @Override
                public void onRepostClick(int pos) {
                    LogUtil.d(TAG, "onRepostClick debug, Position = " + pos);
                    Post post = mPosts.get(pos);
                    if (0 == post.getFrom()) {
                        Intent intent = new Intent(context, RepostActivity.class);
                        intent.putExtra(RepostActivity.REPOST_FROM, post.getFrom());
                        intent.putExtra(RepostActivity.POST_ORIGINAL_DETAIL, post);
                        intent.putExtra(RepostActivity.PHOTO_ORIGINAL_LIST, (ArrayList) post.tryGetPhotoList());
                        startActivityForResult(intent, POST_REPOST_REQUEST);
                    } else {
                        Intent intent = new Intent(context, RepostActivity.class);
                        intent.putExtra(RepostActivity.REPOST_FROM, post.getFrom());
                        intent.putExtra(RepostActivity.REPOST_DETAIL, post);
                        intent.putExtra(RepostActivity.POST_ORIGINAL_DETAIL, post.tryGetPostOriginal());
                        intent.putExtra(RepostActivity.PHOTO_ORIGINAL_LIST, (ArrayList) post.tryGetPostOriginal().tryGetPhotoList());
                        startActivityForResult(intent, POST_REPOST_REQUEST);
                    }
                }
            });
            mRecyclerView.setAdapter(mAdapter);
            loadFirst();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            LogUtil.e(TAG, "onActivityCreated error", e);
        }
    }

    public void loadFirst() {
        pageIndex = 1;
        LogUtil.d(TAG, "loadFirst debug, Page Index = " + pageIndex);
        loadDataByNetworkType();
    }

    public void loadNextPage() {
        pageIndex++;
        LogUtil.d(TAG, "loadNextPage debug, Page Index = " + pageIndex);
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
                List<Post> data = LeanCloudDataService.getAnyPublicPostsByPage((pageIndex - 1) * pageSize, pageSize);
                if (mPosts == null) {
                    mPosts = new ArrayList<Post>();
                }
                if (pageIndex == 1) {
                    mPosts.clear();
                    mPosts = data;
                } else {
                    mPosts.addAll(data);
                }
            }

            @Override
            protected void onPost(AVException e) {
                mAdapter.setPosts(mPosts);
                mAdapter.notifyDataSetChanged();
                loading.stop();
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            protected void onCancel() {
                loading.stop();
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(TAG, "onActivityResult debug, Request Code = " + requestCode + ", Result Code = " + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == POST_PUBLISH_REQUEST) {
                loadFirst();
            } else if (requestCode == ImageDisplayActivity.IMAGE_DISPLAY_REQUEST) {
                try {
//                    Bundle bundle = data.getExtras();
//                    Image image = bundle.getParcelable(ImageDisplayActivity.IMAGE_DETAIL);
//                    int pos = data.getIntExtra(ImageDisplayActivity.IMAGE_POST_INDEX, 0);//bundle.getInt(ImageDisplayActivity.IMAGE_POST_INDEX);
//                    List<Image> images = new ArrayList<Image>();
//                    images.add(image);
//                    mPosts.get(pos).trySetPhotoList(images);
//                    mAdapter.setPosts(mPosts);
//                    mAdapter.notifyDataSetChanged();
//                    LogUtil.d(TAG, "Position = " + pos);
                } catch (Exception e) {
                    LogUtil.e(TAG, "onActivityResult error", e);
                }
            } else if (requestCode == POST_REPOST_REQUEST) {
                loadFirst();
            }
        }
    }
}