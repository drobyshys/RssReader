package com.dev.orium.clearsky.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.dev.orium.clearsky.activities.AddFeedActivity;
import com.dev.orium.clearsky.activities.MainActivity;
import com.dev.orium.clearsky.R;
import com.dev.orium.clearsky.activities.RssViewActivity;
import com.dev.orium.clearsky.Utils;
import com.dev.orium.clearsky.fragments.FeedFragment;
import com.dev.orium.clearsky.fragments.MenuFragment;
import com.dev.orium.clearsky.fragments.ViewRssFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by y.drobysh on 19.11.2014.
 */
public class MainController {
    @Optional @InjectView(R.id.detailContainer)
    FrameLayout contDetail;
    @InjectView(R.id.container)
    FrameLayout contContent;
    @Optional @InjectView(R.id.menuContainer)
    FrameLayout contMenu;
    @Optional @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;


    private ActionBarDrawerToggle mDrawerToggle;
    private MainActivity activity;
    private MenuFragment menuFragment;
    private FeedFragment feedFragment;
    private ViewRssFragment rssFragment;


    private boolean mIsRssShown;
    private boolean isTabletMode;
    private boolean rssViewFull;

    private int smallWidth, bigWidth;
    private int fullWidth;
    private MenuItem menuItemFull;
    private MenuItem menuItemAdd;


    public MainController(MainActivity activity) {
        this.activity = activity;
        ButterKnife.inject(this, activity);

        if (contDetail != null) {
            this.isTabletMode = true;
        }

        if (isTabletMode) {
            fullWidth = Utils.getScreenWidth(activity);
            smallWidth = fullWidth / 3;
            bigWidth = smallWidth * 2;
        }

        menuFragment = new MenuFragment();
        menuFragment.setController(this);

        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.menuContainer, menuFragment)
                .commit();

        if (!isTabletMode) {
            feedFragment = new FeedFragment();
            feedFragment.setController(this);

            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, feedFragment)
                    .commit();

            setupDrawer();
        }

    }

    private void setupDrawer() {
        drawerLayout.setScrimColor(Color.TRANSPARENT);

        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                activity,                    /* host Activity */
                drawerLayout,                    /* DrawerLayout object */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                activity.invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                activity.invalidateOptionsMenu();
            }

            @Override public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                contContent.setTranslationX(slideOffset * contMenu.getWidth());
            }
        };
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
//                if (savedState != null)
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        drawerLayout.setDrawerListener(mDrawerToggle);
    }


    public void onMenuItemClick() {
        if (isTabletMode) {
            showFeedContainer();
        } else {
            if (feedFragment == null) {
                feedFragment = new FeedFragment();
                feedFragment.setController(this);
            }

            animatedTransaction()
                    .replace(R.id.container, feedFragment)
                    .addToBackStack(null)
                    .commit();
            drawerLayout.closeDrawer(contMenu);
        }
    }

    private FragmentTransaction animatedTransaction() {
        return activity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fragment_slide_left_enter,
                        R.anim.fragment_slide_left_exit
                        , R.anim.fragment_slide_right_enter
                        , R.anim.fragment_slide_right_exit);
    }

    private void showFeedContainer() {
        if (feedFragment == null) {
            feedFragment = new FeedFragment();

            feedFragment.setController(this);

            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, feedFragment)
                    .commit();
        }
            changeWidthTo(contMenu, smallWidth);
            changeWidthTo(contContent, bigWidth);

    }

    private void changeWidthTo(FrameLayout frame, int newWidth) {
        ResizeWidthAnimation anim = new ResizeWidthAnimation(frame, newWidth);
        anim.setDuration(500);
        frame.startAnimation(anim);
    }


    public void onFeedItemClick() {
        if (rssFragment == null) {
            rssFragment = new ViewRssFragment();

            rssFragment.setController(this);
        }
        if (isTabletMode) {
            showRssContainer();
            menuItemFull.setVisible(true);
        } else {
            Intent rssIntent = new Intent(activity, RssViewActivity.class);
            activity.startActivity(rssIntent);
        }
    }

    private void showRssContainer() {
        if (!rssFragment.isAdded()) {
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detailContainer, rssFragment)
                    .commit();
        }
        mIsRssShown = true;

        changeWidthTo(contMenu, 0);
        changeWidthTo(contContent, smallWidth);
        changeWidthTo(contDetail, bigWidth);
        menuItemAdd.setVisible(false);
    }

    public boolean onBackPressed() {
        if (mIsRssShown) {
            showMenu();
            mIsRssShown = !mIsRssShown;
            return true;
        }

        return false;
    }

    private void showMenu() {
        changeWidthTo(contMenu, smallWidth);
        changeWidthTo(contContent, bigWidth);
        changeWidthTo(contDetail, 0);

        menuItemFull.setVisible(false);
        menuItemAdd.setVisible(true);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item))
            return true;

        switch (item.getItemId()) {
            case android.R.id.home:
                if (mIsRssShown) {
                    showMenu();
                } else {
                    changeWidthTo(contMenu, 0);
                }
                mIsRssShown = !mIsRssShown;
                return true;
            case R.id.menu_rss_fullscreen:
                if (mIsRssShown && isTabletMode) {
                    if (rssViewFull) {
                        changeWidthTo(contContent, smallWidth);
                        changeWidthTo(contDetail, bigWidth);
                    } else {
                        changeWidthTo(contContent, 0);
                        changeWidthTo(contDetail, fullWidth);
                    }
                    rssViewFull = !rssViewFull;
                }
                return true;
            case R.id.menu_add_feed:
                Intent i = new Intent(activity, AddFeedActivity.class);
                activity.startActivity(i);
                return true;
        }

        return false;
    }

    public void onCreateOptionsMenu(Menu menu) {
        activity.getMenuInflater().inflate(R.menu.menu_controller, menu);
        menuItemFull = menu.findItem(R.id.menu_rss_fullscreen);
        menuItemFull.setVisible(false);
        menuItemAdd = menu.findItem(R.id.menu_add_feed);
        menuItemAdd.setVisible(true);
    }
}
