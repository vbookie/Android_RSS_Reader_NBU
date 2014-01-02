package rssreader.core;

/**
 * @author Viktor Bukurov
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
	
	public static AtomTag getValueOf(String text) {
		if (text != null) {
			for(AtomTag tag : AtomTag.values()) {
				if (text.equalsIgnoreCase(tag.name()))
					return tag;
			}
		}
		return null;
	}
}
