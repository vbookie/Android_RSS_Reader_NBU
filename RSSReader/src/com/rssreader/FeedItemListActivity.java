package com.rssreader;

import java.lang.ref.ReferenceQueue;
import java.util.HashSet;
import java.util.Set;

import com.rssreader.core.FeedsService;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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

	private ProgressBar progressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feeditem_list);

		if (findViewById(R.id.feeditem_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((FeedItemListFragment) getFragmentManager()
					.findFragmentById(R.id.feeditem_list))
					.setActivateOnItemClick(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.
		
		// Sets the default values for the preferences for first time
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		
		this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
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
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(FeedItemDetailFragment.ARG_ITEM_ID, id);
			FeedItemDetailFragment fragment = new FeedItemDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.replace(R.id.feeditem_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, FeedItemDetailActivity.class);
			detailIntent.putExtra(FeedItemDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
	
	public void refreshActionClicked(MenuItem menuItem) {
		// TODO: implement refresh
		// тоя рефреш трябва винаги да се вика
		// TODO: prevent the user to being able to start a new refresh while current one is still running
		RefreshDataTask task = new RefreshDataTask();
		task.execute();
	}
	
	private class RefreshDataTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			Log.d("ListActivity", "RefreshDataTask.onPreExecute");
			progressBar.setVisibility(ProgressBar.VISIBLE);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			Log.d("ListActivity", "RefreshDataTask.doInBackground");
			if (!FeedsService.isInProgress()) {
				Log.d("ListActivity", "Calling start service.");
				startService(new Intent(getApplicationContext(), FeedsService.class));
			}
			do {
				try {
					Thread.sleep(1000);
					Log.d("ListActivity", "Slept for 1 sec.");
				} catch (InterruptedException e) {
					return null;
				}
			} while (FeedsService.isInProgress() && !this.isCancelled());
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			Log.d("ListActivity", "RefreshDataTask.onPostExecute");
			progressBar.setVisibility(ProgressBar.GONE);
		}
	}
//TODO:	задължително трябва да може да се cancel-ва
}
