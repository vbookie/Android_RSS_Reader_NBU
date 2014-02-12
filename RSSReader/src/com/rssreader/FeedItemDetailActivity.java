package com.rssreader;

import android.os.Bundle;

/**
 * An activity representing a single FeedItem detail screen. This activity is
 * only used on handset devices. On tablet-size devices, item details are
 * presented side-by-side with a list of items in a {@link FeedItemListActivity}
 * .
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link FeedItemDetailFragment}.
 */
public class FeedItemDetailActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feeditem_detail);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) {
			Bundle itemData = getIntent().getBundleExtra(FeedItemDetailFragment.ITEM_ARG);
			FeedItemDetailFragment fragment = new FeedItemDetailFragment();
			fragment.setArguments(itemData);
			getFragmentManager().beginTransaction()
					.add(R.id.feeditem_detail_container, fragment).commit();
		}
	}
}
