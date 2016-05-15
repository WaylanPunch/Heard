package com.way.heard.ui.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.victor.loading.rotate.RotateLoading;
import com.way.heard.R;
import com.way.heard.models.Image;
import com.way.heard.ui.activities.UserDisplayActivity;
import com.way.heard.ui.views.swipecard.CardSlidePanel;
import com.way.heard.services.LeanCloudBackgroundTask;
import com.way.heard.services.LeanCloudDataService;
import com.way.heard.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint({"HandlerLeak", "NewApi", "InflateParams"})
public class FindFragment extends Fragment {
    private final static String TAG = FindFragment.class.getName();

    public static final String FIND = "Find";
    private Context mContext;
    private CardSlidePanel.CardSwitchListener cardSwitchListener;
    private List<Image> dataList = new ArrayList<Image>();
    private CardSlidePanel slidePanel;
    private RotateLoading loading;
    private static int pageIndex = 1;
    private final static int pageSize = 15;

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
        slidePanel = (CardSlidePanel) view.findViewById(R.id.image_slide_panel);
        loading = (RotateLoading) view.findViewById(R.id.loading);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getContext();

        cardSwitchListener = new CardSlidePanel.CardSwitchListener() {

            @Override
            public void onShow(int index) {
                if (dataList != null && dataList.size() > 0) {
                    Log.d("CardFragment", "正在显示-Index, " + index + ", Username" + dataList.get(index).getAuthor().getUsername());
                }
            }

            @Override
            public void onCardVanish(int index, int type) {
                if (dataList != null && dataList.size() > 0) {
                    Log.d("CardFragment", "正在消失-Index, " + index + ", Username" + dataList.get(index).getAuthor().getUsername() + " 消失type=" + type);
                    if (type == CardSlidePanel.VANISH_TYPE_RIGHT) {
                        likeImage(true, dataList.get(index));
                    } else if (type == CardSlidePanel.VANISH_TYPE_LEFT) {
                        likeImage(false, dataList.get(index));
                    }
                }
                if ((dataList.size() > index) && (index + 1 == dataList.size())) {
                    loadNextPage();
                }
            }

            @Override
            public void onItemClick(View cardView, int index) {
                /*
                if (dataList != null && dataList.size() > 0) {
                    Log.d("CardFragment", "卡片点击-Index, " + index + ", Username" + dataList.get(index).getAuthor().getUsername());
                    //ImageDisplayActivity.go(getActivity(), dataList.get(index), 0);
                    Image image = dataList.get(index);
                    Intent intent = new Intent(mContext, ImageDisplayActivity.class);
                    intent.putExtra(ImageDisplayActivity.IMAGE_POST_INDEX, index);
                    intent.putExtra(ImageDisplayActivity.IMAGE_DETAIL, image);
                    startActivityForResult(intent, ImageDisplayActivity.IMAGE_DISPLAY_REQUEST);
                }
                */
            }

            @Override
            public void onHomeButtonClick(int index) {
                AVUser user = dataList.get(index).getAuthor();
                UserDisplayActivity.go(getActivity(), user);
            }
        };
        slidePanel.setCardSwitchListener(cardSwitchListener);
        loadFirst();

    }

    public void loadFirst() {
        pageIndex = 1;
        LogUtil.d(TAG, "loadFirst debug, Page Index = " + pageIndex);
        loadImages();
    }

    public void loadNextPage() {
        pageIndex++;
        LogUtil.d(TAG, "loadNextPage debug, Page Index = " + pageIndex);
        loadImages();
    }

    private void loadImages() {
        new LeanCloudBackgroundTask(mContext) {

            @Override
            protected void onPre() {
                loading.start();
            }

            @Override
            protected void doInBack() throws AVException {
                List<Image> data = LeanCloudDataService.getAnyPublicImageByPage((pageIndex - 1) * pageSize, pageSize);
                if (dataList == null) {
                    dataList = new ArrayList<Image>();
                }
                dataList.clear();
                dataList.addAll(data);
            }

            @Override
            protected void onPost(AVException e) {
                try {
                    slidePanel.fillData(dataList);
                } catch (Exception e1) {
                    Toast.makeText(mContext, e1.getMessage(), Toast.LENGTH_SHORT).show();
                    LogUtil.e(TAG, "loadImages error, LeanCloudBackgroundTask onPost,", e1);
                }

                loading.stop();
                if (dataList == null || dataList.size() == 0) {
                    pageIndex = 1;
                }
                if (e != null) {
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void likeImage(final boolean isLike, final Image image) {
        new LeanCloudBackgroundTask(getContext()) {

            @Override
            protected void onPre() {
                loading.start();
            }

            @Override
            protected void doInBack() throws AVException {
                List<String> likeObjectIDs = image.getLikes();
                if (isLike) {//like
                    if (likeObjectIDs == null) {
                        likeObjectIDs = new ArrayList<String>();
                    }
                    if (!likeObjectIDs.contains(AVUser.getCurrentUser().getObjectId())) {
                        likeObjectIDs.add(AVUser.getCurrentUser().getObjectId());
                        image.setLikes(likeObjectIDs);
                        image.save();
                    }
                } else {//dislike
                    if (likeObjectIDs != null) {
                        if (likeObjectIDs.contains(AVUser.getCurrentUser().getObjectId())) {
                            likeObjectIDs.remove(AVUser.getCurrentUser().getObjectId());
                            image.setLikes(likeObjectIDs);
                            image.save();
                        }
                    }
                }
            }

            @Override
            protected void onPost(AVException e) {
                loading.stop();
                if (e != null) {
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(TAG, "onActivityResult debug, Request Code = " + requestCode + ", Result Code = " + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            /*
            if (requestCode == ImageDisplayActivity.IMAGE_DISPLAY_REQUEST) {
                try {
                    Bundle bundle = data.getExtras();
                    Image image = bundle.getParcelable(ImageDisplayActivity.IMAGE_DETAIL);
                    int pos = data.getIntExtra(ImageDisplayActivity.IMAGE_POST_INDEX, 0);//bundle.getInt(ImageDisplayActivity.IMAGE_POST_INDEX);
                    dataList.set(pos, image);
                    LogUtil.d(TAG, "Position = " + pos);
                } catch (Exception e) {
                    LogUtil.e(TAG, "onActivityResult error", e);
                }
            }
            */
        }
    }
}
