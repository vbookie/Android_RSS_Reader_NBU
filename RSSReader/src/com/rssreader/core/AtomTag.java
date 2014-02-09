package com.rssreader.core;

import java.util.Locale;

/**
 * Supported tag elements of an Atom feed. 
 * 
 * @author Viktor Bukurov
 * @version 1.0
 * @since 2014-02-10
 * 
 */
enum AtomTag {
	ENTRY,
	TITLE, // Required
	ID, // Required
	SUBTITLE,
	UPDATED, // Required
	SUMMARY,
	CONTENT,
	FEED; 
	
	/**
	 * Gets AtomTag by it's name.
	 * 
	 * @param text The name of the tag to search for.
	 * @return AtomTag if found, otherwise returns null.
	 */
	public static AtomTag getValueOf(String text) {
		if (text != null) {
			for(AtomTag tag : AtomTag.values()) {
				if (text.equalsIgnoreCase(tag.name()) || text.startsWith(tag.name().toLowerCase(Locale.US)))
					return tag;
			}
		}
		return null;
	}
}
