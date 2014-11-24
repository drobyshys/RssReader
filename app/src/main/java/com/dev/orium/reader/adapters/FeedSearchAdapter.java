package com.dev.orium.reader.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.orium.reader.R;
import com.dev.orium.reader.model.Feed;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by y.drobysh on 24.11.2014.
 */
public class FeedSearchAdapter extends ArrayAdapter<Feed> {

    static class ViewHolder {
        @InjectView(R.id.tvTitle) TextView title;
        @InjectView(R.id.tvTitle2) TextView title2;
        @InjectView(R.id.ivIcon) ImageView icon;
        private ViewHolder(View view) {
            ButterKnife.inject(this, view);
            icon.setVisibility(View.GONE);
            title.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }

    public FeedSearchAdapter(Context context, List<Feed> items) {
        super(context, R.layout.item_feed, items);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_feed, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Feed item = getItem(position);
        holder.title.setText(Html.fromHtml(item.title));
        holder.title2.setText(Html.fromHtml(item.contentSnippet));

        return convertView;
    }



}
