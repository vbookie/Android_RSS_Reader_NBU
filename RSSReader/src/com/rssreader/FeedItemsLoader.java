package com.rssreader;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;

import com.rssreader.db.RssReaderContract.FeedItemsTable;
import com.rssreader.db.RssReaderDbHelper;
import com.rssreader.db.RssReaderContract.FeedsTable;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;

class FeedItemsLoader extends AsyncTaskLoader<Cursor> {
    final ForceLoadContentObserver mObserver;
    private SQLiteDatabase readableDb = null;
    
    private String[] mProjection = new String[] {
    		FeedItemsTable._ID,
    		FeedItemsTable.COLUMN_NAME_TITLE,
    		FeedItemsTable.COLUMN_NAME_SUMMARY,
    		FeedItemsTable.COLUMN_NAME_PUBLICATION_DATE,
    		FeedItemsTable.COLUMN_NAME_DESCRIPTION,
    		FeedItemsTable.COLUMN_NAME_CONTENT,
    		FeedItemsTable.COLUMN_NAME_LINK
    };
    private String mSelection = null;
    private String[] mSelectionArgs = null;
    private String mSortOrder = FeedItemsTable.COLUMN_NAME_PUBLICATION_DATE;

    private synchronized SQLiteDatabase getReadableDb() {
		return this.readableDb;
	}
    
    Cursor mCursor;

    /* Runs on a worker thread */
    @Override
    public Cursor loadInBackground() {
    	initDatabase();
    	SQLiteCursor cursor = (SQLiteCursor) getReadableDb().query(
    			FeedItemsTable.TABLE_NAME, // table name
    			mProjection, // columns
    			mSelection, // selection
    			mSelectionArgs, // selection arguments
    			null, // group by
    			null, // having
    			mSortOrder + " ASC" // order by
    		);
    	if (cursor != null) {
    		cursor.getCount();
    		registerContentObserver(cursor, mObserver);
    	}
    	return cursor;
    }

    private void initDatabase() {
    	if (readableDb == null || !readableDb.isOpen()) {
			RssReaderDbHelper dbHelper = new RssReaderDbHelper(this.getContext());
			readableDb = dbHelper.getReadableDatabase();
    	}
	}
    
    void registerContentObserver(Cursor cursor, ContentObserver observer) {
        cursor.registerContentObserver(mObserver);
    }

    /* Runs on the UI thread */
    @Override
    public void deliverResult(Cursor cursor) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            if (cursor != null) {
                cursor.close();
            }
            return;
        }
        Cursor oldCursor = mCursor;
        mCursor = cursor;

        if (isStarted()) {
            super.deliverResult(cursor);
        }

        if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed()) {
            oldCursor.close();
        }
    }

    public FeedItemsLoader(Context context) {
        super(context);
        mObserver = new ForceLoadContentObserver();
    }

    @Override
    protected void onStartLoading() {
        if (mCursor != null) {
            deliverResult(mCursor);
        }
        if (takeContentChanged() || mCursor == null) {
            forceLoad();
        }
    }

    /**
     * Must be called from the UI thread
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        
        // Ensure the loader is stopped
        onStopLoading();

        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        mCursor = null;
    }
    
    public String[] getProjection() {
        return mProjection;
    }

    public void setProjection(String[] projection) {
        mProjection = projection;
    }

    public String getSelection() {
        return mSelection;
    }

    public void setSelection(String selection) {
        mSelection = selection;
    }

    public String[] getSelectionArgs() {
        return mSelectionArgs;
    }

    public void setSelectionArgs(String[] selectionArgs) {
        mSelectionArgs = selectionArgs;
    }

    public String getSortOrder() {
        return mSortOrder;
    }

    public void setSortOrder(String sortOrder) {
        mSortOrder = sortOrder;
    }

    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        writer.print(prefix); writer.print("mProjection=");
                writer.println(Arrays.toString(mProjection));
        writer.print(prefix); writer.print("mSelection="); writer.println(mSelection);
        writer.print(prefix); writer.print("mSelectionArgs=");
                writer.println(Arrays.toString(mSelectionArgs));
        writer.print(prefix); writer.print("mSortOrder="); writer.println(mSortOrder);
        writer.print(prefix); writer.print("mCursor="); writer.println(mCursor);
    }
}
