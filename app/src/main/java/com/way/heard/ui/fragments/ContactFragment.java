package com.way.heard.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.way.heard.R;
import com.way.heard.adapters.ContactTabAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment{
    private final static String TAG = ContactFragment.class.getName();

    public static final String CONTACT = "Contact";


    private TabLayout tabTile;                            //定义TabLayout
    private ViewPager vpPager;                             //定义viewPager
    private FragmentPagerAdapter fAdapter;                               //定义adapter

    private List<Fragment> list_fragment;                                //定义要装fragment的列表
    private List<String> list_title;                                     //tab名称列表

    private FolloweeFragment followeeFragment;              //Followee Fragment
    private FollowerFragment followerFragment;            //Follower Fragment

    public ContactFragment() {
        // Required empty public constructor
    }

    public static ContactFragment newInstance(int param) {
        ContactFragment contactFragment = new ContactFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("ContactParam", param);
        contactFragment.setArguments(bundle);
        return contactFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int param = getArguments().getInt("ContactParam");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.tabTile = (TabLayout) view.findViewById(R.id.tab_contact_title);
        this.vpPager = (ViewPager) view.findViewById(R.id.vp_contact_pager);

        initData();
    }

    private void initData() {
        followeeFragment = FolloweeFragment.newInstance(0);
        followerFragment = FollowerFragment.newInstance(1);
        //将fragment装进列表中
        list_fragment = new ArrayList<>();
        list_fragment.add(followeeFragment);
        list_fragment.add(followerFragment);
        list_title = new ArrayList<>();
        list_title.add("Followee");
        list_title.add("Follower");

        //设置TabLayout的模式
        tabTile.setTabMode(TabLayout.MODE_FIXED);
        //为TabLayout添加tab名称
        tabTile.addTab(tabTile.newTab().setText(list_title.get(0)));
        tabTile.addTab(tabTile.newTab().setText(list_title.get(1)));

        fAdapter = new ContactTabAdapter(getActivity().getSupportFragmentManager(), list_fragment, list_title);

        //viewpager加载adapter
        vpPager.setAdapter(fAdapter);
        //tab_FindFragment_title.setViewPager(vp_FindFragment_pager);
        //TabLayout加载viewpager
        tabTile.setupWithViewPager(vpPager);
    }


}
