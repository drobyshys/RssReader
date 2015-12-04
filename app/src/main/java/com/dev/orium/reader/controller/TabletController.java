package com.dev.orium.reader.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.dev.orium.reader.R;
import com.dev.orium.reader.activities.MainActivity;
import com.dev.orium.reader.data.DatabaseHelper;
import com.dev.orium.reader.fragments.ViewRssFragment;
import com.dev.orium.reader.model.Feed;
import com.dev.orium.reader.network.UpdateService;
import com.dev.orium.reader.ui.PaneAnimator;

import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by y.drobysh on 04.12.2014.
 */
public class TabletController extends BaseController {

    public static final String FEED_ID = "feed_id";

    private static final String EXTRAS_IS_RSS_VISIBLE = "is_rss_shown";
    private static final String FRAGMENT_RSS_TAG = "rss";

    @Optional
    @InjectView(R.id.detailContainer)
    FrameLayout contDetail;

    private ViewRssFragment mFragmentRss;


    private MenuItem menuItemFull;
    private MenuItem menuItemAdd;
    private PaneAnimator mPaneAnimator;

    private boolean mIsRssVisible;


    public TabletController(MainActivity activity, Bundle savedInstanceState) {
        super(activity);
        this.mActivity = activity;

        if (savedInstanceState != null) {
            mIsRssVisible = savedInstanceState.getBoolean(EXTRAS_IS_RSS_VISIBLE);
        }

        mPaneAnimator = new PaneAnimator(activity, contMenu, contFeed, contDetail);

        mFragmentRss = (ViewRssFragment) activity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_RSS_TAG);
        if (mFragmentRss == null) {
            mFragmentRss = ViewRssFragment.newInstance();

            mActivity.getSupportFragmentManager().beginTransaction()
                    .add(R.id.detailContainer, mFragmentRss, FRAGMENT_RSS_TAG)
                    .commit();
        }

        if (mCurrentFeed != null && savedInstanceState == null) {
            selectFeed(mCurrentFeed);
        } else if (DatabaseHelper.getFeedCount(activity) == 0) {
            startNewFeedActivity();
        }

        if (mIsRssVisible) {
            mPaneAnimator.showRss();
        }
    }

    public void selectFeed(Feed feed) {
        if (mIsRssVisible) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mActivity.getSupportFragmentManager().popBackStack();
                    mIsRssVisible = false;
                }
            });
        }
        mPaneAnimator.onMenuItem();

        mCurrentFeed = feed;
        mFragmentFeed.setFeed(feed);
    }

    public void onRssItemClick(long id, final long feedId) {
        mPaneAnimator.showRss();
        menuItemFull.setVisible(true);

        mFragmentRss.setRssItem(id, mCurrentFeed);
        mIsRssVisible = true;
    }


    public boolean onBackPressed() {
        mPaneAnimator.onBackPress();
        menuItemFull.setVisible(false);
        menuItemAdd.setVisible(true);
        mIsRssVisible = false;
        return !mPaneAnimator.isMenuVisible();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
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
                mPaneAnimator.toggleFullScreen();
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
        super.saveData(outState);

        if (outState != null) {
            outState.putBoolean(EXTRAS_IS_RSS_VISIBLE, mIsRssVisible);
        }
    }

}
