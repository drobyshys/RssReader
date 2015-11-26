package com.dev.orium.reader.fragments;


import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import com.dev.orium.reader.R;
import com.dev.orium.reader.activities.RssViewActivity;
import com.dev.orium.reader.data.RssProvider;
import com.dev.orium.reader.model.Feed;
import com.dev.orium.reader.model.RssItem;
import com.dev.orium.reader.utils.DateUtils;

import java.util.Timer;

import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.qbusict.cupboard.ProviderCompartment;
import timber.log.Timber;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


public class ViewRssFragment extends Fragment {

    public static final String EXTRAS_RSS_ITEM_ID = "rss_item_id";

    @InjectView(R.id.wvContent)
    WebView wvContent;
    @InjectView(R.id.pane_info)
    View mPaneInfo;
    @InjectView(R.id.tv_date) TextView tvDate;
    @InjectView(R.id.tv_feed_title) TextView tvFeedTitle;

    private RssItem rssItem;
    private Feed feed;
    private ProviderCompartment cupboard;
    private long rssId;
    private RssViewActivity mActivity;

    boolean mIsFullView;

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
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        mActivity = (RssViewActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
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

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mActivity.setSupportActionBar(toolbar);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (rssItem != null)
            updateView();

        configureWebView();
    }

    private void configureWebView() {
        wvContent.setFocusable(true);
        wvContent.setFocusableInTouchMode(true);

        wvContent.setWebChromeClient(new WebChromeClient());

        wvContent.getSettings().setUseWideViewPort(false);
        wvContent.getSettings().setDatabaseEnabled(true);
        wvContent.getSettings().setDisplayZoomControls(true);
        wvContent.getSettings().setBuiltInZoomControls(true);
        wvContent.getSettings().setJavaScriptEnabled(true);
        wvContent.getSettings().setAppCacheEnabled(true);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_view_rss, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_rss_web_view:
                mIsFullView = !mIsFullView;
                updateView();
                item.setIcon(mIsFullView ? R.drawable.ic_note_text : R.drawable.ic_eye);
                mPaneInfo.setVisibility(mIsFullView ? View.GONE : View.VISIBLE);
                break;
            case R.id.menu_rss_browser:
                openInBrowser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openInBrowser() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(rssItem.link));
        try {
            startActivity(intent);
        } catch (Exception ignored) {
            Timber.e("failed to open browser", ignored);
        }
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

        if (feed != null)
            mActivity.getSupportActionBar().setTitle(feed.title);
        tvFeedTitle.setText(rssItem.title);
        tvDate.setText(DateUtils.getDateString(rssItem.publicationDate));

        if (mIsFullView) {
            wvContent.loadUrl(rssItem.link);
        } else {
            wvContent.loadDataWithBaseURL(null, rssItem.description, "text/html", "utf-8", null);
        }
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
