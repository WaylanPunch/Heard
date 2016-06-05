package com.way.heard.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.victor.loading.rotate.RotateLoading;
import com.way.heard.R;
import com.way.heard.services.LeanCloudBackgroundTask;
import com.way.heard.services.LeanCloudUserService;
import com.way.heard.utils.GlideImageLoader;
import com.way.heard.utils.LogUtil;

import java.io.IOException;

public class MeEditActivity extends AppCompatActivity {
    private final static String TAG = MeEditActivity.class.getName();

    public final static String USER_DETAIL = "UserDetail";
    public final static int ME_EDIT_REQUEST = 1008;
    public static final int IMAGE_PICK_REQUEST = 1010;

    private ImageView ivAvatar;
    private ImageView ivAvatarNew;
    private ImageView ivAvatarAction;
    private EditText etDisplayName;
    private TextView tvUsername;
    private ImageView ivUsernameValidation;
    private EditText etEmail;
    private ImageView ivEmailValidation;
    private EditText etGender;
    private EditText etCity;
    private EditText etSignature;
    private FloatingActionButton fab;
    private RotateLoading loading;

    private AVUser currentUser;
    private static Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Edit Me");

        getParamData();
        initView();
        initData();
    }

    private void getParamData() {
        try {
            Bundle bundle = getIntent().getExtras();
            currentUser = bundle.getParcelable(ProfileActivity.USER_DETAIL);
            LogUtil.d(TAG, "getParamData debug, Username = " + currentUser.getUsername());
            LogUtil.d(TAG, "getParamData debug, Avatar = " + currentUser.getString("avatar"));
        } catch (Exception e) {
            LogUtil.e(TAG, "getParamData error", e);
        }
    }


    private void initView() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        loading = (RotateLoading) findViewById(R.id.loading);
        ivAvatar = (ImageView) findViewById(R.id.iv_me_edit_avatar);
        ivAvatarNew = (ImageView) findViewById(R.id.iv_me_edit_avatar_new);
        ivAvatarAction = (ImageView) findViewById(R.id.iv_me_edit_avatar_action);
        etDisplayName = (EditText) findViewById(R.id.et_me_edit_displayname_value);
        tvUsername = (TextView) findViewById(R.id.tv_me_edit_username_value);
        ivUsernameValidation = (ImageView) findViewById(R.id.iv_me_edit_username_validation);
        etEmail = (EditText) findViewById(R.id.et_me_edit_email_value);
        ivEmailValidation = (ImageView) findViewById(R.id.iv_me_edit_email_validation);
        etGender = (EditText) findViewById(R.id.et_me_edit_gender_value);
        etCity = (EditText) findViewById(R.id.et_me_edit_city_value);
        etSignature = (EditText) findViewById(R.id.et_me_edit_signature_value);
    }


    private void initData() {
        if (currentUser != null) {
            String displayname = currentUser.getString("displayname");
            if (!TextUtils.isEmpty(displayname)) {
                etDisplayName.setText(displayname);
            }
            String username = currentUser.getUsername();
            if (!TextUtils.isEmpty(username)) {
                tvUsername.setText(username);
            }
            String signature = currentUser.getString("signature");
            if (!TextUtils.isEmpty(signature)) {
                etSignature.setText(signature);
            }
            String avatar = currentUser.getString("avatar");
            if (!TextUtils.isEmpty(avatar)) {
                GlideImageLoader.displayImage(MeEditActivity.this, avatar, ivAvatar);
            }
            String email = currentUser.getEmail();
            if (!TextUtils.isEmpty(email)) {
                etEmail.setText(email);
            }
            String gender = currentUser.getString("gender");
            if (!TextUtils.isEmpty(gender)) {
                etGender.setText(gender);
            }
            String city = currentUser.getString("city");
            if (!TextUtils.isEmpty(city)) {
                etCity.setText(city);
            }

            ivAvatarAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickImage(MeEditActivity.this, IMAGE_PICK_REQUEST);
                }
            });

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveChange();
                }
            });
        }
    }

    public static void pickImage(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, requestCode);
    }


    private void saveChange() {
        new LeanCloudBackgroundTask(MeEditActivity.this) {

            @Override
            protected void onPre() {
                loading.start();
            }

            @Override
            protected void doInBack() throws AVException {
                String displaynamenew = etDisplayName.getText().toString();
                String emailnew = etEmail.getText().toString();
                String gendernew = etGender.getText().toString();
                String citynew = etCity.getText().toString();
                String signaturenew = etSignature.getText().toString();
                LeanCloudUserService.saveUserProfile(bitmap, displaynamenew, emailnew, gendernew, citynew, signaturenew);
            }

            @Override
            protected void onPost(AVException e) {
                loading.stop();
                if (e != null) {
                    Toast.makeText(MeEditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MeEditActivity.this, "Good", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        }.execute();
    }

    public static void go(Context context, AVUser user) {
        Intent intent = new Intent(context, MeEditActivity.class);
        intent.putExtra(MeEditActivity.USER_DETAIL, user);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_REQUEST) {
                LogUtil.d(TAG, "onActivityResult debug");
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    if (bitmap != null) {
                        ivAvatar.setVisibility(View.GONE);
                        ivAvatarNew.setVisibility(View.VISIBLE);
                        ivAvatarNew.setImageBitmap(bitmap);
                    } else {
                        ivAvatar.setVisibility(View.VISIBLE);
                        ivAvatarNew.setVisibility(View.GONE);
                        LogUtil.d(TAG, "onActivityResult debug, Bitmap == NULL");
                    }
                } catch (IOException e) {
                    LogUtil.e(TAG, "onActivityResult error", e);
                }
            }
        }
    }
}
