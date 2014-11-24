package com.dev.orium.reader;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.dev.orium.reader.activities.AddFeedActivity;
import com.dev.orium.reader.activities.MainActivity;
import com.dev.orium.reader.activities.RssViewActivity;
import com.dev.orium.reader.fragments.FeedFragment;
import com.dev.orium.reader.fragments.MenuFragment;
import com.dev.orium.reader.fragments.ViewRssFragment;
import com.dev.orium.reader.model.Feed;
import com.dev.orium.reader.network.UpdateService;
import com.dev.orium.reader.ui.ResizeWidthAnimation;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by y.drobysh on 19.11.2014.
 */
public class MainController {

    public static final String FEED_ID = "feed_id";

    private static final int REQUEST_ADD_FEED = 66;

    @Optional
    @InjectView(R.id.detailContainer)
    FrameLayout contDetail;
    @InjectView(R.id.container)
    FrameLayout contContent;
    @Optional
    @InjectView(R.id.menuContainer)
    FrameLayout contMenu;
    @Optional
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;


    private ActionBarDrawerToggle mDrawerToggle;
    private MainActivity mActivity;
    private MenuFragment mFragmentMenu;
    private FeedFragment mFragmentFeed;
    private ViewRssFragment mFragmentRss;


    private boolean mIsRssShown;
    private boolean isTabletMode;
    private boolean rssViewFull;

    private int smallWidth, bigWidth;
    private int fullWidth;

    private MenuItem menuItemFull;
    private MenuItem menuItemAdd;
    private Feed mCurrentFeed;


    public MainController(MainActivity activity) {
        this.mActivity = activity;
        ButterKnife.inject(this, activity);

        if (contDetail != null) {
            this.isTabletMode = true;
        }

        if (isTabletMode) {
            fullWidth = Utils.getScreenWidth(activity);
            smallWidth = fullWidth / 3;
            bigWidth = smallWidth * 2;
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

        mFragmentFeed = FeedFragment.newInstance(lastSelectedFeedId);
        mFragmentFeed.setController(this);

        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, mFragmentFeed)
                .commit();

        if (!isTabletMode) {
            setupDrawer();
        } else if (mCurrentFeed != null) {
            showFeedContainer();
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
                contContent.setTranslationX(slideOffset * contMenu.getWidth());
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


    public void onMenuItemClick(Feed feed) {
        if (isTabletMode) {
            showFeedContainer();
        } else {
            mCurrentFeed = feed;
            drawerLayout.closeDrawer(contMenu);
        }
        mFragmentFeed.setFeed(feed);
    }

    public void onFeedItemClick() {
        if (isTabletMode) {
            if (mFragmentRss == null) {
                mFragmentRss = new ViewRssFragment();
                mFragmentRss.setController(this);
            }
            showRssContainer();
            menuItemFull.setVisible(true);
        } else {
            Intent rssIntent = new Intent(mActivity, RssViewActivity.class);
            mActivity.startActivity(rssIntent);
        }
    }

    private FragmentTransaction animatedTransaction() {
        return mActivity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fragment_slide_left_enter,
                        R.anim.fragment_slide_left_exit
                        , R.anim.fragment_slide_right_enter
                        , R.anim.fragment_slide_right_exit);
    }

    private void showFeedContainer() {
        changeWidthTo(contMenu, smallWidth);
        changeWidthTo(contContent, bigWidth);
    }

    private void changeWidthTo(FrameLayout frame, int newWidth) {
        ResizeWidthAnimation anim = new ResizeWidthAnimation(frame, newWidth);
        anim.setDuration(500);
        frame.startAnimation(anim);
    }

    private void showRssContainer() {
        if (!mFragmentRss.isAdded()) {
            mActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detailContainer, mFragmentRss)
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
                Intent i = new Intent(mActivity, AddFeedActivity.class);
                mActivity.startActivityForResult(i, REQUEST_ADD_FEED);
                return true;
        }
        return false;
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
                int feedId = data.getIntExtra(FEED_ID, -1);
                if (feedId != -1)
                    UpdateService.startActionUpdate(mActivity.getApplicationContext(), feedId);
                break;
        }
    }

    public void saveData() {
        if (mCurrentFeed != null)
            SharedUtils.saveLastSelectedFeed(mCurrentFeed._id);
    }
}
