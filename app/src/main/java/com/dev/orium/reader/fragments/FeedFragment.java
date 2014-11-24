package com.dev.orium.reader.fragments;


import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.dev.orium.reader.MainController;
import com.dev.orium.reader.R;
import com.dev.orium.reader.adapters.RssItemAdapter;
import com.dev.orium.reader.data.RssProvider;
import com.dev.orium.reader.model.Feed;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import nl.qbusict.cupboard.CupboardFactory;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class FeedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARGS_LAST_FEED = "last_feed";

    @InjectView(android.R.id.list)
    ListView listView;
    private MainController controller;
    private RssItemAdapter adapter;
    private Feed feed;

    public static FeedFragment newInstance(long lastFeedId) {
        FeedFragment fragment = new FeedFragment();
        if (lastFeedId >= 0) {
            Bundle args = new Bundle();
            args.putLong(ARGS_LAST_FEED, lastFeedId);
            fragment.setArguments(args);
        }
        return fragment;
    }

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Bundle args = getArguments();
        if (args != null) {
            long feedId = args.getLong(ARGS_LAST_FEED);
            feed = cupboard().withContext(getActivity())
                    .query(ContentUris.withAppendedId(RssProvider.FEEDS_URI, feedId), Feed.class)
                    .get();
        }

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

        adapter = new RssItemAdapter(getActivity());
        listView.setAdapter(adapter);
    }


    @OnItemClick(android.R.id.list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), position + "", Toast.LENGTH_SHORT).show();
        controller.onFeedItemClick();
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
        if (feed != null)
            loader.setSelectionArgs(new String[] {String.valueOf(feed._id)});
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.swapCursor(cursor);
        adapter.setFeed(feed);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }

    public void setFeed(Feed newFeed) {
        feed = newFeed;
        getLoaderManager().restartLoader(0, null, this);
    }
}
