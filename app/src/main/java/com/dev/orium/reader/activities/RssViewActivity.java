package com.dev.orium.reader.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.dev.orium.reader.R;
import com.dev.orium.reader.data.RssProvider;
import com.dev.orium.reader.fragments.ViewRssFragment;
import com.dev.orium.reader.model.RssItem;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.qbusict.cupboard.CupboardFactory;
import timber.log.Timber;

public class RssViewActivity extends AppCompatActivity implements FeedLoaderListener, ViewRssFragment.NavigationListener {

    public static final String EXTRAS_RSS_ITEM_ID = "rss_item_id";
    public static final String EXTRAS_FEED_ID = "feed_id";

    @InjectView(R.id.pager)
    ViewPager mPager;
    @InjectView(R.id.progress)
    ProgressBar mProgress;
    private ScreenSlidePagerAdapter mPagerAdapter;

    private long mRssId;
    private long mFeedId;
    private FeedLoader mFeedLoadTask;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_feed);

        ButterKnife.inject(this);

        prepareView();
        parseInput(getIntent().getExtras());
        loadFeed();
    }

    private void prepareView() {
        mProgress.setVisibility(View.VISIBLE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void parseInput(final Bundle data) {
        if (data != null) {
            mRssId = data.getLong(EXTRAS_RSS_ITEM_ID);
            mFeedId = data.getLong(EXTRAS_FEED_ID);
        }
    }

    private void loadFeed() {
        mFeedLoadTask = new FeedLoader(getApplicationContext(), this);
        mFeedLoadTask.execute(mFeedId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFeedLoadTask != null) {
            mFeedLoadTask.cancel(true);
        }
    }

    @Override
    public void onFeedLoaded(final long[] data) {
        if (data != null) {
            mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), data, this);
            mPager.setAdapter(mPagerAdapter);
            int pos = 0;
            for (long id : data) {
                if (id == mRssId) break;
                pos++;
            }
            mPager.setCurrentItem(pos);
        }
        mProgress.setVisibility(View.GONE);
    }

    @Override
    public void onPrev() {
        mPager.setCurrentItem(mPager.getCurrentItem() - 1);
    }

    @Override
    public void onNext() {
        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private final long[] mData;
        private final ViewRssFragment.NavigationListener mNavigationListener;

        public ScreenSlidePagerAdapter(FragmentManager fm, final long[] data, ViewRssFragment.NavigationListener navigationListener) {
            super(fm);
            mData = data;
            mNavigationListener = navigationListener;
        }

        @Override
        public Fragment getItem(int position) {
            ViewRssFragment fragment = ViewRssFragment.newInstance(mData[position]);
            fragment.setNavigationListener(mNavigationListener);
            return fragment;
        }

        @Override
        public int getCount() {
            return mData.length;
        }
    }

    static class FeedLoader extends AsyncTask<Long, Void, long[]> {

        private final Context mContext;
        private WeakReference<FeedLoaderListener> mListener;

        public FeedLoader(Context context, FeedLoaderListener listener) {
            mContext = context;
            mListener = new WeakReference<FeedLoaderListener>(listener);
        }

        @Override
        protected long[] doInBackground(final Long... params) {
            long feedId = params[0];

            try {
                List<RssItem> list = CupboardFactory.getInstance().withContext(mContext)
                        .query(RssProvider.RSS_URI, RssItem.class)
                        .withSelection("_feedId = ?", String.valueOf(feedId))
                        .list();
                long[] items = new long[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    items[i] = list.get(i)._id;
                }

                return items;
            } catch (Exception ignored) {
                Timber.e(ignored, " exception while loading feed");
            }

            return null;
        }

        @Override
        protected void onPostExecute(final long[] rss) {
            FeedLoaderListener listener = mListener.get();
            if (listener != null) {
                listener.onFeedLoaded(rss);
            }
        }
    }
}

interface FeedLoaderListener {
    void onFeedLoaded(long[] data);
}

