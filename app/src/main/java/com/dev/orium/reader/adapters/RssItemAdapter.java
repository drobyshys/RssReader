package com.dev.orium.reader.adapters;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.orium.reader.R;
import com.dev.orium.reader.model.Feed;
import com.dev.orium.reader.model.RssItem;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.qbusict.cupboard.Cupboard;

/**
 * Created by y.drobysh on 21.11.2014.
 */
public class RssItemAdapter extends CursorAdapter {
    private final Cupboard mCupboard;
    private final SimpleDateFormat dateF;
    private final SimpleDateFormat timeF;
    private final int year;
    private final String yesterday;
    private Calendar calendar = Calendar.getInstance();
    private final int todayDay;
    private StringBuilder sb;
    private final String today;
    private Feed feed;

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    static class Holder {
        @InjectView(R.id.tvTitle) TextView tvTitle;
        @InjectView(R.id.tvDate) TextView date;
        @InjectView(R.id.ivIcon) ImageView icon;
        @InjectView(R.id.tvInfo) TextView info;
        @InjectView(R.id.tvFeedTitle) TextView feedTitle;

        Holder(View v) {
            ButterKnife.inject(this, v);
        }
    }

    public RssItemAdapter(Context context) {
        super(context, null, 0);

        dateF = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        timeF = new SimpleDateFormat(", hh.mm", Locale.getDefault());

        mCupboard = new Cupboard();
        mCupboard.register(RssItem.class);

        calendar.setTime(new Date());
        todayDay = calendar.get(Calendar.DAY_OF_YEAR);
        year = calendar.get(Calendar.YEAR);

        sb = new StringBuilder();


        today = context.getString(R.string.today);
        yesterday = context.getString(R.string.yesterday);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_rss, parent, false);
        v.setTag(new Holder(v));
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Holder holder = (Holder) view.getTag();
        RssItem item = mCupboard.withCursor(cursor).get(RssItem.class);
        holder.tvTitle.setText(item.title);
        holder.date.setText(getDateString(item.publicationDate));
        holder.info.setText(Html.fromHtml(item.description));
        ImageLoader.getInstance().displayImage(item.mediaURL, holder.icon);
        if (feed != null) {
            holder.feedTitle.setText(feed.title);
        }

    }

    private String getDateString(Date date) {
        calendar.setTime(date);

        int day = calendar.get(Calendar.DAY_OF_YEAR);
        if (calendar.get(Calendar.YEAR) == year) {
            if (day == todayDay) {
                return today + timeF.format(date);
            }
            if (day == todayDay - 1) {
                return yesterday + timeF.format(date);
            }
        }
        return dateF.format(date);
    }
}
