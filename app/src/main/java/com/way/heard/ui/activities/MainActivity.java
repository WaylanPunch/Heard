package com.way.heard.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.way.heard.R;
import com.way.heard.ui.fragments.FindFragment;
import com.way.heard.ui.fragments.HomeFragment;
import com.way.heard.ui.fragments.MeFragment;
import com.way.heard.ui.fragments.MessageFragment;
import com.way.heard.ui.fragments.SettingFragment;
import com.way.heard.ui.fragments.TopicFragment;
import com.way.heard.utils.GlideImageLoader;
import com.way.heard.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final static String TAG = MainActivity.class.getName();

    private FragmentManager fragmentManager;

    private HomeFragment homeFragment;
    private FindFragment findFragment;
    private TopicFragment topicFragment;
    private MessageFragment messageFragment;
    private MeFragment meFragment;
    private SettingFragment settingFragment;

    private Fragment mContent;
    private List<String> tags;

    private Toolbar toolbar;
    private DrawerLayout drawer;

    /**
     * 上一次点击 back 键的时间
     * 用于双击退出的判断
     */
    private static long lastBackTime = 0;

    /**
     * 当双击 back 键在此间隔内是直接触发 onBackPressed
     */
    private final int BACK_INTERVAL = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setLeanCloudAnalytics();
        setContentFragment();
        setToolbar();
        setDrawerLayout();

        initView();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        //getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        toolbar.invalidate();
    }

    private void setLeanCloudAnalytics() {
        AVAnalytics.trackAppOpened(getIntent());
    }

    private void setContentFragment() {
        LogUtil.d(TAG, "setContentFragment debug");
        tags = new ArrayList<>();
        tags.add(HomeFragment.HOME);
        tags.add(FindFragment.FIND);
        tags.add(TopicFragment.TOPIC);
        tags.add(MessageFragment.MESSAGE);
        tags.add(MeFragment.ME);
        tags.add(SettingFragment.SETTING);

        fragmentManager = getSupportFragmentManager();
        homeFragment = HomeFragment.newInstance(0);

        mContent = homeFragment;
        fragmentManager.beginTransaction().add(R.id.fragment_container, mContent, tags.get(0)).commit();
        //switchContent(mContent, homeFragment, 0);
    }

    /**
     * fragment 切换
     *
     * @param newFragment
     * @param position
     */
    public void switchContent(Fragment newFragment, int position) {
        if (mContent != newFragment) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (!newFragment.isAdded()) { // 先判断是否被add过
                transaction.hide(mContent)
                        .add(R.id.fragment_container, newFragment, tags.get(position)).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(mContent).show(newFragment).commit(); // 隐藏当前的fragment，显示下一个
            }
            mContent = newFragment;
        }
    }


    private void setDrawerLayout() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //(R.drawable.app_icon);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private ImageView ivHeaderAvatar;
    private TextView tvHeaderUsername;
    private TextView tvHeaderEmail;

    private void initView() {
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        View headerLayout = navigationView.getHeaderView(0);
        ivHeaderAvatar = (ImageView) headerLayout.findViewById(R.id.iv_header_avatar);
        tvHeaderUsername = (TextView) headerLayout.findViewById(R.id.tv_header_username);
        tvHeaderEmail = (TextView) headerLayout.findViewById(R.id.tv_header_email);
        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser != null) {
            if (!TextUtils.isEmpty(currentUser.getString("avatar"))) {
                GlideImageLoader.displayImage(MainActivity.this, currentUser.getString("avatar"), ivHeaderAvatar);
            }
            if (!TextUtils.isEmpty(currentUser.getUsername())) {
                tvHeaderUsername.setText(currentUser.getUsername());
            }
            if (!TextUtils.isEmpty(currentUser.getEmail())) {
                tvHeaderEmail.setText(currentUser.getEmail());
            }
        }
    }

    /*
    private void replaceFragment(int index) {
        switch (index){
            case 1:
                LogUtil.d(TAG, "replaceFragment debug, HOME, Index = " + index);
                if (homeFragment == null) {
                    homeFragment = HomeFragment.newInstance(1);
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, homeFragment)
                        .commit();
                break;
            case 2:
                LogUtil.d(TAG, "replaceFragment debug, FIND, Index = " + index);
                if (findFragment == null) {
                    findFragment = FindFragment.newInstance(2);
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, findFragment)
                        .commit();
                break;
            case 3:
                LogUtil.d(TAG, "replaceFragment debug, TOPIC, Index = " + index);
                if (topicFragment == null) {
                    topicFragment = TopicFragment.newInstance(3);
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, topicFragment)
                        .commit();
                break;
            case 4:
                LogUtil.d(TAG, "replaceFragment debug, MESSAGE, Index = " + index);
                if (messageFragment == null) {
                    messageFragment = MessageFragment.newInstance(4);
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, messageFragment)
                        .commit();
                break;
            case 5:
                LogUtil.d(TAG, "replaceFragment debug, ME, Index = " + index);
                if (meFragment == null) {
                    meFragment = MeFragment.newInstance(5);
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, meFragment)
                        .commit();
                break;
            case 6:
                LogUtil.d(TAG, "replaceFragment debug, SETTING, Index = " + index);
                if (settingFragment == null) {
                    settingFragment = SettingFragment.newInstance(6);
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, settingFragment)
                        .commit();
                break;
            case 7:
                break;
            default:
                break;
        }


//        View view = findViewById(R.id.fragment_container);
//        int finalRadius = Math.max(view.getWidth(), view.getHeight());
//        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(view, 0, 400, 0, finalRadius);
//        animator.setInterpolator(new AccelerateInterpolator());
//        animator.setDuration(500);
//        //findViewById(R.id.main_content_overlay).setBackgroundDrawable(new BitmapDrawable(getResources(), settingFragment.getBitmap()));
//        animator.start();
    }
    */

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastBackTime < BACK_INTERVAL) {
                super.onBackPressed();
            } else {
                Toast.makeText(MainActivity.this, "Double Click To Exit", Toast.LENGTH_SHORT).show();
            }
            lastBackTime = currentTime;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            SearchPostActivity.go(MainActivity.this);
            return true;
        } else if (id == R.id.action_notification) {
            NotificationActivity.go(MainActivity.this);
            return true;
        } else if (id == R.id.action_me) {
//            Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
//            startActivity(intent);
            ProfileActivity.go(MainActivity.this, AVUser.getCurrentUser());
            return true;
        }
        else if (id == R.id.action_scan) {
            ScanActivity.go(MainActivity.this);
            return true;
        }
