package com.dev.orium.reader.fragments;


import android.content.ContentUris;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.dev.orium.reader.R;
import com.dev.orium.reader.MainController;
import com.dev.orium.reader.Utils.DateUtils;
import com.dev.orium.reader.data.RssProvider;
import com.dev.orium.reader.model.Feed;
import com.dev.orium.reader.model.RssItem;

import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.CupboardFactory;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


public class ViewRssFragment extends Fragment {

    public static final String EXTRAS_RSS_ITEM_ID = "rss_item_id";

    @InjectView(R.id.wvContent)
    WebView wvContent;

    private MainController controller;
    private RssItem rssItem;
    private Feed feed;


//    @InjectView(android.R.id.list)
//    ListView mListView;

    public ViewRssFragment() {
        // Required empty public constructor
    }

    public static ViewRssFragment newInstance(long id) {
        ViewRssFragment fragment = new ViewRssFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            long rssId = args.getLong(EXTRAS_RSS_ITEM_ID);
            if (rssId > 0) {
                rssItem = cupboard().withContext(getActivity())
                        .query(ContentUris.withAppendedId(RssProvider.RSS_URI, rssId), RssItem.class)
                        .get();
                if (rssItem != null) {
                    feed = cupboard().withContext(getActivity())
                            .query(ContentUris.withAppendedId(RssProvider.FEEDS_URI, rssItem._feedId), Feed.class)
                            .get();
                }
            }
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

    private void updateView() {
        StringBuilder content = new StringBuilder((int) (rssItem.description.length() * 1.5));

        content.append("<h3>" + rssItem.title + "</h3><hr/>");
        content.append("<table style='font-size:80%;'><tr>");
        if (feed != null)
            content.append("<td>" + feed.title + "</td>");
        content.append("<td style='width:1%;white-space:nowrap;color:#558;'>"
                + DateUtils.getDateString(rssItem.publicationDate) + "</td>");
        content.append("</tr></table><br/><br/>");
        content.append(rssItem.description);

        wvContent.loadDataWithBaseURL(null, content.toString(), "text/html", "utf-8", null);
    }

    public void setController(MainController controller) {
        this.controller = controller;
    }

    public MainController getController() {
        return controller;
    }

//    @Override
//    public void onViewCreated(View v, Bundle savedInstanceState) {
//        super.onViewCreated(v, savedInstanceState);
//        ButterKnife.inject(this, v);
//
//        mListView.setAdapter(new ArrayAdapter<FeedItem>(getActivity(), android.R.layout.simple_list_item_1,
//                android.R.id.text1, FeedItem.getFakeData()));
//
//    }

//    @OnItemClick(android.R.id.list)
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(getActivity(), position + "", Toast.LENGTH_SHORT).show();
//    }


}
