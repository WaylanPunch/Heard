package com.way.heard.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.way.heard.R;
import com.way.heard.services.CustomUserProvider;
import com.way.heard.services.LeanCloudBackgroundTask;
import com.way.heard.services.LeanCloudDataService;
import com.way.heard.ui.fragments.ContactFragment;
import com.way.heard.utils.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.LCChatKitUser;
import cn.leancloud.chatkit.activity.LCIMConversationActivity;
import cn.leancloud.chatkit.activity.LCIMConversationListFragment;
import cn.leancloud.chatkit.utils.LCIMConstants;

public class ChatActivity extends BaseActivity {
    private final static String TAG = ChatActivity.class.getName();

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initView();
        initTabLayout();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.vp_chat_pager);
        tabLayout = (TabLayout) findViewById(R.id.tl_chat_tablayout);
    }

    private void initTabLayout() {
        String[] tabList = new String[]{"会话", "联系人"};
        final Fragment[] fragmentList = new Fragment[]{new LCIMConversationListFragment(),
                new ContactFragment()};

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        for (int i = 0; i < tabList.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(tabList[i]));
        }

        TabFragmentAdapter adapter = new TabFragmentAdapter(getSupportFragmentManager(),
                Arrays.asList(fragmentList), Arrays.asList(tabList));
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (0 == position) {
                    // EventBus.getDefault().post(new ConversationFragmentUpdateEvent());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        if (menuId == R.id.action_square) {
            gotoSquareConversation();
        }
        return super.onOptionsItemSelected(item);
    }


    private void gotoSquareConversation() {
        LogUtil.d(TAG, "gotoSquareConversation debug");

        List<LCChatKitUser> userList = CustomUserProvider.getInstance().getAllUsers();
        if (userList != null && userList.size() > 0) {
            LogUtil.d(TAG, "gotoSquareConversation debug, CustomUserProvider.getInstance().getAllUsers() != NULL");
            List<String> idList = new ArrayList<>();
            for (LCChatKitUser user : userList) {
                idList.add(user.getUserId());
            }

            LCChatKit.getInstance().getClient().createConversation(
                    idList, getString(R.string.square), null, false, true, new AVIMConversationCreatedCallback() {
                        @Override
                        public void done(AVIMConversation avimConversation, AVIMException e) {
                            Intent intent = new Intent(ChatActivity.this, LCIMConversationActivity.class);
                            intent.putExtra(LCIMConstants.CONVERSATION_ID, avimConversation.getConversationId());
                            startActivity(intent);
                        }
                    });
        } else {
            LogUtil.d(TAG, "gotoSquareConversation debug, CustomUserProvider.getInstance().getAllUsers() == NULL, Retrieve.");
            new LeanCloudBackgroundTask(ChatActivity.this) {
                List<LCChatKitUser> partUsers = new ArrayList<LCChatKitUser>();

                @Override
                protected void onPre() {

                }

                @Override
                protected void doInBack() throws AVException {
                    if (partUsers == null) {
                        partUsers = new ArrayList<LCChatKitUser>();
                    }
                    List<AVUser> friends = LeanCloudDataService.getAllMyFriends(AVUser.getCurrentUser().getObjectId());
                    if (friends != null && friends.size() > 0) {
                        for (AVUser user : friends) {
                            String userid = user.getUsername();
                            String username = user.getUsername();
                            String avatar = user.getString("avatar");
                            LCChatKitUser chatKitUser = new LCChatKitUser(userid, username, avatar);
                            partUsers.add(chatKitUser);
                        }
                    }
                }

                @Override
                protected void onPost(AVException e) {
                    if (e == null) {
                        CustomUserProvider.getInstance().setAllUsers(partUsers);
                        if (partUsers != null && partUsers.size() > 0) {
                            List<String> idList = new ArrayList<>();
                            for (LCChatKitUser user : partUsers) {
                                idList.add(user.getUserId());
                            }

                            LCChatKit.getInstance().getClient().createConversation(
                                    idList, getString(R.string.square), null, false, true, new AVIMConversationCreatedCallback() {
                                        @Override
                                        public void done(AVIMConversation avimConversation, AVIMException e) {
                                            Intent intent = new Intent(ChatActivity.this, LCIMConversationActivity.class);
                                            intent.putExtra(LCIMConstants.CONVERSATION_ID, avimConversation.getConversationId());
                                            startActivity(intent);
                                        }
                                    });
                        } else {
                            Toast.makeText(ChatActivity.this, "No Friends At All", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        }


    }

    public static void go(Context context) {
        Intent intent = new Intent(context, ChatActivity.class);
        //intent.putExtra(ChatActivity.USER_DETAIL, user);
        context.startActivity(intent);
    }


}
