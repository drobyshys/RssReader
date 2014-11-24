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
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.qbusict.cupboard.Cupboard;

/**
 * Created by y.drobysh on 21.11.2014.
 */
public class MenuFeedAdapter extends CursorAdapter {

    private final Cupboard mCupboard;

    static class ViewHolder {
        @InjectView(R.id.tvTitle) TextView title;
        @InjectView(R.id.tvTitle2) TextView title2;
        @InjectView(R.id.ivIcon) ImageView icon;

        private ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public MenuFeedAdapter(Context context) {
        super(context, null, 0);

        mCupboard = new Cupboard();
        mCupboard.register(Feed.class);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Feed feed = mCupboard.withCursor(cursor).get(Feed.class);
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.title.setText(Html.fromHtml(feed.title));
        ImageLoader.getInstance().displayImage(feed.iconUrl, holder.icon);
    }
}
