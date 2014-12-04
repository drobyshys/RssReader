package com.dev.orium.reader.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.FrameLayout;

import com.dev.orium.reader.R;
import com.dev.orium.reader.Utils.SharedUtils;
import com.dev.orium.reader.activities.AddFeedActivity;
import com.dev.orium.reader.fragments.FeedFragment;
import com.dev.orium.reader.fragments.MenuFragment;
import com.dev.orium.reader.model.Feed;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by y.drobysh on 04.12.2014.
 */
public abstract class BaseController implements Controller {


    protected static final int REQUEST_ADD_FEED = 66;
    protected static final String FRAGMENT_FEED_TAG = "feed";
    protected static final String FRAGMENT_MENU_TAG = "tag_menu";

    @InjectView(R.id.container)
    FrameLayout contFeed;
    @InjectView(R.id.menuContainer)
    FrameLayout contMenu;

    protected ActionBarActivity mActivity;
    protected MenuFragment mFragmentMenu;
    protected FeedFragment mFragmentFeed;

    protected Feed mCurrentFeed;
    protected final int mOrientation;

    BaseController(ActionBarActivity context) {
        mActivity = context;

        ButterKnife.inject(this, mActivity);

        mOrientation = mActivity.getResources().getConfiguration().orientation;

        long lastSelectedFeedId = SharedUtils.getLastSelectedFeed();
        if (lastSelectedFeedId >= 0) {
            mCurrentFeed = Feed.getById(context, lastSelectedFeedId);
        }

        mFragmentMenu = (MenuFragment) mActivity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_MENU_TAG);
        if (mFragmentMenu == null) {
            mFragmentMenu = new MenuFragment();
            mActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.menuContainer, mFragmentMenu, FRAGMENT_MENU_TAG)
                    .commit();
        }
        mFragmentMenu.setController(this);

        mFragmentFeed = (FeedFragment) mActivity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_FEED_TAG);
        if (mFragmentFeed == null) {
            mFragmentFeed = FeedFragment.newInstance(mCurrentFeed != null ? mCurrentFeed._id : -1);
            mActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, mFragmentFeed, FRAGMENT_FEED_TAG)
                    .commit();
        }
        mFragmentFeed.setController(this);
    }

    protected void startNewFeedActivity() {
        Intent i = new Intent(mActivity, AddFeedActivity.class);
        mActivity.startActivityForResult(i, REQUEST_ADD_FEED);
    }

    @Override
    public void saveData(Bundle outState) {
        if (mCurrentFeed != null)
            SharedUtils.saveLastSelectedFeed(mCurrentFeed._id);
    }
}
