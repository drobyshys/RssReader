package com.dev.orium.reader.controller;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dev.orium.reader.R;
import com.dev.orium.reader.Utils.SharedUtils;
import com.dev.orium.reader.activities.MainActivity;
import com.dev.orium.reader.activities.RssViewActivity;
import com.dev.orium.reader.data.DatabaseHelper;
import com.dev.orium.reader.fragments.ViewRssFragment;
import com.dev.orium.reader.model.Feed;
import com.dev.orium.reader.network.UpdateService;

import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by y.drobysh on 19.11.2014.
 */
public class MainController extends BaseController {

    public static final String FEED_ID = "feed_id";

    private static final String FRAGMENT_FEED_TAG = "feed";

    @Optional @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;


    private ActionBarDrawerToggle mDrawerToggle;


    private MenuItem menuItemFull;
    private MenuItem menuItemAdd;


    public MainController(MainActivity activity, Bundle savedInstanceState) {
        super(activity);


        setupDrawer();

        if (mCurrentFeed != null)
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
        drawerLayout.closeDrawer(contMenu);

        mCurrentFeed = feed;
        mFragmentFeed.setFeed(feed);
    }

    public void onRssItemClick(long id) {
        Intent rssIntent = new Intent(mActivity, RssViewActivity.class);
        rssIntent.putExtra(ViewRssFragment.EXTRAS_RSS_ITEM_ID, id);
        mActivity.startActivity(rssIntent);
    }



    public boolean onBackPressed() {
        if (drawerLayout.isDrawerOpen(contMenu)) {
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
            case R.id.menu_add_feed:
                startNewFeedActivity();
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

    public void saveData(Bundle outState) {
        if (mCurrentFeed != null)
            SharedUtils.saveLastSelectedFeed(mCurrentFeed._id);
    }
}
