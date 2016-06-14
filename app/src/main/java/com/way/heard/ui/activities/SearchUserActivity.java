package com.way.heard.ui.activities;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import com.avos.avoscloud.AVUser;
import com.victor.loading.rotate.RotateLoading;
import com.way.heard.R;
import com.way.heard.adapters.SearchUserAdapter;
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

public class SearchUserActivity extends AppCompatActivity {
    private final static String TAG = SearchUserActivity.class.getName();

    private InitiateSearch initiateSearch;
    private View line_divider;
    private RelativeLayout view_search;
    private CardView card_search;
    private ImageView image_search_back, clearSearch;
    private EditText edit_text_search;
    private ListView listView, listContainer;
    private LogQuickSearchAdapter logQuickSearchAdapter;
    private Set<String> set;
    private List<AVUser> searchUsers;
    private SearchUserAdapter searchUserAdapter;
    private RotateLoading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

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
        listContainer = (ListView) findViewById(R.id.listContainer);
        loading = (RotateLoading) findViewById(R.id.loading);
        logQuickSearchAdapter = new LogQuickSearchAdapter(this, 0, LogQuickSearch.all());

        searchUsers = new ArrayList<>();
        searchUserAdapter = new SearchUserAdapter(this, searchUsers);
        listView.setAdapter(logQuickSearchAdapter);
        listContainer.setAdapter(searchUserAdapter);
        set = new HashSet<>();

        initTypeFace();
        initSearchView();
        initSearchHandle();
        isAdapterEmpty();
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
        edit_text_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edit_text_search.getText().toString().length() == 0) {
                    logQuickSearchAdapter = new LogQuickSearchAdapter(SearchUserActivity.this, 0, LogQuickSearch.all());
                    listView.setAdapter(logQuickSearchAdapter);
                    clearSearch.setImageResource(R.drawable.ic_microphone_gray);
                    isAdapterEmpty();
                } else {
                    logQuickSearchAdapter = new LogQuickSearchAdapter(SearchUserActivity.this, 0, LogQuickSearch.FilterByName(edit_text_search.getText().toString()));
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
                    edit_text_search.setText("");
                    listView.setVisibility(View.VISIBLE);
                    clearItems();
                    ((InputMethodManager) SearchUserActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    isAdapterEmpty();
                }
            }
        });
    }

    private void initSearchHandle() {
        image_search_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateSearch.handleNoToolBar(SearchUserActivity.this, card_search, view_search, listView, edit_text_search, line_divider);
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
        searchUsers.clear();
        searchUserAdapter.notifyDataSetChanged();
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
        new LeanCloudBackgroundTask(SearchUserActivity.this) {

            @Override
            protected void onPre() {
                loading.start();
            }

            @Override
            protected void doInBack() throws AVException {
                searchUsers = LeanCloudDataService.getAnyUserByUsernameFuzy(query);
                if (searchUsers == null) {
                    searchUsers = new ArrayList<AVUser>();
                }
            }

            @Override
            protected void onPost(AVException e) {
                loading.stop();
                LogUtil.d(TAG, "getSearchResult debug, Result Count = " + searchUsers.size());
                if (e == null) {
                    searchUserAdapter.setData(searchUsers);
                    searchUserAdapter.notifyDataSetChanged();
                    if (searchUsers != null && searchUsers.size() > 0) {
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
                        listContainer.setSelection(0);
                    } else {
                        listContainer.setVisibility(View.GONE);
                    }
                } else {
                    listContainer.setVisibility(View.GONE);
                    Toast.makeText(SearchUserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
