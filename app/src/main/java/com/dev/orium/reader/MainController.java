package com.dev.orium.reader;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.dev.orium.reader.Utils.SharedUtils;
import com.dev.orium.reader.activities.AddFeedActivity;
import com.dev.orium.reader.activities.MainActivity;
import com.dev.orium.reader.activities.RssViewActivity;
import com.dev.orium.reader.data.DatabaseHelper;
import com.dev.orium.reader.fragments.FeedFragment;
import com.dev.orium.reader.fragments.MenuFragment;
import com.dev.orium.reader.fragments.ViewRssFragment;
import com.dev.orium.reader.model.Feed;
import com.dev.orium.reader.network.UpdateService;
import com.dev.orium.reader.ui.PaneAnimator;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by y.drobysh on 19.11.2014.
 */
public class MainController {

    public static final String FEED_ID = "feed_id";

    private static final int REQUEST_ADD_FEED = 66;

    private static final String FRAGMENT_FEED_TAG = "feed";
    private static final String FRAGMENT_RSS_TAG = "rss";

    @Optional @InjectView(R.id.detailContainer)
    FrameLayout contDetail;
    @InjectView(R.id.container)
    FrameLayout contFeed;
    @Optional @InjectView(R.id.menuContainer)
    FrameLayout contMenu;
    @Optional @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;


    private ActionBarDrawerToggle mDrawerToggle;
    private MainActivity mActivity;
    private MenuFragment mFragmentMenu;
    private FeedFragment mFragmentFeed;
    private ViewRssFragment mFragmentRss;


    private boolean isTabletMode;

    private MenuItem menuItemFull;
    private MenuItem menuItemAdd;
    private Feed mCurrentFeed;
    private PaneAnimator mPaneAnimator;


    public MainController(MainActivity activity, Bundle savedInstanceState) {
        this.mActivity = activity;
        ButterKnife.inject(this, activity);

        if (contDetail != null) {
            this.isTabletMode = true;
        }

        if (isTabletMode) {
            mPaneAnimator = new PaneAnimator(activity, contMenu, contFeed, contDetail);
        }

        if (!isTabletMode) {
            setupDrawer();
        }

        mFragmentMenu = new MenuFragment();
        mFragmentMenu.setController(this);
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.menuContainer, mFragmentMenu)
                .commit();


        long lastSelectedFeedId = SharedUtils.getLastSelectedFeed();
        if (lastSelectedFeedId >= 0) {
            mCurrentFeed = Feed.getById(activity, lastSelectedFeedId);
        }

        mFragmentFeed = (FeedFragment) mActivity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_FEED_TAG);
        if (mFragmentFeed == null) {
            mFragmentFeed = FeedFragment.newInstance(lastSelectedFeedId);
        }
        mFragmentFeed.setController(this);


        mFragmentRss = (ViewRssFragment) activity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_RSS_TAG);
        boolean isRssVisible = false;

        if (!isTabletMode) {
            if (mFragmentRss == null) {
                mFragmentRss = new ViewRssFragment();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, mFragmentFeed, FRAGMENT_FEED_TAG)
                        .commit();
            } else {
                isRssVisible = true;
            }
        } else {
            mActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detailContainer, mFragmentRss)
                    .commit();
        }
        mFragmentRss.setController(this);



        if (mCurrentFeed != null && !isRssVisible)
            selectFeed(mCurrentFeed);
        else if (DatabaseHelper.getFeedCount(activity) == 0) {
            startNewFeedActivity();
        }
    }

    private void setupDrawer() {
        drawerLayout.setScrimColor(Color.TRANSPARENT);

        ActionBar actionBar = mActivity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                mActivity,                    /* host Activity */
                drawerLayout,                    /* DrawerLayout object */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mActivity.invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mActivity.invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                contFeed.setTranslationX(slideOffset * contMenu.getWidth());
            }
        };
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
//                if (savedState != null)
            }
        });

        drawerLayout.setDrawerListener(mDrawerToggle);
    }


    public void selectFeed(Feed feed) {
        if (mFragmentRss.isAdded()) {
            mActivity.getSupportFragmentManager().popBackStack();
        }
        if (isTabletMode) {
            mPaneAnimator.onMenuItem();
        } else {
            drawerLayout.closeDrawer(contMenu);
        }

        mCurrentFeed = feed;
        mFragmentFeed.setFeed(feed);
    }

    public void onRssItemClick(long id) {
        if (isTabletMode) {
            mPaneAnimator.showRss();


            menuItemFull.setVisible(true);
        } else {
            mActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, mFragmentRss, FRAGMENT_RSS_TAG)
                    .addToBackStack(null)
                    .commit();
        }
        mFragmentRss.setRssItem(id);
    }



    public boolean onBackPressed() {
        if (isTabletMode) {
            mPaneAnimator.onBackPress();

            menuItemFull.setVisible(false);
            menuItemAdd.setVisible(true);
            return !mPaneAnimator.isMenuVisible();
        } else if (mFragmentRss.isAdded()) {
            mActivity.getSupportFragmentManager().popBackStack();
            return true;
        } else if (drawerLayout.isDrawerOpen(contMenu)) {
            drawerLayout.openDrawer(contMenu);
            return true;
        }
        return false;
    }



    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item))
            return true;
        switch (item.getItemId()) {
            case android.R.id.home:
//                if (mIsRssShown) {
//                    showMenu();
//                } else {
//                    hideMenu();
//                }
//                mIsRssShown = !mIsRssShown;
                return true;
            case R.id.menu_rss_fullscreen:
                if (isTabletMode) {
                    mPaneAnimator.toggleFullScreen();
                }
                return true;
            case R.id.menu_add_feed:
                startNewFeedActivity();
                return true;
        }
        return false;
    }

    private void startNewFeedActivity() {
        Intent i = new Intent(mActivity, AddFeedActivity.class);
        mActivity.startActivityForResult(i, REQUEST_ADD_FEED);
    }

    public void onCreateOptionsMenu(Menu menu) {
        mActivity.getMenuInflater().inflate(R.menu.menu_controller, menu);
        menuItemFull = menu.findItem(R.id.menu_rss_fullscreen);
        menuItemFull.setVisible(false);
        menuItemAdd = menu.findItem(R.id.menu_add_feed);
        menuItemAdd.setVisible(true);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ADD_FEED:
                int feedId = data != null ? data.getIntExtra(FEED_ID, -1) : -1;
                if (feedId != -1) {
                    UpdateService.startActionUpdate(mActivity.getApplicationContext(), feedId);
                    Feed feed = Feed.getById(mActivity, feedId);
                    if (feed != null)
                        selectFeed(feed);
                }

                break;
        }
    }

    public void saveData() {
        if (mCurrentFeed != null)
            SharedUtils.saveLastSelectedFeed(mCurrentFeed._id);
    }
}
