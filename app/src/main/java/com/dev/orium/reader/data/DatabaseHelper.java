package com.dev.orium.reader.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dev.orium.reader.model.Feed;
import com.dev.orium.reader.model.RssItem;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by y.drobysh on 21.11.2014.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "app.db";
    public static final int DATABASE_VERSION = 1;

    static {
        cupboard().register(Feed.class);
        cupboard().register(RssItem.class);
    }


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        cupboard().withDatabase(db).createTables();

        db.execSQL("CREATE UNIQUE INDEX feed_f_r_1 ON RssItem (_uniqueId, _feedId);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        cupboard().withDatabase(db).upgradeTables();
        //migration here
    }


    public static long getFeedCount(Context context) {
        return cupboard().withContext(context).query(RssProvider.FEEDS_URI, Feed.class).getCursor().getCount();
    }
}
