package com.dev.orium.reader.fragments;


import android.content.ContentUris;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.dev.orium.reader.R;
import com.dev.orium.reader.utils.DateUtils;
import com.dev.orium.reader.controller.Controller;
import com.dev.orium.reader.data.RssProvider;
import com.dev.orium.reader.model.Feed;
import com.dev.orium.reader.model.RssItem;

import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.qbusict.cupboard.ProviderCompartment;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


public class ViewRssFragment extends Fragment {

    public static final String EXTRAS_RSS_ITEM_ID = "rss_item_id";

    @InjectView(R.id.wvContent)
    WebView wvContent;

    private Controller controller;
    private RssItem rssItem;
    private Feed feed;
    private ProviderCompartment cupboard;
    private long rssId;


//    @InjectView(android.R.id.list)
//    ListView mListView;

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

        StringBuilder content = new StringBuilder((int) (rssItem.description.length() * 1.5));

        content.append("<h3>").append(rssItem.title).append("</h3><hr/>");
        content.append("<table style='font-size:80%;'><tr>");
        if (feed != null)
            content.append("<td>").append(feed.title).append("</td>");
        content.append("<td style='width:1%;white-space:nowrap;color:#558;'>")
                .append(DateUtils.getDateString(rssItem.publicationDate)).append("</td>");
        content.append("</tr></table><br/><br/>");
        content.append(rssItem.description);

        wvContent.loadDataWithBaseURL(null, content.toString(), "text/html", "utf-8", null);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public Controller getController() {
        return controller;
    }

//    @Override

    //    public void onViewCreated(View v, Bundle savedInstanceState) {
    private void getData(long rssId) {
        rssItem = cupboard
                .query(ContentUris.withAppendedId(RssProvider.RSS_URI, rssId), RssItem.class)
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
