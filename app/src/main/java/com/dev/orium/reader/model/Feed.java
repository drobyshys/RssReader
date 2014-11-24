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

/**
 * Created by y.drobysh on 20.11.2014.
 */
public class Feed implements Parcelable {

    public Long _id;

    public String title;
    public String urlFeed;
    public String contentSnippet;
    public String link;
    public String thumbnail;
    public Date lastBuildDate;
    public Date pubDate;
    public String iconUrl;



    public transient List<RssItem> entries;

    public Feed() {
    }

    @Override
    public String toString() {
        return title;
    }


    //region Parcelable code

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(urlFeed);
        dest.writeString(contentSnippet);
        dest.writeString(link);
    }

    public static final Creator<Feed> CREATOR = new Creator<Feed>() {
        @Override
        public Feed createFromParcel(Parcel source) {
            return new Feed(source);
        }

        @Override
        public Feed[] newArray(int size) {
            return new Feed[size];
        }
    };

    private Feed(Parcel in) {
        title = in.readString();
        urlFeed = in.readString();
        contentSnippet = in.readString();
        link = in.readString();
    }

    public static Feed getById(Context context, long id) {
        return CupboardFactory.cupboard().withContext(context)
                .get(ContentUris.withAppendedId(RssProvider.FEEDS_URI, id), Feed.class);
    }

    //endregion
}
