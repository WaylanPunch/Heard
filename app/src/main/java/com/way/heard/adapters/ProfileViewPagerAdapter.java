package com.way.heard.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by pc on 2016/5/22.
 */
public class ProfileViewPagerAdapter extends FragmentPagerAdapter {

    private int PAGE_COUNT;
    private List<Fragment> fragments;
    private String[] tabTitles;
    private Context context;

    public ProfileViewPagerAdapter(FragmentManager fm, Context context, List<Fragment> fragments1, String[] titles) {
        super(fm);
        this.context = context;
        this.fragments = fragments1;
        this.tabTitles = titles;
        this.PAGE_COUNT = fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        if (fragments != null && fragments.size() > 0)
            return fragments.get(position);
        else
            return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
