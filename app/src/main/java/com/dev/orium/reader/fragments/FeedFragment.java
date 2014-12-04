package com.dev.orium.reader.fragments;


import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dev.orium.reader.MainController;
import com.dev.orium.reader.R;
import com.dev.orium.reader.adapters.RssItemAdapter;
import com.dev.orium.reader.data.RssProvider;
import com.dev.orium.reader.events.FeedUpdatedEvent;
import com.dev.orium.reader.model.Feed;
import com.dev.orium.reader.model.RssItem;
import com.dev.orium.reader.network.UpdateService;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import de.greenrobot.event.EventBus;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class FeedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARGS_LAST_FEED = "last_feed";

    @InjectView(android.R.id.list) 
    ListView mListView;
    @InjectView(R.id.swipeLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    
    private MainController controller;
    private RssItemAdapter adapter;
    private Feed feed;

    public static FeedFragment newInstance(long lastFeedId) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        if (lastFeedId >= 0) {
            args.putLong(ARGS_LAST_FEED, lastFeedId);
        }
        fragment.setArguments(args);
        return fragment;
    }

    public void updateArgs(long feedId) {
        Bundle args = getArguments();
        if (args == null) {
            args = new Bundle();
        }
        args.putLong(ARGS_LAST_FEED, feedId);
    }

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);

        Bundle args = getArguments();
        if (args != null) {
            long feedId = args.getLong(ARGS_LAST_FEED);
            feed = cupboard().withContext(getActivity())
                    .query(ContentUris.withAppendedId(RssProvider.FEEDS_URI, feedId), Feed.class)
                    .get();
        }

        EventBus.getDefault().register(this);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        ButterKnife.inject(this, v);
        
        initSwipeLayout();

        adapter = new RssItemAdapter(getActivity());
        mListView.setAdapter(adapter);
    }

    private void initSwipeLayout() {
        mSwipeRefreshLayout.setColorSchemeColors(Color.GREEN, Color.RED, Color.BLUE);
//        mSwipeRefreshLayout.setProgressBackgroundColor(R.color.dark);
        SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateFeed();
            }
        };
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);
    }

    private void updateFeed() {
        mSwipeRefreshLayout.setRefreshing(true);
        UpdateService.startActionUpdate(getActivity().getApplicationContext(), feed._id.intValue());
    }


    public void onEventMainThread(FeedUpdatedEvent event) {
        if (event.getId() == feed._id)
            mSwipeRefreshLayout.setRefreshing(false);
    }


    @OnItemClick(android.R.id.list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        controller.onRssItemClick(id);
    }

    public void setController(MainController controller) {
        this.controller = controller;
    }

    public MainController getController() {
        return controller;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        CursorLoader loader = new CursorLoader(getActivity());
        loader.setUri(RssProvider.RSS_URI);
        loader.setSelection("_feedId = ?");
        loader.setSortOrder(RssItem.PUBLICATION_DATE + " desc");
        if (feed != null)
            loader.setSelectionArgs(new String[] {String.valueOf(feed._id)});
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.swapCursor(cursor);
        adapter.setFeed(feed);
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }

    public void setFeed(Feed newFeed) {
        feed = newFeed;
        updateArgs(newFeed._id);
        if (isAdded())
            getLoaderManager().restartLoader(0, null, this);
    }
}
