package com.dev.orium.clearsky.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by y.drobysh on 20.11.2014.
 */
public class Feed implements Parcelable {

    public String title;
    public String url;
    public String contentSnippet;
    public String link;

    public Feed() {}

    @Override
    public String toString() {
        return title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
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
        url = in.readString();
        contentSnippet = in.readString();
        link = in.readString();
    }
}