//        else if (id == R.id.action_developer) {
//            Intent intent = new Intent(MainActivity.this,TestActivity.class);
//            startActivity(intent);
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            //replaceFragment(1);
            if (homeFragment == null) {
                homeFragment = HomeFragment.newInstance(0);
            }
            switchContent(homeFragment, 0);
        } else if (id == R.id.nav_find) {
            //replaceFragment(2);
            if (findFragment == null) {
                findFragment = FindFragment.newInstance(1);
            }
            switchContent(findFragment, 1);
        } else if (id == R.id.nav_topic) {
            //replaceFragment(3);
            if (topicFragment == null) {
                topicFragment = TopicFragment.newInstance(2);
            }
            switchContent(topicFragment, 2);
        } else if (id == R.id.nav_message) {
            //replaceFragment(4);
            if (messageFragment == null) {
                messageFragment = MessageFragment.newInstance(3);
            }
            switchContent(messageFragment, 3);
        } else if (id == R.id.nav_me) {
            //replaceFragment(5);
            if (meFragment == null) {
                meFragment = MeFragment.newInstance(4);
            }
            switchContent(meFragment, 4);
        } else if (id == R.id.nav_setting) {
            //replaceFragment(6);
            if (settingFragment == null) {
                settingFragment = SettingFragment.newInstance(5);
            }
            switchContent(settingFragment, 5);
        } else if (id == R.id.nav_share) {
            //ScanActivity.go(MainActivity.this, null);
            shareSocial();
        } else if (id == R.id.nav_send) {
            toFeedbank();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void shareSocial() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, "发现一个好玩的应用，赶快来下载吧！http://fir.im/qs4a");
        intent.putExtra(Intent.EXTRA_TITLE, "Heard - Waylan Punch");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, "请选择"));
    }

    private void toFeedbank() {
        FeedbackAgent agent = new FeedbackAgent(MainActivity.this);
        agent.startDefaultThreadActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(TAG, "Request Code = " + requestCode + ", Result Code = " + resultCode);
    }


}
