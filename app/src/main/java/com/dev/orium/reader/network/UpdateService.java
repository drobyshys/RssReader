package com.dev.orium.reader.network;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.Intent;
import android.content.Context;

import com.dev.orium.reader.model.Feed;
import com.dev.orium.reader.data.RssProvider;
import com.dev.orium.reader.model.RssItem;
import com.dev.orium.reader.parser.FeedParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class UpdateService extends IntentService {
    private static final String ACTION_UPDATE = "com.dev.orium.reader.network.action.UPDATE";
    private static final String ACTION_UPDATE_ALL = "com.dev.orium.reader.network.action.UPDATE_ALL";

    public static final String EXTRA_FEED_TO_UPDATE = "com.dev.orium.reader.network.extra.FEED_ID";

    public static void startActionUpdate(Context context, int id) {
        Intent intent = new Intent(context, UpdateService.class);
        intent.setAction(ACTION_UPDATE);
        intent.putExtra(EXTRA_FEED_TO_UPDATE, id);
        context.startService(intent);
    }

    public static void startActionUpdateAll(Context context) {
        Intent intent = new Intent(context, UpdateService.class);
        intent.setAction(ACTION_UPDATE_ALL);
        context.startService(intent);
    }

    public UpdateService() {
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE.equals(action)) {
                final int id = intent.getIntExtra(EXTRA_FEED_TO_UPDATE, 0);
                handleActionUpdateFeed(id);
            } else if (ACTION_UPDATE_ALL.equals(action)) {
                handleActionUpdateAll();
            }
        }
    }


    private void handleActionUpdateFeed(int id) {
        Feed feed = cupboard().withContext(this)
                .get(ContentUris.withAppendedId(RssProvider.FEEDS_URI, id), Feed.class);
        if (feed != null) {

            try {
                XmlPullParser parser = RequestHelper.getParserByUrl(feed.urlFeed);
                FeedParser.parseFeed(parser, feed);

                cupboard().withContext(this).put(RssProvider.RSS_URI, RssItem.class, feed.entries);


                cupboard().withContext(this).put(RssProvider.FEEDS_URI, feed);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (FeedParser.UnknownFeedException e) {
                e.printStackTrace();
            }



        }
    }

    private void handleActionUpdateAll() {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
