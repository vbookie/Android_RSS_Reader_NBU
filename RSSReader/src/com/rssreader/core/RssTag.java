package com.rssreader.core;

import java.util.Locale;

/**
 * Tag elements of a RSS feed. 
 * 
 * @author Viktor Bukurov
 * @version 1.0
 * @since 2014-02-10
 * 
 */
enum RssTag{
	ITEM,
	TITLE, // Required
	LINK, // Required
	DESCRIPTION, // Required
	LASTBUILDDATE,
	PUBDATE,
	GUID,
	CONTENT,
	CHANNEL;

	/**
	 * Gets a RssTag by it's name.
	 * 
	 * @param text The name of the tag to search for.
	 * @return RssTag if found, otherwise returns null.
	 */
	public static RssTag getValueOf(String text) {
		if (text != null) {
			for(RssTag tag : RssTag.values()) {
				if (text.equalsIgnoreCase(tag.name()) || text.startsWith(tag.name().toLowerCase(Locale.US)))
					return tag;
			}
		}
		return null;
	}
}
