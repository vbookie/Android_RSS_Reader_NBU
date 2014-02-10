package com.rssreader.db;

import android.provider.BaseColumns;

/**
 * Describes the tables and columns in the Database.
 * 
 * @author Viktor Bukurov
 * @version 1.0
 * @since 2014-02-10
 */
public final class RssReaderContract {
    public RssReaderContract() {}

    /**
     * Describes the Feeds table.
     * 
	 * @author Viktor Bukurov
	 * @version 1.0
	 * @since 2014-02-10
     */
    public static abstract class FeedsTable implements BaseColumns {
    	public static final String TABLE_NAME = "feeds";
    	public static final String COLUMN_NAME_FEED_URL = "feed_url";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_LINK = "link";
        public static final String COLUMN_NAME_LAST_UPDATED = "last_updated";
    }

    /**
     * Describes the FeedItems table.
     * 
     * @author Viktor Bukurov
	 * @version 1.0
	 * @since 2014-02-10
     */
    public static abstract class FeedItemsTable implements BaseColumns {
        public static final String TABLE_NAME = "feed_items";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_LINK = "link";
        public static final String COLUMN_NAME_GUID = "guid";
        public static final String COLUMN_NAME_CONTENT= "content";
        public static final String COLUMN_NAME_SUMMARY= "summary";
        public static final String COLUMN_NAME_PUBLICATION_DATE = "publication_date";
        public static final String COLUMN_NAME_FEED_ID= "feed_id";
    }
}
