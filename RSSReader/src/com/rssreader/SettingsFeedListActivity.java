package com.rssreader;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class SettingsFeedListActivity extends ListActivity {
	SettingsFeedListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_feed_list);
		// Show the Up button in the action bar.
		setupActionBar();
		
		adapter = new SettingsFeedListAdapter(this, R.layout.activity_settings_feed_list_row);
		this.setListAdapter(adapter);
		
		registerForContextMenu(this.getListView());
	}
	
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings_feed_list, menu);
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.settings_feed_contextual_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.settings_feed_edit_action:
	        	editFeedClicked(info.position);
	            return true;
	        case R.id.settings_feed_delete_action:
	            deleteFeedClicked(info.position);
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	public void editFeedClicked(int position) {
		String url = adapter.getItem(position);
		
		Intent intent = new Intent(this, SettingsSingleFeedActivity.class);
		intent.putExtra("url", url);
		intent.putExtra("position", position);
		this.startActivityForResult(intent, 2);	
	}
	
	public void deleteFeedClicked(int position) {
		adapter.deleteFeedUrl(position);
	}
	
	public void addNewFeedClicked(MenuItem menuItem) {
		Intent intent = new Intent(this, SettingsSingleFeedActivity.class);
		this.startActivityForResult(intent, 1);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			switch (resultCode) {
			case RESULT_OK:
				String newUrl = data.getStringExtra("url");
				adapter.addNewFeedUrl(newUrl);
				break;
			case RESULT_CANCELED:
				break;
			}
		} else if (requestCode == 2) {
			switch (resultCode) {
			case RESULT_OK:
				String newUrl = data.getStringExtra("url");
				int position = data.getIntExtra("position", -1);
				adapter.replaceFeedUrl(position, newUrl);
				break;
			case RESULT_CANCELED:
				break;
			}
		}
	}
}
