package com.dev.orium.reader.parser;

import com.dev.orium.reader.model.Feed;
import com.dev.orium.reader.model.RssItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class FeedParser {

	public static class UnknownFeedException extends Exception {
		private static final long serialVersionUID = -4953090101978301549L;
		public UnknownFeedException() {
			super("This is not a RSS or Atom feed and is unsupported by Riasel.");
		}
		public UnknownFeedException(Throwable throwable) {
			super("This is not a RSS or Atom feed and is unsupported by Riasel.", throwable);
		}
	}

	public FeedParser() {
	}

	public static void parseFeed(XmlPullParser parser, Feed feed) throws XmlPullParserException, IOException, UnknownFeedException {

		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.START_TAG)
			eventType = parser.next();
		if (parser.getName().equals("rss")) {
             RSSParser.process(parser, feed);
		} else if (parser.getName().equals("feed")) {
             AtomParser.process(parser, feed);
		} else {
			throw new UnknownFeedException();
		}

        processThumbnails(feed.entries);
    }

    private static void processThumbnails(List<RssItem> entries) {
        if (entries == null) return;
        int size = entries.size();
        for (int i = 0; i < size; i++) {
            RssItem item = entries.get(i);
            if (item.mediaURL != null && item.mediaURL.trim().length() > 0)
                continue;

            Document doc = Jsoup.parse(item.description);
            Elements images = doc.select("img");
            if (images.size() > 0) {
                item.mediaURL = images.get(0).absUrl("src");
            }
        }
    }

}
