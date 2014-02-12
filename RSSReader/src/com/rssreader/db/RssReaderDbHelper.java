package com.rssreader.db;

import com.rssreader.db.RssReaderContract.*;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper class to work with SQLite Database.
 * 
 * @author Viktor Bukurov
 * @version 1.0
 * @since 2014-02-10
 */
public class RssReaderDbHelper extends SQLiteOpenHelper {
	// If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RssReader.db";
    
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String UNIQUE = " UNIQUE";
    
    /**
     * SQLite query for creating Feeds table.
     */
    private static final String SQL_CREATE_TABLE_FEEDS =
        "CREATE TABLE " + FeedsTable.TABLE_NAME + " (" +
		FeedsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        FeedsTable.COLUMN_NAME_FEED_URL + TEXT_TYPE + UNIQUE + COMMA_SEP +	
		FeedsTable.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
        FeedsTable.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
        FeedsTable.COLUMN_NAME_LINK + TEXT_TYPE + COMMA_SEP +
        FeedsTable.COLUMN_NAME_LAST_UPDATED + TEXT_TYPE + 
        " )";
    
    /**
     * SQLite query for creating FeedItems table.
     */
    private static final String SQL_CREATE_TABLE_FEED_ITEMS =
            "CREATE TABLE " + FeedItemsTable.TABLE_NAME + " (" +
    		FeedItemsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
    		FeedItemsTable.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
            FeedItemsTable.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
            FeedItemsTable.COLUMN_NAME_LINK + TEXT_TYPE + UNIQUE + COMMA_SEP +
            FeedItemsTable.COLUMN_NAME_CONTENT + TEXT_TYPE + COMMA_SEP +
            FeedItemsTable.COLUMN_NAME_SUMMARY + TEXT_TYPE + COMMA_SEP +
            FeedItemsTable.COLUMN_NAME_GUID + TEXT_TYPE + COMMA_SEP +
            FeedItemsTable.COLUMN_NAME_PUBLICATION_DATE + TEXT_TYPE + COMMA_SEP +
            FeedItemsTable.COLUMN_NAME_FEED_ID + INT_TYPE + COMMA_SEP +
            "FOREIGN KEY(" + FeedItemsTable.COLUMN_NAME_FEED_ID + ") " +
            "REFERENCES " + FeedsTable.TABLE_NAME + "(" + FeedsTable._ID + ")" +
            " )";
    
    /**
     * SQLite query for deletion of Feeds table.
     */
    private static final String SQL_DROP_TABLE_FEEDS =
        "DROP TABLE IF EXISTS " + FeedsTable.TABLE_NAME;
    
    /**
     * SQLite query for deletion of FeedItems table.
     */
    private static final String SQL_DROP_TABLE_FEED_ITEMS =
        "DROP TABLE IF EXISTS " + FeedItemsTable.TABLE_NAME;
    
    /**
     * Creates a new instance of RssHelperDbHelper class.
     * 
     * @param context the application context.
     */
    public RssReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onConfigure(SQLiteDatabase db) {
    	super.onConfigure(db);
    	db.setForeignKeyConstraintsEnabled(true);
    }
    
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_FEEDS);
        db.execSQL(SQL_CREATE_TABLE_FEED_ITEMS);
    }
    
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
    	db.execSQL(SQL_DROP_TABLE_FEED_ITEMS);
    	db.execSQL(SQL_DROP_TABLE_FEEDS); 
        onCreate(db);
    }
    
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
