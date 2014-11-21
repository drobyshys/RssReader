package com.dev.orium.clearsky.network;

import android.net.Uri;

import com.dev.orium.clearsky.data.Feed;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by y.drobysh on 20.11.2014.
 */
public class RequestHelper {

    private static final String SEARCH_URL = "https://ajax.googleapis.com/ajax/services/feed/find";
    private static final String ENTRIES = "entries";
    private static final String RESPONSE_DATA = "responseData";

    public static List<Feed> searchFeedRequest(String query) throws IOException, JSONException {

        try {
            URL url = new URL(query);
            return findFeedsOnSite();
        }
        catch (MalformedURLException e) { }


        return searchFeeds(query);
    }

    private static List<Feed> findFeedsOnSite() {

        return null;
    }

    private static List<Feed> searchFeeds(String query) throws IOException, JSONException {
        String urlString = Uri.parse(SEARCH_URL).buildUpon()
                .appendQueryParameter("v", "1.0")
                .appendQueryParameter("q", query)
                .build().toString();

        String body = getBodyStringByUrl(urlString);

        ArrayList<Feed> result = new ArrayList<Feed>();

        JSONObject jsonObject = new JSONObject(body);

        if (jsonObject.has(RESPONSE_DATA)) {
            JSONObject jsonResponse = jsonObject.getJSONObject(RESPONSE_DATA);
            if (jsonResponse.has(ENTRIES)) {
                JSONArray feeds = jsonResponse.getJSONArray(ENTRIES);

                for (int i = 0; i < feeds.length(); i++) {
                    Feed f = new Feed();
                    JSONObject feed = feeds.getJSONObject(i);
                    f.title = feed.getString("title");
                    f.contentSnippet = feed.getString("contentSnippet");
                    f.url = feed.getString("url");
                    f.link = feed.getString("link");

                    result.add(f);
                }
            }
        }

        return result;
    }

    private static String getBodyStringByUrl(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();


        Response response = client.newCall(request).execute();

        return response.body().string();
    }

}
