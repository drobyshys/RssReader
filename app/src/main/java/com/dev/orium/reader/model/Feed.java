package com.dev.orium.reader.model;

import android.content.ContentUris;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.dev.orium.reader.activities.MainActivity;
import com.dev.orium.reader.data.RssProvider;

import java.util.Date;
import java.util.List;

import nl.qbusict.cupboard.CupboardFactory;

public class Feed {

    public Long _id;

    public String title;
    public String urlFeed;
    public String contentSnippet;
    public String link;
    public String thumbnail;
    public Date lastBuildDate;
    public Date pubDate;
    public String iconUrl;
    public long unreadCount;

    public transient List<RssItem> entries;

    public Feed() {
    }

    @Override
    public String toString() {
        return title;
    }

    public static Feed getById(Context context, long id) {
        return CupboardFactory.cupboard().withContext(context)
                .get(ContentUris.withAppendedId(RssProvider.FEEDS_URI, id), Feed.class);
    }
}
