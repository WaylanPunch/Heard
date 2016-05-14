package com.way.heard.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchItem;
import com.lapism.searchview.SearchView;
import com.way.heard.R;
import com.way.heard.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchTagActivity extends AppCompatActivity {
    private final static String TAG = SearchTagActivity.class.getName();

    public final static String SEARCH_TYPE = "SearchType";
    public final static String SEARCH_RESULT = "SearchResult";
    public final static int SEARCH_TYPE_TAG = 0;
    public final static int SEARCH_TYPE_LOCATION = 1;

    private Intent mIntent;
    protected SearchView mSearchView;

    private static String mSearchResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tag);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        getParamData();

        mSearchView = (SearchView) findViewById(R.id.searchView);
        setSearchView();
    }

    private void getParamData() {
        mIntent = getIntent();
        int searchtype = mIntent.getIntExtra(SEARCH_TYPE, -1);
        LogUtil.d(TAG, "getParamData debug, SEARCH_TYPE = " + searchtype);
    }

    private void setSearchView() {
        if (mSearchView != null) {
            //waylan punch
//            mSearchView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
//            mSearchView.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
//            mSearchView.setIconColor(ContextCompat.getColor(this, R.color.colorWhite));
//            mSearchView.setHintColor(ContextCompat.getColor(this, R.color.colorWhite));
//            mSearchView.setShadowColor(ContextCompat.getColor(this, R.color.colorWhite));
            mSearchView.addFocus();
            mSearchView.setVersion(SearchView.VERSION_TOOLBAR);
            mSearchView.setVersionMargins(SearchView.VERSION_MARGINS_TOOLBAR_BIG);
            mSearchView.setHint("Search");
            mSearchView.setTextSize(16);
            mSearchView.setDivider(false);
            mSearchView.setVoice(true);
            mSearchView.setVoiceText("Set permission on Android 6+ !");
            mSearchView.setAnimationDuration(SearchView.ANIMATION_DURATION);
            mSearchView.setShadowColor(ContextCompat.getColor(this, R.color.search_shadow_layout));
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    mSearchView.close(false);
                    mSearchResult = query;
                    turnBack();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    mSearchResult = newText;
                    return false;
                }
            });
            mSearchView.setOnOpenCloseListener(new SearchView.OnOpenCloseListener() {
                @Override
                public void onOpen() {
//                    if (mFab != null) {
//                        mFab.hide();
//                    }
                }

                @Override
                public void onClose() {
//                    if (mFab != null) {
//                        mFab.show();
//                    }
                }
            });

            List<SearchItem> suggestionsList = new ArrayList<>();
            suggestionsList.add(new SearchItem("search1"));
            suggestionsList.add(new SearchItem("search2"));
            suggestionsList.add(new SearchItem("search3"));

            SearchAdapter searchAdapter = new SearchAdapter(this, suggestionsList);
            searchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    mSearchView.close(false);
                    TextView textView = (TextView) view.findViewById(R.id.textView_item_text);
                    String query = textView.getText().toString();
                    mSearchResult = query;
                    turnBack();
                }
            });

            mSearchView.setAdapter(searchAdapter);
        }
    }

    public static void go(Activity activity, int searchType, int requestCode) {
        Intent intent = new Intent(activity, SearchTagActivity.class);
        intent.putExtra(SearchTagActivity.SEARCH_TYPE, searchType);
        activity.startActivityForResult(intent, requestCode);
    }

    private void turnBack() {
        mIntent.putExtra(SEARCH_RESULT, mSearchResult);
        setResult(RESULT_OK, mIntent);
        finish();
    }
}
