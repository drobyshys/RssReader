package com.dev.orium.reader.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.orium.reader.R;
import com.dev.orium.reader.utils.DateUtils;
import com.dev.orium.reader.model.Feed;
import com.dev.orium.reader.model.RssItem;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.qbusict.cupboard.Cupboard;

public class RssItemAdapter extends CursorAdapter {
    private final Cupboard mCupboard;
    private Feed feed;

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    static class Holder {
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

        mCupboard = new Cupboard();
        mCupboard.register(RssItem.class);
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
        holder.date.setText(DateUtils.getDateString(item.publicationDate));
//        holder.info.setText(Html.fromHtml(item.description));
        holder.info.setText(makeTitle(item));
        if (feed != null) {
            holder.feedTitle.setText(feed.title);
        }
        holder.icon.setVisibility(View.GONE);
        ImageLoader.getInstance().displayImage(item.mediaURL, holder.icon, new ImageLoadingListener() {
            @Override public void onLoadingStarted(String imageUri, View view) { }
            @Override public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                view.setVisibility(View.GONE);
            }
            @Override public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                view.setVisibility(loadedImage != null ? View.VISIBLE : View.GONE);
            }
            @Override public void onLoadingCancelled(String imageUri, View view) {
                if (view != null) view.setVisibility(View.GONE);
            }
        });
    }

    private Spanned makeTitle(RssItem item) {
        Document doc = Jsoup.parse(item.description);
        for (Element image : doc.select("img")) {
            image.remove();
        }
        String spanned = Html.fromHtml(doc.toString()).toString();
        SpannableString spanString = new SpannableString(item.title + " - " + spanned);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, item.title.length(), 0);
        spanString.setSpan(new ForegroundColorSpan(!item.readed ? Color.BLACK : Color.GRAY), 0, spanString.length(), 0);
        return spanString;
    }


}
