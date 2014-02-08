package com.rssreader.core;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import com.rssreader.R;
import com.rssreader.db.RssReaderDbHelper;
import com.rssreader.db.RssReaderContract.FeedItemsTable;
import com.rssreader.db.RssReaderContract.FeedsTable;

class FeedRefresher {
	private SharedPreferences sharedPrefs;
	private SQLiteDatabase writableDb;
	private SQLiteDatabase readableDb;
	private Context context;

	private synchronized SQLiteDatabase getWritableDb() {
		return this.writableDb;
	}
	
	private synchronized SQLiteDatabase getReadableDb() {
		return this.readableDb;
	}
	
	FeedRefresher(Context context) {
		this.context = context;
		this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public int refreshFeeds() {
		String urlsPrefKey = this.context.getString(R.string.pref_feeds_key);
		if (!sharedPrefs.contains(urlsPrefKey)) {
			return 0;
		}
		Set<String> urls = this.sharedPrefs.getStringSet(urlsPrefKey, null);
		int summaryPercent = getSummaryPercentFromPreference();		
		initDatabase();
		
		int result = 0; // The count of imported feeds
		for (String url : urls) {
			Log.d("FeedsService", "Now importing: " + url);
			try {
				FeedImporter feedImporter = new FeedImporter(url, this.context);
				File file = feedImporter.importFeed();
				Log.d("FeedsService", "Feed imported: " + url);
				Log.d("FeedsService", "Now parsing feed: " + url);
				Feed feed = FeedParser.parseFeed(file, context, summaryPercent);
				Log.d("FeedsService", "Feed parsed: " + url);
				if (feed != null) {
					this.updateDatabase(feed, url);
				} else {
					Log.e("FeedsService", "Feed is empty for url: " + url);
				}
				result++;
			} catch (MalformedURLException e) {
				Log.e("FeedsService", e.getMessage());
			} catch (IOException e) {
				Log.e("FeedsService", e.getMessage());
			} catch (ParseException e) {
				Log.e("FeedsService", e.getMessage());
			} catch (XmlPullParserException e) {
				Log.e("FeedsService", e.getMessage());
			}
		}
		
		this.readableDb.close();
		this.writableDb.close();
		
		return result;
	}

	private int getSummaryPercentFromPreference() {
		String summaryPrefKey = context.getString(R.string.pref_summary_perc_key);
		String defaultSummaryPercent = context.getString(R.string.pref_summary_perc_interval_default);
		String summaryPercentString = sharedPrefs.getString(summaryPrefKey, defaultSummaryPercent);
		int summaryPercent = Integer.parseInt(summaryPercentString);
		return summaryPercent;
	}
	
	private void initDatabase() {	
		RssReaderDbHelper dbHelper = new RssReaderDbHelper(this.context);
		readableDb = dbHelper.getReadableDatabase();
		writableDb = dbHelper.getWritableDatabase();
		// TODO: remove log msg
		Log.d("Database", "Database initialized.");
	}
	
	private final String[] feedColumns = new String[] {
		FeedsTable._ID
	};
	private void updateDatabase(Feed feed, String url) {
		Log.d("FeedsService", "Now updating database for " + url);
		SQLiteCursor cursor = (SQLiteCursor) getReadableDb().query(
			FeedsTable.TABLE_NAME, // table name
			feedColumns, // columns
			FeedsTable.COLUMN_NAME_FEED_URL + " = ?", // selection
			new String[] {url}, // selection arguments
			null, // group by
			null, // having
			null  // order by
		);
		long feedRowId;
		if (cursor.getCount() == 1) {
			Log.d("FeedsService", "Feed already exists. Feed will be updated. " + url);
			cursor.moveToFirst();
			feedRowId = updateFeedEntry(feed, cursor);
			Log.d("FeedsService", "Feed updated.");
		} else {
			Log.d("FeedsService", "Feed dosn't exists. Feed will be added. " + url);
			feedRowId = insertNewFeedEntry(feed, url);
			Log.d("FeedsService", "Feed inserted.");
		}
		Log.d("FeedsService", "Now updating feed items for " + url);
		insertFeedItems(feed, feedRowId);
		Log.d("FeedsService", "Database updated for " + url);
	}

	private long updateFeedEntry(Feed feed, SQLiteCursor cursor) {
		int idColumnIndex = cursor.getColumnIndexOrThrow(FeedsTable._ID);
		long feedRowId = cursor.getLong(idColumnIndex);
		
		ContentValues newValues = new ContentValues();
		newValues.put(FeedsTable.COLUMN_NAME_TITLE, feed.getTitle());
		newValues.put(FeedsTable.COLUMN_NAME_LINK, feed.getLink());
		newValues.put(FeedsTable.COLUMN_NAME_DESCRIPTION, feed.getDescription());
		newValues.put(FeedsTable.COLUMN_NAME_LAST_UPDATED, feed.getLastUpdatedDate().toString());
		
		getWritableDb().update(
			FeedsTable.TABLE_NAME,
			newValues,
			FeedsTable._ID + " = " + feedRowId,
			null
		);
		return feedRowId;
	}
	
	private long insertNewFeedEntry(Feed feed, String url) {
		ContentValues values = new ContentValues();
		values.put(FeedsTable.COLUMN_NAME_FEED_URL, url);
		values.put(FeedsTable.COLUMN_NAME_TITLE, feed.getTitle());
		values.put(FeedsTable.COLUMN_NAME_LINK, feed.getLink());
		values.put(FeedsTable.COLUMN_NAME_DESCRIPTION, feed.getDescription());
		values.put(FeedsTable.COLUMN_NAME_LAST_UPDATED, feed.getLastUpdatedDate().toString());
		long feedRowId = getWritableDb().insertOrThrow(FeedsTable.TABLE_NAME, null, values);
		return feedRowId;
	}
	
	private void insertFeedItems(Feed feed, long feedRowId) {
		for (FeedItem feedItem : feed.getFeedItems()) {
			Log.d("FeedsService", "No inserting feed item: " + feedItem.getLink());
			ContentValues values = new ContentValues();
			values.put(FeedItemsTable.COLUMN_NAME_TITLE, feedItem.getTitle());
			values.put(FeedItemsTable.COLUMN_NAME_LINK, feedItem.getLink());
			values.put(FeedItemsTable.COLUMN_NAME_DESCRIPTION, feedItem.getDescription());
			values.put(FeedItemsTable.COLUMN_NAME_CONTENT, feedItem.getContent());
			values.put(FeedItemsTable.COLUMN_NAME_SUMMARY, feedItem.getSummary());
			values.put(FeedItemsTable.COLUMN_NAME_GUID, feedItem.getGuid());
			values.put(FeedItemsTable.COLUMN_NAME_PUBLICATION_DATE, feedItem.getPublicationDate().toString());
			values.put(FeedItemsTable.COLUMN_NAME_FEED_ID, feedRowId);
			
			// Because the link should be unique, if the item already exists,
			// it won't be inserted and -1 will be returned.
			getWritableDb().insert(FeedItemsTable.TABLE_NAME, null, values);
			Log.d("FeedsService", "Feed item inserted.");
		}
	}
}
