package com.dev.orium.reader.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.dev.orium.reader.Settings;

/**
 * Created by y.drobysh on 24.11.2014.
 */
public class SharedUtils {

    private static final String LAST_SELECTED_FEED = "_last_feed";


    private static Context ctx;

    public static void init(Application context) {
        ctx = context;
    }

    public static long getLastSelectedFeed() {
        SharedPreferences sp = ctx.getSharedPreferences(Settings.SHARED_PREF_FILE, Context.MODE_PRIVATE);
        return sp.getLong(LAST_SELECTED_FEED, -1);
    }

    public static void saveLastSelectedFeed(long id) {
        SharedPreferences.Editor edit = ctx.getSharedPreferences(Settings.SHARED_PREF_FILE, Context.MODE_PRIVATE)
                .edit();
        edit.putLong(LAST_SELECTED_FEED, id);
        edit.commit();
    }

}
