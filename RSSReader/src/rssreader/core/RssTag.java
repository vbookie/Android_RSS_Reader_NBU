package rssreader.core;

import java.util.Locale;

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
