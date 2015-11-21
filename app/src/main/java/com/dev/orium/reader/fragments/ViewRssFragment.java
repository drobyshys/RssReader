package com.dev.orium.reader.fragments;


import android.content.ContentUris;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.dev.orium.reader.R;
import com.dev.orium.reader.utils.DateUtils;
import com.dev.orium.reader.data.RssProvider;
import com.dev.orium.reader.model.Feed;
import com.dev.orium.reader.model.RssItem;

import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.qbusict.cupboard.ProviderCompartment;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


public class ViewRssFragment extends Fragment {

    public static final String EXTRAS_RSS_ITEM_ID = "rss_item_id";

    @InjectView(R.id.wvContent) WebView wvContent;
    @InjectView(R.id.tv_title) TextView tvTitle;
    @InjectView(R.id.tv_date) TextView tvDate;
    @InjectView(R.id.tv_feed_title) TextView tvFeedTitle;

    private RssItem rssItem;
    private Feed feed;
    private ProviderCompartment cupboard;
    private long rssId;

    public ViewRssFragment() {
        // Required empty public constructor
    }

    public static ViewRssFragment newInstance() {
        ViewRssFragment fragment = new ViewRssFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cupboard = cupboard().withContext(getActivity());

        Bundle args = getArguments();
        if (args != null) {
            rssId = args.getLong(EXTRAS_RSS_ITEM_ID);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (rssItem == null && rssId > 0) {
            getData(rssId);
            updateView();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_rss, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        if (rssItem != null)
            updateView();
    }

    public void setRssItem(long id) {
        setRssId(id);
        if (isAdded()) {
            getData(id);
            updateView();
        }
    }

    private void updateView() {
        if (rssItem == null)
            return;

        tvTitle.setText(rssItem.title);
        if (feed != null)
            tvFeedTitle.setText(feed.title);
        tvDate.setText(DateUtils.getDateString(rssItem.publicationDate));
        wvContent.loadDataWithBaseURL(null, rssItem.description, "text/html", "utf-8", null);
    }

    private void getData(long rssId) {
        rssItem = cupboard.query(ContentUris.withAppendedId(RssProvider.RSS_URI, rssId), RssItem.class)
                .get();
        if (rssItem != null) {
            feed = cupboard
                    .query(ContentUris.withAppendedId(RssProvider.FEEDS_URI, rssItem._feedId), Feed.class)
                    .get();
            if (!rssItem.readed) {
                rssItem.readed = true;
                cupboard.put(RssProvider.RSS_URI, rssItem);
            }
        }

    }

    public void setRssId(long rssId) {
        this.rssId = rssId;
        Bundle args = getArguments();
        if (args == null) {
            args = new Bundle();
        }
        args.putLong(EXTRAS_RSS_ITEM_ID, rssId);
    }
}
