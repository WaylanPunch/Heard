package com.way.heard.ui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.way.heard.R;
import com.way.heard.ui.fragments.BookmarkFragment;
import com.way.heard.ui.fragments.ContactFragment;
import com.way.heard.ui.fragments.FindFragment;
import com.way.heard.ui.fragments.HomeFragment;
import com.way.heard.ui.fragments.MeFragment;
import com.way.heard.ui.fragments.MessageFragment;
import com.way.heard.ui.fragments.SettingFragment;
import com.way.heard.utils.LogUtil;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

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

    private FragmentManager fragmentManager;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private List<SlideMenuItem> list = new ArrayList<>();
    private HomeFragment homeFragment;
    private FindFragment findFragment;
    private BookmarkFragment bookmarkFragment;
    private ContactFragment contactFragment;
    private MessageFragment messageFragment;
    private MeFragment meFragment;
    private SettingFragment settingFragment;
    private ViewAnimator viewAnimator;
    //private int res = R.drawable.content_music;
    private LinearLayout linearLayout;
    private ViewAnimator.ViewAnimatorListener viewAnimatorListener;
    private ContextMenuDialogFragment mMenuDialogFragment;
    private OnMenuItemClickListener onMenuItemClickListener;
    private OnMenuItemLongClickListener onMenuItemLongClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setLeanCloudAnalytics();
        setContentFragment();
        setDrawerLayout();
        setToolBar();
        setMenuList();
        setViewAnimator();
        setContextMenuFragment();
    }

    private void setLeanCloudAnalytics() {
        AVAnalytics.trackAppOpened(getIntent());
    }

    private void setContentFragment() {
        LogUtil.d(TAG, "setContentFragment debug");
        fragmentManager = getSupportFragmentManager();
        homeFragment = HomeFragment.newInstance(1);

        fragmentManager.beginTransaction()
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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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
        SlideMenuItem menuItemClose = new SlideMenuItem(HomeFragment.CLOSE, R.drawable.ic_close);
        list.add(menuItemClose);
        SlideMenuItem menuItemHome = new SlideMenuItem(HomeFragment.HOME, R.drawable.ic_home);
        list.add(menuItemHome);
        SlideMenuItem menuItemFind = new SlideMenuItem(FindFragment.FIND, R.drawable.ic_find);
        list.add(menuItemFind);
        SlideMenuItem menuItemBookmark = new SlideMenuItem(BookmarkFragment.BOOKMARK, R.drawable.ic_bookmark);
        list.add(menuItemBookmark);
        SlideMenuItem menuItemContact = new SlideMenuItem(ContactFragment.CONTACT, R.drawable.ic_contact);
        list.add(menuItemContact);
        SlideMenuItem menuItemMessage = new SlideMenuItem(MessageFragment.MESSAGE, R.drawable.ic_message);
        list.add(menuItemMessage);
        SlideMenuItem menuItemMe = new SlideMenuItem(MeFragment.ME, R.drawable.ic_me);
        list.add(menuItemMe);
        SlideMenuItem menuItemSetting = new SlideMenuItem(SettingFragment.SETTING, R.drawable.ic_settings);
        list.add(menuItemSetting);
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
                    case BookmarkFragment.BOOKMARK:
                        LogUtil.d(TAG, "setViewAnimator debug, BOOKMARK, Index = 3");
                        return replaceFragment(screenShotable, 3, position);
                    case ContactFragment.CONTACT:
                        LogUtil.d(TAG, "setViewAnimator debug, CONTACT, Index = 4");
                        return replaceFragment(screenShotable, 4, position);
                    case MessageFragment.MESSAGE:
                        LogUtil.d(TAG, "setViewAnimator debug, MESSAGE, Index = 5");
                        return replaceFragment(screenShotable, 5, position);
                    case MeFragment.ME:
                        LogUtil.d(TAG, "setViewAnimator debug, ME, Index = 6");
                        return replaceFragment(screenShotable, 6, position);
                    default:
                        LogUtil.d(TAG, "setViewAnimator debug, SETTING, Index = 7");
                        return replaceFragment(screenShotable, 7, position);
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
        if (index == 0) {
            return screenShotable;
        }
        //this.res = this.res == R.drawable.content_music ? R.drawable.content_films : R.drawable.content_music;

        if (index == 1) {
            LogUtil.d(TAG, "replaceFragment debug, HOME, Index = " + index);
            if (homeFragment == null) {
                homeFragment = HomeFragment.newInstance(1);
            }
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content_frame, homeFragment)
                    .commit();
            screenShotable = homeFragment;
        } else if (index == 2) {
            LogUtil.d(TAG, "replaceFragment debug, FIND, Index = " + index);
            if (findFragment == null) {
                findFragment = FindFragment.newInstance(2);
            }
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content_frame, findFragment)
                    .commit();
            screenShotable = findFragment;
        } else if (index == 3) {
            LogUtil.d(TAG, "replaceFragment debug, BOOKMARK, Index = " + index);
            if (bookmarkFragment == null) {
                bookmarkFragment = BookmarkFragment.newInstance(3);
            }
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content_frame, bookmarkFragment)
                    .commit();
            screenShotable = bookmarkFragment;
        } else if (index == 4) {
            LogUtil.d(TAG, "replaceFragment debug, CONTACT, Index = " + index);
            if (contactFragment == null) {
                contactFragment = ContactFragment.newInstance(4);
            }
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content_frame, contactFragment)
                    .commit();
            screenShotable = contactFragment;
        } else if (index == 5) {
            LogUtil.d(TAG, "replaceFragment debug, MESSAGE, Index = " + index);
            if (messageFragment == null) {
                messageFragment = MessageFragment.newInstance(5);
            }
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content_frame, messageFragment)
                    .commit();
            screenShotable = messageFragment;
        } else if (index == 6) {
            LogUtil.d(TAG, "replaceFragment debug, ME, Index = " + index);
            if (meFragment == null) {
                meFragment = MeFragment.newInstance(6);
            }
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content_frame, meFragment)
                    .commit();
            screenShotable = meFragment;
        } else {
            LogUtil.d(TAG, "replaceFragment debug, SETTING, Index = " + index);
            if (settingFragment == null) {
                settingFragment = SettingFragment.newInstance(7);
            }
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content_frame, settingFragment)
                    .commit();
            screenShotable = settingFragment;
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

    private void setContextMenuFragment() {
        LogUtil.d(TAG, "setContextMenuFragment debug");
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(setContextMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);

        onMenuItemClickListener = new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(View clickedView, int position) {
                if(position == 4) {
                    Intent intent = new Intent(MainActivity.this,TestActivity.class);
                    startActivity(intent);
                }
            }
        };
        onMenuItemLongClickListener = new OnMenuItemLongClickListener() {
            @Override
            public void onMenuItemLongClick(View clickedView, int position) {
                LogUtil.d(TAG, "setContextMenuFragment debug, onMenuItemLongClick, Position = " + position);
                Toast.makeText(MainActivity.this, "Long clicked on position: " + position, Toast.LENGTH_SHORT).show();
            }
        };

        mMenuDialogFragment.setItemClickListener(onMenuItemClickListener);
        mMenuDialogFragment.setItemLongClickListener(onMenuItemLongClickListener);
    }

    private List<MenuObject> setContextMenuObjects() {
        LogUtil.d(TAG, "setContextMenuObjects debug");
        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject objClose = new MenuObject();
        objClose.setResource(R.drawable.ic_close);

        MenuObject objSearch = new MenuObject("Search");
        objSearch.setResource(R.drawable.ic_search);

        MenuObject objNotification = new MenuObject("Notification");
        //Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification);
        //like.setBitmap(b);
        objNotification.setResource(R.drawable.ic_notification);

        MenuObject objShare = new MenuObject("Share");
        //BitmapDrawable bd = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.ic_share));
        //addFr.setDrawable(bd);
        objShare.setResource(R.drawable.ic_share);

        MenuObject objDeveloper = new MenuObject("Developer");
        objDeveloper.setResource(R.drawable.ic_developer);

        menuObjects.add(objClose);
        menuObjects.add(objSearch);
        menuObjects.add(objNotification);
        menuObjects.add(objShare);
        menuObjects.add(objDeveloper);
        return menuObjects;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
                mMenuDialogFragment.dismiss();
            } else {
                //finish();
                super.onBackPressed();
            }
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
        /*
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
        */
        switch (item.getItemId()) {
            case R.id.context_menu:
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
