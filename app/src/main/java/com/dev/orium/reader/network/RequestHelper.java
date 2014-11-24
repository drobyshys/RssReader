package com.dev.orium.reader.network;

import android.net.Uri;

import com.dev.orium.reader.model.Feed;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
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
                    f.urlFeed = feed.getString("url");
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

    public static XmlPullParser getParserByUrl(String urlFeed) throws IOException, XmlPullParserException {
        String bodyString = getBodyStringByUrl(urlFeed);
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(false);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new StringReader(bodyString));
        return parser;
    }
}
