package com.way.heard.ui.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.avos.avoscloud.AVAnalytics;
import com.way.heard.R;
import com.way.heard.ui.fragments.BookmarkFragment;
import com.way.heard.ui.fragments.FindFragment;
import com.way.heard.ui.fragments.HomeFragment;
import com.way.heard.ui.fragments.MeFragment;
import com.way.heard.ui.fragments.MessageFragment;
import com.way.heard.ui.fragments.SettingFragment;
import com.way.heard.utils.LogUtil;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final static String TAG = MainActivity.class.getName();

    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    private FindFragment findFragment;
    private BookmarkFragment bookmarkFragment;
    private MessageFragment messageFragment;
    private MeFragment meFragment;
    private SettingFragment settingFragment;
    private Toolbar toolbar;
    private DrawerLayout drawer;

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
    }

    private void setLeanCloudAnalytics() {
        AVAnalytics.trackAppOpened(getIntent());
    }

    private void setContentFragment() {
        LogUtil.d(TAG, "setContentFragment debug");
        fragmentManager = getSupportFragmentManager();
        homeFragment = HomeFragment.newInstance(1);

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit();
    }

    private void setDrawerLayout() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

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
    }

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
                LogUtil.d(TAG, "replaceFragment debug, BOOKMARK, Index = " + index);
                if (bookmarkFragment == null) {
                    bookmarkFragment = BookmarkFragment.newInstance(3);
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, bookmarkFragment)
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
            return true;
        } else if (id == R.id.action_notification) {
            return true;
        } else if (id == R.id.action_share) {
            return true;
        } else if (id == R.id.action_developer) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            replaceFragment(1);
        } else if (id == R.id.nav_find) {
            replaceFragment(2);
        } else if (id == R.id.nav_bookmark) {
            replaceFragment(3);
        } else if (id == R.id.nav_message) {
            replaceFragment(4);
        } else if (id == R.id.nav_me) {
            replaceFragment(5);
        } else if (id == R.id.nav_setting) {
            replaceFragment(6);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
