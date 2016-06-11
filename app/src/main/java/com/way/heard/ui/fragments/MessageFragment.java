package com.way.heard.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.way.heard.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {
    private final static String TAG = MessageFragment.class.getName();

    public static final String MESSAGE = "Message";
//    private ViewPager viewPager;
//    private TabLayout tabLayout;


    public MessageFragment() {
        // Required empty public constructor
    }

    public static MessageFragment newInstance(int param) {
        MessageFragment messageFragment = new MessageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("MessageParam", param);
        messageFragment.setArguments(bundle);
        return messageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int param = getArguments().getInt("MessageParam");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        viewPager = (ViewPager) view.findViewById(R.id.pager);
//        tabLayout = (TabLayout) view.findViewById(R.id.tablayout);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        initTabLayout();
    }

    /*
    private void initTabLayout() {
        LogUtil.d(TAG, "initTabLayout debug");
        try {
            String[] tabList = new String[]{"会话", "联系人", "我"};
            final Fragment[] fragmentList = new Fragment[]{new ConversationFragment(),
                    new ContactFragment(), new PersonalProfileFragment()};

            tabLayout.setTabMode(TabLayout.MODE_FIXED);
            for (int i = 0; i < tabList.length; i++) {
                tabLayout.addTab(tabLayout.newTab().setText(tabList[i]));
            }

            TabFragmentAdapter adapter = new TabFragmentAdapter(getChildFragmentManager(),
                    Arrays.asList(fragmentList), Arrays.asList(tabList));
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    if (0 == position) {
                        //EventBus.getDefault().post(new ConversationFragmentUpdateEvent());
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.setTabsFromPagerAdapter(adapter);
        } catch (Exception e) {
            LogUtil.e(TAG, "initTabLayout error", e);
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    */

    /*
    public class TabFragmentAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> mFragments;
        private List<String> mTitles;

        public TabFragmentAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
            super(fm);
            mFragments = fragments;
            mTitles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }
    }
    */
}
