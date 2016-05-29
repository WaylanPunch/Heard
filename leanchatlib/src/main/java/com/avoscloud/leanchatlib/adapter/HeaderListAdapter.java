package com.avoscloud.leanchatlib.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.avoscloud.leanchatlib.viewholder.CommonFooterItemHolder;
import com.avoscloud.leanchatlib.viewholder.CommonHeaderItemHolder;
import com.avoscloud.leanchatlib.viewholder.CommonViewHolder;

/**
 * Created by wli on 15/11/25.
 * 现在还仅仅支持单类型 item，多类型 item 稍后在重构
 */
public class HeaderListAdapter<T> extends CommonListAdapter<T> {

  public static final int HEADER_ITEM_TYPE = -1;
  public static final int FOOTER_ITEM_TYPE = -2;
  public static final int COMMON_ITEM_TYPE = 1;

  private View headerView = null;
  private View footerView = null;

  public HeaderListAdapter(Class<?> vhClass) {
    super(vhClass);
  }

  public void setHeaderView(View view) {
    headerView = view;
  }

  public void setFooterView(View view) {
    footerView = view;
  }

  @Override
  public int getItemCount() {
    int itemCount = super.getItemCount();
    if (null != headerView) {
      ++itemCount;
    }
    if (null != footerView) {
      ++itemCount;
    }
    return itemCount;
  }

  @Override
  public long getItemId(int position) {
    if (position == 0 && position == getItemCount() - 1) {
      return -1;
    }
    return super.getItemId(position);
  }

  @Override
  public int getItemViewType(int position) {
    if (null != headerView && 0 == position) {
      return HEADER_ITEM_TYPE;
    }

    if (null != footerView && position == getItemCount() - 1) {
      return FOOTER_ITEM_TYPE;
    }

    return COMMON_ITEM_TYPE;
  }

  @Override
  public void onBindViewHolder(CommonViewHolder holder, int position) {
    if (position == 0 && position == getItemCount() - 1) {
      return;
    }
    super.onBindViewHolder(holder, position);
  }

  @Override
  public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (HEADER_ITEM_TYPE == viewType) {
      CommonHeaderItemHolder itemHolder = new CommonHeaderItemHolder(parent.getContext(), parent);
      itemHolder.setView(headerView);
      return itemHolder;
    }

    if (FOOTER_ITEM_TYPE == viewType) {
      CommonFooterItemHolder itemHolder = new CommonFooterItemHolder(parent.getContext(), parent);
      itemHolder.setView(footerView);
      return itemHolder;
    }

    return super.onCreateViewHolder(parent, viewType);
  }
}
