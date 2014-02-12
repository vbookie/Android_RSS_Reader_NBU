package com.rssreader;

import com.rssreader.core.FeedsService;

import android.app.Fragment;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

/**
 * An activity representing a list of FeedItems. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link FeedItemDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link FeedItemListFragment} and the item details (if present) is a
 * {@link FeedItemDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link FeedItemListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class FeedItemListActivity extends BaseActivity implements
		FeedItemListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private boolean forceRefreshTask = false;
	private ProgressBar progressBar;
	private RefreshDataTask refreshTask = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("RefreshTask", "OnCreate of Activity");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feeditem_list);

		if (findViewById(R.id.feeditem_detail_container) != null) {
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((FeedItemListFragment) getFragmentManager()
					.findFragmentById(R.id.feeditem_list))
					.setActivateOnItemClick(true);
		}

		// Sets the default values for the preferences for first time
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		
		this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
		
		
//		if (!FeedsService.isInProgress()) {
//			forceRefreshTask = true;
//		} else {
//			forceRefreshTask = false;
//		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.feed_item_list, menu);
		return true;
	}
	
	/**
	 * Callback method from {@link FeedItemListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(Bundle itemData) {
		if (mTwoPane) {
			FeedItemDetailFragment fragment = new FeedItemDetailFragment();
			fragment.setArguments(itemData);
			getFragmentManager().beginTransaction()
					.replace(R.id.feeditem_detail_container, fragment).commit();
		} else {
			Intent intent = new Intent(this, FeedItemDetailActivity.class);
			intent.putExtra(FeedItemDetailFragment.ITEM_ARG, itemData);
			startActivity(intent);
		}
	}
	
	@Override
	protected void onPause() {
		Log.d("RefreshTask", "OnPause.");
		if (refreshTask != null) {
			Log.d("RefreshTask", "Canceling refresh task.");
			refreshTask.cancel(true);
		}
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		Log.d("RefreshTask", "OnResume");
		if (FeedsService.isInProgress() || forceRefreshTask) {
			Log.d("RefreshTask", "Resuming refresh task.");
			refreshTask = new RefreshDataTask();
			refreshTask.execute();
			forceRefreshTask = false;
		}
		super.onResume();
	}
	
	public void refreshActionClicked(MenuItem menuItem) {
		if (refreshTask == null) {
			refreshTask = new RefreshDataTask();
			refreshTask.execute();
		}
	}
	
	private class RefreshDataTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			Log.d("RefreshTask", "RefreshDataTask.onPreExecute");
			progressBar.setVisibility(ProgressBar.VISIBLE);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			Log.d("RefreshTask", "RefreshDataTask.doInBackground");
			if (!FeedsService.isInProgress()) {
				Log.d("RefreshTask", "Calling start service.");
				startService(new Intent(getApplicationContext(), FeedsService.class));
			}
			do {
				try {
					Thread.sleep(1000);
					Log.d("RefreshTask", "Slept for 1 sec.");
				} catch (InterruptedException e) {
					return null;
				}
			} while (FeedsService.isInProgress() && !this.isCancelled());
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			Log.d("RefreshTask", "RefreshDataTask.onPostExecute");
			progressBar.setVisibility(ProgressBar.GONE);
			refreshTask = null;
			
			
			Loader<Cursor> loader = getFragmentManager().findFragmentById(R.id.feeditem_list).getLoaderManager().getLoader(0);
			if (loader != null && loader.isStarted())
				loader.forceLoad();
		}
	}
}
