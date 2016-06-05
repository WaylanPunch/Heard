package com.way.heard.ui.fragments;

import android.content.Context;
import android.content.Intent;
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
import com.victor.loading.rotate.RotateLoading;
import com.way.heard.R;
import com.way.heard.adapters.PostAdapter;
import com.way.heard.models.Image;
import com.way.heard.models.Post;
import com.way.heard.services.LeanCloudBackgroundTask;
import com.way.heard.services.LeanCloudDataService;
import com.way.heard.ui.activities.ImageDisplayActivity;
import com.way.heard.ui.activities.RepostActivity;
import com.way.heard.ui.views.autoloadrecyclerview.AutoLoadRecyclerView;
import com.way.heard.ui.views.autoloadrecyclerview.LoadMoreListener;
import com.way.heard.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2016/5/22.
 */
public class ProfilePostFragment extends Fragment {
    private final static String TAG = ProfilePostFragment.class.getName();

    public static final String ARG_PAGE = "ARG_PAGE";
    private String userObjextId;

    private Context context;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AutoLoadRecyclerView mRecyclerView;
    private RotateLoading loading;

    private PostAdapter mAdapter;
    private static int pageIndex = 1;
    private final static int pageSize = 5;
    private List<Post> mPosts;

    private View rootView = null;

    public static ProfilePostFragment newInstance(String userID) {
        Bundle args = new Bundle();
        args.putString(ARG_PAGE, userID);
        ProfilePostFragment profilePostFragment = new ProfilePostFragment();
        profilePostFragment.setArguments(args);
        return profilePostFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userObjextId = getArguments().getString(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.content_profile_post, container, false);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_profile_post_swiperefreshlayout);
        this.mRecyclerView = (AutoLoadRecyclerView) view.findViewById(R.id.alrv_profile_post_recyclerview);
        this.loading = (RotateLoading) view.findViewById(R.id.loading);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            context = getContext();

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
                    Post post = mPosts.get(pos);
                    Image image = post.tryGetPhotoList().get(0);
                    Intent intent = new Intent(context, ImageDisplayActivity.class);
                    intent.putExtra(ImageDisplayActivity.IMAGE_POST_INDEX, pos);
                    intent.putExtra(ImageDisplayActivity.IMAGE_DETAIL, image);
                    startActivityForResult(intent, ImageDisplayActivity.IMAGE_DISPLAY_REQUEST);
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
                        startActivity(intent);
                        //startActivityForResult(intent, POST_REPOST_REQUEST);
                    } else {
                        Intent intent = new Intent(context, RepostActivity.class);
                        intent.putExtra(RepostActivity.REPOST_FROM, post.getFrom());
                        intent.putExtra(RepostActivity.REPOST_DETAIL, post);
                        intent.putExtra(RepostActivity.POST_ORIGINAL_DETAIL, post.tryGetPostOriginal());
                        intent.putExtra(RepostActivity.PHOTO_ORIGINAL_LIST, (ArrayList) post.tryGetPostOriginal().tryGetPhotoList());
                        startActivity(intent);
                        //startActivityForResult(intent, POST_REPOST_REQUEST);
                    }
                }
            });
            mRecyclerView.setAdapter(mAdapter);
            loadFirst();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
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
        }.execute();
    }
}
