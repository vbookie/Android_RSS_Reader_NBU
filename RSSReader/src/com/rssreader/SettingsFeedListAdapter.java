package com.rssreader;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;

public class SettingsFeedListAdapter extends ArrayAdapter<String> {
	String feedsPreferenceKey;
	
	public SettingsFeedListAdapter(Context context, int resource) {
		super(context, resource);
		
		feedsPreferenceKey = context.getString(R.string.pref_feeds_key);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Set<String> feedsUrls;
		feedsUrls = prefs.getStringSet(feedsPreferenceKey, null);
		if (feedsUrls != null) {
			this.addAll(feedsUrls);
		}
	}
	
	public void addNewFeedUrl(String newUrl) {
		this.add(newUrl);
		this.persistFeedUrls();
	}
	
	public void replaceFeedUrl(int position, String newUrl) {
		if (position >= 0) {
			String currentUrl = this.getItem(position);
			if (currentUrl != newUrl) {
				this.remove(currentUrl);
				this.insert(newUrl, position);
				this.persistFeedUrls();
			}
		}
	}
	
	public void deleteFeedUrl(int position) {
		String url = this.getItem(position);
		this.remove(url);
		this.persistFeedUrls();
	}
	
	public void persistFeedUrls() {
		HashSet<String> set = new HashSet<String>(this.getCount());
		for (int i = 0; i < this.getCount(); i++) {
			set.add(this.getItem(i));
		}
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		Editor editor = prefs.edit();
		editor.remove(feedsPreferenceKey);
		editor.putStringSet(feedsPreferenceKey, set);
		editor.commit();
		// TODO: delete feeds from DB
	}
}
