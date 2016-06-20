package com.way.heard.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.way.heard.R;
import com.way.heard.adapters.NineGridImageViewAdapter;
import com.way.heard.internal.GlidePauseOnScrollListener;
import com.way.heard.services.LeanCloudDataService;
import com.way.heard.ui.views.NineGridImageView;
import com.way.heard.ui.views.TagCloudView;
import com.way.heard.utils.GlideImageLoader;
import com.way.heard.utils.GlideLocalImageLoader;
import com.way.heard.utils.LogUtil;
import com.way.heard.utils.NetAsyncTask;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.PauseOnScrollListener;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.galleryfinal.widget.GFImageView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.github.mthli.knife.KnifeParser;

public class WritingActivity extends BaseActivity {
    private final static String TAG = WritingActivity.class.getName();

    private final int REQUEST_CODE_CAMERA = 1000;
    private final int REQUEST_CODE_GALLERY = 1001;
    private final int REQUEST_CODE_CROP = 1002;
    private final int REQUEST_CODE_EDIT = 1003;
    private Toolbar mToolbar;
    private static String articleContent;
    private static List<String> tagsData = null;
    private ProgressBar progressBar;
    private EditText etTitle;
    private TagCloudView tcvTags;
    private EditText etTags;
    private NineGridImageView ngivGallery;
    private static List<PhotoInfo> mPhotoList;
    private static List<String> photopaths;
    private NineGridImageViewAdapter<PhotoInfo> mAdapter;
    private FloatingActionButton fab;
    private TextView tvDataStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        setToolBar();
        initData();
        initView();
    }


    private void setToolBar() {
        LogUtil.d(TAG, "setToolBar debug");
        mToolbar = (Toolbar) findViewById(R.id.tb_writing_toolbar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        //mToolbar.setNavigationIcon(R.drawable.btn_back);
        getSupportActionBar().setTitle("Send");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initData() {
        LogUtil.d(TAG, "initData debug");
        tagsData = new ArrayList<>();
        mPhotoList = new ArrayList<>();
        photopaths = new ArrayList<>();
        mAdapter = new NineGridImageViewAdapter<PhotoInfo>() {
            @Override
            public void onDisplayImage(Context context, final GFImageView imageView, PhotoInfo photoInfo) {
                try {
                    GlideImageLoader.displayImageFromPath(context, photoInfo.getPhotoPath(), imageView, R.drawable.ic_picture_default, R.drawable.ic_picture_default);
                } catch (Exception e) {
                    LogUtil.e(TAG, "initData error, onDisplayImage", e);
                }
            }

            @Override
            public GFImageView generateImageView(Context context) {
                return super.generateImageView(context);
            }

            @Override
            public void onItemImageClick(Context context, int index, List<PhotoInfo> list) {
                openGallery();
            }
        };
    }

    private void initView() {
        progressBar = (ProgressBar) findViewById(R.id.writing_progress);
        etTitle = (EditText) findViewById(R.id.et_writing_title);
        tcvTags = (TagCloudView) findViewById(R.id.tcv_writing_tags);
        etTags = (EditText) findViewById(R.id.et_writing_tags);
        ngivGallery = (NineGridImageView) findViewById(R.id.ngiv_writing_gallery);
        fab = (FloatingActionButton) findViewById(R.id.fab_writing_action);
        tvDataStorage = (TextView) findViewById(R.id.tv_writing_storage);

        tcvTags.setVisibility(View.GONE);
        etTags.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    LogUtil.d(TAG, "initView debug, OnKeyListener, KEYCODE_ENTER");
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                        if (TextUtils.isEmpty(etTags.getText())) {
                            Toast.makeText(WritingActivity.this, "No Tag Input!", Toast.LENGTH_SHORT).show();
                        }
                        if (tagsData.size() > 4) {
                            Toast.makeText(WritingActivity.this, "Only Five Tags Allowed!", Toast.LENGTH_SHORT).show();
                        }
                        String tagStr = etTags.getText().toString();
                        tagsData.add(tagStr);
                        if (tcvTags.getVisibility() == View.GONE) {
                            tcvTags.setVisibility(View.VISIBLE);
                        }
                        tcvTags.setTags(tagsData);
                        etTags.setText("");
                    }
                    return true;
                }
                return false;
            }
        });

        if (tagsData != null && tagsData.size() > 0) {
            tcvTags.setVisibility(View.VISIBLE);
            tcvTags.setTags(tagsData);
        }

        tcvTags.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
            @Override
            public void onTagClick(int position) {
                if (position == -1) {
                    Toast.makeText(WritingActivity.this, "Click End Text", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WritingActivity.this, "Click Position : " + position, Toast.LENGTH_SHORT).show();
                }
            }
        });
        tcvTags.setOnTagLongClickListener(new TagCloudView.OnTagLongClickListener() {
            @Override
            public boolean onTagLongClick(final int position) {
                if (position == -1) {
                    Toast.makeText(WritingActivity.this, "Long Click End Text", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    new SweetAlertDialog(WritingActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure?")
                            .setContentText("Won't be able to recover this tag!")
                            .setCancelText("No,cancel plx!")
                            .setConfirmText("Yes,delete it!")
                            .showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    // reuse previous dialog instance, keep widget user state, reset them if you need
                                    sDialog.setTitleText("Cancelled!")
                                            .setContentText("Your tag is safe :)")
                                            .setConfirmText("OK")
                                            .showCancelButton(false)
                                            .setCancelClickListener(null)
                                            .setConfirmClickListener(null)
                                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);

                                    // or you can new a SweetAlertDialog to show
                               /* sDialog.dismiss();
                                new SweetAlertDialog(SampleActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Cancelled!")
                                        .setContentText("Your imaginary file is safe :)")
                                        .setConfirmText("OK")
                                        .show();*/
                                }
                            })
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    //TODO delete the tag
                                    tagsData.remove(position);
                                    tcvTags.setTags(tagsData);
                                    if (tagsData.size() == 0) {
                                        tcvTags.setVisibility(View.GONE);
                                    }
                                    sDialog.setTitleText("Deleted!")
                                            .setContentText("Your tag has been deleted!")
                                            .setConfirmText("OK")
                                            .showCancelButton(false)
                                            .setCancelClickListener(null)
                                            .setConfirmClickListener(null)
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                }
                            })
                            .show();
                    return true;
                }
                //return false;
            }
        });

        tcvTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(WritingActivity.this, "TagView onClick",
                        Toast.LENGTH_SHORT).show();
            }
        });

        ngivGallery.setAdapter(mAdapter);
        if (mPhotoList.size() == 0) {
            mPhotoList.add(new PhotoInfo());
            ngivGallery.setImagesData(mPhotoList);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WritingActivity.this, DraftActivity.class);
                Bundle bundle = new Bundle();
                if (TextUtils.isEmpty(etTitle.getText())) {
                    bundle.putString("TITLE", ""); /* 将Bundle对象assign给Intent */
                    //intent.putExtra("TITLE", "");
                } else {
                    //intent.putExtra("TITLE", etTitle.getText().toString());
                    bundle.putString("TITLE", etTitle.getText().toString());
                }
                if (TextUtils.isEmpty(articleContent)) {
                    bundle.putString("CONTENT", "");
                } else {
                    bundle.putString("CONTENT", articleContent);
                }

                intent.putExtras(bundle); /* 调用Activity EX03_11_1 */
                //startActivity(intent);
                startActivityForResult(intent, 0);
            }
        });
    }

    private void openGallery() {
        try {
            //公共配置都可以在application中配置，这里只是为了代码演示而写在此处
            ThemeConfig themeConfig = null;
            themeConfig = ThemeConfig.DEFAULT;

            FunctionConfig.Builder functionConfigBuilder = new FunctionConfig.Builder();
            cn.finalteam.galleryfinal.ImageLoader imageLoader;
            PauseOnScrollListener pauseOnScrollListener = null;

            imageLoader = new GlideLocalImageLoader();
            pauseOnScrollListener = new GlidePauseOnScrollListener(false, true);
            functionConfigBuilder.setMutiSelectMaxSize(9);
            functionConfigBuilder.setSelected(mPhotoList);//添加过滤集合
            final FunctionConfig functionConfig = functionConfigBuilder.build();


            CoreConfig coreConfig = new CoreConfig.Builder(WritingActivity.this, imageLoader, themeConfig)
                    //.setDebug(BuildConfig.DEBUG)
                    .setFunctionConfig(functionConfig)
                    .setPauseOnScrollListener(pauseOnScrollListener)
                    .setNoAnimcation(false)
                    .build();
            GalleryFinal.init(coreConfig);
            GalleryFinal.openGalleryMuti(REQUEST_CODE_GALLERY, functionConfig, mOnHanlderResultCallback);
        } catch (Exception e) {
            LogUtil.e(TAG, "openGallery error", e);
        }
    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            try {
                if (resultList != null) {
                    mPhotoList = resultList;
                    ngivGallery.setImagesData(mPhotoList);
                    ngivGallery.setTag(0, mPhotoList);
                    if (resultList == null || resultList.size() == 0) {
                        etTitle.setText("Noooooooooo");
                        Toast.makeText(WritingActivity.this, "Noooooooooo", Toast.LENGTH_LONG).show();
                    } else {
                        String photoStr = "";
                        for (PhotoInfo ph : resultList) {
                            photoStr += ph.getPhotoPath() + ";";
                        }
                        etTitle.setText(photoStr);
                        Toast.makeText(WritingActivity.this, photoStr, Toast.LENGTH_LONG).show();
                    }
                }else {
                    etTitle.setText("Nullnullllll");
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "OnHanlderResultCallback error", e);
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(WritingActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_writing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_publish:
                if (TextUtils.isEmpty(etTitle.getText().toString())) {
//                    new SweetAlertDialog(WritingActivity.this, SweetAlertDialog.ERROR_TYPE)
//                            .setTitleText("Oops...")
//                            .setContentText("Your article has no title!")
//                            .show();
                    Toast.makeText(WritingActivity.this, "Your article has no title!", Toast.LENGTH_SHORT).show();
                } else {
                    new SweetAlertDialog(WritingActivity.this, SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText("Do you wish to publish the article?")
                            .setContentText("You will be able to edit it again!")
                            .setConfirmText("Yes,publish it!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    /*
                                    Article article = new Article();
                                    article.setTitle(etTitle.getText().toString());
                                    if (tagsData != null && tagsData.size() > 0) {
                                        article.setTags(tagsData);
                                    }
                                    if (mPhotoList == null || mPhotoList.size() == 0 || TextUtils.isEmpty(mPhotoList.get(0).getPhotoPath())) {
                                        LogUtil.d(TAG, "onOptionsItemSelected debug, action_publish, Photo List = NULL");
                                    } else {
                                        List<ArticlePhoto> photos = new ArrayList<ArticlePhoto>();
                                        for (PhotoInfo photoInfo : mPhotoList) {
                                            ArticlePhoto photo = new ArticlePhoto(null, photoInfo.getPhotoPath());
                                            photos.add(photo);
                                        }
                                        article.setPhotos(photos);
                                    }
                                    if (!TextUtils.isEmpty(articleContent)) {
                                        article.setContent(articleContent);
                                    }
                                    */
                                    //Article article = new Article();
                                    //article.get
                                    List<PhotoInfo> dataStorage = (List<PhotoInfo>) ngivGallery.getTag(0);
                                    if (dataStorage == null || dataStorage.size() == 0 || TextUtils.isEmpty(dataStorage.get(0).getPhotoPath())) {
                                        Toast.makeText(WritingActivity.this, "Er123, nonono", Toast.LENGTH_LONG).show();
                                    }else {
                                        for (PhotoInfo photoInfo : dataStorage) {
                                            String photopath = photoInfo.getPhotoPath();
                                            photopaths.add(photopath);
                                        }
                                    }
//                                    if(photopaths == null || photopaths.size() == 0){
//                                        Toast.makeText(WritingActivity.this, "Noooooooo", Toast.LENGTH_LONG).show();
//                                    }else {
//                                        Toast.makeText(WritingActivity.this, photopaths.get(0), Toast.LENGTH_LONG).show();
//                                    }

                                    sDialog.dismissWithAnimation();
                                    new PulblicTask(WritingActivity.this).execute();
//                                    sDialog.setTitleText("Published!")
//                                            .setContentText("Your article has been published!")
//                                            .setConfirmText("OK")
//                                            .setConfirmClickListener(null)
//                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                }
                            })
                            .show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Bundle bunde = data.getExtras();
                    String title = bunde.getString("TITLE");
                    String content = bunde.getString("CONTENT");
                    articleContent = content;

                    SpannableStringBuilder builder = new SpannableStringBuilder();
                    builder.append(KnifeParser.fromHtml(content));
                    //((TextView) findViewById(R.id.tv_writing_storage)).setText(builder.toString());
                    LogUtil.d(TAG, "onActivityResult debug, requestCode = 0, Article's Title = " + title + " & Content = " + content);
                }
                break;
            default:
                break;
        }
    }

    class PulblicTask extends NetAsyncTask {

        protected PulblicTask(Context ctx) {
            super(ctx);
        }

        @Override
        protected void showProgress(boolean isDone) {
            showProgressBar(isDone);
        }

        @Override
        protected void doInBack() throws Exception {
            //LeanCloudDataService.publishArticle(etTitle.getText().toString(), tagsData, photopaths, articleContent, AVUser.getCurrentUser());
            LeanCloudDataService.test2(photopaths);
        }

        @Override
        protected void onPost(Exception e) {
            showProgressBar(false);
            if (e == null) {
                new SweetAlertDialog(WritingActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Yes...")
                        .setContentText("Your article has been published!")
                        .show();
            } else {
                new SweetAlertDialog(WritingActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(e.getMessage())
                        .show();
            }

        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgressBar(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
