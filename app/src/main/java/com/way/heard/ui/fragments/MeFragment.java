package com.way.heard.ui.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.victor.loading.rotate.RotateLoading;
import com.way.heard.R;
import com.way.heard.services.LeanCloudBackgroundTask;
import com.way.heard.services.LeanCloudDataService;
import com.way.heard.ui.activities.MeEditActivity;
import com.way.heard.utils.GlideImageLoader;
import com.way.heard.utils.LogUtil;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment {
    private final static String TAG = MeFragment.class.getName();

    public static final String ME = "Me";

    private TextView tvDisplayName;
    private TextView tvUsername;
    private TextView tvSignature;
    private ImageView ivAvatar;
    private TextView tvShareCount;
    private TextView tvLikeCount;
    private TextView tvCommentCount;
    private TextView tvEdit;
    private TextView tvPostValue;
    private TextView tvFolloweeValue;
    private TextView tvFollowerValue;
    private TextView tvEmailValue;
    private TextView tvGenderValue;
    private TextView tvCityValue;
    private ImageView ivQRCodeValue;
    private RotateLoading loading;

    private int mShareCount;
    private int mLikeCount;
    private int mCommentCount;
    private int mPostCount;
    private int mFolloweeCount;
    private int mFollowerCount;

    public MeFragment() {

    }

    public static MeFragment newInstance(int param) {
        MeFragment meFragment = new MeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("MeParam", param);
        meFragment.setArguments(bundle);
        return meFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int param = getArguments().getInt("MeParam");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.tvDisplayName = (TextView) view.findViewById(R.id.tv_me_displayname);
        this.tvUsername = (TextView) view.findViewById(R.id.tv_me_username);
        this.tvSignature = (TextView) view.findViewById(R.id.tv_me_signature);
        this.ivAvatar = (ImageView) view.findViewById(R.id.iv_me_avatar);
        this.tvShareCount = (TextView) view.findViewById(R.id.tv_me_sharecount);
        this.tvLikeCount = (TextView) view.findViewById(R.id.tv_me_likecount);
        this.tvCommentCount = (TextView) view.findViewById(R.id.tv_me_commentcount);
        this.tvEdit = (TextView) view.findViewById(R.id.tv_me_edit);
        this.tvPostValue = (TextView) view.findViewById(R.id.tv_me_post_value);
        this.tvFolloweeValue = (TextView) view.findViewById(R.id.tv_me_followee_value);
        this.tvFollowerValue = (TextView) view.findViewById(R.id.tv_me_follower_value);
        this.tvEmailValue = (TextView) view.findViewById(R.id.tv_me_email_value);
        this.tvGenderValue = (TextView) view.findViewById(R.id.tv_me_gender_value);
        this.tvCityValue = (TextView) view.findViewById(R.id.tv_me_city_value);
        this.ivQRCodeValue = (ImageView) view.findViewById(R.id.iv_me_qrcode_value);
        this.loading = (RotateLoading) view.findViewById(R.id.loading);
        initData();
    }

    private void initData() {
        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser != null) {
            String displayname = currentUser.getString("displayname");
            if (!TextUtils.isEmpty(displayname)) {
                tvDisplayName.setText(displayname);
            }
            String username = currentUser.getUsername();
            if (!TextUtils.isEmpty(username)) {
                tvUsername.setText(username);
            }
            String signature = currentUser.getString("signature");
            if (!TextUtils.isEmpty(signature)) {
                tvSignature.setText(signature);
            }
            String avatar = currentUser.getString("avatar");
            if (!TextUtils.isEmpty(avatar)) {
                GlideImageLoader.displayImage(getContext(), avatar, ivAvatar);
            }
            String email = currentUser.getEmail();
            if (!TextUtils.isEmpty(email)) {
                tvEmailValue.setText(email);
            }
            String gender = currentUser.getString("gender");
            if (!TextUtils.isEmpty(gender)) {
                tvGenderValue.setText(gender);
            }
            String city = currentUser.getString("city");
            if (!TextUtils.isEmpty(city)) {
                tvCityValue.setText(city);
            }
//            String qrcode = currentUser.getString("avatar");
//            if (!TextUtils.isEmpty(avatar)) {
//                GlideImageLoader.displayImage(getContext(), avatar, ivAvatar);
//            }

            tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), MeEditActivity.class);
                    intent.putExtra(MeEditActivity.USER_DETAIL, AVUser.getCurrentUser());
                    startActivityForResult(intent, MeEditActivity.ME_EDIT_REQUEST);
                }
            });

            getDataFromCloud();
        }
    }

    private void getDataFromCloud() {
        new LeanCloudBackgroundTask(getContext()) {

            @Override
            protected void onPre() {
                loading.start();
            }

            @Override
            protected void doInBack() throws AVException {
                String currentuserId = AVUser.getCurrentUser().getObjectId();
                mShareCount = LeanCloudDataService.getRepostCountByUser(currentuserId);
                mLikeCount = LeanCloudDataService.getLikeCountByUser(currentuserId);
                mCommentCount = LeanCloudDataService.getCommentCountByUser(currentuserId);
                mPostCount = LeanCloudDataService.getPostCountByUser(currentuserId);
                mFolloweeCount = LeanCloudDataService.getFolloweeCountByUser(currentuserId);
                mFollowerCount = LeanCloudDataService.getFollowerCountByUser(currentuserId);
            }

            @Override
            protected void onPost(AVException e) {
                loading.stop();
                if (e != null) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    initDataDelay();
                }
            }

            @Override
            protected void onCancel() {
                loading.stop();
            }
        }.execute();
    }

    private void initDataDelay() {
        tvShareCount.setText(mShareCount + "");
        tvLikeCount.setText(mLikeCount + "");
        tvCommentCount.setText(mCommentCount + "");
        tvPostValue.setText(mPostCount + "");
        tvFolloweeValue.setText(mFolloweeCount + "");
        tvFollowerValue.setText(mFollowerCount + "");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MeEditActivity.ME_EDIT_REQUEST) {
                LogUtil.d(TAG, "onActivityResult debug");
                initData();
            }
        }
    }
}
