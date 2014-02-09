package com.rssreader.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.xmlpull.v1.*;

import com.rssreader.utils.Summarizer;

import android.content.Context;

/**
 * Class used to parse a single feed from a given file.
 * 
 * @author Viktor Bukurov
 * @version 1.0
 * @since 2014-02-10
 */
class FeedParser {
	private static final int RSS_MAIN_PROPERTIES_DEPTH = 3;
	private static final int ATOM_MAIN_PROPERTIES_DEPTH = 2;

	/**
	 * Parsing a single feed from an given file.
	 * 
	 * @param file the file the feed is stored to.
	 * @param context the application context.
	 * @param summarizationPercent the summarization percent used to generate summaries for the feed items.
	 * @return an instance of a Feed class.
	 * @throws XmlPullParserException if error occurs while parsing the feed.
	 * @throws IOException if error occurs while reading from the file.
	 * @throws ParseException if error occurs while parsing the feed.
	 */
	public static Feed parseFeed(File file, Context context, int summarizationPercent)
			throws XmlPullParserException, IOException, ParseException{
		Summarizer summarizer = new Summarizer(summarizationPercent);
		
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
				feed = parseRssFeed(parser, summarizer);
			}
			else if (tagName.equalsIgnoreCase("feed")) {
				feed = parseAtomFeed(parser, summarizer);
			}
        }
        
        return feed;
	}

	/**
	 * Parsing a RSS feed.
	 * 
	 * @param parser the XmlPullParser used for parsing.
	 * @param summarizer the Summarizer used to generate summaries for the feed items.
	 * @return an instance of a Rss Feed.
	 * @throws XmlPullParserException if error occurs while parsing the feed.
	 * @throws IOException if error occurs while reading from the file.
	 * @throws ParseException if error occurs while parsing the feed.
	 */
	private static Feed parseRssFeed(XmlPullParser parser, Summarizer summarizer) throws XmlPullParserException, IOException, ParseException {
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
				RssTag endRssTag = RssTag.getValueOf(tagName);
				if (endRssTag != null) {
					switch (endRssTag) {
						case CHANNEL:
							done = true;
							break;
						case ITEM:
							currentFeedItem.generateSummary(summarizer);
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

	/**
	 * Parsing an Atom feed.
	 * 
	 * @param parser the XmlPullParser used for parsing.
	 * @param summarizer the Summarizer used to generate summaries for the feed items.
	 * @return an instance of a Atom Feed.
	 * @throws XmlPullParserException if error occurs while parsing the feed.
	 * @throws IOException if error occurs while reading from the file.
	 * @throws ParseException if error occurs while parsing the feed.
	 */
	private static Feed parseAtomFeed(XmlPullParser parser, Summarizer summarizer) throws XmlPullParserException, IOException, ParseException {
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
							currentFeedItem.generateSummary(summarizer);
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
	
	/**
	 * Parsing a date using the specified format for RSS feeds.
	 * 
	 * @param dateString the date as a string.
	 * @return the parsed date
	 * @throws ParseException if the string is of incorrect format.
	 */
	private static Date parseRssDate(String dateString) throws ParseException  {
		SimpleDateFormat dateFormater = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
		try {
			return dateFormater.parse(dateString);
		} catch (ParseException e) {
			dateFormater = new SimpleDateFormat("EEE, dd MMM yy HH:mm:ss Z", Locale.US);
			return dateFormater.parse(dateString);
		}
	}
	
	/**
	 * Parsing a date using the specified format for Atom feeds.
	 * 
	 * @param dateString the date as a string.
	 * @return the parsed date
	 * @throws ParseException if the string is of incorrect format.
	 */
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
