package com.dev.orium.reader.fragments;


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

import com.dev.orium.reader.R;
import com.dev.orium.reader.adapters.MenuFeedAdapter;
import com.dev.orium.reader.controller.Controller;
import com.dev.orium.reader.data.RssProvider;
import com.dev.orium.reader.model.Feed;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MenuFragment extends Fragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {


    private ListView list;
    private Controller controller;
    private MenuFeedAdapter adapter;
    private boolean firstLoad;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);

        getLoaderManager().initLoader(0, null, this);

        firstLoad = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        list = (ListView) inflater.inflate(R.layout.fragment_list, container, false);
        return list;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new MenuFeedAdapter(getActivity());
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor c = (Cursor) adapter.getItem(position);
        Feed feed = cupboard().withCursor(c).get(Feed.class);
        controller.selectFeed(feed);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        CursorLoader loader = new CursorLoader(getActivity());
        loader.setUri(RssProvider.FEEDS_URI);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> objectLoader, Cursor o) {
        adapter.swapCursor(o);

        if (firstLoad && o.getCount() == 1) {
            Feed feed = cupboard().withCursor(o).get(Feed.class);
            controller.selectFeed(feed);
        }

        firstLoad = false;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> objectLoader) {
        adapter.swapCursor(null);
    }
}
