package com.rssreader.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.xmlpull.v1.*;

import android.content.Context;

class FeedParser {
	private static final int RSS_MAIN_PROPERTIES_DEPTH = 3;
	private static final int ATOM_MAIN_PROPERTIES_DEPTH = 2;

	public static Feed parseFeed(File file, Context context)
			throws XmlPullParserException, IOException, ParseException{
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(false);
        XmlPullParser parser = factory.newPullParser();
        FileInputStream in = context.openFileInput(file.getName());
        parser.setInput(in, null);

        // Searches for the first tag to help determine the type of the feed - rss or atom.
        int eventType = parser.getEventType(); 
        while (eventType != XmlPullParser.START_TAG && eventType != XmlPullParser.END_DOCUMENT) {
        	eventType = parser.next();
        }
        
        Feed feed = null; 
        if (eventType == XmlPullParser.START_TAG) {
        	String tagName = parser.getName();
			if (tagName.equalsIgnoreCase("rss")) {
				feed = parseRssFeed(parser);
			}
			else if (tagName.equalsIgnoreCase("feed")) {
				feed = parseAtomFeed(parser);
			}
        }
        
        return feed;
	}

	private static Feed parseRssFeed(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
		Feed feed = new Feed(FeedType.RSS);
		FeedItem currentFeedItem = null;

		boolean done = false;
		boolean isItem = false;
		String tagName = null;
		int eventType = parser.getEventType(); 
		while (eventType != XmlPullParser.END_DOCUMENT && !done) {
			switch(eventType) {
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				System.out.println("Rss Start Tag: " + tagName);
				RssTag rssTag = RssTag.getValueOf(tagName);
				if (rssTag != null) {
					switch (rssTag) {
					case ITEM:
						currentFeedItem = new FeedItem();
						isItem = true;
						break;
					case TITLE:
						String title = parser.nextText();
						if (isItem) {
							currentFeedItem.setTitle(title);
						} else if (parser.getDepth() == RSS_MAIN_PROPERTIES_DEPTH) {
							feed.setTitle(title);
						}
						break;
					case LINK: 
						String link = parser.nextText();
						if (isItem) {
							currentFeedItem.setLink(link);
						} else if (parser.getDepth() == RSS_MAIN_PROPERTIES_DEPTH) {
							feed.setLink(link);
						}
						break;
					case DESCRIPTION:
						String description = parser.nextText();
						if (isItem) {
							currentFeedItem.setDescription(description);
						} else if (parser.getDepth() == RSS_MAIN_PROPERTIES_DEPTH) {
							feed.setDescription(description);
						}
						break;
					case LASTBUILDDATE:
						String lastBuildDateString = parser.nextText();
						Date lastBuildDate = parseRssDate(lastBuildDateString);
						feed.setLastUpdatedDate(lastBuildDate);
						break;
					case CONTENT:
						String content = parser.nextText();
						currentFeedItem.setContent(content);
						break;
					case GUID:
						String guid = parser.nextText();
						currentFeedItem.setGuid(guid);
						break;
					case PUBDATE:
						String pubDateString = parser.nextText();
						Date pubDate = parseRssDate(pubDateString);
						currentFeedItem.setPublicationDate(pubDate);
						break;
					case CHANNEL:
						break;
					default:
						break;			
					}
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				System.out.println("Rss End Tag: " + tagName);
				RssTag endRssTag = RssTag.getValueOf(tagName);
				if (endRssTag != null) {
					switch (endRssTag) {
						case CHANNEL:
							done = true;
							break;
						case ITEM:
							feed.addFeedItem(currentFeedItem);
							isItem = false;
							break;
						default:
							break;
					}
				}
				break;
			}
			eventType = parser.next();
		}
		return feed;
	}

	private static Feed parseAtomFeed(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
		Feed feed = new Feed(FeedType.Atom);
		int eventType = parser.getEventType(); 
		boolean done = false;
		boolean isItem = false;
		String tagName = null;
		FeedItem currentFeedItem = null;
		while (eventType != XmlPullParser.END_DOCUMENT && !done) {
			switch(eventType) {
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				System.out.println("Atom Start Tag: " + tagName);
				AtomTag atomTag = AtomTag.getValueOf(tagName);
				if (atomTag != null) {
					switch (atomTag) {
					case ENTRY:
						currentFeedItem = new FeedItem();
						isItem = true;
						break;
					case TITLE:
						String title = parser.nextText();
						if (isItem) {
							currentFeedItem.setTitle(title);
						} else if (parser.getDepth() == ATOM_MAIN_PROPERTIES_DEPTH) {
							feed.setTitle(title);
						}
						break;
					case ID: 
						String link = parser.nextText();
						if (isItem) {
							currentFeedItem.setLink(link);
						} else if (parser.getDepth() == ATOM_MAIN_PROPERTIES_DEPTH) {
							feed.setLink(link);
						}
						break;
					case SUBTITLE:
						String subtitle = parser.nextText();
						feed.setDescription(subtitle);
						break;
					case SUMMARY:
						String summary = parser.nextText();
						currentFeedItem.setDescription(summary);
						break;
					case UPDATED:
						String updatedString = parser.nextText();
						Date updatedDate = parseAtomDate(updatedString);
						if (isItem) {
							currentFeedItem.setPublicationDate(updatedDate);
						} else if (parser.getDepth() == ATOM_MAIN_PROPERTIES_DEPTH) {
							feed.setLastUpdatedDate(updatedDate);
						}
						break;
					case CONTENT:
						break;
					case FEED:
						break;
					default:
						break;		
					}
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				System.out.println("Rss End Tag: " + tagName);
				AtomTag endAtomTag = AtomTag.getValueOf(tagName);
				if (endAtomTag != null) {
					switch (endAtomTag) {
						case FEED:
							done = true;
							break;
						case ENTRY:
							feed.addFeedItem(currentFeedItem);
							isItem = false;
							break;
						default:
							break;
					}
				}
				break;
			}
			eventType = parser.next();
		}
		return feed;
	}
	
	private static Date parseRssDate(String dateString) throws ParseException  {
		SimpleDateFormat dateFormater = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
		try {
			return dateFormater.parse(dateString);
		} catch (ParseException e) {
			dateFormater = new SimpleDateFormat("EEE, dd MMM yy HH:mm:ss Z", Locale.US);
			return dateFormater.parse(dateString);
		}
	}
	
	private static Date parseAtomDate(String dateString) throws ParseException {
        SimpleDateFormat dateFormater = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssz", Locale.US);
        
        if ( dateString.endsWith( "Z" ) ) {
        	dateString = dateString.substring( 0, dateString.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;
        
            String s0 = dateString.substring( 0, dateString.length() - inset );
            String s1 = dateString.substring( dateString.length() - inset, dateString.length() );

            dateString = s0 + "GMT" + s1;
        }
        
        return dateFormater.parse( dateString );
	}
}
