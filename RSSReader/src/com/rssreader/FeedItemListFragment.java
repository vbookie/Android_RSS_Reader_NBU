package com.rssreader;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.rssreader.db.RssReaderContract.FeedItemsTable;

/**
 * A list fragment representing a list of FeedItems. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link FeedItemDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class FeedItemListFragment extends ListFragment implements LoaderCallbacks<Cursor>{

	private SimpleCursorAdapter mAdapter;
	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(Bundle itemData);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(Bundle itemData) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public FeedItemListFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.fragment_feeditem_list_row,
                null,
                new String[] {
					FeedItemsTable.COLUMN_NAME_TITLE,
					FeedItemsTable.COLUMN_NAME_SUMMARY,
					FeedItemsTable.COLUMN_NAME_PUBLICATION_DATE
				},
                new int[] {
					R.id.itemlist_row_headline,
					R.id.itemlist_row_summary,
					R.id.itemlist_row_date
				},
				0);
        setListAdapter(mAdapter);
		
		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		SQLiteCursor cursor = (SQLiteCursor) getListView().getItemAtPosition(position);
		String title = cursor.getString(1);
		String date = cursor.getString(3);
		String description = cursor.getString(4);
		String content = cursor.getString(5);
		String link = cursor.getString(6);

		Bundle itemData = new Bundle(5);
		itemData.putLong(FeedItemDetailFragment.ARG_ITEM_ID, id);
		itemData.putString(FeedItemDetailFragment.ARG_ITEM_TITLE, title);
		itemData.putString(FeedItemDetailFragment.ARG_ITEM_DATE, date);
		itemData.putString(FeedItemDetailFragment.ARG_ITEM_DESCRIPTION, description);
		itemData.putString(FeedItemDetailFragment.ARG_ITEM_CONTENT, content);
		itemData.putString(FeedItemDetailFragment.ARG_ITEM_LINK, link);

		mCallbacks.onItemSelected(itemData);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		FeedItemsLoader loader = new FeedItemsLoader(getActivity());
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		this.mAdapter.swapCursor(data);		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		this.mAdapter.swapCursor(null);
	}
}
