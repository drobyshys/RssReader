package com.dev.orium.reader.parser;

import com.dev.orium.reader.model.Feed;
import com.dev.orium.reader.model.RssItem;

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

        try {
            URL url = new URL(feed.urlFeed);
            feed.iconUrl = url.getProtocol() + "://" + url.getAuthority() + "/favicon.ico";
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

}
