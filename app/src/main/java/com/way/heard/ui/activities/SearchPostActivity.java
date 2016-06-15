package com.way.heard.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.victor.loading.rotate.RotateLoading;
import com.way.heard.R;
import com.way.heard.adapters.PostAdapter;
import com.way.heard.models.Image;
import com.way.heard.models.Post;
import com.way.heard.services.LeanCloudBackgroundTask;
import com.way.heard.services.LeanCloudDataService;
import com.way.heard.utils.InitiateSearch;
import com.way.heard.utils.LogQuickSearchData.LogQuickSearch;
import com.way.heard.utils.LogQuickSearchData.LogQuickSearchAdapter;
import com.way.heard.utils.LogUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchPostActivity extends AppCompatActivity {
    private final static String TAG = SearchPostActivity.class.getName();
    public static final int POST_REPOST_REQUEST = 1010;

    private InitiateSearch initiateSearch;
    private View line_divider;
    private RelativeLayout view_search;
    private CardView card_search;
    private ImageView image_search_back, clearSearch;
    private EditText edit_text_search;
    private ListView listView;
    private RecyclerView listContainer;
    private LogQuickSearchAdapter logQuickSearchAdapter;
    private Set<String> set;
    private List<Post> searchPosts;
    private PostAdapter searchPostAdapter;
    private RotateLoading loading;
    private LeanCloudBackgroundTask backgroundTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_post);

        initView();
    }

    private void initView() {
        initiateSearch = new InitiateSearch();
        view_search = (RelativeLayout) findViewById(R.id.view_search);
        line_divider = findViewById(R.id.line_divider);
        edit_text_search = (EditText) findViewById(R.id.edit_text_search);
        card_search = (CardView) findViewById(R.id.card_search);
        image_search_back = (ImageView) findViewById(R.id.image_search_back);
        clearSearch = (ImageView) findViewById(R.id.clearSearch);
        listView = (ListView) findViewById(R.id.listView);
        listContainer = (RecyclerView) findViewById(R.id.listContainer);
        loading = (RotateLoading) findViewById(R.id.loading);
        logQuickSearchAdapter = new LogQuickSearchAdapter(this, 0, LogQuickSearch.all());

        searchPosts = new ArrayList<>();
        listView.setAdapter(logQuickSearchAdapter);
        set = new HashSet<>();

        initAdapter();
        initTypeFace();
        initSearchView();
        initSearchHandle();
        isAdapterEmpty();
    }

    private void initAdapter(){
        searchPostAdapter = new PostAdapter(SearchPostActivity.this);
        searchPostAdapter.setPosts(searchPosts);
        searchPostAdapter.setOnImageClickListener(new PostAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(int pos) {
                LogUtil.d(TAG, "onImageClick debug, Position = " + pos);
                Post post = searchPosts.get(pos);
                if (0 == post.getFrom()) {
                    Image image = post.tryGetPhotoList().get(0);
                    Intent intent = new Intent(SearchPostActivity.this, ImageDisplayActivity.class);
                    intent.putExtra(ImageDisplayActivity.IMAGE_POST_INDEX, pos);
                    intent.putExtra(ImageDisplayActivity.IMAGE_DETAIL, image);
                    startActivityForResult(intent, ImageDisplayActivity.IMAGE_DISPLAY_REQUEST);
                } else {
                    Post postOriginal = post.tryGetPostOriginal();
                    Image image = postOriginal.tryGetPhotoList().get(0);
                    Intent intent = new Intent(SearchPostActivity.this, ImageDisplayActivity.class);
                    intent.putExtra(ImageDisplayActivity.IMAGE_POST_INDEX, pos);
                    intent.putExtra(ImageDisplayActivity.IMAGE_DETAIL, image);
                    startActivityForResult(intent, ImageDisplayActivity.IMAGE_DISPLAY_REQUEST);
                }
            }
        });
        searchPostAdapter.setOnRepostClickListener(new PostAdapter.OnRepostClickListener() {
            @Override
            public void onRepostClick(int pos) {
                LogUtil.d(TAG, "onRepostClick debug, Position = " + pos);
                Post post = searchPosts.get(pos);
                if (0 == post.getFrom()) {
                    Intent intent = new Intent(SearchPostActivity.this, RepostActivity.class);
                    intent.putExtra(RepostActivity.REPOST_FROM, post.getFrom());
                    intent.putExtra(RepostActivity.POST_ORIGINAL_DETAIL, post);
                    intent.putExtra(RepostActivity.PHOTO_ORIGINAL_LIST, (ArrayList) post.tryGetPhotoList());
                    startActivityForResult(intent, POST_REPOST_REQUEST);
                } else {
                    Intent intent = new Intent(SearchPostActivity.this, RepostActivity.class);
                    intent.putExtra(RepostActivity.REPOST_FROM, post.getFrom());
                    intent.putExtra(RepostActivity.REPOST_DETAIL, post);
                    intent.putExtra(RepostActivity.POST_ORIGINAL_DETAIL, post.tryGetPostOriginal());
                    intent.putExtra(RepostActivity.PHOTO_ORIGINAL_LIST, (ArrayList) post.tryGetPostOriginal().tryGetPhotoList());
                    startActivityForResult(intent, POST_REPOST_REQUEST);
                }
            }
        });
        listContainer.setAdapter(searchPostAdapter);
    }

    private void initTypeFace() {
        Typeface roboto_regular = Typeface.createFromAsset(getAssets(), "fonts/RobotoSlab-Thin.ttf");
        edit_text_search.setTypeface(roboto_regular);
    }

    private void initSearchView() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogQuickSearch logQuickSearch = logQuickSearchAdapter.getItem(position);
                edit_text_search.setText(logQuickSearch.getName());
                listView.setVisibility(View.GONE);
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(edit_text_search.getWindowToken(), 0);

                getSearchResult(logQuickSearch.getName());
            }
        });
        listContainer.setHasFixedSize(false);
        listContainer.setLayoutManager(new LinearLayoutManager(SearchPostActivity.this));
