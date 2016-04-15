package com.way.heard;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import com.way.heard.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.model.SlideMenuItem;
import yalantis.com.sidemenu.util.ViewAnimator;

public class MainActivity extends ActionBarActivity {
    private final static String TAG = MainActivity.class.getName();

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private List<SlideMenuItem> list = new ArrayList<>();
    private HomeFragment homeFragment;
    private FindFragment findFragment;
    private ContactFragment contactFragment;
    private ViewAnimator viewAnimator;
    //private int res = R.drawable.content_music;
    private LinearLayout linearLayout;
    ViewAnimator.ViewAnimatorListener viewAnimatorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContentFragment();
        setDrawerLayout();
        setToolBar();
        setMenuList();
        setViewAnimator();
    }


    private void setContentFragment() {
        LogUtil.d(TAG, "setContentFragment debug");
        homeFragment = HomeFragment.newInstance(1);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content_frame, homeFragment)
                .commit();
    }

    private void setDrawerLayout() {
        LogUtil.d(TAG, "setDrawerLayout debug");
        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        linearLayout = (LinearLayout) findViewById(R.id.main_left_drawer);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });
    }

    private void setToolBar() {
        LogUtil.d(TAG, "setToolBar debug");
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        //toolbar.setNavigationIcon(R.drawable.ic_menu_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                linearLayout.removeAllViews();
                linearLayout.invalidate();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (slideOffset > 0.6 && linearLayout.getChildCount() == 0)
                    viewAnimator.showMenuContent();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }

    private void setMenuList() {
        LogUtil.d(TAG, "setMenuList debug");
        SlideMenuItem menuItemClose = new SlideMenuItem(HomeFragment.CLOSE, R.drawable.icn_close);
        list.add(menuItemClose);
        SlideMenuItem menuItemHome = new SlideMenuItem(HomeFragment.HOME, R.drawable.icn_1);
        list.add(menuItemHome);
        SlideMenuItem menuItemFind = new SlideMenuItem(FindFragment.FIND, R.drawable.icn_2);
        list.add(menuItemFind);
        SlideMenuItem menuItemContact = new SlideMenuItem(ContactFragment.CONTACT, R.drawable.icn_3);
        list.add(menuItemContact);
        SlideMenuItem menuItem4 = new SlideMenuItem(HomeFragment.CASE, R.drawable.icn_4);
        list.add(menuItem4);
        SlideMenuItem menuItem5 = new SlideMenuItem(HomeFragment.SHOP, R.drawable.icn_5);
        list.add(menuItem5);
        SlideMenuItem menuItem6 = new SlideMenuItem(HomeFragment.PARTY, R.drawable.icn_6);
        list.add(menuItem6);
        //SlideMenuItem menuItem7 = new SlideMenuItem(HomeFragment.MOVIE, R.drawable.icn_7);
        //list.add(menuItem7);
    }

    private void setViewAnimator() {
        LogUtil.d(TAG, "setViewAnimator debug");
        viewAnimatorListener = new ViewAnimator.ViewAnimatorListener() {
            @Override
            public ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable, int position) {
                switch (slideMenuItem.getName()) {
                    case HomeFragment.CLOSE:
                        LogUtil.d(TAG, "setViewAnimator debug, CLOSE, Index = 0");
                        return screenShotable;
                    case HomeFragment.HOME:
                        LogUtil.d(TAG, "setViewAnimator debug, HOME, Index = 1");
                        return replaceFragment(screenShotable, 1, position);
                    case FindFragment.FIND:
                        LogUtil.d(TAG, "setViewAnimator debug, FIND, Index = 2");
                        return replaceFragment(screenShotable, 2, position);
                    case ContactFragment.CONTACT:
                        LogUtil.d(TAG, "setViewAnimator debug, CONTACT, Index = 3");
                        return replaceFragment(screenShotable, 3, position);
                    default:
                        LogUtil.d(TAG, "setViewAnimator debug, HOME, Index = 1");
                        return replaceFragment(screenShotable, 1, position);
                }
            }

            @Override
            public void disableHomeButton() {
                getSupportActionBar().setHomeButtonEnabled(false);

            }

            @Override
            public void enableHomeButton() {
                getSupportActionBar().setHomeButtonEnabled(true);
                drawerLayout.closeDrawers();

            }

            @Override
            public void addViewToContainer(View view) {
                linearLayout.addView(view);
            }
        };
        viewAnimator = new ViewAnimator<>(this, list, homeFragment, drawerLayout, viewAnimatorListener);
    }

    private ScreenShotable replaceFragment(ScreenShotable screenShotable, int index, int topPosition) {
        if(index == 0) {
            return screenShotable;
        }
        //this.res = this.res == R.drawable.content_music ? R.drawable.content_films : R.drawable.content_music;

        if(index == 1){
            LogUtil.d(TAG, "replaceFragment debug, HOME, Index = " + index);
            if(homeFragment == null) {
                homeFragment = HomeFragment.newInstance(1);
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.main_content_frame, homeFragment).commit();
            screenShotable = homeFragment;
        }else if(index == 2){
            LogUtil.d(TAG, "replaceFragment debug, FIND, Index = " + index);
            if(findFragment == null) {
                findFragment = FindFragment.newInstance(2);
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.main_content_frame, findFragment).commit();
            screenShotable = findFragment;
        }else if(index == 3){
            LogUtil.d(TAG, "replaceFragment debug, CONTACT, Index = " + index);
            if(contactFragment == null) {
                contactFragment = ContactFragment.newInstance(3);
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.main_content_frame, contactFragment).commit();
            screenShotable = contactFragment;
        }
        else {
            LogUtil.d(TAG, "replaceFragment debug, HOME, Index = " + index);
            if(homeFragment == null) {
                homeFragment = HomeFragment.newInstance(1);
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.main_content_frame, homeFragment).commit();
            screenShotable = homeFragment;
        }

        View view = findViewById(R.id.main_content_frame);
        int finalRadius = Math.max(view.getWidth(), view.getHeight());
        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(view, 0, topPosition, 0, finalRadius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(ViewAnimator.CIRCULAR_REVEAL_ANIMATION_DURATION);
        findViewById(R.id.main_content_overlay).setBackgroundDrawable(new BitmapDrawable(getResources(), screenShotable.getBitmap()));
        animator.start();

        return screenShotable;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_notification:
                return true;
            case R.id.action_share:
                return true;
            case R.id.action_author:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
