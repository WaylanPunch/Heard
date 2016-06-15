package com.way.heard.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

import com.avos.avoscloud.AVException;
import com.way.heard.R;
import com.way.heard.adapters.BaseListAdapter;
import com.way.heard.ui.views.xlist.XListView;
import com.way.heard.services.LeanCloudBackgroundTask;
import com.way.heard.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2016/5/1.
 */
public class BaseListView<T> extends XListView implements XListView.IXListViewListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static final int ONE_PAGE_SIZE = 15;
    BaseListAdapter<T> adapter;
    DataInterface<T> dataInterface = new DataInterface<T>();
    private boolean toastIfEmpty = true;

    public BaseListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(DataInterface<T> dataInterface, BaseListAdapter<T> adapter) {
        this.dataInterface = dataInterface;
        this.adapter = adapter;
        setAdapter(adapter);
        setXListViewListener(this);
        setOnItemClickListener(this);
        setOnItemLongClickListener(this);
        setPullLoadEnable(true);
        setPullRefreshEnable(true);
    }

    public static class DataInterface<T> {
        public List<T> getDatas(int skip, int limit, List<T> currentDatas) throws AVException {
            return new ArrayList<T>();
        }

        public void onItemSelected(T item) {
        }

        public void onItemLongPressed(T item) {
        }
    }

    @Override
    public void onRefresh() {
        loadDatas(false);
    }

    public void loadDatas(final boolean loadMore) {
        final int skip;
        if (loadMore) {
            skip = adapter.getCount();
        } else {
            if (getPullRefreshing() == false) {
                pullRefreshing();
            }
            skip = 0;
        }
        new LeanCloudBackgroundTask(getContext()) {
            List<T> datas;

            @Override
            protected void onPre() {

            }

            @Override
            protected void doInBack() throws AVException {
                if (dataInterface != null) {
                    datas = dataInterface.getDatas(skip, ONE_PAGE_SIZE, adapter.getDatas());
                } else {
                    datas = new ArrayList<T>();
                }
            }

            @Override
            protected void onPost(AVException e) {
                if (e != null) {
                    e.printStackTrace();
                    Util.toast(getContext(), e.getMessage());
                } else {
                    if (loadMore == false) {
                        stopRefresh();
                        adapter.setDatas(datas);
                        adapter.notifyDataSetChanged();
                        if (datas != null) {
                            if(datas.size() < ONE_PAGE_SIZE) {
                                if (isToastIfEmpty()) {
                                    if (datas.size() == 0) {
                                        Util.toast(getContext(), R.string.listEmptyHint);
                                    }
                                }
                                setPullLoadEnable(false);
                            }
                        } else {
                            setPullLoadEnable(true);
                        }
                    } else {
                        stopLoadMore();
                        adapter.addAll(datas);
                        if (datas.size() == 0) {
                            Util.toast(getContext(), R.string.noMore);
                        }
                    }
                }
            }

            @Override
            protected void onCancel() {
                //loading.stop();
            }
        }.execute();
    }

    @Override
    public void onLoadMore() {
        loadDatas(true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        T item = (T) parent.getAdapter().getItem(position);
        if (dataInterface != null) {
            dataInterface.onItemSelected(item);
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        T item = (T) parent.getAdapter().getItem(position);
        if (dataInterface != null) {
            dataInterface.onItemLongPressed(item);
        }
        return false;
    }


    public boolean isToastIfEmpty() {
        return toastIfEmpty;
    }

    public void setToastIfEmpty(boolean toastIfEmpty) {
        this.toastIfEmpty = toastIfEmpty;
    }
}