//        listContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                AVUser user = searchUserAdapter.getItem(position);
//                ProfileActivity.go(SearchPostActivity.this, user);
//            }
//        });

        edit_text_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edit_text_search.getText().toString().length() == 0) {
                    logQuickSearchAdapter = new LogQuickSearchAdapter(SearchPostActivity.this, 0, LogQuickSearch.all());
                    listView.setAdapter(logQuickSearchAdapter);
                    clearSearch.setImageResource(R.drawable.ic_microphone_gray);
                    isAdapterEmpty();
                } else {
                    logQuickSearchAdapter = new LogQuickSearchAdapter(SearchPostActivity.this, 0, LogQuickSearch.FilterByName(edit_text_search.getText().toString()));
                    listView.setAdapter(logQuickSearchAdapter);
                    clearSearch.setImageResource(R.drawable.ic_close_gray);
                    isAdapterEmpty();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_text_search.getText().toString().length() == 0) {

                } else {
                    backgroundTask.cancel(true);
                    edit_text_search.setText("");
                    listView.setVisibility(View.VISIBLE);
                    clearItems();
                    ((InputMethodManager) SearchPostActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    isAdapterEmpty();
                }
            }
        });
    }

    private void initSearchHandle() {
        image_search_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateSearch.handleNoToolBar(SearchPostActivity.this, card_search, view_search, listView, edit_text_search, line_divider);
                listContainer.setVisibility(View.GONE);

                clearItems();

                onBackPressed();
            }
        });
        edit_text_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (edit_text_search.getText().toString().trim().length() > 0) {
                        clearItems();
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(edit_text_search.getWindowToken(), 0);
                        updateQuickSearch(edit_text_search.getText().toString());
                        listView.setVisibility(View.GONE);
                        getSearchResult(edit_text_search.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void updateQuickSearch(String item) {
        for (int i = 0; i < logQuickSearchAdapter.getCount(); i++) {
            LogQuickSearch ls = logQuickSearchAdapter.getItem(i);
            String name = ls.getName();
            set.add(name.toUpperCase());
        }
        if (set.add(item.toUpperCase())) {
            LogQuickSearch recentLog = new LogQuickSearch();
            recentLog.setName(item);
            recentLog.setDate(new Date());
            recentLog.save();
            logQuickSearchAdapter.add(recentLog);
            logQuickSearchAdapter.notifyDataSetChanged();
        }
    }

    private void clearItems() {
        listContainer.setVisibility(View.GONE);
        searchPosts.clear();
        searchPostAdapter.notifyDataSetChanged();
    }

    private void isAdapterEmpty() {
        if (logQuickSearchAdapter.getCount() == 0) {
            line_divider.setVisibility(View.GONE);
        } else {
            line_divider.setVisibility(View.VISIBLE);
        }
    }

    private void getSearchResult(final String query) {
        LogUtil.d(TAG, "getSearchResult debug");
        backgroundTask = new LeanCloudBackgroundTask(SearchPostActivity.this) {

            @Override
            protected void onPre() {
                loading.start();
            }

            @Override
            protected void doInBack() throws AVException {
                searchPosts = LeanCloudDataService.getAnyPublicPostsByContentFuzy(query);
                if (searchPosts == null) {
                    searchPosts = new ArrayList<Post>();
                }
            }

            @Override
            protected void onPost(AVException e) {
                loading.stop();
                LogUtil.d(TAG, "getSearchResult debug, Result Count = " + searchPosts.size());
                if (e == null) {
                    searchPostAdapter.setPosts(searchPosts);
                    searchPostAdapter.notifyDataSetChanged();
                    if (searchPosts != null && searchPosts.size() > 0) {
                        LogUtil.d(TAG, "getSearchResult debug, View.VISIBLE");
                        TranslateAnimation slide = new TranslateAnimation(0, 0, listContainer.getHeight(), 0);
                        slide.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                listContainer.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        slide.setDuration(300);
                        listContainer.startAnimation(slide);
                        listContainer.setVerticalScrollbarPosition(0);
                        //listContainer.setSelected(true);
                    } else {
                        listContainer.setVisibility(View.GONE);
                    }
                } else {
                    listContainer.setVisibility(View.GONE);
                    Toast.makeText(SearchPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected void onCancel() {
                loading.stop();
            }
        };

        backgroundTask.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(TAG, "onActivityResult debug, Request Code = " + requestCode + ", Result Code = " + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ImageDisplayActivity.IMAGE_DISPLAY_REQUEST) {
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
                getSearchResult(edit_text_search.getText().toString());
            }
        }
    }

    public static void go(Context context) {
        Intent intent = new Intent(context, SearchPostActivity.class);
        context.startActivity(intent);
    }
}
