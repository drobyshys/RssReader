package com.dev.orium.reader.parser;

import com.dev.orium.reader.model.Feed;
import com.dev.orium.reader.model.RssItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AtomParser {

	final static String NS_ATOM = "http://www.w3.org/2005/Atom";

	public static Feed process(XmlPullParser parser, Feed feed) throws XmlPullParserException, IOException {
		for (int eventType = parser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = parser.next()) {
			if (eventType == XmlPullParser.START_TAG) {
				if (isAtomElement(parser, "title"))
					feed.title = parser.nextText();
				else if (isAtomElement(parser, "icon"))
					feed.thumbnail  = parser.nextText();
				else if (isAtomElement(parser, "updated"))
					feed.lastBuildDate  = Utils.parseDate(parser.nextText());
				else if (isAtomElement(parser, "entry"))
					break;
			}
		}
        parseEntries(parser, feed);
        return feed;
	}

	private static void parseEntries(XmlPullParser parser, Feed feed) throws XmlPullParserException, IOException {
        feed.entries = new ArrayList<RssItem>();
        RssItem item = null;

		// grab podcasts from item tags
		for (int eventType = parser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = parser.next()) {
			if (eventType == XmlPullParser.START_TAG) {
				if (isAtomElement(parser, "entry")) {
					item = new RssItem();
				} else if (isAtomElement(parser, "id")) {
					item._uniqueId = parser.nextText();
				} else if (isAtomElement(parser, "title")) {
					item.title = parser.nextText();
				} else if (isAtomElement(parser, "link")) {
					String rel = parser.getAttributeValue(null, "rel");
					if (rel == null || rel.equals("alternate"))
						item.link = parser.getAttributeValue(null, "href");
					else if (rel.equals("payment"))
						item.paymentURL  = parser.getAttributeValue(null, "href");
					else if (rel.equals("enclosure")) {
//						if (parser.getAttributeValue(null, "length") != null)
//							item.med(Long.valueOf(parser.getAttributeValue(null, "length")));
						item.mediaURL = parser.getAttributeValue(null, "href");
					}
				} else if (isAtomElement(parser, "summary") && item.description == null)
					item.description = parser.nextText();
				else if (isAtomElement(parser, "content"))
					item.description  = parser.nextText();
				else if (isAtomElement(parser, "published"))
					item.publicationDate = Utils.parseDate(parser.nextText());
				else if (isAtomElement(parser, "updated") && item.publicationDate == null)
					item.publicationDate = Utils.parseDate(parser.nextText());
			} else if (eventType == XmlPullParser.END_TAG) {
				if (isAtomElement(parser, "entry")) {
                    item._feedId = feed._id;
                    feed.entries.add(item);
				}
			}
		}
	}

	private static boolean isAtomElement(XmlPullParser parser, String name) {
		return parser.getName().equals(name) && parser.getNamespace().equals(NS_ATOM);
	}
}
