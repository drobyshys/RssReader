package com.dev.orium.reader.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.dev.orium.reader.R;
import com.dev.orium.reader.utils.SharedUtils;
import com.dev.orium.reader.activities.AddFeedActivity;
import com.dev.orium.reader.fragments.FeedListFragment;
import com.dev.orium.reader.fragments.MenuFragment;
import com.dev.orium.reader.model.Feed;

import java.util.Timer;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

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

    protected AppCompatActivity mActivity;
    protected MenuFragment mFragmentMenu;
    protected FeedListFragment mFragmentFeed;

    Handler mHandler;

    protected Feed mCurrentFeed;
    protected final int mOrientation;

    BaseController(AppCompatActivity context) {
        Timber.d("Controller creation...");
        mActivity = context;
        mHandler = new Handler(Looper.getMainLooper());

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

        mFragmentFeed = (FeedListFragment) mActivity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_FEED_TAG);
        if (mFragmentFeed == null) {
            mFragmentFeed = FeedListFragment.newInstance(mCurrentFeed != null ? mCurrentFeed._id : -1);
            mActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, mFragmentFeed, FRAGMENT_FEED_TAG)
                    .commit();
        }
        mFragmentFeed.setController(this);

        Toolbar toolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
        mActivity.setSupportActionBar(toolbar);
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
