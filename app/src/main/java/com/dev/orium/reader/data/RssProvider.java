package com.dev.orium.reader.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.dev.orium.reader.model.Feed;
import com.dev.orium.reader.model.RssItem;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by y.drobysh on 21.11.2014.
 */
public class RssProvider extends ContentProvider {
    private DatabaseHelper dbHelper;

    // used for the UriMacher
    private static final int FEEDS = 10;
    private static final int FEED_ITEM = 20;

    private static final int RSS = 30;
    private static final int RSS_ITEM = 31;

    private static final String AUTHORITY = "com.dev.orium.reader.data";

    private static final String FEEDS_PATH = "feeds";
    private static final String RSS_PATH = "rss";


    public static final Uri FEEDS_URI = Uri.parse("content://" + AUTHORITY
            + "/" + FEEDS_PATH);
    public static final Uri RSS_URI = Uri.parse("content://" + AUTHORITY
            + "/" + RSS_PATH);


    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, FEEDS_PATH, FEEDS);
        sURIMatcher.addURI(AUTHORITY, FEEDS_PATH + "/#", FEED_ITEM);

        sURIMatcher.addURI(AUTHORITY, RSS_PATH, RSS);
        sURIMatcher.addURI(AUTHORITY, RSS_PATH + "/#", RSS_ITEM);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (sURIMatcher.match(uri)) {
            case FEEDS:
                cursor = cupboard().withDatabase(db).query(Feed.class)
                        .withProjection(projection)
                        .withSelection(selection)
                        .orderBy(sortOrder)
                        .getCursor();
                break;
            case FEED_ITEM:
                cursor = cupboard().withDatabase(db).query(Feed.class)
                        .byId(ContentUris.parseId(uri))
                        .getCursor();
                break;
            case RSS:
                cursor = cupboard().withDatabase(db).query(RssItem.class)
                        .withProjection(projection)
                        .withSelection(selection, selectionArgs)
                        .orderBy(sortOrder)
                        .getCursor();
                break;
            case RSS_ITEM:
                cursor = cupboard().withDatabase(db).query(RssItem.class)
                        .byId(ContentUris.parseId(uri))
                        .getCursor();
                break;
        }
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        String base;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id;
        switch (uriType) {
            case FEEDS:
            case FEED_ITEM:
                id = cupboard().withDatabase(db).put(Feed.class, values);
                base = FEEDS_PATH;


                break;
            case RSS:
                id = db.insertWithOnConflict("RssItem", null, values, SQLiteDatabase.CONFLICT_IGNORE);
                base = RSS_PATH;
                break;
            case RSS_ITEM:
//                id = db.insertWithOnConflict("RssItem", null, values, SQLiteDatabase.CONFLICT_REPLACE);
                id = cupboard().withDatabase(db).put(RssItem.class, values);
                base = RSS_PATH;
                break;
            default:
                throw new IllegalArgumentException("unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(base + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
