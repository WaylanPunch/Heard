package com.way.heard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.way.heard.R;
import com.way.heard.models.SearchItem;

import java.util.ArrayList;

/**
 * Created by pc on 2016/6/14.
 */
public class SearchAdapter extends ArrayAdapter<SearchItem> {

    private final Context mContext;
    private final ArrayList<SearchItem> mSearchItem;

    public SearchAdapter(Context context, ArrayList<SearchItem> itemsArrayList) {
        super(context, R.layout.list_search_row, itemsArrayList);
        this.mContext = context;
        this.mSearchItem = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.list_search_row, parent, false);
        TextView mFoodName = (TextView) v.findViewById(R.id.food_name);
        TextView mBrand = (TextView) v.findViewById(R.id.food_brand);
        mFoodName.setText(mSearchItem.get(position).getTitle());
        mBrand.setText(mSearchItem.get(position).getBrand());
        RelativeLayout back = (RelativeLayout) v.findViewById(R.id.list_back);
        return v;
    }

}